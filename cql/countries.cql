create keyspace if not exists countries with replication={'dc1': 1, 'class': 'NetworkTopologyStrategy'};

create table countries.country(
  name text,
  latitude decimal,
  longitude decimal,
  code text,
  primary key(name)
);

copy countries.country(code, latitude, longitude, name) from '/tmp/countries.csv'
with header=true;

