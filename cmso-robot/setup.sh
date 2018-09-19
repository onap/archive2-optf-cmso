#!/bin/bash
#
# setup : script to setup required runtime environment. This script can be run again to update anything
# this should stay in your project directory
#

# get the path
path=$(pwd)
pip install --upgrade pip
pip install --no-cache-dir --target="$path/robot/library" 'selenium<=3.0.0' 
pip install --no-cache-dir --target="$path/robot/library" 'requests==2.11.1'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-selenium2library==1.8.0'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-databaselibrary==0.8.1'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-extendedselenium2library==0.9.1'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-requests==0.4.5'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-sshlibrary==2.1.2'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-sudslibrary==0.8'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-ftplibrary==1.3'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-rammbock==0.4.0.1'
pip install --no-cache-dir --target="$path/robot/library" 'deepdiff==2.5.1'
pip install --no-cache-dir --target="$path/robot/library" 'dnspython==1.15.0'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-httplibrary==0.4.2'
pip install --no-cache-dir --target="$path/robot/library" 'robotframework-archivelibrary==0.3.2'
pip install --no-cache-dir --target="$path/robot/library" 'PyYAML==3.12'

# NOTE: Patch to incude explicit install of paramiko to 2.0.2 to work with sshlibrary 2.1.2
# This should be removed on new release of paramiko (2.1.2) or sshlibrary
# https://github.com/robotframework/SSHLibrary/issues/157
pip install --no-cache-dir --target="$path/robot/library" -U 'paramiko==2.0.2'

#
# Get the appropriate chromedriver. Default to linux64
#
CHROMEDRIVER_URL=http://chromedriver.storage.googleapis.com/2.29
CHROMEDRIVER_ZIP=chromedriver_linux64.zip

# Handle mac and windows
OS=`uname -s`
case $OS in
  MINGW*_NT*)
  	CHROMEDRIVER_ZIP=chromedriver_win32.zip
  	;;
  Darwin*)
  	CHROMEDRIVER_ZIP=chromedriver_mac64.zip
  	;;
  *) echo "Defaulting to Linux 64" ;;
esac

	
#  Temporar  remove until we figure out proxy blocking issue
#    wget -O chromedriver.zip $CHROMEDRIVER_URL/$CHROMEDRIVER_ZIP
#	unzip chromedriver.zip -d /usr/local/bin

if [ $CHROMEDRIVER_ZIP == 'chromedriver_linux64.zip' ]
then
	echo Skipping
else
    curl $CHROMEDRIVER_URL/$CHROMEDRIVER_ZIP -o chromedriver.zip
	unzip chromedriver.zip
fi
