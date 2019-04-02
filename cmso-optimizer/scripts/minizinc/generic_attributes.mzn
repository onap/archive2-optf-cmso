%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% element scheduling problem
%% Modified on Mar 08, 2019
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Required (core) parameters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Number of elements.
int: numElements;

% Number of loaders/engineers.
int: numLoaders;

% Index of last possible time-slot for scheduling These time slots can be
% minutes, hours, or nights. E.g., if scheduling happens at the granularity of
% nights, 1st night is counted as 1, 2nd night counted as 2, and so on.  OTOH,
% if we have 6 hours each night and we are scheduling at the granularity of
% hours, 1st hour of 1st night is 1, 1st hour of 2nd night is 7, and so on.
int: maxTime;

% TRUE, if i-th element does NOT have a conflict on j-th timeslot/night.
array[1..numElements, 1..maxTime] of bool: noConflict;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Optional parameters (defined via policy)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Each attribute is composed of 3 parameters that must be supplied. When an
% attribute is selected, all parameters related to it must be given.

%%%%%%%%%%%%%%%%%%%%
% Timeslots / nights capacities
%%%%%%%%%%%%%%%%%%%%

% Maximum number of elements that can be scheduled on j-th timeslot/night.
array[1..maxTime] of 0..numElements: elementSlotCapacity;

%%%%%%%%%%%%%%%%%%%%
% Loader capacities
%%%%%%%%%%%%%%%%%%%%

% Maximum number of elements that can be assigned to the j-th loader per
% timeslot/night.
array[1..numLoaders, 1..maxTime] of 0..numElements: loaderCapacity;

%%%%%%%%%%%%%%%%%%%%
% Attribute matrix
%%%%%%%%%%%%%%%%%%%%

% Number of attributes for wach node, e.g., hardware, software, oss, market,
% pool, etc.
int: numAttributes;

% Assume that i-th attribute has a range 0..attribute_range[i].
% Assume that 0 indicates NA. E.g, we may have pools and a node
% that does not belong to any pools  will have 'pool attribute = 0
% when we write constraints, we only consider attribute values >= 1.
array[1..numAttributes] of int: attributesRange;

% The attribute matrix that holds values of each attribute for a element.
array[1..numElements, 1..numAttributes] of int: attributes;

% Maximum number of nodes per time-slot that match on a given attribute.
array[1..numAttributes, 1..maxTime] of int: attributeConcurrencyLimit;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Variables
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% TRUE, if i-th element gets scheduled on j-th night.
array[1..numElements, 1..maxTime] of var bool: ELEMENT2TIMESLOT;

% TRUE, if i-th element gets scheduled to j-th loader.
array[1..numElements, 1..numLoaders] of var bool: ELEMENT2LOADER;

% Indicates the time slot (nigth) in which the elements were scheduled.
array[1..numElements] of var 0..maxTime: SCHEDULED_SLOT =
    [sum(j in 1..maxTime)(j * bool2int(ELEMENT2TIMESLOT[i,j])) | i in 1..numElements];

% Indicates the loader for which the elements were scheduled.
array[1..numElements] of var 0..numLoaders: SCHEDULED_LOADER =
    [sum(l in 1..numLoaders)(l * bool2int(ELEMENT2LOADER[i,l])) | i in 1..numElements];

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Constraints
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Required (core) constraints
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Schedule only on noConclict nights.
constraint
forall(i in 1..numElements, j in 1..maxTime)(
    ELEMENT2TIMESLOT[i,j] -> noConflict[i,j]
);

% Schedule a element to a loader
constraint
forall(i in 1..numElements)(
    sum(t in 1..maxTime)(bool2int(ELEMENT2TIMESLOT[i,t])) ==
    sum(l in 1..numLoaders)(bool2int(ELEMENT2LOADER[i,l]))
);

% Schedule each element exactly one timeslot/night (or none).
constraint
forall(i in 1..numElements)(
    sum(j in 1..maxTime)(bool2int(ELEMENT2TIMESLOT[i,j])) <= 1
);

% Schedule each element exactly one loader (or none).
constraint
forall(i in 1..numElements)(
    sum(j in 1..numLoaders)(bool2int(ELEMENT2LOADER[i,j])) <= 1
);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Optional constraints (defined via policy)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%These constraints are defined via policy and require sets of parameters also
%defined via policy. They must be both available.

%%%%%%%%%%%%%%%%%%%%
% General capacity constraints
%%%%%%%%%%%%%%%%%%%%

% Satisfy element timeslot/nightly capacity.
constraint
forall(j in 1..maxTime)(
    sum(i in 1..numElements)(bool2int(ELEMENT2TIMESLOT[i,j])) <= elementSlotCapacity[j]
);

% Satisfy loader/timeslot capacity.
constraint
forall(l in 1..numLoaders, t in 1..maxTime)(
    sum(i in 1..numElements)(
        bool2int(ELEMENT2LOADER[i,l] /\ ELEMENT2TIMESLOT[i,t])
    ) <= loaderCapacity[l,t]
);

%%%%%%%%%%%%%%%%%%%%
% Attribute capacity constraints
%%%%%%%%%%%%%%%%%%%%

% For attribute a and timeslot/night t, limits the number of elements having
% the same value for attribute k, scheduled in the same timeslot/night t.
constraint
forall(t in 1..maxTime, a in 1..numAttributes)(
    forall(m in 1..attributesRange[a])(
        sum(i in 1..numElements)(
            bool2int(attributes[i,a] == m /\ ELEMENT2TIMESLOT[i,t])
        ) <= attributeConcurrencyLimit[a,t]
    )
);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Objective function
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Computes the number of scheduled elements.
var int: NUM_SCHEDULED = sum(i in 1..numElements)(bool2int(SCHEDULED_SLOT[i] > 0));

% Computes the (average) completion time of all elements. Note that average is
% just a simple division by a constant, and it can be dropped from the model
% for robusteness.
var int: TOTAL_COMPLETION_TIME = sum(i in 1..numElements)(SCHEDULED_SLOT[i]);

% First, maximize the number of scheduled elements (using a heavy weight)
% and then minimize the (average) completion time for all elements.
solve maximize maxTime * numElements * NUM_SCHEDULED - TOTAL_COMPLETION_TIME;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Output
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

output
["results:"] ++
["\n  -"] ++
["\n    num_scheduled: " ++ show(NUM_SCHEDULED)] ++
["\n    total_completion_time: " ++ show(TOTAL_COMPLETION_TIME)] ++
["\n    element_slot_loader: |"] ++
[
 "\n      " ++ show(element) ++ "," ++ show(SCHEDULED_SLOT[element]) ++ "," ++
 show(SCHEDULED_LOADER[element])
| element in 1..numElements
]

%output
%["\n - num_scheduled: " ++ show(NUM_SCHEDULED)] ++
%["\n total_completion_time: " ++ show(TOTAL_COMPLETION_TIME)] ++
%["\n elementSlotLoader |"] ++
%[ "\n " ++ show(element) ++ "," ++
%show(SCHEDULED_SLOT[element]) ++ "," ++ show(SCHEDULED_LOADER[element]) | element in
%1..numElements ]
