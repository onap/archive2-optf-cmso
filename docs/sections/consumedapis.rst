.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Consumed APIs
=============================================
The following are the dependencies for CMSO project.


SO (TBD)
--------------------------------------------

CMSO is designed to invoke SO API to

 * Submit  change management work flow requests
 * Poll for status of submitted requests
 
While the CMSO schedule optimizer considers concurrency when creating  a scheduler, the actual SO workload at runtime remains outside the capabilities of CMSO. It has been proposed that CMSO interfaces to a dispatcher service which would manage the actual workload to SO at runtime.  


Schedule Optimizer with Conflict Avoidance
-------------------------------------------------------

CMSO in Dublin includes schedule optmizer that implements conflict avoidance. Services were added to CMSO in Dublin to support providing data to the scheduler optimizer which requires:

 * Network Topology
 
   * Vertical topology assets relevant the availability of the VNF(s) under consideration. These assets must be available in order to accomplish the change.

   * Horizontal topology assets. Horizontal topology represents assets that must be available in order to avoid a network outage during the change. As an example, the assets supporting the backup instance(s) of the VNF under change.
   
   * Dublin provides a CMSO Topology Service which implements an API desinged to provide CMSO optimizer the network topology information that is required for conflict avoidance. This service currently only returns the VNF itself, however, El Alto will simulate vertical topology for the VNFs to expand the scope of the CMSO CSIT test cases.

 * Availability of the VNFs and related network elements identified by the Toplogy service.
   
   * Determining the availability of related assests generally requires a change management
   tracking/ticketing system system that identifies scheduled changes (unavailaibility) to all assets that contribute to the
   functioning of the network.

   * Dublin provides a CMSO Ticket Management service to simulate a change management ticketing system within ONAP. This service currently returns empty results, however, El Alto will simulate change tickets for the network elements to expand the scope of the CMSO CSIT test cases.

CMSO Topology Service
-----------------------------------------

CMSO Ticket Management Service
-----------------------------------------


