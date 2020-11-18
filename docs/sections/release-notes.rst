..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============

..      ===========================
..      * * *    GUILIN    * * *
..      ===========================

Abstract
========

This document provides the release notes for the Guilin release.

Summary
=======


Release Data
============


+--------------------------------------+--------------------------------------+
| **OOF Project**                      |                                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Docker images**                    | - optf-cmso-ticketmgt 2.3.0          |
|                                      | - optf-cmso-topology 2.3.0           |
|                                      | - optf-cmso-optimizer 2.3.0          |
|                                      | - optf-cmso-service 2.3.0            |
|                                      |                                      |
|                                      |                                      |
|                                      |                                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Release designation**              | 7.0.0 guilin                         |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Release date**                     | 2020-11-19 (TBD)                     |
|                                      |                                      |
+--------------------------------------+--------------------------------------+


New features
------------



Known Limitations, Issues and Workarounds
=========================================

System Limitations
------------------


Known Vulnerabilities
---------------------


Workarounds
-----------


Security Notes
--------------

**Fixed Security issues**

- OPTFRA-752 Upgrade Vulnerable Direct Dependencies
- OPTFRA-838 OOF has root pods
- OPTFRA-841 Remove checker-framework from CMSO logger

References
==========

For more information on the ONAP Guilin release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Quick Links:
    - `OOF project page <https://wiki.onap.org/display/DW/Optimization+Framework+Project>`_
    - `Passing Badge information for OOF <https://bestpractices.coreinfrastructure.org/en/projects/1720>`_

..      ===========================
..      * * *    FRANKFURT    * * *
..      ===========================

Abstract
========

This document provides the release notes for the Frankfurt release.

Summary
=======


Release Data
============


+--------------------------------------+--------------------------------------+
| **OOF Project**                      |                                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Docker images**                    | - optf-cmso-ticketmgt 2.2.0          |
|                                      | - optf-cmso-topology 2.2.0           |
|                                      | - optf-cmso-optimizer 2.2.0          |
|                                      | - optf-cmso-service 2.2.0            |
|                                      |                                      |
|                                      |                                      |
|                                      |                                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Release designation**              | 6.0.0 frankfurt                      |
|                                      |                                      |
+--------------------------------------+--------------------------------------+
| **Release date**                     | 2020-05-07 (TBD)                     |
|                                      |                                      |
+--------------------------------------+--------------------------------------+


New features
------------



Known Limitations, Issues and Workarounds
=========================================

System Limitations
------------------


Known Vulnerabilities
---------------------


Workarounds
-----------


Security Notes
--------------


References
==========

For more information on the ONAP Frankfurt release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Quick Links:
    - `OOF project page <https://wiki.onap.org/display/DW/Optimization+Framework+Project>`_
    - `Passing Badge information for OOF <https://bestpractices.coreinfrastructure.org/en/projects/1720>`_

..      ===========================
..      * * *    El Alto      * * *
..      ===========================

Version: 5.0.1
--------------

:Release Date: 2019-09-30 (El Alto Release)

Artifacts released:

optf-cmso:2.1.1

**New Features**

While no new features were added in the release, the following Stories were delivered as enhancements.

    * [OPTFRA-427] CMSO - Schedule a workflow in SO and track status to completion

* Platform Maturity Level 1
    * ~56.4+ unit test coverage

**Bug Fixes**

The El Alto release for CMSO fixed the following Bugs.

    * [OPTFRA-577] Need for "ReadWriteMany" access on storage when deploying on Kubernetes?
    * [OPTFRA-517] Clean up optf/cmso in integration/csit for Dublin
    * [OPTFRA-403] OOF CMSO Service kubernetes resources allocation is not done
    * [OPTFRA-526] OOF pods not running
    * [OPTFRA-593] OOF-CSMO healthcheck is failing in Master


**Known Issues**

    * [OPTFRA-596] CMSO - Sonar and CSIT jobs failing

**Security Notes**

*Fixed Security Issues*

    * [OPTFRA-455] CMSO - Mitigate License Threat tomcat-embed-core

*Known Security Issues*

    * [OPTFRA-481] Fix Vulnerability with spring-data-jpa package
    * [OPTFRA-431] Fix Vulnerability with spring-security-web package

*Known Vulnerabilities in Used Modules*

**Upgrade Notes**


**Deprecation Notes**


**Other**


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