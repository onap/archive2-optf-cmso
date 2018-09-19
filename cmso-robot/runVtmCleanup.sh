#!/bin/bash
#
ROBOT_CMD="python -m robot.run"
ROBOT_HOME=`pwd`
ROBOT_PATH=${ROBOT_PATH}:${ROBOT_HOME}/robot/library
ROBOT_PATH=${ROBOT_PATH}:${ROBOT_HOME}/robot/locallibrary/cmsoUtils
VARIABLE_FILES="${VARIABLE_FILES} -V ${ROBOT_HOME}/robot/assets/test_properties.py" 

${ROBOT_CMD} ${OUTPUT} -P ${ROBOT_PATH} ${VARIABLE_FILES} ${VARIABLES} -s SchedulerRobot.Robot.Testsuites.VtmAccess -t "SchedulerRobot.Robot.Testsuites.VtmAccess.delete old scheduler tickets" ${ROBOT_HOME}

