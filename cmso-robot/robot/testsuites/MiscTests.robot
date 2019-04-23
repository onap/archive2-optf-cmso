*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID
Library   Collections

Resource    ../resources/optimizer_common.robot
Resource    ../resources/scheduler_common.robot
Resource    ../resources/ticketmgt_common.robot
Resource    ../resources/topology_common.robot

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
    
