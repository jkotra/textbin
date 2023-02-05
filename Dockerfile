FROM amazoncorretto:17-alpine3.17-jdk as build
RUN mkdir textbin
WORKDIR textbin
COPY . .
RUN ls
RUN ./mvnw -DskipTests install

FROM amazoncorretto:17-alpine3.17
EXPOSE 8080

ENV SPRING_DATASOURCE_URL="" \
    SPRING_DATASOURCE_USERNAME="" \
    SPRING_DATASOURCE_PASSWORD="" \
    GRC_SECRET=""

COPY --from=build /textbin/target/textbin-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "textbin-0.0.1-SNAPSHOT.jar"]