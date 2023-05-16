docker run \
-e BUCKET_NAME=spark-job-build \
-e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
-it spark-job /opt/spark/bin/spark-submit \
    --deploy-mode client \
    --name hello \
    --class demo.ParquetReadWrite \
    --conf spark.executor.instances=1 \
    --conf spark.executor.userClassPathFirst=false \
    --conf spark.driver.userClassPathFirst=false \
    /opt/app/jars/spark-job-all.jar


