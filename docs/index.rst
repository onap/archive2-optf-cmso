.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Optimization Framework: Change Management Schedule Optimization
================================================================

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

.. toctree::
   :maxdepth: 1

   ./sections/architecture.rst
   ./sections/offeredapis.rst
   ./sections/consumedapis.rst
   ./sections/logging.rst
   ./sections/installation.rst
   ./sections/configuration.rst
   ./sections/administration.rst
   ./sections/humaninterfaces.rst
   ./sections/glossary.rst
   Example CMSO Messages <./sections/example.rst>
   ./sections/release-notes.rst

