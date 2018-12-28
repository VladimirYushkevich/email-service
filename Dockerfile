FROM java:8 as builder
RUN mkdir -p /opt/email-service
COPY . /opt/email-service
WORKDIR /opt/email-service/
RUN ./gradlew build

FROM anapsix/alpine-java:latest
VOLUME /tmp
COPY --from=builder /opt/email-service/build/libs/email-service-*.jar email-service.jar
EXPOSE 8878
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "email-service.jar"]
