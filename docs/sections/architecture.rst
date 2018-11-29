.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Architecture
=============================================

Introduction
------------------
OOF-CMSO is an optimizing service that allows for the scheduling of VNF change management
work flows to be executed at a time in the future. It enables a 3rd party client to provide
SO work flow requests for multiple VNFs to be executed within a provided change window. The schedule
optimizer is designed to determine a "conflict free" time within that change window that is suitable for
submitting the changes to SO.

The initial release provides a skeletal implementation that runs in "standalone" mode, that is, the
intended interfaces are stubbed out (i,e, "loop-back mode").

 * SO interface for dispatching the work flow and checking status
 * Optimizer Interface for determining the "conflict free" change window (loop-back mode selects the start of change window provided the client)

CMSO also models interfacing an external ticket/change management system to create, update, close/cancel tickets at relevant points in the CMSO flow.

CMSO in Change Management Flow
--------------------------------------------
CMSO is designed to be agnostic of the type of change management work flow that is to be scheduled in SO. A 3rd party
client application will be responsible for preparing the change management request messages to be forwarded to SO. This data,
along with the list of targeted VNFs and the scheduling requirements are used by CMSO to create and ultimately execute
the schedule to dispathc the work to SO.

The information provided to CMSO to accomplish the scheduling of the changes:

 * Work flow information

   * Name of the work flow
   * Message(s) to be forwarded to SO to initiate the work flow

 * Schedule information

   * The list of targeted VNFs
   * The desired change window

     * Earliest start date/time
     * Latest end date/time

   * Expected duration of the work flow execution
   * Number of concurrent work flows to be scheduled

The design of CMSO is to ensure that the scheduling of the work flows will not conflict with other scheduled work.

 #. Ensure that asset(s) required to execute the work flow are available so that the work flow will be able to complete successfully
 #. Ensure that the execution of the work flow does not cause a network outage.

Architectural Flow Diagram
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. image:: ./diagrams/ONAP_CMSO_FLOW.png

Scheduling Optimization and Confict Avoidance
-----------------------------------------------

The Casablanca implementation of CMSO does not attempt any conflict avoidance. It will assume that no
conflicts exist and creates a achedule based upon the earliest start time, expected duration of each work flow,
the number of concurrent workflows to be executed and the number of VNFs. The optimized schedule
provides a start time for each VNF in the schedule.

Conflict avoidance to achieve the goals of CMSO, successful completion of change requests  without incurring network outages,
requires a system to track the availability (or rather unavailability) of assets required to determine an
optimal time for exectution. No such system exists at this time within ONAP. CMSO itself can be used to track changes to VNFs and
the initial optimization to be included in Dublin will be limited to ensuring that a VNF is not double booked within CMSO.

SO Change Request Dispatching
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

CMSO does not serve as a throttling dispatcher to SO. Rather, the dispatching of work to SO is based solely on
the start time assigned to each VNF. CMSO will dispatch a VNF change to SO regardless of how many outstanding
change management requests there are to SO within CMSO.

CMSO will expect that SO will throttle its own workload and reject requests that arrive when the system is busy.
CMSO will not interpret these system busy rejections as "try again later" as the changes are assumed to be
time sensitive based upon the conflict avoidance objectives of CMSO.




