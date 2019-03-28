*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID
Library   OperatingSystem
Resource    ../resources/test_templates/optimizer.robot

# Test Setup            
Test Template         Optimizer Validation Template
# Test Teardown         
*** Variable ***
${status_code_variable}=    400
${template_folder}=    robot/assets/templates/OptimizerValidation
*** Test Cases ***
    
Missing RequestId    ${template_folder}   MissingRequestId.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["requestId"]
   [Tags]   opt_validation

Missing ConcurrencyLimit    ${template_folder}   MissingConcurrencyLimit.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["concurrencyLimit"]
   [Tags]   opt_validation

Missing NormalDuration    ${template_folder}   MissingNormalDuration.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["normalDuration"]
   [Tags]   opt_validation

Missing ChangeWindow    ${template_folder}   MissingChangeWindow.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["changeWindows"]
   [Tags]   opt_validation

Empty ChangeWindows    ${template_folder}   EmptyChangeWindows.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["changeWindows"]
   [Tags]   opt_validation

Missing Elements    ${template_folder}   MissingElements.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["elements"]
   [Tags]   opt_validation

Empty Elements    ${template_folder}   EmptyElements.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["elements"]
   [Tags]   opt_validation

Missing ElementId    ${template_folder}   MissingElementId.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["elementId"]
   [Tags]   opt_validation

Missing StartTime    ${template_folder}   MissingStartTime.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["startTime"]
   [Tags]   opt_validation

Missing EndTime    ${template_folder}   MissingEndTime.json    400    CMSO.MISSING_REQUIRED_ATTRIBUTE   ["endTime"]
   [Tags]   opt_validation

Invalid Change Window    ${template_folder}   InvalidChangeWindow.json    400    CMSO.INVALID_CHANGE_WINDOW 
   [Tags]   opt_validation
