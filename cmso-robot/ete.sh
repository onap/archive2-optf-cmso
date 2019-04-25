#!/bin/bash
#

ROBOT_CMD="python -m robot.run"
ROBOT_HOME=`pwd`
ROBOT_PATH=${ROBOT_PATH}:${ROBOT_HOME}/library
ROBOT_PATH=${ROBOT_PATH}:${ROBOT_HOME}/robot/locallibrary/cmsoUtils
VARIABLE_FILES="${VARIABLE_FILES} -V ${ROBOT_HOME}/robot/assets/test_properties.py" 

VARIABLES="${VARIABLES} -v GLOBAL_SCHEDULER_URL:${GLOBAL_SCHEDULER_URL}"
VARIABLES="${VARIABLES} -v GLOBAL_OPTIMIZER_URL:${GLOBAL_OPTIMIZER_URL}"
VARIABLES="${VARIABLES} -v GLOBAL_TICKET_MGT_URL:${GLOBAL_TICKET_MGT_URL}"
VARIABLES="${VARIABLES} -v GLOBAL_TOPOLOGY_URL:${GLOBAL_TOPOLOGY_URL}"
VARIABLES="${VARIABLES} -v CMSO_STARTUP_WAIT_TIME:${CMSO_STARTUP_WAIT_TIME}"

HTTP_PROXY=
HTTPS_PROXY=

export PYTHONPATH=${ROBOT_PATH}:${PYTHONPATH}
echo PYTHONPATH=${PYTHONPATH}
pgrep -f mock.py
if [ $? == 1 ]
then
    nohup python ${ROBOT_HOME}/mocking/mock.py &
fi  


${ROBOT_CMD} ${OUTPUT} -P ${ROBOT_PATH} ${VARIABLE_FILES} ${VARIABLES} ${TAGS} ${ROBOT_HOME}

pkill -f mock.py