*** Settings ***
Documentation	  Creates VID VNF Instance
Library   StringTemplater
Library   String
Library   OperatingSystem
Library   UUID
Library   Collections
Library   DateTime
Resource    ../optimizer_common.robot
Resource    ../json_templater.robot
Resource    ../files.robot
*** Variables ****


*** Keywords ***
Optimizer Validation Template
   [Arguments]    ${template_folder}   ${request_file}    ${expected_status_code}   ${expected_message}   ${variables}=[]    
   ${uuid}=   Generate UUID
   ${map}=   Create Dictionary   uuid=${uuid}   
   ${data}=   Fill JSON Template File    ${template_folder}/${request_file}   ${map}    
   ${resp}=   Post Optimizer   alias   optimize/schedule   ${data}
   @{listVars}=   Evaluate   ${variables}
   Validate JSON Error    ${resp.json()}   ${expected_Message}   ${listVars}
   
