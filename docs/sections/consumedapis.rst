.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Consumed APIs
=============================================
The following are the dependencies for CMSO project.


SO (Dublin)
--------------------------------------------

CMSO is designed to invoke SO API to

 * Submit  change management work flow requests
 * Poll for status of submitted requests


Schedule Optimizer with Conflict Avoidance (Dublin)
-------------------------------------------------------

There is currently no schedule optmizer that implements conflict avoidance.
Conflict avoidance requires:

 * Vertical topology assets relevant the availability of the VNF(s) under consideration.

   * Within ONAP topology information is available in A&AI

 * Horizontal topology assets ???

 * Availability of the VNFs and the availability of the assets identified in the previous items.
   This generally requires a change management
   tracking/ticketing system system that identifies scheduled changes to all assets that contribute to the
   functioning of the network.

   * There is no change management ticketing system within ONAP. CMSO itself may serve as such in a very limited capacity as it
     tracks scheduled changes to VNFs. It does not track changes the all network assets which is necessarilty required for full
     conflict avoidance. For ONAP Dublin, the conflict avoidance will necessarily be limited to VNF level conflict
     checking using CMSO as the source of asset avaialability/unavailability.


Change Management Ticketing System (TBD)
-----------------------------------------


