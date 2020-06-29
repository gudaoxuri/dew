FROM openjdk:11-jre-slim as builder
WORKDIR application
COPY ./serv.jar ./
RUN java -Djarmode=layertools -jar serv.jar extract

FROM openjdk:11-jre-slim
MAINTAINER dewms

WORKDIR application

COPY ./run-java.sh ./
COPY ./debug-java.sh ./
COPY ./debug-clear.sh ./
RUN chmod 777 ./run-java.sh
RUN chmod 777 ./debug-java.sh
RUN chmod 777 ./debug-clear.sh
RUN echo 'Asia/Shanghai' >/etc/timezone
ARG PORT=8080
EXPOSE $PORT

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

CMD [ "./run-java.sh" ]
