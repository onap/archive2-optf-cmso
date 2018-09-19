*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management.robot

# Test Setup            
Test Template         Change Management DB Failover Template
# Test Teardown   
*** Variable***      
${status_code_variable}=    202
${template_folder}=    robot/assets/templates/changemanagement
*** Test Cases ***
Step 2     OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}    e2e006   None
        
Step 5     OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}    e2e002   e2e001    
    
Step 9     OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}    e2e003   e2e002    
    
Step 13     OneVnfOneChangeWindowReplaceVNFInfra.json.template    ${template_folder}    e2e004   e2e003    
    
 
    
