.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Installation
=============================================

Installing from the Source Code
------------------------------------
Get CMSO seed code from the Linux Foundation Projects page

.. code-block:: bash

    $ git clone https://gerrit.onap.org/r/a/optf/cmso

There are 5 folders in the project that represent the CMSO services

 * cmso-service - Java Maven project (cmso-service/pom.xml)
 * cmso-database - Java Maven project (cmso-database/pom.xml) for managing database schema and migrations
 * cmso-optimizer - Java Maven project (cmso-optimizer/pom.xml)
 * cmso-topology - Java Maven project (cmso-topology/pom.xml)
 * cmso-ticketmgt - Java Maven project (cmso-ticketmgt/pom.xml)

There are 2 support folders

 * cmso-robot - Robot framework project for used for unit and functional testing. See the project README for setup instructions. This produces a docker container as well. 
 * cmso-sonar - This project executed the full robot test suites to generate the code coverage data for Sonar.  

Note that CMSO was developed using Robot framework as the primary unit testing vehicle so the cmso-sonar project was developed to generate the jacoco files for the 4 CMSO services to augment the limited Junit test cases. This same test suites are those executed in the CMSO CSIT job. Docker compose is used to create all of the required containers, including a Maria DB instance and  the cmso-robot docker container. 
