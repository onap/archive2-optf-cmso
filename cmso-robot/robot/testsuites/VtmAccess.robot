*** Settings ***
Documentation	  Creates VID VNF Instance

Library         String
Library        OperatingSystem
Library 	      StringTemplater
Library        Collections
Library        XML

Resource    ../resources/scheduler_common.robot
Resource    ../resources/vtm_common.robot
*** Variables ****
${AOTS_USER}    ${GLOBAL_VID_USERID}
${ASSETSSTRING}       dummy-id

*** Test Cases ***
Test1
   ${display}   Create List    actualStartDate   actualEndDate   dateModified   assetId
   ${assets}=   Create List   dummy
   ${status}=   Create List   New   Assigned   Planning   Scheduled   Pending   WorkInProgress   Resolved  
   ${filter}=   Create Dictionary   plannedStartDateFrom=1505389844   assetId=${assets}   
   vTM Query Template   vtm   display=${display}   filter=${filter}   
  
Delete Tickets
   ${tickets}   Create List   dummy-id     
   :for   ${ticket}   in   @{tickets}
   \   vTM Close Ticket  vtm   ${ticket}   ${GLOBAL_VID_USERID}

Delete Old Tickets Atomic
   @{assets}=   Split String    ${ASSETS}      separator=,   
   ${end_time}=   Get Current Date  UTC   - 1440 minutes   result_format=timestamp    exclude_millis=False
   ${end_time}=   Convert Date  ${end_time}   epoch
   ${end_time}=   Evaluate   int(${end_time})
   ${display}   Create List    actualStartDate   actualEndDate   dateModified   assetId
   ${status}=   Create List   New   Assigned   Planning   Scheduled   Pending   WorkInProgress   Resolved  
   ${filter}=   Create Dictionary   plannedStartDateFrom=0   plannedEndDateTo=${end_time}   assetId=@{assets}   status=${status} 
   ${resp}=  vTM Query Template   vtm   display=${display}   filter=${filter}   
   Log   ${resp.json()} 
   ${list}=   Get From Dictionary    ${resp.json()}  changeInfo
   ${changeIds}=   Create Dictionary 
   :for   ${ticket}   in   @{list}
   \   ${changeId}=   Get From Dictionary    ${ticket}   changeId
   \   Set To Dictionary    ${changeIds}    ${changeId}=1
   # Weed out dupes if any
   ${idlist}   Get Dictionary Keys    ${changeIds}      
   :for   ${changeId}   in   @{idlist}
   \   vTM Close Ticket  vtm   ${changeId}   ${AOTS_USER}     

Delete Old Scheduler Tickets
   :for   ${env}   in    @{DELETE_TICKET_ENVS}
   \    Set Global Variable    ${GLOBAL_SCHEDULER_HOST}   ${env['scheduler']}    
   \    Set Global Variable    ${GLOBAL_VTM_HOST}   ${env['vtm']}    
   \    Delete Scheduler Tickets For ENV

    
Cancel Tickets
   ${tickets}   Create List   dummy-id   
   :for   ${ticket}   in   @{tickets}
   \   vTM Cancel Ticket  vtm   ${ticket}     


Delete Schedule
    ${uuid}=   Catenate   dummy-id
    ${resp}=   Delete Change Management   auth   schedules/${uuid}      


*** Keywords ***
Delete Scheduler Tickets For ENV
   # Make sure that the Scheduler in the config and the vTM system are the same.
   # We will match the tickets to be deleted to the tickets in the scheduler DB
   # to ensure we don't clobber other folks test data!
   ${scheduler_tickets}=   Get Scheduler Tickets
   # ELiminate dupes 
   @{assets}   Get Dictionary Values    ${scheduler_tickets}
   @{assets}=   Remove Duplicates   ${assets}
   ${end_time}=   Get Current Date  UTC   - 1440 minutes   result_format=timestamp    exclude_millis=False
   ${end_time}=   Convert Date  ${end_time}   epoch
   ${end_time}=   Evaluate   int(${end_time})
   ${display}   Create List    actualStartDate   actualEndDate   dateModified   assetId   requesterId
   ${status}=   Create List   New   Assigned   Planning   Scheduled   Pending   WorkInProgress   Resolved  
   ${filter}=   Create Dictionary   plannedStartDateFrom=0   plannedEndDateTo=${end_time}   assetId=@{assets}   status=${status} 
   ${resp}=  vTM Query Template   vtm   display=${display}   filter=${filter}   
   Log   ${resp.json()} 
   ${list}=   Get From Dictionary    ${resp.json()}  changeInfo
   
   ## Get list of tickets that are both in our DB and in AOTS
   ${changeIds}=   Create Dictionary 
   :for   ${ticket}   in   @{list}
   \   ${changeId}=   Get From Dictionary    ${ticket}   changeId
   \   ${status}   ${value}=   Run Keyword and Ignore Error   Get From Dictionary   ${scheduler_tickets}    ${changeId}
   \   Run Keyword If   '${status}'=='PASS'   Set To Dictionary    ${changeIds}    ${changeId}=1
   # Weed out dupes if any and only cance ones in our DB!
   ${idlist}   Get Dictionary Keys    ${changeIds}      
   :for   ${changeId}   in   @{idlist}
   \   vTM Cancel Ticket  vtm   ${changeId}

Get Scheduler Tickets 
    ${resp}=   Get Change Management   auth   schedules/scheduleDetails
    ${dict}=    Create Dictionary
    Log   ${resp.json()}
    :for    ${details}   in   @{resp.json()}
    \    ${status}   ${value}=   Run Keyword and Ignore Error   Get From Dictionary   ${details}   aotsChangeId
    \    ${assetId}=   Get From Dictionary   ${details}   vnfName
    \    Run Keyword If   '${status}'=='PASS'   Set To Dictionary   ${dict}   ${value}=${assetId}
    [Return]   ${dict}
