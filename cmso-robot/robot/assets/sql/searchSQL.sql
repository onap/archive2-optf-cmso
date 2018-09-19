select distinct 
s.vnf_name as vnFName,
s.vnf_id as vnfId,
s.status as status,
s.aots_change_id as aotsChangeId,
s.start_time as startTime,
s.finish_time as finishTime,
g.group_id as groupId,
g.last_instance_start_time as lastInstanceStartTime,
g.policy_id as policyId,
g.schedules_id as id,
ss.schedule_id as scheduleId,
dd.name
from change_management_schedules s 
inner join change_management_groups g on s.change_management_groups_id = g.id 
inner join schedules ss on g.schedules_id = ss.id
left outer join domain_data dd on ss.id = dd.schedules_id 
left outer join schedule_approvals sa on ss.id = sa.schedules_id 
inner join approval_types at on sa.approval_type_id = at.id
left outer join change_management_related_assets cmra on cmra.change_management_schedule_id = s.id
