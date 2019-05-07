FROM openjdk:8u191-jdk-alpine3.9

MAINTAINER "The Onap Team"
LABEL Description="This image contains alpine with the openjdk installed"
ENV APP_HOME=/opt/app/cmso-dbinit
ARG http_proxy
ARG https_proxy
ENV HTTP_PROXY=$http_proxy
ENV HTTPS_PROXY=$https_proxy
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true &&  \
    apk add bash
	
COPY onap-cmso/docker-liquibase.jar ${APP_HOME}/app.jar
COPY onap-cmso/startService.sh ${APP_HOME}/startService.sh

RUN chmod 700 ${APP_HOME}/startService.sh
RUN ln -s /share/etc ${APP_HOME}/etc
RUN ln -s /share/logs ${APP_HOME}/logs

VOLUME /share/etc
VOLUME /share/logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
