FROM ubuntu:16.04

MAINTAINER "CMSO"

LABEL name="Docker image for the CMSO Robot Testing Framework"
LABEL usage="docker run -e <testname> optf-cmso-robot"

ARG http_proxy_arg
ARG https_proxy_arg
ENV HTTP_PROXY=$http_proxy_arg
ENV HTTPS_PROXY=$https_proxy_arg
ENV http_proxy=$HTTP_PROXY
ENV https_proxy=$HTTPS_PROXY

RUN test -n "$" && echo "Acquire::Proxy \"http://$http_proxy\";" > /etc/apt/apt.conf.d/02proxy || true && \
apt-get update && \
apt-get install -y software-properties-common vim && \
add-apt-repository ppa:deadsnakes/ppa && \
apt-get update  && \
apt-get install -y build-essential python3.6 python3.6-dev python3-pip python3.6-venv  && \
python3.6 -m pip install pip --upgrade && \
python3.6 -m pip install wheel  && \
apt-get install \
        --no-install-recommends \
        --assume-yes \
            chromium-browser \
            chromium-chromedriver \
            dnsutils \
            git \
            gcc 
            

COPY /onap-cmso/robot /opt/cmso-robot/robot
COPY /onap-cmso/mocking /opt/cmso-robot/mocking

COPY /onap-cmso/ete.sh /opt/cmso-robot

RUN ln -s /usr/bin/python3.6 /usr/bin/python && \
    python --version && \
    pip --version && \
    pip install robotframework==3.1.1 && \
    pip install Flask && \
    pip install requests && \
    pip install selenium && \
    pip install robotframework-sshlibrary && \
    pip install robotframework-requests

### Start up the mock server for 
CMD ["/usr/bin/python", "/opt/cmso-robot/mocking/mock.py"]

### Use --entrypoint to override to run the tests test and exit
### --entrypoint /opt/cmso-robot/ete.sh
