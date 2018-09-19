FROM dockercentral.it.att.com:5300/library/ubuntu:16.04
## Be careful of Windows newlines

MAINTAINER "Scheduler"

LABEL name="Docker image for the Scheduler Robot Testing Framework"
LABEL usage="docker run -e <testname> scheduler_robot

# Install Python Pip, Robot framework, firefox, lighttpd web server, wget
RUN apt-get update
RUN apt-get --assume-yes upgrade
RUN apt-get --assume-yes install python2.7 
RUN apt-get --assume-yes install build-essential
RUN apt-get --assume-yes install dbus
RUN apt-get --assume-yes install dnsutils
RUN apt-get --assume-yes install git
RUN apt-get --assume-yes install libappindicator1
RUN apt-get --assume-yes install libffi-dev
RUN apt-get --assume-yes install libindicator7
RUN apt-get --assume-yes install libssl-dev
RUN apt-get --assume-yes install libxss1
RUN apt-get --assume-yes install lighttpd
RUN apt-get --assume-yes install net-tools
RUN apt-get --assume-yes install python-dev
RUN apt-get --assume-yes install python-pip
RUN apt-get --assume-yes install unzip
RUN apt-get --assume-yes install wget 
RUN apt-get --assume-yes install xvfb
RUN pip install --upgrade pip
RUN pip install robotframework
RUN python --version

# Install chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list  && \
    apt-get update && \
    apt-get --assume-yes install google-chrome-stable

# Copy the robot code

COPY / /opt/app/scheduler_robot/
COPY docker/lighttpd.conf /etc/lighttpd/lighttpd.conf
RUN ls -ltr /opt/app/scheduler_robot
RUN chmod 777 /opt/app/scheduler_robot/setup.sh

# Update the ssh library so that it will run properly in the docker env
RUN cd  /opt/app/scheduler_robot/ && ./setup.sh && apt-get clean

###CMD ["lighttpd", "-D", "-f",  "/etc/lighttpd/lighttpd.conf"]



