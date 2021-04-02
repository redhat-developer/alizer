FROM openjdk:14-alpine
COPY target/untitled-*.jar untitled.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "untitled.jar"]