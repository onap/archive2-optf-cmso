FROM openjdk:8u191-jdk-alpine3.9

MAINTAINER "The Onap Team"
LABEL Description="This image contains ubuntu 16.04 with the openjdk installed" Version="16.04-8"
ENV APP_HOME=/opt/app/cmso
ARG http_proxy
ARG https_proxy
ENV HTTP_PROXY=$http_proxy
ENV HTTPS_PROXY=$https_proxy
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true &&  \
    apk add bash

COPY onap-cmso-tm/cmso-ticketmgt.jar ${APP_HOME}/app.jar

VOLUME /share/etc
VOLUME /share/logs
VOLUME /share/debug-logs

COPY onap-cmso-tm/startService.sh ${APP_HOME}/startService.sh
COPY onap-cmso-tm/data ${APP_HOME}/data

RUN chmod 700 ${APP_HOME}/startService.sh
RUN ln -s /share/etc ${APP_HOME}/etc
RUN ln -s /share/logs ${APP_HOME}/logs
RUN ln -s /share/debug-logs ${APP_HOME}/debug-logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
