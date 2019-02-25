FROM ppodgorsek/robot-framework:latest

MAINTAINER "CMSO"

LABEL name="Docker image for the CMSO Robot Testing Framework"
LABEL usage="docker run -e <testname> optf-cmso-robot"

COPY /onap-cmso/robot /opt/cmso-robot/robot

COPY /onap-cmso/ete.sh /opt/cmso-robot
RUN chmod 777 /opt/cmso-robot/ete.sh

COPY /onap-cmso/server.py /opt/cmso-robot
RUN pip install Flask
RUN pip install 'PyYAML==3.12'
RUN pip install  'selenium' 
RUN pip install  'requests'
RUN pip install  'robotframework-selenium2library'
RUN pip install  'robotframework-databaselibrary'
RUN pip install  'robotframework-extendedselenium2library'
RUN pip install  'robotframework-requests'
RUN pip install  'robotframework-sshlibrary'
RUN pip install  'robotframework-sudslibrary'
RUN pip install  'robotframework-ftplibrary'
RUN pip install  'robotframework-rammbock'
RUN pip install  'deepdiff'
RUN pip install  'dnspython'
RUN pip install  'robotframework-httplibrary'
RUN pip install  'robotframework-archivelibrary'



###Just to keep it running
CMD ["/usr/bin/python", "/opt/cmso-robot/server.py"]

### Use --entrypoint to override to run the tests test and exit
### --entrypoint /opt/cmso-robot/ete.sh


