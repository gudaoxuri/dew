FROM pangm/nginx-node:8.5

RUN mkdir /dist

COPY ./dist /dist
COPY ./custom.conf /etc/nginx/conf.d/custom.conf

RUN echo 'Asia/Shanghai' >/etc/timezone

RUN yum install git -y

ARG PORT=80

EXPOSE $PORT

ARG startCmd

ENV startCmd=${startCmd}

CMD ["sh","-c","cd /dist && rm -rf node_modules/ && npm install && ${startCmd}"]
