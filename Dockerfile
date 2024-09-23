######################## STAGE 1 ###########################
FROM maven:3.9.0-eclipse-temurin-17 as builder

WORKDIR /build
COPY . .

RUN mvn clean package

######################## STAGE 2 ###########################
FROM eclipse-temurin:17.0.12_7-jre-ubi9-minimal

WORKDIR /opt/app

COPY --from=builder /build/target/spanner-0.1.0.jar spanner-0.1.0.jar
COPY --from=builder /build/run.sh run.sh

# Uncomment to run via SA
# COPY --from=builder /build/sa.json creds.json
# ENV GOOGLE_APPLICATION_CREDENTIALS=/opt/app/creds.json

RUN chmod +x run.sh

ENTRYPOINT ["./run.sh"]
CMD [""]