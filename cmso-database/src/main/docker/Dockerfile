FROM ubuntu:18.04

MAINTAINER "The Onap Team"
LABEL Description="This image contains ubuntu 16.04 with the openjdk installed" Version="16.04-8"
ENV APP_HOME=/opt/app/cmso-dbinit

ARG http_proxy_arg
ARG https_proxy_arg
ENV HTTP_PROXY=$http_proxy_arg
ENV HTTPS_PROXY=$https_proxy_arg
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true &&  \
    apt-get update &&  \
    apt-get -y dist-upgrade &&  \
    apt-get install -y openjdk-11-jre-headless && \
	apt-get install -y curl

COPY onap-cmso/docker-liquibase.jar ${APP_HOME}/app.jar
COPY onap-cmso/startService.sh ${APP_HOME}/startService.sh

RUN chmod 700 ${APP_HOME}/startService.sh && \
    ln -s /share/etc ${APP_HOME}/etc && \
    ln -s /share/logs ${APP_HOME}/logs

VOLUME /share/etc
VOLUME /share/logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
