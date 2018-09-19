*** Settings ***
Documentation	  Creates VID VNF Instance

Library   StringTemplater
Library   UUID

Resource    ../resources/test_templates/change_management_ete.robot

# Test Setup            
Test Template         Change Management Template
# Test Teardown   

*** Test Cases ***
One VNF One Change Window   OneVnfOneChangeWindow.json.template   Replace   
   [Tags]   ete   future
       
Multiple VNFs One Change Window   OneGroupMultipleVNFsOneChangeWindow.json.template   Replace
   [Tags]   ete   future

Multiple VNFs Two Change Windows   OneGroupMultipleVNSsTwoChangeWindows.json.template   Replace
   [Tags]   ete   future

One VNF One Change Window Update   OneVnfOneChangeWindow.json.template   Update
   [Tags]   ete   future
