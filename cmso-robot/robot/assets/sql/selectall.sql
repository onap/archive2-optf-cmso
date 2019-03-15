select DOMAINS.* from CMSO.DOMAINS;
select hex(uuid), hex(schedules_uuid), DOMAIN_DATA.* from CMSO.DOMAIN_DATA order by schedules_uuid;
select hex(uuid), hex(change_management_group_uuid), CHANGE_MANAGEMENT_SCHEDULES.* from CMSO.CHANGE_MANAGEMENT_SCHEDULES order by uuid desc;
select hex(uuid), hex(schedules_uuid), hex(change_management_group_uuid), CHANGE_MANAGEMENT_CHANGE_WINDOWS.* from CMSO.CHANGE_MANAGEMENT_CHANGE_WINDOWS;
select hex(uuid), hex(schedules_uuid), CHANGE_MANAGEMENT_GROUPS.* from CMSO.CHANGE_MANAGEMENT_GROUPS;
select hex(uuid), SCHEDULES.* from CMSO.SCHEDULES order by create_date_time desc;
select hex(uuid), APPROVAL_TYPES.* from CMSO.APPROVAL_TYPES;
select hex(uuid), hex(approval_types_uuid), hex(schedules_uuid), SCHEDULE_APPROVALS.* from CMSO.SCHEDULE_APPROVALS;
select hex(uuid), hex(change_management_schedules_uuid), ELEMENT_DATA.* from CMSO.ELEMENT_DATA;

