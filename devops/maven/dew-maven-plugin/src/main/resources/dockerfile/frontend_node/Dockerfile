FROM nginx:alpine

COPY ./dist /usr/share/nginx/html
COPY ./custom.conf /etc/nginx/conf.d/custom.conf

RUN echo 'Asia/Shanghai' >/etc/timezone

ARG PORT=80

EXPOSE $PORT
