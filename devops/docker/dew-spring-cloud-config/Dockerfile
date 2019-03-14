FROM java:8-jdk-alpine
MAINTAINER gudaoxuri <i@sunisle.org>

ADD ./config.jar ./config.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","./config.jar"]
