select DOMAINS.* from CMSO.DOMAINS;
select hex(uuid), hex(schedules_uuid), DOMAIN_DATA.* from CMSO.DOMAIN_DATA order by schedules_uuid;
select hex(uuid), CHANGE_MANAGEMENT_SCHEDULES.* from CMSO.CHANGE_MANAGEMENT_SCHEDULES order by id desc;
select hex(uuid), DOMAINS.* from CMSO.CHANGE_MANAGEMENT_CHANGE_WINDOWS;
select hex(uuid), DOMAINS.* from CMSO.CHANGE_MANAGEMENT_GROUPS;
select hex(uuid), DOMAINS.* from CMSO.SCHEDULES order by id desc;
select hex(uuid), DOMAINS.* from CMSO.APPROVAL_TYPES;
select hex(uuid), DOMAINS.* from CMSO.SCHEDULE_APPROVALS;
