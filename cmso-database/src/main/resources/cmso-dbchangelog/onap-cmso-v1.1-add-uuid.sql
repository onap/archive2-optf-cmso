USE CMSO;

-- Schedules Add UUID column 
ALTER TABLE SCHEDULES ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Add foreign keys
ALTER TABLE DOMAIN_DATA ADD COLUMN schedules_uuid BINARY(16) NOT NULL; 
ALTER TABLE SCHEDULE_APPROVALS ADD COLUMN schedules_uuid BINARY(16) NOT NULL; 
ALTER TABLE CHANGE_MANAGEMENT_GROUPS ADD COLUMN schedules_uuid BINARY(16) NOT NULL; 
ALTER TABLE CHANGE_MANAGEMENT_CHANGE_WINDOWS ADD COLUMN schedules_uuid BINARY(16); 



-- CHANGE_MANAGEMENT_GROUPS Add UUID column 
ALTER TABLE CHANGE_MANAGEMENT_GROUPS ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys
ALTER TABLE CHANGE_MANAGEMENT_SCHEDULES ADD COLUMN change_management_group_uuid BINARY(16) NOT NULL; 
ALTER TABLE CHANGE_MANAGEMENT_CHANGE_WINDOWS ADD COLUMN change_management_group_uuid BINARY(16); 


-- CHANGE_MANAGEMENT_SCHEDULES Add UUID column 
ALTER TABLE CHANGE_MANAGEMENT_SCHEDULES ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys


-- DOMAIN_DATA Add UUID column 
ALTER TABLE DOMAIN_DATA ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys


-- SCHEDULE_APPROVALS Add UUID column 
ALTER TABLE SCHEDULE_APPROVALS ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys


--  CHANGE_MANAGEMENT_CHANGE_WINDOWS Add UUID column
--  Note that change window will be related to either a schedule or a group.
ALTER TABLE CHANGE_MANAGEMENT_CHANGE_WINDOWS ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys


--  APPROVAL_TYPES Add UUID column
ALTER TABLE APPROVAL_TYPES ADD COLUMN uuid BINARY(16) NOT NULL FIRST; 
-- Foreign keys
ALTER TABLE SCHEDULE_APPROVALS ADD COLUMN approval_types_uuid BINARY(16) NOT NULL; 


--  SCHEDULE_EVENTS not used
DROP TABLE IF EXISTS SCHEDULE_EVENTS; 

SET SQL_SAFE_UPDATES = 0;

-- ----------------------------------------------------
-- Populate UUID in all existing uuid fields
-- ----------------------------------------------------
UPDATE SCHEDULES SET uuid = unhex(replace(uuid(), '-', '')) where uuid is null; 
UPDATE CHANGE_MANAGEMENT_GROUPS SET uuid = unhex(replace(uuid(), '-', ''))  where uuid is null; 
UPDATE CHANGE_MANAGEMENT_SCHEDULES SET uuid = unhex(replace(uuid(), '-', ''))  where uuid is null; 
UPDATE DOMAIN_DATA SET uuid = unhex(replace(uuid(), '-', '')) where uuid is null; 
UPDATE SCHEDULE_APPROVALS SET uuid = unhex(replace(uuid(), '-', '')) where uuid is null; 
UPDATE CHANGE_MANAGEMENT_CHANGE_WINDOWS SET uuid = unhex(replace(uuid(), '-', '')) where uuid is null; 
UPDATE APPROVAL_TYPES SET uuid = unhex(replace(uuid(), '-', '')); 

-- ----------------------------------------------------
-- SCHEDULES Update all of the foreign key columns 
-- ----------------------------------------------------
update DOMAIN_DATA set schedules_uuid = 
	(select distinct s.uuid from SCHEDULES s where schedules_id = s.id);
update SCHEDULE_APPROVALS set schedules_uuid = 
	(select distinct s.uuid from SCHEDULES s where schedules_id = s.id);
update CHANGE_MANAGEMENT_GROUPS set schedules_uuid = 
	(select distinct s.uuid from SCHEDULES s where schedules_id = s.id);

-- ----------------------------------------------------
-- CHANGE_MANAGEMENT_GROUPS Update all of the foreign key columns 
-- ----------------------------------------------------
update CHANGE_MANAGEMENT_SCHEDULES set change_management_group_uuid = 
	(select distinct s.uuid from CHANGE_MANAGEMENT_GROUPS s where change_management_groups_id = s.id);
update CHANGE_MANAGEMENT_CHANGE_WINDOWS set change_management_group_uuid = 
	(select distinct s.uuid from CHANGE_MANAGEMENT_GROUPS s where change_management_groups_id = s.id);

-- ----------------------------------------------------
-- APPROVAL_TYPES Update all of the foreign key columns 
-- ----------------------------------------------------
update SCHEDULE_APPROVALS set approval_types_uuid = 
	(select distinct s.uuid from APPROVAL_TYPES s where approval_type_id = s.id);

-- ----------------------------------------------------
-- APPROVAL_TYPES Update all of the foreign key columns
-- ----------------------------------------------------
update SCHEDULE_APPROVALS set approval_types_uuid = 
	(select distinct s.uuid from SCHEDULE_APPROVALS t, APPROVAL_TYPES s where t.approval_type_id = s.id);

SET SQL_SAFE_UPDATES = 1;
