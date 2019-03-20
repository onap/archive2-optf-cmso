FROM robotframework/rfdocker:3.1.1

MAINTAINER "CMSO"

LABEL name="Docker image for the CMSO Robot Testing Framework"
LABEL usage="docker run -e <testname> optf-cmso-robot"

COPY /onap-cmso/robot /opt/cmso-robot/robot

COPY /onap-cmso/ete.sh /opt/cmso-robot
RUN chmod 777 /opt/cmso-robot/ete.sh

COPY /onap-cmso/server.py /opt/cmso-robot
RUN pip install Flask

###Just to keep it running
CMD ["/usr/bin/python", "/opt/cmso-robot/server.py"]

### Use --entrypoint to override to run the tests test and exit
### --entrypoint /opt/cmso-robot/ete.sh


