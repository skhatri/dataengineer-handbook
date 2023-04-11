### Running against Docker

Run the pre-requisite Cassandra so that our process can do something useful.
```docker-compose up -d```

The gradle clean build command would have produced the jar but we can run 
build.sh to create jar and then to produce an image file at the end.
```
./build.sh
```

We will execute this image file against the same network as one used in docker-compose.yaml file.

Find out what networks you have setup.
```
docker network ls
```

Double check that your network is spark-job-sparkjob. Otherwise, adjust it accordingly

```
docker run --network=spark-job_sparkjob \
-e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
-it spark-job /opt/spark/bin/spark-submit \
    --deploy-mode client \
    --name hello \
    --class demo.Count \
    --conf spark.executor.instances=1 \
    --conf spark.executor.userClassPathFirst=false \
    --conf spark.driver.userClassPathFirst=false \
    /opt/app/jars/spark-job-all.jar`
```

You should have exported AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY by this point.
