FROM ubuntu:20.04

MAINTAINER "CMSO"

LABEL name="Docker image for the CMSO Robot Testing Framework"
LABEL usage="docker run -e <testname> optf-cmso-robot"

ARG DEBIAN_FRONTEND=noninteractive
ARG http_proxy_arg
ARG https_proxy_arg
ENV HTTP_PROXY=$http_proxy_arg
ENV HTTPS_PROXY=$https_proxy_arg
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true && \
apt-get update && \
apt-get install -y software-properties-common vim build-essential python3 python3-dev python3-pip python3-venv  && \
python3.8 -m pip install pip --upgrade && \
python3.8 -m pip install wheel  && \
apt-get install \
        --no-install-recommends \
        --assume-yes \
            chromium-browser \
            chromium-chromedriver \
            dnsutils \
            git \
            gcc

RUN groupadd onap \
    && useradd -m -g onap onap

COPY /onap-cmso/robot /opt/cmso-robot/robot
COPY /onap-cmso/mocking /opt/cmso-robot/mocking

COPY /onap-cmso/ete.sh /opt/cmso-robot

RUN ln -s /usr/bin/python3.8 /usr/bin/python && \
    python --version && \
    pip --version && \
    pip install robotframework==3.1.1 && \
    pip install Flask && \
    pip install requests && \
    pip install selenium && \
    pip install robotframework-sshlibrary && \
    pip install robotframework-requests

RUN chown -R onap:onap /opt/cmso-robot

USER onap

### Start up the mock server for
CMD ["/usr/bin/python", "/opt/cmso-robot/mocking/mock.py"]

### Use --entrypoint to override to run the tests test and exit
### --entrypoint /opt/cmso-robot/ete.sh
