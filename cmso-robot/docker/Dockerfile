FROM ubuntu:16.04

MAINTAINER "CMSO"

LABEL name="Docker image for the CMSO Robot Testing Framework"
LABEL usage="docker run -e <testname> optf-cmso-robot"

RUN apt-get update
RUN apt-get install -y software-properties-common vim
RUN add-apt-repository ppa:jonathonf/python-3.6
RUN apt-get update
RUN apt-get install -y build-essential python3.6 python3.6-dev python3-pip python3.6-venv

# update pip
RUN python3.6 -m pip install pip --upgrade
RUN python3.6 -m pip install wheel

RUN apt-get update \
    && apt-get install \
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
RUN chmod 777 /opt/cmso-robot/ete.sh

COPY /onap-cmso/server.py /opt/cmso-robot


RUN ln -s /usr/bin/python3.6 /usr/bin/python
RUN python --version
RUN pip --version
RUN pip install robotframework==3.1.1

RUN pip install Flask
RUN pip install requests
RUN pip install selenium
RUN pip install robotframework-sshlibrary
RUN pip install robotframework-requests

###Just to keep it running
CMD ["/usr/bin/python", "/opt/cmso-robot/server.py"]

### Use --entrypoint to override to run the tests test and exit
### --entrypoint /opt/cmso-robot/ete.sh
