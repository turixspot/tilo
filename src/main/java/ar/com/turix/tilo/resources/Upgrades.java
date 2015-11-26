package ar.com.turix.tilo.resources;

import java.util.UUID;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

import ar.com.turix.tilo.model.Upgrade;
import ar.com.turix.tilo.utils.Elastic;
import ar.com.turix.tilo.utils.Elastic.Index;

public class Upgrades extends AbstractResource {

	public Upgrades() {
		super(Index.upgrades.name(), Index.upgrades.type());
	}

	public Upgrades(Elastic elastic) {
		super(elastic, Index.upgrades.name(), Index.upgrades.type());
	}

	public void save(Upgrade u) {
		if (u.getId() == null) // create
			u.setId(UUID.randomUUID().toString());

		prepareIndex(u.getId()).setSource(write(u)).get();
		refresh();
	}

	public Upgrade last() {
		SearchResponse response = prepareSearch()//
				.setQuery(QueryBuilders.matchAllQuery())//
				.addSort("id", SortOrder.DESC)//
				.get();

		return read(response.getHits().getAt(0).getSourceAsString(), Upgrade.class);
	}
}