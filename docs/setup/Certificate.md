### Create a CA and Certs
Run ./ca.sh <certname> to generate all keys and certs signed by a self-signed CA

For instance 

```
./ca.sh dse
```
will create a folder "dse" where the certs are stored.
Password for all keys, truststore are set to "cassandra"