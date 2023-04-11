### Setup Cassandra SSL
This setup can help test cassandra setup with TLS which is often the case with production environments.

Copy a cassandra.yaml from the cassandra image of your choice and update it such that you configure the TLS behaviour.
```
docker run -v $(pwd)/cql:/tmp --entrypoint bash -it datastax/dse-server:6.8.26
#in the shell
$ cp /opt/dse/resources/cassandra/conf/cassandra.yaml /tmp/cassandra.yaml
$ exit

```

With the copied file, the Client Connection TLS behaviour can be added like so:

```
client_encryption_options:
    enabled: true
    optional: false
    keystore: resources/dse/conf/keystore.p12
    keystore_password: cassandra
    keystore_type: PKCS12
    cipher_suites: [TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384]
```
Refer to [Certificate](../Certificate) on how to create keystore.p12 used in this example.
Cassandra by default supports weak ciphers as well. The cipher_suites list contains strong ciphers.



