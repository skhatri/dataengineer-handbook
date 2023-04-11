FROM cloudnativek8s/spark:3.3.2-b1.0.6
RUN mkdir -p /opt/app/jars && mkdir -p /opt/spark/work-dir
COPY --chown=app:app build/libs/spark-job-all.jar /opt/app/jars/
COPY --chown=app:app dse/truststore.jks /opt/app/jars/truststore.jks
ENV TRUSTSTORE_PATH="/opt/app/jars/truststore.jks"




