package ar.com.turix.tilo.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.inject.Singleton;

import org.apache.lucene.util.IOUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.jboss.logging.Logger;

import ar.com.turix.tilo.model.Upgrade;
import ar.com.turix.tilo.resources.Upgrades;

import com.google.common.net.InetAddresses;

@Singleton
public class Elastic implements Closeable {

	private static final Logger LOGGER = Logger.getLogger(Elastic.class);

	private static final String VERSION = "2-0-0";

	public static enum Index {
		upgrades, users, projects, logs;

		public String versioned() {
			return VERSION + "-" + this.name();
		}

		public String versioned(String version) {
			return version + "-" + this.name();
		}

		public String type() {
			return this.name().substring(0, this.name().length() - 1);
		}

		public static Index[] upgradables() {
			return new Index[] { users, projects, logs };
		}
	}

	private TransportClient client;

	private Upgrades upgrades;

	public Elastic() {
		client = TransportClient.builder()
				.settings(Settings.builder().loadFromStream("elasticsearch.yml", this.getClass().getResourceAsStream("/elasticsearch.yml")))//
				.build();
		upgrades = new Upgrades(this);

		LOGGER.info("Settings " + client.settings().getAsStructuredMap().toString());

		connect();
	}

	/**
	 * Look for client.transport.init property at elasticsearch.yml to add configured transport addresses. <br>
	 * <br>
	 * <code>
	 * client.transport.init: ["localhost:9300", "anotherhost:9300"]
	 * </code>
	 */
	private void connect() {
		for (String host : client.settings().getAsArray("client.transport.init")) {
			int port = 9300;

			// or parse it from the host string...
			String[] splitHost = host.split(":", 2);
			if (splitHost.length == 2) {
				host = splitHost[0];
				port = Integer.parseInt(splitHost[1]);
			}

			client.addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString(host), port));
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("resource")
	public synchronized void upgrade() {
		IndicesAdminClient indices = client.admin().indices();
		if (indices.prepareExists(Index.upgrades.name()).get().isExists()) {
			// get newest version installed
			Upgrade upgrade = upgrades.last();
			String latest = upgrade.getId();

			if (!VERSION.equals(latest)) {
				LOGGER.info("Index version upgrade is required.");
				for (Index index : Index.upgradables()) {
					LOGGER.info("Upgrading " + index.name());
					if (indices.prepareExists(index.versioned()).get().isExists()) {
						LOGGER.info("Delete index " + index.versioned() + ", a previous not completed upgrade already exists.");
						DeleteIndexRequestBuilder delete = indices.prepareDelete(index.versioned());
						delete.get();
					}

					// Create new index.
					InputStream is = null;
					Scanner scanner = null;
					try {
						is = this.getClass().getResourceAsStream("/mapping/" + index.versioned() + ".json");
						if (is != null) {
							scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");

							// create index.
							CreateIndexRequestBuilder request = indices.prepareCreate(index.versioned());
							request.addMapping(index.type(), scanner.next());
							request.get();
							LOGGER.info("Created index " + index.versioned());
						} else {
							LOGGER.warn("Index " + index.name() + " has no mapping available for version " + VERSION);
						}
					} finally {
						IOUtils.closeWhileHandlingException(is, scanner);
					}

					// Bulk copy.
					LOGGER.info("Migrating data from index " + index.versioned(latest) + " to " + index.versioned());
					SearchResponse scroll = client.prepareSearch(index.versioned(latest))//
							.setSearchType(SearchType.SCAN)//
							.setScroll(new TimeValue(60000))//
							.setQuery(QueryBuilders.matchAllQuery())//
							.setSize(1000).get();

					LOGGER.info(scroll.getHits().getTotalHits() + " documents to copy ");

					// Scroll until no hits are returned
					long count = 0;
					while (true) {

						scroll = client.prepareSearchScroll(scroll.getScrollId()).setScroll(new TimeValue(60000)).get();
						// Break condition: No hits are returned
						if (scroll.getHits().getHits().length == 0)
							break;

						BulkRequestBuilder bulk = client.prepareBulk();
						for (SearchHit hit : scroll.getHits()) {
							bulk.add(client.prepareIndex(index.versioned(), index.type()).setId(hit.getId()).setSource(hit.getSourceRef()));
							count++;
						}

						bulk.get();

						LOGGER.info(count + " documents copied from " + index.versioned(latest) + " to " + index.versioned());
					}

					indices.prepareAliases()//
							.removeAlias(index.versioned(latest), index.name())//
							.addAlias(index.versioned(), index.name())//
							.get();
					LOGGER.info("Updated alias " + index.name() + " to " + index.versioned());

					// Close old indexes.
					indices.prepareClose(index.versioned(latest)).get();
					LOGGER.info("Closed index " + index.versioned(latest));
				}

				upgrades.save(new Upgrade(VERSION));
			}

		} else {

			InputStream is = null;
			Scanner scanner = null;
			// Create upgrade record.
			try {
				is = this.getClass().getResourceAsStream("/mapping/upgrades.json");
				if (is != null) {
					scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");

					// create index.
					CreateIndexRequestBuilder request = indices.prepareCreate(Index.upgrades.name());
					request.addMapping(Index.upgrades.type(), scanner.next());
					request.get();

				} else {
					LOGGER.warn("Index " + Index.upgrades.name() + " has no mapping available for this version.");
				}
			} finally {
				IOUtils.closeWhileHandlingException(is, scanner);
			}

			for (Index index : Index.upgradables()) {
				try {
					is = this.getClass().getResourceAsStream("/mapping/" + index.versioned() + ".json");
					if (is != null) {
						scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");

						// create index.
						CreateIndexRequestBuilder request = indices.prepareCreate(index.versioned());
						request.addMapping(index.type(), scanner.next());
						request.get();

						// create alias
						IndicesAliasesRequestBuilder alias = indices.prepareAliases();
						alias.addAlias(index.versioned(), index.name());
						alias.get();
					} else {
						LOGGER.warn("Index " + index.name() + " has no mapping available for this version.");
					}
				} finally {
					IOUtils.closeWhileHandlingException(is, scanner);
				}
			}
			upgrades.save(new Upgrade(VERSION));
		}
	}

	public TransportClient client() {
		return this.client;
	}

	@Override
	public void close() throws IOException {
		client.close();
	}
}