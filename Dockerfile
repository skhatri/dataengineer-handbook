FROM cloudnativek8s/spark:3.4.0-b1.0.8

RUN mkdir -p /opt/app/jars && mkdir -p /opt/spark/work-dir
COPY --chown=user:app build/libs/spark-job-all.jar /opt/app/jars/
COPY --chown=user:app dse/truststore.jks /opt/app/jars/truststore.jks
ENV TRUSTSTORE_PATH="/opt/app/jars/truststore.jks"




