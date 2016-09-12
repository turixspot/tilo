
# Tilo
Time logging made easy. [Tilo](http://tilo.turix.com.ar) is an open source time tracking solution for your company, project or personal use. 

## Install

* Install and configure [Elastic Search](https://www.elastic.co/downloads/past-releases/elasticsearch-1-7-5)
* Install and configure [Wildfly](http://wildfly.org/downloads/)
* Download latest Tilo release
* Extract the configuration file.
```bash
    jar xvf tilo.war WEB-INF/classes/elasticsearch.yml
```
* Modify elasticsearch.yml file to pint to your existent elastic search installation `client.transport.init: ["{{yourserver}}:9300"]`
* Update the configuration file.
```bash
    jar uvf tilo.war WEB-INF/classes/elasticsearch.yml
```
* Deploy Tilo
```bash
    $WILDFLY_HOME/bin/jboss-cli.sh --connect --command="deploy --force [PATH_TO_WAR]"
```
* Create your administrative user.
```bash
curl -XPUT 'http://localhost:9200/users/user/admin@yourorganization.com' -d '{
    "id" : "admin@yourorganization.com",
    "name" : "Administrator",
    "roles" : ["administrator"],
    "password" : "admin"
}'
```
* You are now ready to start using Tilo at http://yourwildflyserver:8080/tilo/