*** Settings ***
Documentation     The private interface for interacting with Openstack. It handles low level stuff like managing the authtoken and Openstack required fields

Library           Collections
Library 	      RequestsLibrary
Library	          UUID
Library           HTTPUtils
Library           String
Resource   misc.robot
*** Variables ***
*** Variables ***
${TICKET_MGT_PATH}    /ticketmgt/v1
#**************** Test Case Variables ******************

*** Keywords ***


Post Ticket Mgt
    [Documentation]    Runs a scheduler POST request
    [Arguments]    ${alias}    ${resource}   ${data}={}
    ${data_path}=   Catenate   ${TICKET_MGT_PATH}/${resource}
    ${url}=   Catenate   ${GLOBAL_TICKET_MGT_URL}
    ${uuid}=    Generate UUID
    ${proxies}=   Create Dictionary   no=pass
    ${session}=    Create Session 	${alias}   ${url}   
    ${auth_string}=   B64 Encode     ${GLOBAL_TICKET_MGT_USER}:${GLOBAL_TICKET_MGT_PASSWORD}
    ${headers}=  Create Dictionary   Accept=application/json    Content-Type=application/json    X-TransactionId=${GLOBAL_APPLICATION_ID}-${uuid}    X-FromAppId=${GLOBAL_APPLICATION_ID}   Authorization=Basic ${auth_string}
    ${resp}= 	Post Request 	${alias} 	${data_path}     headers=${headers}   data=${data}
    Log    Received response from scheduler ${resp.text}
    [Return]    ${resp}

Delete Ticket Mgt
    [Documentation]    Runs a scheduler DELETE request (this may need to be changed for 1802 US change Delete schedule to Cancel Schedule)
    [Arguments]    ${alias}    ${resource}
    ${data_path}=   Catenate   ${TICKET_MGT_PATH}/${resource}
    ${url}=   Catenate   ${GLOBAL_TICKET_MGT_URL}
    ${uuid}=    Generate UUID
    ${proxies}=   Create Dictionary   no=pass
    ${session}=    Create Session 	${alias}   ${url}     
    ${auth_string}=   B64 Encode    ${GLOBAL_TICKET_MGT_USER}:${GLOBAL_TICKET_MGT_PASSWORD}
    ${headers}=  Create Dictionary   Accept=application/json    Content-Type=application/json    X-TransactionId=${GLOBAL_APPLICATION_ID}-${uuid}    X-FromAppId=${GLOBAL_APPLICATION_ID}      Authorization=Basic ${auth_string}  
    ${resp}= 	Delete Request 	${alias} 	${data_path}     headers=${headers}
    Log    Received response from scheduler ${resp.text}
    [Return]   ${resp}   

Get Ticket Mgt
    [Documentation]    Runs a scheduler GET request
    [Arguments]    ${alias}    ${resource}  
    ${data_path}=   Catenate   ${TICKET_MGT_PATH}/${resource} 
    ${url}=   Catenate   ${GLOBAL_TICKET_MGT_URL}
    ${uuid}=    Generate UUID
    ${proxies}=   Create Dictionary   no=pass
    ${session}=    Create Session 	${alias}   ${url}     
    ${auth_string}=   B64 Encode    ${GLOBAL_TICKET_MGT_USER}:${GLOBAL_TICKET_MGT_PASSWORD}
    ${headers}=  Create Dictionary   Accept=application/json    Content-Type=application/json    X-TransactionId=${GLOBAL_APPLICATION_ID}-${uuid}    X-FromAppId=${GLOBAL_APPLICATION_ID}      Authorization=Basic ${auth_string}
    ${resp}= 	Get Request 	${alias} 	${data_path}     headers=${headers}
    Log    Received response from scheduler ${resp.json()}
    [Return]   ${resp}    

Get Ticket Mgt Plain Text
    [Documentation]    Runs a scheduler GET request
    [Arguments]    ${alias}    ${resource}  
    ${data_path}=   Catenate   ${TICKET_MGT_PATH}/${resource} 
    ${url}=   Catenate   ${GLOBAL_TICKET_MGT_URL}
    ${uuid}=    Generate UUID
    ${proxies}=   Create Dictionary   no=pass
    ${session}=    Create Session 	${alias}   ${url}     
    ${auth_string}=   B64 Encode    ${GLOBAL_TICKET_MGT_USER}:${GLOBAL_TICKET_MGT_PASSWORD}
    ${headers}=  Create Dictionary   Accept=text/plain    Content-Type=application/json    X-TransactionId=${GLOBAL_APPLICATION_ID}-${uuid}    X-FromAppId=${GLOBAL_APPLICATION_ID}      Authorization=Basic ${auth_string}
    ${resp}= 	Get Request 	${alias} 	${data_path}     headers=${headers}
    Log    Received response from scheduler ${resp.text}
    [Return]   ${resp}    
