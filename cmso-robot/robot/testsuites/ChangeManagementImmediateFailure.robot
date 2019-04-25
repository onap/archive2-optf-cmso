*** Settings ***
Documentation	  Verifies immediate request failures

Library   StringTemplater
Library   UUID
Library   OperatingSystem
Resource    ../resources/test_templates/change_management.robot

# Test Setup            
Test Template         Change Management Failure Template
# Test Teardown         
*** Variable ***
${status_code_variable}=    400
${one_vnf_template_folder}=    robot/assets/templates/OneVNFImmediateFailureCases
${multiple_vnf_template_folder}=    robot/assets/templates/MutipleVNFImmediateFailureCases
*** Test Cases ***
#@{Global_cm...} may show as an error but it still seems to work I guess
#One VNF Immediate Failure    
#    :FOR    ${current_template_file}    IN     @{GLOBAL_CM_ONEVNF_FAILURE_TEMPLATES}
#    \    ${current_template_file}    ${status_code_variable}    ${one_vnf_template_folder}

#Multiple VNF Immediate Failure    
#    :FOR    ${current_template_file}    IN     @{GLOBAL_CM_MULTIPLE_VNF_FAILURE_TEMPLATES}
#    \    ${current_template_file}    ${status_code_variable}    ${multiple_vnf_template_folder}
    
One Vnf Immediate Empty Domain    OneVnfImmediateEmptyDomain.json.template    ${status_code_variable}    ${one_vnf_template_folder}
   [Tags]   ete   failure   immediate   single VNF

One Vnf Immediate Empty Schedule Id    OneVnfImmediateEmptyScheduleId.json.template    ${status_code_variable}    ${one_vnf_template_folder}
   [Tags]   ete   failure   immediate   single VNF

##  schedule name is now optional
One Vnf Immediate Empty Schedule Name    OneVnfImmediateEmptyScheduleName.json.template    202    ${one_vnf_template_folder}
   [Tags]   ete   failure   immediate   single VNF

One Vnf Immediate Empty Scheduling Info    OneVnfImmediateEmptySchedulingInfo.json.template    ${status_code_variable}    ${one_vnf_template_folder}
   [Tags]   ete   failure   immediate   single VNF

One Vnf Immediate Empty User Id    OneVnfImmediateEmptyUserId.json.template    ${status_code_variable}    ${one_vnf_template_folder}
   [Tags]   ete   failure   immediate   single VNF

Multiple Vnf Immediate Empty Domain    MultipleVnfImmediateEmptyDomain.json.template    ${status_code_variable}    ${multiple_vnf_template_folder}
   [Tags]   ete   failure   immediate   multiple VNF

Multiple Vnf Immediate Empty Schedule Id    MultipleVnfImmediateEmptyScheduleId.json.template    ${status_code_variable}    ${multiple_vnf_template_folder}
   [Tags]   ete   failure   immediate   multiple VNF

Multiple Vnf Immediate Empty Additional Duration    MultipleVnfImmediateEmptyAdditionalDuration.json.template    ${status_code_variable}    ${multiple_vnf_template_folder}
   [Tags]   ete   failure   immediate   multiple VNF

Multiple Vnf Immediate Empty User Id    MultipleVnfImmediateEmptyUserId.json.template    ${status_code_variable}    ${multiple_vnf_template_folder}
   [Tags]   ete   failure   immediate   multiple VNF