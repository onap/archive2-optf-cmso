*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management.robot
Resource    ../resources/test_templates/check_logs.robot

# Test Setup
# Test Teardown   
*** Variable***
${user_name}=    
${password}=    
${uuid_list_file}=    robot/assets/get_schedule_UUIDs.txt
${template_folder}=    robot/assets/templates/changemanagement
*** Test Cases ***
Get Schedule   
    Get Schedule Test Template    ${uuid_list_file}

Post Existing Immediate Schedule
    Change Management Already Exists Immediate Template    OneVnfImmediateReplaceVNFInfra.json.template    ${template_folder}

