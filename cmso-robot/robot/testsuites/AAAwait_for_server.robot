*** Settings ***
Documentation	  Wait for service to be up before starting 

Library   StringTemplater
Library   UUID

Resource    ../resources/scheduler_common.robot


*** Test Cases ***
Wait For Healthy CMSO
    [Tags]   ete
    Wait Until Keyword Succeeds   240s   30s   CMSO Health Check
    
*** Keywords ***
CMSO Health Check
    ${resp}=   Get Change Management   alias   health
    Should Be Equal as Strings    ${resp.status_code}    200