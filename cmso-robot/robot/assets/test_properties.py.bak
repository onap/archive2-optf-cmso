from os import listdir
from os.path import isfile, join

# Should be provided in Jenkins job

GLOBAL_SCHEDULER_HOST = "dummy-host"
GLOBAL_SCHEDULER_PORT = dummy-port
GLOBAL_SCHEDULER_PROTOCOL = "https"
GLOBAL_VID_USERID = "onap-user"
GLOBAL_SCHEDULER_PASSWORD = "pwd"

GLOBAL_SCHEDULER_USER = "dummy-user"
GLOBAL_APPLICATION_ID= "schedulertest"
GLOBAL_VTM_URL="http://localhost:25055"
GLOBAL_LISTCHANGE_PATH="/service=searchchangerecord.vtm.att.com/vtm/searchChangeRecord/v1/listChangeRecords/version=1.0.0/envContext=TEST/routeOffer=DEFAULT"


GLOBAL_VTM_PROTO="http"
GLOBAL_VTM_HOST="dummy-host"
GLOBAL_VTM_CLOSE_PORT=31127

GLOBAL_VTM_USER="dummy-user"
GLOBAL_VTM_PASSWORD="dummy-pwd"
GLOBAL_VID_CALLBACK_URL="http://localhost:8900/scheduler/v1/loopbacktest/vid"
cmFailurePath= "robot/assets/templates/FailureCasesChangeManagement"
GLOBAL_CM_FAILURE_TEMPLATES= [f for f in listdir(cmFailurePath) if isfile(join(cmFailurePath, f))]

OneVNFImmediateFailurePath="robot/assets/templates/OneVNFImmediateFailureCases"
GLOBAL_CM_ONEVNF_FAILURE_TEMPLATES=[f for f in listdir(OneVNFImmediateFailurePath) if isfile(join(OneVNFImmediateFailurePath, f))]

MultipleVNFImmediateFailurePath="robot/assets/templates/MutipleVNFImmediateFailureCases"
GLOBAL_CM_MULTIPLE_VNF_FAILURE_TEMPLATES=[f for f in listdir(MultipleVNFImmediateFailurePath) if isfile(join(MultipleVNFImmediateFailurePath, f))]


DELETE_TICKET_ENVS = [
    {"scheduler" : "dummy", "vtm" : "dummy"},
    {"scheduler" : "dummy", "vtm" : "dummy"},
]

NODES = "dummy,dummy,dummy,dummy";
 