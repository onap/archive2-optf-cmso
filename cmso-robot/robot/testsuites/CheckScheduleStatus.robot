*** Settings ***
Documentation	  Verifies scheduler queries

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management.robot

# Test Setup          
# Test Teardown   
*** Variable***      
${template_folder}=    robot/assets/templates/changemanagement
*** Test Cases ***
Change Management Status Immediate    
    Record Status Immediate Template    OneVnfImmediateReplaceVNFInfra.json.template    ${template_folder}
    
Change Management Status
    Record Status Template    OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}