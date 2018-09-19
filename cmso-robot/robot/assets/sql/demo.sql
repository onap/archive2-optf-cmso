  SELECT DISTINCT
    s.schedule_id, s.status, s.schedule_info, s.schedule, g.start_time,
    d.vnf_name, d.status vnf_status, d.status_message, d.aots_change_id, d.aots_approval_status, d.mso_request_id, d.mso_status, d.aots_status, s.user_id, FROM_UNIXTIME(d.start_time/1000) as StartTime, FROM_UNIXTIME(s.create_date_time/1000) as CreateDateTime
FROM
    SCHEDULES s,
    CHANGE_MANAGEMENT_GROUPS g,
    CHANGE_MANAGEMENT_SCHEDULES d
WHERE
    g.schedules_id = s.id
	AND d.change_management_groups_id = g.id
    order by CreateDateTime desc;    