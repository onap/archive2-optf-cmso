..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============


Version: 2.0.0
--------------

:Release Date: 2019-06-06 (Dublin Release)

**New Features**

   * Implement encryption for CMSO internal and external communication
   * CMSO to support change management schedule optimization
   * Design, Implement Ticket Management API
   * Design, Implement Topology API
   * Implement AAF Authentication

* Platform Maturity Level 1
    * ~56.4+ unit test coverage

The Dublin release for OOF delivered the following Epics.

    * [OPTFRA-426]	Track the changes to CMSO to support change management schedule optimization
    * [OPTFRA-424]	Extend OOF to support traffic distribution optimization
    * [OPTFRA-422]	Move OOF projects' CSIT to run on OOM
    * [OPTFRA-276]	Implementing a POC for 5G SON Optimization
    * [OPTFRA-270]	This epic captures stories related to maintaining current S3P levels of the project as new functional requirements are supported

**Bug Fixes**
    * [OPTFRA-500]	CMSO  - Update version to 2.0.0
    * [OPTFRA-484]	OOF-CMSO fails health check
    * [OPTFRA-480]	Fix tomcat-embed-core vulnerability
    * [OPTFRA-479]	Fix Vulnerability with commons-codec package
    * [OPTFRA-478]	Fix Vulnerability with spring-security-core package
    * [OPTFRA-474]	Update CMSO build to support Sonar Code Coverage
    * [OPTFRA-466]	CMSO Audit and update all source code for ONAP compliance
    * [OPTFRA-462]	CMSO - Upgrade robot from Python 2 to Python 3
    * [OPTFRA-458]	CMSO - Implement the CMSO Create Optimized Schedule API
    * [OPTFRA-457]	CMSO - Define CMSO Create Optimized Scheduler API
    * [OPTFRA-453]	CMSO - Mitigate sonatype-2017-0507 security vulnerability
    * [OPTFRA-451]	Create OOM based CSIT for CMSO
    * [OPTFRA-437]	CMSO - Define the API to be used to invoke the optimizer
    * [OPTFRA-436]	CMSO -Implement model driven optimizer to provide conflict-free schedules
    * [OPTFRA-433]	CMSO - Implement Ticket Management Simulator to support Ticket Management API for testing purposes
    * [OPTFRA-432]	CMSO - Define Ticket Management API
    * [OPTFRA-431]	Fix Vulnerability with spring-security-web package
    * [OPTFRA-430]	CMSO - Define API for requesting topology for an element
    * [OPTFRA-425]	Multiple Sonar Fixes
    * [OPTFRA-414]	AuthProvider.java - sonar fixes
    * [OPTFRA-413]	Junit for AuthProvider
    * [OPTFRA-403]	OOF CMSO Service kubernetes resources allocation is not done
    * [OPTFRA-397]	CMSO Update to Spring Boot 2.1.3-RELEASE
**Known Issues**

    * [OPTFRA-517]	Clean up optf/cmso in integration/csit for Dublin


**Security Issues**

    * [OPTFRA-481]	Fix Vulnerability with spring-data-jpa  package

**Upgrade Notes**
None. Initial release R3 Casablanca. No previous versions

**Deprecation Notes**
None. Initial release R3 Casablanca. No previous versions

**Other**
None

Quick Links:
    - `OPTFRA project page <https://wiki.onap.org/display/DW/Optimization+Framework+Project>`_
    - `Passing Badge information for OPTFRA <https://bestpractices.coreinfrastructure.org/en/projects/1720>`_
    - `Project Vulnerability Review Table for CMSO <https://wiki.onap.org/pages/viewpage.action?pageId=64005463>`_



Version: 1.0.1
--------------

:Release Date: 2018-11-30 (Casablanca)

**New Project**

**Known Issues**

    * [OPTFRA-386] - Integrate with SO

    * [OPTFRA-387] - Add conflict avoidance optimizaation to schedule creation


**Security Issues**

    * [OPTFRA-397] - Upgrade Spring Boot release

    * [OPTFRA-390] - Support AAF authentication/authorization

    * [OPTFRA-391] - Implement HTTPS on incoming requests



**Upgrade Notes**
None. Initial release R3 Casablanca. No previous versions

**Deprecation Notes**
None. Initial release R3 Casablanca. No previous versions

**Other**
None