FROM ubuntu:16.04

MAINTAINER "The Onap Team"
LABEL Description="This image contains ubuntu 16.04 with the openjdk installed" Version="16.04-8"
ENV APP_HOME=/opt/app/cmso

ARG http_proxy_arg
ARG https_proxy_arg
ENV HTTP_PROXY=$http_proxy_arg
ENV HTTPS_PROXY=$https_proxy_arg
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true &&  \
    apt-get update &&  \
    apt-get -y dist-upgrade &&  \
    apt-get install -y openjdk-8-jre-headless && \
	apt-get install -y curl

COPY onap-cmso-topology/cmso-topology.jar ${APP_HOME}/app.jar

VOLUME /share/etc
VOLUME /share/logs
VOLUME /share/debug-logs

COPY onap-cmso-topology/startService.sh ${APP_HOME}/startService.sh
COPY onap-cmso-topology/data ${APP_HOME}/data

RUN chmod 700 ${APP_HOME}/startService.sh && \
    ln -s /share/etc ${APP_HOME}/etc && \
    ln -s /share/logs ${APP_HOME}/logs && \
    ln -s /share/debug-logs ${APP_HOME}/debug-logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
