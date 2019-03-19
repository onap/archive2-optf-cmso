#!/bin/bash
#
# setup : script to setup required runtime environment. This script can be run again to update anything
# this should stay in your project directory
#

# get the path
path=$(pwd)
pip install --no-cache-dir --target="$path/library" \
	 'robotframework' \
	 'requests' \
	 'robotframework-requests' \
	 'selenium' \
	 'robotframework-seleniumlibrary' \
	 'robotframework-sshlibrary' \
	 'paramiko'  
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
