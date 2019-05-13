FROM openjdk:8-alpine

COPY ./run-java.sh ./
COPY ./debug-java.sh ./
COPY ./serv.jar ./

RUN chmod 777 ./run-java.sh
RUN chmod 777 ./debug-java.sh

RUN echo 'Asia/Shanghai' >/etc/timezone
RUN yum install dejavu-sans-fonts fontconfig -y

ARG PORT=8080

EXPOSE $PORT

CMD [ "./run-java.sh" ]
