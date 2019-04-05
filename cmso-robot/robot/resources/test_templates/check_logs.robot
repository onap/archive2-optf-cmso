*** Settings ***
Documentation    Tests for checking Scheduler Logs

Library   UUID
Library   SSHLibrary
Library   String


Resource    ../files.robot
Resource    ../scheduler_requests/create_schedule.robot
Resource    ../scheduler_requests/approval_requests.robot
Resource    ../json_templater.robot
*** Variables ****
${log_location}=    /opt/app/scheduler/logs/
${debug_log_location}=    /opt/app/scheduler/debug-logs/
${date_time_regex}=    ((([0-9]{2,4}-?){3}.([0-9]{2}:?){3}.*))
${uuid_regex}=    [0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}

*** Keywords ***
#GENERAL Notes this
Check Audit Logs
    [Documentation]    This test runs Create, Get, and Delete Schedule then checks logs in the server for the result
    [Arguments]    ${user-id}    ${user-pass}    ${request_file}    ${template_folder}     
    ${uuid}=    Generate UUID
    ${resp}=    Create Schedule    ${uuid}   ${request_file}   ${template_folder}
    ${resp}=    Get Change Management    auth    schedules/${uuid}
    ${resp}=    Delete Change Management    auth   schedules/${uuid}
    ${server}=    Convert to String    mtanjv9sdlg10    #This should not be hardcoded. From test_properties.py replace this with some modification of GLOBAL_SCHEDULER_HOST  
    ${log_level}=    Convert to String    (INFO|WARN|ERROR|FATAL)
    ${log_msg}=    Convert to String    (Accepted|No Content|OK)
    ${status_codes}=    Convert to String    (204|202|200)
    ${audit_regex}=    Convert to String    \\|UNKNOWN\\|.*\\|scheduler\\|.*\\|COMPLETE\\|${status codes}\\|${log_msg}\\|${uuid_regex}\\|${log_level}\\|.*\\|[0-9]{1,}\\|${server}\\|.*
    #THis regex string follows the current expected audit.log structure logging guidelines as of 1710 here https://wiki.web.att.com/pages/viewpage.action?pageId=545861390
    Open Connection    ${GLOBAL_SCHEDULER_HOST}    port=22
    Login    ${user-id}    ${user-pass}    #This may only work with dev server should investigate using Pageant with Robot
    ${result}=    Grep Local File    -E '${date_time_regex}{2}\\|${uuid}${audit_regex}CREATE_SCHEDULE_REQUEST'    ${log_location}audit.log
    @{create_grep_result}=    Split to Lines    ${result}
    ${result}=    Grep Local File    -E '${date_time_regex}{2}\\|${uuid}${audit_regex}GET_SCHEDULE_REQUEST'    ${log_location}audit.log
    @{get_grep_result}=    Split to Lines    ${result}
    ${result}=    Grep Local File    -E '${date_time_regex}{2}\\|${uuid}${audit_regex}DELETE_SCHEDULE_REQUEST'    ${log_location}audit.log
    @{delete_grep_result}=    Split to Lines    ${result}
    Close Connection
    Log many    ${create_grep_result}    ${get_grep_result}    ${delete_grep_result}
    Should Contain    ${create_grep_result}[1]    Accepted    #This is only present in logs for create schedule
    Should Contain    ${get_grep_result}[1]    OK    #This is only present in logs for get schedule
    Should Contain    ${delete_grep_result}[1]    No Content    #This is only present in logs for delete
    
    
Check Debug Logs
    [Arguments]    ${user-id}    ${user-pass}    ${request_file}    ${template_folder}
    ${uuid}=    Generate UUID
    ${resp}=    Create Schedule    ${uuid}   ${request_file}   ${template_folder}
    ${resp}=    Get Change Management    auth    schedules/${uuid}
    ${resp}=    Delete Change Management    auth   schedules/${uuid}
    Open Connection    ${GLOBAL_SCHEDULER_HOST}    port=22
    Login    ${user-id}    ${user-pass}
    ${result}=    Grep Local File    -E '${date_time_regex}\\|${uuid}'    /opt/app/scheduler/debug-logs/debug.log
    #THis regex string follows the current expected debug.log structure logging guidelines as of 1710 here https://wiki.web.att.com/pages/viewpage.action?pageId=545861390
    Close Connection
    Should not be Empty    ${result}

Check Metric Logs
    [Arguments]    ${user-id}    ${user-pass}    ${request_file}    ${template_folder}    
    ${uuid}=    Generate UUID
    ${resp}=    Create Schedule    ${uuid}   ${request_file}   ${template_folder}
    ${resp}=    Get Change Management    auth    schedules/${uuid}
    ${resp}=    Delete Change Management    auth   schedules/${uuid}
    ${server}=    Convert to String    mtanjv9sdlg10
    ${log_level}=    Convert to String    (INFO|WARN|ERROR|FATAL)
    ${log_msg}=    Convert to String    (Accepted|No Content|OK|[a-zA-Z]+)
    ${status_codes}=    Convert to String    (204|202|200)
    ${regex}=    Convert To String    \\|UNKNOWN\\|.*\\|scheduler\\|.*\\|http://([a-zA-Z]*\.){2,}(:[0-9]{1,5})?\\|(.*/?){1,}\\|COMPLETE\\|${status codes}\\|${log_msg}\\|${uuid_regex}\\|${log_level}\\|.*\\|[0-9]{1,}\\|${server}\\|.*
    #THis regex string follows the current expected metric.log structure logging guidelines as of 1710 here https://wiki.web.att.com/pages/viewpage.action?pageId=545861390
    Open Connection    ${GLOBAL_SCHEDULER_HOST}    port=22
    Login    ${user-id}    ${user-pass}
    ${result}=    Grep Local File    '${date_time_regex}{2}\\|${uuid}${regex}'    ${log_location}metrics.log
    @{grep_result}=    Split to Lines    ${result}
    Close Connection
    Should Not be Empty    ${grep_result}
    Log    ${grep_result}
    
    
Check Error Logs
    [Arguments]    ${user-id}    ${user-pass}    ${request_file}    ${template_folder}    
    
    Open Connection    ${GLOBAL_SCHEDULER_HOST}    port=22
    Login    ${user-id}    ${user-pass}
    ${result}=    Grep Local File    '${date_time_regex}\\|${uuid_regex}\\|.*\\|scheduler\\|.*\\|.*(WARN|ERROR|FATAL).*\\|.*\\|'    ${log_location}error.log
    #THis regex string follows the current expected error.log structure logging guidelines as of 1710 here https://wiki.web.att.com/pages/viewpage.action?pageId=545861390
    #It is difficult to generate errors that would be logged in error.log. so this only tests that any error in the log matches the expected format
    @{grep_result}=    Split to Lines    ${result}
    Close Connection
    Should Not be Empty    ${grep_result}
    Log    ${grep_result}
    