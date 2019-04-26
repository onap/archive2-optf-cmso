*** Settings ***
Documentation	  Verifies queries

Library   StringTemplater
Library   UUID
Library   Collections

Resource    ../resources/test_templates/change_management_ete.robot

Suite Setup   Create Scheduler Request
Suite Teardown   Delete Scheduler Request
 
*** Variables ***
${QUERY_TEST_UUID}    ""

*** Test Cases ***
Change Management Query Simple   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}
   ${response}=   Get Change Management   alias   ${query}
   Row Count Should Be    ${response}   3

Change Management Query In   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}&vnfName=node1&vnfName=node3&WorkflowName=Replace
   ${response}=   Get Change Management   alias   ${query}
   Row Count Should Be    ${response}   2

Change Management Query Like   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}&vnfName=node%&request.createDateTime=2018-01-01T00:00:00Z
   ${response}=   Get Change Management   alias   ${query}
   Row Count Should Be    ${response}   3

Change Management Not Found   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}&vnfName=NoNode&request.createDateTime=2018-01-01T00:00:00Z,2018-01-01T01:00:00Z
   ${response}=   Get Change Management   alias   ${query}
   Should Be Equal As Strings    ${response.status_code}   404

Change Management Invalid Argument   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}&bad=NoNode
   ${response}=   Get Change Management   alias   ${query}
   Should Be Equal As Strings    ${response.status_code}   400

Change Management Invalid Date   
   [Tags]   ete   query
   ${query}=   Catenate   schedules/scheduleDetails?request.scheduleId=${QUERY_TEST_UUID}&request.createDateTime=2018-01-010
   ${response}=   Get Change Management   alias   ${query}
   Should Be Equal As Strings    ${response.status_code}   400

*** Keywords *** 
Row Count Should Be 
   [Arguments]   ${response}   ${count}
   Should Be Equal As Strings   ${response.status_code}   200       
   ${json}=   Set Variable   ${response.json()}
   Log    ${json}
   ${length}=   Get Length   ${json}
   Should be Equal as Integers   ${count}   ${length}
       
    
Create Scheduler Request
   [Documentation]   Creates a future request, runs query, deletes request 
   ${QUERY_TEST_UUID}=    Generate UUID
   Set Suite Variable   ${QUERY_TEST_UUID}   ${QUERY_TEST_UUID}
   ${request_file}=   Catenate   OneGroupMultipleVNFsOneChangeWindow.json.template
   ${workflow}=   Catenate   Replace  
   ${template_folder}=    Catenate   ${TEMPLATES}/changemanagement
   ${resp}=   Create Schedule   ${QUERY_TEST_UUID}   ${request_file}   ${template_folder}   workflow=${workflow}
   Should Be Equal as Strings    ${resp.status_code}   202
   Wait Until Keyword Succeeds    600s    30s    Wait For Pending Approval   ${QUERY_TEST_UUID}

Delete Scheduler Request
   [Documentation]   Creates a future request, runs query, deletes request 
   Delete Change Management   auth   schedules/${QUERY_TEST_UUID}
