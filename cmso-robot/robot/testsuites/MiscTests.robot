*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID
Library   Collections

Resource    ../resources/optimizer_common.robot
Resource    ../resources/scheduler_common.robot
Resource    ../resources/ticketmgt_common.robot
Resource    ../resources/topology_common.robot
Resource    ../resources/scheduler_requests/approval_requests.robot

# Test Setup
# Test Teardown   
*** Variable***
${user_name}=    
${password}=    
${uuid_list_file}=    robot/assets/get_schedule_UUIDs.txt
${template_folder}=    robot/assets/templates/changemanagement
*** Test Cases ***
Test CMSO Optimizer Admin
    [Tags]   ete   
    ${response}=    Get Optimizer Plain Text   alias    admin/password   
    Should Contain   ${response.text}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Service Admin
    [Tags]   ete 
    ${response}=    Get Scheduler Plain Text   alias    /cmso/v1/admin/password
    Should Contain   ${response.text}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Ticket Mgt Admin
    [Tags]   ete 
    ${response}=    Get Ticket Mgt Plain Text   alias    admin/password
    Should Contain   ${response.text}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Topology Admin
    [Tags]   ete 
    ${response}=    Get Topology Plain Text   alias    admin/password
    Should Contain   ${response.text}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Optimizer Health
    [Tags]   ete   
    ${response}=    Get Optimizer   alias    health   
    Dictionary Should Contain Item   ${response.json()}   healthy  True 
     
Test CMSO Service Health
    [Tags]   ete 
    ${response}=    Get Scheduler   alias    /cmso/v1/health
    Dictionary Should Contain Item   ${response.json()}   healthy  True 
       

Test CMSO Ticket Mgt Health
    [Tags]   ete 
    ${response}=    Get Ticket Mgt   alias    health
    Dictionary Should Contain Item   ${response.json()}   healthy  True 
    

Test CMSO Topology Health
    [Tags]   ete 
    ${response}=    Get Topology   alias    health
    Dictionary Should Contain Item   ${response.json()}   healthy  True 
    
Test CMSO Optimizer Policies
    [Tags]   ete   
    ${response}=    Get Optimizer   alias  policies   
    Should Be Equal As Strings   ${response.status_code}   200
    ##Should Contain   ${response.json()}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Optimizer Get Schedule
    [Tags]   ete   
    ${response}=    Get Optimizer   alias  optimize/schedule/id1   
    Should Be Equal As Strings   ${response.status_code}   200
    ##Should Contain   ${response.json()}   kECFDaLusYNHTN6Q4DmsYw==

Test CMSO Optimizer Delete Schedule
    [Tags]   ete   
    ${response}=    Delete Optimizer   alias  optimize/schedule/id1   
    Should Be Equal As Strings   ${response.status_code}   204
    ##Should Contain   ${response.json()}   kECFDaLusYNHTN6Q4DmsYw==


Test CMSO Ticket Mgt Get Tickets
    [Tags]   ete 
    ${response}=    Get Ticket Mgt   alias    tickets
    Should Be Equal As Strings   ${response.status_code}   200
    ##Dictionary Should Contain Item   ${response.json()}   healthy  True 

Test CMSO Ticket Mgt Get Ticket
    [Tags]   ete 
    ${response}=    Get Ticket Mgt   alias    ticket/none
    Should Be Equal As Strings   ${response.status_code}   200
    ##Dictionary Should Contain Item   ${response.json()}   healthy  True 

Get Not Found Schedule
    [Tags]   ete 
    ${response}=   Get Change Management   alias   schedules/doesNotExist
    Should Be Equal As Strings   ${response.status_code}   404

Delete Not Found Schedule
    [Tags]   ete 
    ${response}=   Delete Change Management   alias   schedules/doesNotExist
    Should Be Equal As Strings   ${response.status_code}   404

Approve Not Found Schedule
    [Tags]   ete 
    Send Tier2 Approval   DoesNotExist   jf9860    Accespted   status_code=400
 