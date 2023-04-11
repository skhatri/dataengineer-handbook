### Running Locally
Easiest way to run spark is to use deploy-mode of client and by accessing the jar file by it's exact location.


1. Build Jar file

```
./gradlew clean build

```

2. Setup local SPARK_HOME

```
mkdir -p downloads
curl -o downloads/spark-3.3.2-bin-hadoop3.tgz \
  https://archive.apache.org/dist/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3.tgz
tar zxf downloads/spark-3.3.2-bin-hadoop3.tgz -C downloads
export SPARK_HOME=downloads/spark-3.3.2-bin-hadoop3
```

3. Run Spark Submit against Local SPARK_HOME

```
$SPARK_HOME/bin/spark-submit \
    --deploy-mode client \
    --name hello \
    --class demo.Count \
    --conf spark.executor.instances=1 \
    ./build/libs/spark-job-all.jar
```