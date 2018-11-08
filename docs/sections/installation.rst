.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Installation
=============================================

Installing from the Source Code
------------------------------------
Get CMSO seed code from the Linux Foundation Projects page

.. code-block:: bash

    $ git clone https://gerrit.onap.org/r/a/optf/cmso

There are 3 folders in the project

 * cmso-service - Java Maven project (cmso-service/pom.xml)
 * cmso-database - Java Maven project (cmso-database/pom.xml) for managing database schema and migrations

   * Schema for the CMSO MariaDB database is in cmso-database/src/main/resources/cmso-dbchangelog/onap-cmso-v1-schema.sql

 * cmso-robot - Rabot framework project for used for unit and functional testing. See the project README for setup instructions.

   * Note that CMSO was developed using Robot framework as the primary unit testing vehicle.
