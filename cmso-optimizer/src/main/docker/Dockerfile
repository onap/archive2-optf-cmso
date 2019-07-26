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

ENV MZN 2.2.3
ENV MZN_BASENAME MiniZincIDE-${MZN}-bundle-linux-x86_64
ENV MZN_UNZIPNAME MiniZincIDE-${MZN}-bundle-linux
ENV MZN_GH_BASE https://github.com/MiniZinc/MiniZincIDE
ENV MZN_DL_URL ${MZN_GH_BASE}/releases/download/${MZN}/${MZN_BASENAME}.tgz

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true  && \
    apt-get update &&  \
    apt-get -y dist-upgrade &&  \
    apt-get install -y openjdk-8-jre-headless && \
    apt-get install -y wget && \
    apt-get install -y curl && \
    set && \
    wget -q $MZN_DL_URL -O mz.tgz && \
    tar xzf mz.tgz && \
    mv $MZN_UNZIPNAME /mz-dist && \
    rm mz.tgz && \
    echo PATH=/mz-dist:$PATH >> ~/.bashrc


COPY onap-cmso-optimizer/cmso-optimizer.jar ${APP_HOME}/app.jar

VOLUME /share/etc
VOLUME /share/logs
VOLUME /share/debug-logs

COPY onap-cmso-optimizer/startService.sh ${APP_HOME}/startService.sh
COPY onap-cmso-optimizer/data ${APP_HOME}/data
COPY onap-cmso-optimizer/scripts ${APP_HOME}/scripts

RUN chmod 700 ${APP_HOME}/startService.sh && \
    ln -s /share/etc ${APP_HOME}/etc && \
    ln -s /share/logs ${APP_HOME}/logs && \
    ln -s /share/debug-logs ${APP_HOME}/debug-logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
