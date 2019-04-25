*** Settings ***
Documentation	  Verifies log output (Not part of ETE)

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/check_logs.robot

# Test Setup
# Test Teardown   
*** Variable***
${user_name}=   cr057g
${password}=   cr057g
${template_folder}=    robot/assets/templates/changemanagement
${fail_template_folder}=    robot/assets/templates/FailureCasesChangeManagement
*** Test Cases ***

Check Audit Logs
    Check Audit Logs    ${user_name}    ${password}    OneVnfImmediateReplaceVNFInfra.json.template    ${template_folder}

Check Debug Logs
    Check Debug Logs    ${user_name}    ${password}    OneVnfImmediateReplaceVNFInfra.json.template    ${template_folder}

Check Metric Logs
    Check Metric Logs    ${user_name}    ${password}    OneVnfImmediateReplaceVNFInfra.json.template    ${template_folder}

Check Error Logs
    Check Error Logs     ${user_name}    ${password}    OneVnfOneChangeWindowEmptyScheduleID.json.template    ${fail_template_folder}
    [Tags]    










