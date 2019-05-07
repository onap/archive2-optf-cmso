FROM openjdk:8u191-jdk-alpine3.9

MAINTAINER "The Onap Team"
LABEL Description="This image contains alpine with the openjdk installed"
ENV APP_HOME=/opt/app/cmso
ARG http_proxy
ARG https_proxy
ENV HTTP_PROXY=$http_proxy
ENV HTTPS_PROXY=$https_proxy
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

ENV MZN 2.2.3
ENV MZN_BASENAME MiniZincIDE-${MZN}-bundle-linux-x86_64
ENV MZN_UNZIPNAME MiniZincIDE-${MZN}-bundle-linux
ENV MZN_GH_BASE https://github.com/MiniZinc/MiniZincIDE
ENV MZN_DL_URL ${MZN_GH_BASE}/releases/download/${MZN}/${MZN_BASENAME}.tgz

RUN test -n "$http_proxy" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true &&  \
    apk add bash

# Minizinc
RUN wget -q $MZN_DL_URL -O mz.tgz
RUN tar xzf mz.tgz
RUN mv $MZN_UNZIPNAME /mz-dist
RUN rm mz.tgz
RUN echo PATH=/mz-dist:$PATH >> ~/.bashrc


COPY onap-cmso-optimizer/cmso-optimizer.jar ${APP_HOME}/app.jar

VOLUME /share/etc
VOLUME /share/logs
VOLUME /share/debug-logs

COPY onap-cmso-optimizer/startService.sh ${APP_HOME}/startService.sh
COPY onap-cmso-optimizer/data ${APP_HOME}/data
COPY onap-cmso-optimizer/scripts ${APP_HOME}/scripts

RUN chmod 700 ${APP_HOME}/startService.sh
RUN ln -s /share/etc ${APP_HOME}/etc
RUN ln -s /share/logs ${APP_HOME}/logs
RUN ln -s /share/debug-logs ${APP_HOME}/debug-logs

WORKDIR ${APP_HOME}
ENTRYPOINT ./startService.sh 
