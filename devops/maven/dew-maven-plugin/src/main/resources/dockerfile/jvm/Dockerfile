FROM openjdk:8-alpine

COPY ./run-java.sh ./
COPY ./serv.jar ./

RUN chmod 777 ./run-java.sh

ARG PORT=8080

EXPOSE $PORT

CMD [ "./run-java.sh" ]
