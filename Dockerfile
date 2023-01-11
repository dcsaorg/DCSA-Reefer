FROM eclipse-temurin:17-jre-alpine

EXPOSE 9090
ENV db_hostname dcsa_db
COPY run-in-container.sh /run.sh
RUN chmod +x /run.sh
COPY reefer-commercial-application/src/main/resources/application.yml .
COPY reefer-commercial-application/target/dcsa-reefer-commercial-application.jar .
CMD ["/run.sh"]
