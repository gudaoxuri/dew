FROM nginx:alpine

COPY ./dist /usr/share/nginx/html
COPY ./custom.conf /etc/nginx/conf.d/custom.conf

ARG PORT=80

EXPOSE $PORT