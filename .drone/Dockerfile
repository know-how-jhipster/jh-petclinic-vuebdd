FROM docker-registry.continuousdelivery.store/jdk:openjdk-11-jre-slim
ARG BUILD_VERSION
ENV APPLICATION_BUILD_VERSION=$BUILD_VERSION
WORKDIR /opt/app
USER javauser
COPY ./build/libs/*.jar /opt/app/app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Duser.timezone=UTC -jar /opt/app/app.jar"]
EXPOSE 8080
