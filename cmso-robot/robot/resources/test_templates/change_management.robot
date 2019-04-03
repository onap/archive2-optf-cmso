*** Settings ***
Documentation	  Creates VID VNF Instance
Library   StringTemplater
Library   String
Library   OperatingSystem
Library   UUID
Library   Collections
Library   DateTime
Resource    ../scheduler_common.robot
Resource    ../json_templater.robot
Resource    ../files.robot
Resource    ../scheduler_requests/create_schedule.robot
Resource    ../scheduler_requests/approval_requests.robot
*** Variables ****
#Variable can only be assigned String variables initially
${status_list}=
${expected_status_list_immediate}=    "Scheduled","Notifications Initiated"    
${expected_status_list}=    "Pending Schedule","Pending Approval","Scheduled","Notifications Initiated","Optimization in Progress"
*** Keywords ***
#GENERAL NOTES about Robot Framework
# Keywords are effectively equivalent to functions/methods
# ${} denotes a scalar variable @{} denotes a list variable and &{} denotes a Dictionary
# Only scalar variables should be passed to a function. Even if your function calls for a list it is easier to pass the list in as a scalar ${}
# To do this simply declare your non-scalar normally (@{}&{}), then when passing the non-scalar into a function pass it as a ${}
# 
Change Management Template
   [Arguments]    ${request_file}    ${expected_status_code}    ${template_folder}
   ${uuid}=   Generate UUID     
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}    #Sends the POST request to create schedule. Result stored in ${resp} resp is an object that comes from the RequestLibrary
   Wait Until Keyword Succeeds    120s    5s    Wait For Pending Approval   ${uuid}    #Runs Wait For Pending Approval every 5s for 120s or until it passes
   Should Be Equal as Strings    ${resp.status_code}    ${expected_status_code}    #This will fail if an unexpected result is received from Scheduler
   Send Tier2 Approval   ${uuid}   jf9860    Accepted      
   ${resp}=   Get Change Management   auth   schedules/${uuid}    #GETs the schedule from Scheduler. The result is stored in $resp
   Wait Until Keyword Succeeds    120s    5s    Wait For All VNFs Reach Status   Completed   ${uuid}    #Makes sure VNF(s) is(are) completed in the schedule
   Wait Until Keyword Succeeds    120s    5s    Wait for Schedule to Complete   Completed   ${uuid}    #Makes sure Schedule is marked completed
   ${reps}=   Delete Change Management   auth   schedules/${uuid}    #sends DELETE request to scheduler this may have to be changed for 1802 US to change Delete Schedule to Cancel Schedule

Change Management Rejection Template
   [Arguments]    ${request_file}    ${template_folder}
   ${uuid}=   Generate UUID
   ${expected_status_code}=    Convert to String    202    #Variables in keywords section have to be assigned keywords. So ${expected_status_code}= 202
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}    
   Wait Until Keyword Succeeds    120s    5s    Wait For Pending Approval   ${uuid}
   Should Be Equal as Strings    ${resp.status_code}    ${expected_status_code} 
   Send Tier2 Approval   ${uuid}   jf9860    Rejected    #Sends and checks Approval POST request

Change Management Failure Template
   [Documentation]    Sends a post request expecting a failure. expected_status_code should be whatever code is expected for this call
   [Arguments]    ${request_file}    ${expected_status_code}    ${template_folder}   ${variables}=[]
   ${uuid}=   Generate UUID
   ${resp}=   Create Schedule   ${uuid}   ${request_file}    ${template_folder}
   Should Be Equal as Strings    ${resp.status_code}    ${expected_status_code}
   Return from Keyword If   '${resp.status_code}' == '202'
   #List of possible reasons that the request should fail - we should look for exact message.....
   @{status_list}=    Create List    Scheduler.INVALID_ATTRIBUTE   Scheduler.MISSING_REQUIRED_ATTRIBUTE    Scheduler.NODE_LIST_CONTAINS_EMTPY_NODE    Scheduler.INVALID_CHANGE_WINDOW   
   @{listVars}=   Evaluate   ${variables}
   Validate Json Error    ${resp.json()}    ${status_list}   ${listVars}
   
Change Management Immediate Template
   [Arguments]    ${request_file}    ${expected_status_code}    ${template_folder}
   ${uuid}=   Generate UUID
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}    #Immediate schedules do not need approval so there is no wait for pending approval or send approval
   Should Be Equal as Strings    ${resp.status_code}    ${expected_status_code}
   Wait Until Keyword Succeeds    120s    5s    Wait For All VNFs Reach Status   Completed   ${uuid}
   Wait Until Keyword Succeeds    120s    5s    Wait for Schedule to Complete   Completed   ${uuid}
   ${reps}=   Delete Change Management   auth   schedules/${uuid}
Change Management Already Exists Immediate Template
   [Arguments]    ${request_file}    ${template_folder}
   ${uuid}=    Convert to String    46969fb7-0d4c-4e78-80ca-e20759628be5    #This value was taken from DEV env Scheduler DB. To actually automate may want to make SQL query Keyword to get an existing scheduleId
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}
   Should Be Equal as Strings    ${resp.status_code}    409
Record Status Immediate Template
   [Arguments]    ${request_file}    ${template_folder}
   ${uuid}=   Generate UUID
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}
   Set Global Variable    ${status_list}    ${EMPTY}       #$EMPTY is a empty string variable defined by ROBOT. This makes sure the global variable is cleared each run
   Wait Until Keyword Succeeds    120s    5s    Add to Status List    Completed    ${uuid}    
   ${reps}=   Delete Change Management   auth   schedules/${uuid}
   Compare Status List    ${expected_status_list_immediate}    ${status_list}
   Log    ${status_list}
Record Status Template
   [Documentation]     This test checks statuses for future schedules. It tends to fail due to scheduled status being missed.
   [Arguments]    ${request_file}    ${template_folder}
   ${uuid}=   Generate UUID 
   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}
   Set Global Variable    ${status_list}    ${EMPTY}
   Wait Until Keyword Succeeds    120s    5s    Add to Status List    Pending Approval   ${uuid} 
   Send Tier2 Approval   ${uuid}   jf9860    Accepted      
   ${resp}=   Get Change Management   auth   schedules/${uuid}
   Wait Until Keyword Succeeds    300s    5s    Wait For All VNFs Reach Status and Add to Status   Completed   ${uuid}
   ${reps}=   Delete Change Management   auth   schedules/${uuid}
   Compare Status List    ${expected_status_list}    ${status_list}
   Log    ${status_list}
#Check Status Template
#   [Arguments]    ${request_file}    ${template_folder}
#   ${uuid}=   Generate UUID
#   ${resp}=   Create Schedule   ${uuid}   ${request_file}   ${template_folder}
#   Check Schedule Status    Pending Schedule    ${uuid} 

Get Schedule Test Template
    [Documentation]    This function reads scheduleIds and the expected values from a text file (see robot/assets/get_schedule_UUIDs.txt for the format) then runs a get on them to confirm the GET schedule functionality     #this could be enhanced with SQL query to db
    [Arguments]    ${existing_uuid_file}    
    ${uuid_file}=    OperatingSystem.Get File    ${existing_uuid_file}    #this file works with the dev server as of 11/9/2017
    @{file_lines}=    Split to Lines    ${uuid_file}
    &{uuid_dictionary}=    Create Dictionary
    :For    ${line}    IN    @{file_lines}
    \    @{line_array}=    Split String    ${line}
    \    log    ${line_array[1]}
    \    Set To Dictionary    ${uuid_dictionary}	@{line_array}[0]    @{line_array}[1]    #You can pass singular list items as scalar variables
    \
    Log    ${uuid_dictionary}
    @{resp_list}=    Create List
    :For    ${uuid}    IN    @{uuid_dictionary.keys()}
    \    ${resp}=   Get Change Management   auth   schedules/${uuid}
    \    ${actual_status}=    Get from dictionary    ${uuid_dictionary}    ${uuid}
    \    Should be equal as Strings    ${actual_status}    ${resp.status_code}
    \    Run Keyword If    ${resp.status_code} == 200    Dictionary should contain key    ${resp.json()}    status    ELSE    Dictionary Should Contain Key    ${resp.json()['requestError']}    messageId    #${resp.json()['requestError']} this is a scalar reference to a singular item from a dictionary.
    \    Append to List    ${resp_list}    ${resp.json()}    
    Log    ${resp_list}
Wait For All VNFs Reach Status
    [Documentation]    Checks the status of the VNFs in a schedule.
    [Arguments]   ${status}   ${uuid}
    ${resp}=   Get Change Management   auth   schedules/scheduleDetails?request.scheduleId=${uuid}
    : for   ${vnf}   IN  @{resp.json()}
    \   Dictionary Should Contain Item   ${vnf}   status   Completed 
Wait For All VNFs Reach Status and Add to Status
    [Documentation]    This records the status of the vnf in the global status list 
    [Arguments]   ${status}   ${uuid}
    ${resp}=   Get Change Management   auth   schedules/scheduleDetails?request.scheduleId=${uuid}
    : for   ${vnf}   IN  @{resp.json()}
    \   Dictionary Should Contain Item   ${vnf}   status   Completed
    Add to Status List    Completed    ${uuid}     #This only runs if there are no failures in Dictionary should Contain Item for loop previously
Wait for Schedule to Complete
    [Documentation]    This is used in wait for keyword to succeed generally it checks if the status of the schedule returned by a GET request is Complete
    [Arguments]   ${status}   ${uuid}
    ${resp}=   Get Change Management   auth   schedules/${uuid}
    Dictionary Should Contain Item   ${resp.json()}   status   Completed 
#Check Schedule Status
#    [Arguments]   ${status}   ${uuid}
#    ${resp}=   Get Change Management   auth   schedules/${uuid}
#   Dictionary Should Contain Item   ${resp.json()}   status   ${status}
Add To Status List
    [Documentation]    Takes List and Schedule ID and changes global list of Statuses    #A global list was used because Wait for Keyword to Succeed only seems to return pass or fail
    [Arguments]    ${end_status}    ${uuid}
    ${resp}=   Get Change Management   auth   schedules/${uuid}
    ${json}=    Set Variable    ${resp.json()}
    ${status}=    Get From Dictionary    ${json}    status
    ${temp_list}=    Catenate    ${status_list}    ${status},
    ${temp_list}=    Replace String    ${temp_list}    ${SPACE}"    ${EMPTY}"
    Set Global Variable    ${status_list}    ${temp_list}
    Should Contain   ${status}    ${end_status}
Compare Status List
   [Arguments]    ${expected}    ${actual}
   @{expected_list}=    Split String    ${expected}    ,    
   @{actual_list}=    Split String    ${actual}    ,
   :For    ${current}    IN    @{expected_list}
   \    Should Contain    ${actual_list}    ${current}

Change Management DB Failover Template
   [Arguments]    ${request_file}    ${template_folder}    ${uuid1}   ${uuid2}
   Set Global Variable   ${NODES}   ${uuid1}
   Run Keyword If    '${uuid2}' != 'None'   Delete Change Management   auth   schedules/${uuid2}   #Sends and checks Approval POST request
   ${expected_status_code}=    Convert to String    202    #Variables in keywords section have to be assigned keywords. So ${expected_status_code}= 202
   ${resp}=   Create Schedule   ${uuid1}   ${request_file}   ${template_folder}    
   Wait Until Keyword Succeeds    120s    5s    Wait For Pending Approval    ${uuid1}   Optimization Failed
   Should Be Equal as Strings    ${resp.status_code}    ${expected_status_code} 
