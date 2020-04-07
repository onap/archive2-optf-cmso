.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. _master_index:

Optimization Framework: Change Management Schedule Optimization
================================================================

OOF-CMSO is an optimizing service that allows for the scheduling of VNF change management
work flows to be executed at a time in the future. It enables a 3rd party client to provide 
SO work flow requests for multiple VNFs to be executed within a provided change window. The schedule 
optimizer is designed to determine a "conflict free" time within that change window that is suitable for 
submitting the changes to SO. 

The Dublin release provides a an schedule optimizer framework that provides an interface to a model driven schedule optimizer developed using MiniZinc technolgy to provide a best effort at a conflict free schedule.  Inputs to the schedule optimizer require network topology and and scheduled change information on relevant network elements in order to do conflict avoidance. To this end, a Change Management Topology and Ticket Management interfaces were designed to abstract the vendor specific topology and availability data required for schedule optimization. Dublin provides skeletal implementations of these services.

 * Dublin does not include an interface to SO for initiating the work flows and checking status. Rather, it has been suggested that a SO dispatcher service be provided to manage the runtime SO workload. While CMSO may take into account work scheduled for SO when creating a schedule. it is outside the domain of CMSO to manage the runtime actual workload on a target service such as SO. 
 * Dublin Topology and Ticket Management simulator services are skeletal interfaces. These services will be expanded in El Alto to provide data to support additional conflict avoidance test cases.  Currently, only sunny day test cases are implemented in the CSIT test suite. 
 
CMSO also models interfacing an external ticket/change management system to create, update, close/cancel tickets at relevant points in the CMSO flow. 

.. toctree::
   :maxdepth: 1

   ./sections/architecture.rst
   ./sections/offeredapis.rst
   ./sections/consumedapis.rst
   ./sections/delivery.rst
   ./sections/logging.rst
   ./sections/installation.rst
   ./sections/configuration.rst
   ./sections/administration.rst
   ./sections/humaninterfaces.rst
   ./sections/glossary.rst
   Example CMSO Messages <./sections/example.rst>
   ./sections/release-notes.rst

