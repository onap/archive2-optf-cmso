*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management.robot

# Test Setup            
Test Template         Change Management Rejection Template
# Test Teardown   
*** Variable***      
${status_code_variable}=    202
${template_folder}=    robot/assets/templates/changemanagement
*** Test Cases ***
Change Management Rejection    OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}   
   [Tags]   ete   rejection
    
