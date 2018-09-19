*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID
Library   OperatingSystem
Resource    ../resources/test_templates/change_management.robot

# Test Setup            
Test Template         Change Management Failure Template
# Test Teardown         
*** Variable ***
${status_code_variable}=    400
${template_folder}=    robot/assets/templates/FailureCasesChangeManagement
*** Test Cases ***
# This for loop will generate a test for each file. It has been replaced by GenerateRobot.py which generates something similar to the below cases
# This was done because all tests run by the for loop counted as a single test and could not be tagged individually
#@{Global_cm...} may show as an error but it still seems to work I guess
#One VNF One Change Window ete   failure
#    :FOR    ${current_template_file}    IN     @{GLOBAL_CM_ete   failure_TEMPLATES}
#    \    ${current_template_file}    ${status_code_variable}    ${template_folder}
    
One Vnf One Change Window Empty Domain    OneVnfOneChangeWindowEmptyDomain.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window Empty Schedule ID    OneVnfOneChangeWindowEmptyScheduleID.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

##   Schedule name is now optional
One Vnf One Change Window Empty Schedule Name    OneVnfOneChangeWindowEmptyScheduleName.json.template    202    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window Empty User ID    OneVnfOneChangeWindowEmptyUserID.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

## Policies are validated by SNIRO 
One Vnf One Change Window Incorrect Policy Id    OneVnfOneChangeWindowIncorrectPolicyId.json.template    202    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

## 1806 allow for unknown workflows
One Vnf One Change Window Incorrect Workflow    OneVnfOneChangeWindowIncorrectWorkflow.json.template    202    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window Negative Normal Duration In Seconds    OneVnfOneChangeWindowNegativeNormalDurationInSeconds.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window No End Time    OneVnfOneChangeWindowNoEndTime.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window No Node Name    OneVnfOneChangeWindowNoNodeName.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window No Start Time    OneVnfOneChangeWindowNoStartTime.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window

One Vnf One Change Window Switched Time    OneVnfOneChangeWindowSwitchedTime.json.template    ${status_code_variable}    ${template_folder}
   [Tags]   ete   failure   future   single VNF    single window
