### Cassandra in Docker
For example purpose, we will be reading and writing to/from Cassandra and S3. Hence this step is recommended.

Refer to [docker-compose.yaml](http://github.com/skhatri/spark-by-example/docker-compose.yaml)

Here is the same docker-compose file

```
version: '3.7'
volumes:
    dsedb: {}
services:

    dse:
      build: .
      image: datastax/dse-server:6.8.26 
      environment:
        - DS_LICENSE=accept
        - SSL_VALIDATE=false
      ports:
        - "9042:9042"
      container_name: dse
      volumes:
        - ./cql/data:/var/lib/cassandra/data
        - ./cql/cassandra.yaml:/config/cassandra.yaml
        - ./dse/keystore.p12:/opt/dse/resources/dse/conf/keystore.p12
      networks:
        sparkjob:
          aliases:
            - dse
networks:
  sparkjob: {}
```
We are using the network called sparkjob so that adhoc docker runs can use this network to connect with "dse".
CQL/data is a folder that is mapped to cassandra/data so our table data is persisted in this project itself.

The file cql/cassandra.yaml is a copy of cassandra yaml found in docker image with modifications done on it to 
configure keystore.p12 to enable TLS and ciphers.
