*** Settings ***
Documentation	  Verifies ETE Scheduling for immediate requests

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management_ete.robot

# Test Setup            
Test Template         Change Management Immediate Template
# Test Teardown         

*** Test Cases ***
One Vnf Immediate Replace   OneVnfImmediate.json.template   Replace 
   [Tags]   ete   immediate

One Vnf Immediate Update Config    OneVnfImmediate.json.template   VNF Config Update 
   [Tags]   ete   immediate

One Vnf Immediate Update In Place    OneVnfImmediate.json.template   VNF Update Software In Place 
   [Tags]   ete   immediate

One Vnf Immediate Update    OneVnfImmediate.json.template   Update 
   [Tags]   ete   immediate

Multiple Vnf Immediate   MultipleVnfImmediate.json.template   Replace   
   [Tags]   ete   immediate
