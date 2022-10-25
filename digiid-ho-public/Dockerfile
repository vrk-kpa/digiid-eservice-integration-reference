#Build java applications
FROM maven@sha256:13f5c095c4407fbdb426e5eb27e565207f38324185ee06fd19a83d5c18db319e AS builder
WORKDIR /build
COPY . .
RUN mvn clean package

#Run op-rest Application
FROM maven@sha256:13f5c095c4407fbdb426e5eb27e565207f38324185ee06fd19a83d5c18db319e AS op_rest
WORKDIR /app
COPY --from=builder /build/digiid-ho-public-op/rest/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Run VDR Application
FROM maven@sha256:13f5c095c4407fbdb426e5eb27e565207f38324185ee06fd19a83d5c18db319e AS vdr
WORKDIR /app
COPY --from=builder /build/digiid-ho-public-vdr/rest/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]