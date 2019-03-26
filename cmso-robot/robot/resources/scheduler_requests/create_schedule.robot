*** Settings ***
Documentation	  SCheduler tests

Library   StringTemplater
Library   UUID
Library   String
Library   DateTime
Library   Collections 
Library   OperatingSystem 
Library   JSONUtils

Resource    ../scheduler_common.robot
Resource    ../json_templater.robot

*** Variables ****
${VID_TEMPLATES}    robot/assets/templates/changemanagement
${UTC}   %Y-%m-%dT%H:%M:%SZ

*** Keywords ***
Create Schedule
    [Arguments]   ${uuid}   ${request_file}    ${TEMPLATES}   ${workflow}=Unknown    ${minutesFromNow}=5
    ${testid}=   Catenate   ${uuid}
    ${testid}=   Get Substring   ${testid}   -4
    ${dict}=   Create Dictionary   serviceInstanceId=${uuid}   parent_service_model_name=${uuid}
	${map}=   Create Dictionary   uuid=${uuid}   callbackUrl=${GLOBAL_CALLBACK_URL}    testid=${testid}   workflow=${workflow}      userId=${GLOBAL_CALLBACK_USERID}
	${nodelist}=   Split String    ${NODES}   ,
	${nn}=    Catenate    1
    # Support up to 4 ChangeWindows
    : For   ${i}   IN RANGE   1    4    
    \  ${today}=    Evaluate   ((${i}-1)*1440)+${minutesFromNow}
    \  ${tomorrow}   Evaluate   ${today}+1440 
    \  ${last_time}   Evaluate  ${today}+30   
    \  ${start_time}=    Get Current Date   UTC  + ${today} minutes   result_format=${UTC}
    \  ${end_time}=    Get Current Date   UTC   + ${tomorrow} minutes   result_format=${UTC}
    \  Set To Dictionary    ${map}   start_time${i}=${start_time}   end_time${i}=${end_time}      

    ${requestList}=   Create List 
    
	: For   ${vnf}   IN    @{nodelist}
	\   Set To Dictionary    ${map}   node${nn}   ${vnf}   
	\   ${nn}=   Evaluate    ${nn}+1
	\   Set To DIctionary   ${dict}   vnfName=${vnf}      
    \   ${requestInfo}=   Fill JSON Template File    ${VID_TEMPLATES}/VidCallbackData.json.template   ${dict}
    \   Append To List   ${requestList}   ${requestInfo}


    ${callBackDataMap}=  Create Dictionary   requestType=Update   requestDetails=${requestList}
    
    ${callbackDataString}=   Json Escape    ${callbackDataMap}   

    Log    ${callbackDataString}
    Set To Dictionary   ${map}   callbackData=${callbackDataString}   

    ${data}=   Fill JSON Template File    ${TEMPLATES}/${request_file}   ${map}    
    ${resp}=   Post Change Management   auth   schedules/${uuid}   data=${data}
    [Return]   ${resp}
    
       
    