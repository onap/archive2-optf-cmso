
CREATE DATABASE IF NOT EXISTS CMSO;
USE CMSO;

CREATE TABLE IF NOT EXISTS DOMAINS (
    domain VARCHAR(256) NOT NULL,
    CONSTRAINT PK_DOMAINS PRIMARY KEY (domain)
)  ENGINE=INNODB;

CREATE UNIQUE INDEX DOMAIN_UNIQUE ON DOMAINS (domain ASC);


-- -----------------------------------------------------
-- Table SCHEDULES
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS SCHEDULES (
    id INT NOT NULL AUTO_INCREMENT,
    domain VARCHAR(256) NOT NULL,
    schedule_id VARCHAR(256) NULL,
    schedule_name VARCHAR(256) NULL,
    user_id VARCHAR(45) NULL,
    status VARCHAR(45) NOT NULL,
    create_date_time BIGINT NULL,
    schedule_info MEDIUMTEXT NOT NULL,
    schedule MEDIUMTEXT NULL,
    optimizer_status VARCHAR(45) NULL,
    optimizer_message MEDIUMTEXT NULL,
    optimizer_date_time BIGINT NULL,
    optimizer_return_date_time BIGINT NULL,
    optimizer_attempts_to_schedule INT NOT NULL DEFAULT 0,
    optimizer_transaction_id VARCHAR(128) NULL,
    delete_date_time BIGINT NULL,
    CONSTRAINT PK_SCHEDULES PRIMARY KEY (id),
    CONSTRAINT FK_SCHEDULES_DOMAIN FOREIGN KEY (domain)
        REFERENCES DOMAINS (domain)
        ON DELETE NO ACTION ON UPDATE CASCADE
)  ENGINE=INNODB;
CREATE UNIQUE INDEX SCHEDULE_KEY ON SCHEDULES (domain ASC, schedule_id ASC);

CREATE UNIQUE INDEX OPTIMIZER_TRANSACTION_ID_UNIQUE ON SCHEDULES (optimizer_transaction_id ASC);


-- -----------------------------------------------------
-- Table SCHEDULE_EVENTS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS SCHEDULE_EVENTS (
    id INT(11) NOT NULL AUTO_INCREMENT,
    schedules_id INT(11) NOT NULL,
    event_time BIGINT(20) NOT NULL,
    reminder_time BIGINT(20) NOT NULL,
    domain VARCHAR(45) NULL DEFAULT NULL,
    event_text MEDIUMTEXT NULL DEFAULT NULL,
    status VARCHAR(45) NULL DEFAULT NULL,
    CONSTRAINT PK_SCHEDULE_EVENTS PRIMARY KEY (id),
    CONSTRAINT FK_SCHEDULE_EVENTS_SCHEDULES FOREIGN KEY (schedules_id)
        REFERENCES SCHEDULES (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE INDEX FK_SCHEDULE_EVENTS_SCHEDULES_IDX ON SCHEDULE_EVENTS (schedules_id ASC);

CREATE INDEX SEQ_SVENTS ON SCHEDULE_EVENTS (reminder_time ASC);



-- -----------------------------------------------------
-- Table DOMAIN_DATA
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS DOMAIN_DATA (
    id INT NOT NULL AUTO_INCREMENT,
    schedules_id INT NOT NULL,
    name VARCHAR(128) NULL,
    value MEDIUMTEXT NULL,
    CONSTRAINT PK_DOMAIN_DATA PRIMARY KEY (id),
    CONSTRAINT FK_DOMAIN_DATA_SCHEDULES FOREIGN KEY (schedules_id)
        REFERENCES SCHEDULES (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE UNIQUE INDEX ID_UNIQUE ON DOMAIN_DATA (id ASC);

CREATE INDEX FK_DOMAIN_DATA_SCHEDULES_IDX ON DOMAIN_DATA (schedules_id ASC);


-- -----------------------------------------------------
-- Table APPROVAL_TYPES
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS APPROVAL_TYPES (
    id INT NOT NULL AUTO_INCREMENT,
    domain VARCHAR(256) NOT NULL,
    approval_type VARCHAR(45) NOT NULL,
    approval_count INT NOT NULL DEFAULT 1,
    CONSTRAINT PK_APPROVAL_TYPES PRIMARY KEY (id),
    CONSTRAINT FK_APPROVAL_TYPES_DOMAIN FOREIGN KEY (domain)
        REFERENCES DOMAINS (domain)
        ON DELETE NO ACTION ON UPDATE CASCADE
)  ENGINE=INNODB; 
CREATE INDEX FK_DOMAIN_IDX ON APPROVAL_TYPES (domain ASC);


-- -----------------------------------------------------
-- Table SCHEDULE_APPROVALS
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS SCHEDULE_APPROVALS (
    id INT NOT NULL AUTO_INCREMENT,
    schedules_id INT NOT NULL,
    user_id VARCHAR(45) NOT NULL,
    approval_type_id INT NOT NULL,
    status VARCHAR(45) NOT NULL DEFAULT 'Pending Approval',
    approval_date_time BIGINT NULL,
    CONSTRAINT PK_SCHEDULE_APPROVALS PRIMARY KEY (id),
    CONSTRAINT FK_SCHEDULE_APPROVALS_SCHEDULES FOREIGN KEY (schedules_id)
        REFERENCES SCHEDULES (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_SCHEDULE_APPROVALS_APPROVAL_TYPES FOREIGN KEY (approval_type_id)
        REFERENCES APPROVAL_TYPES (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE INDEX FK_SCHEDULE_APPROVALS_SCHEDULES_IDX ON SCHEDULE_APPROVALS (schedules_id ASC);

CREATE INDEX FK_APPROVAL_TYPES_IDX ON SCHEDULE_APPROVALS (approval_type_id ASC);


-- -----------------------------------------------------
-- Table CHANGE_MANAGEMENT_GROUPS
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS CHANGE_MANAGEMENT_GROUPS (
    id INT NOT NULL AUTO_INCREMENT,
    group_id VARCHAR(45) NULL,
    schedules_id INT NULL,
    start_time BIGINT NULL,
    finish_time BIGINT NULL,
    last_instance_start_time BIGINT NULL,
    normal_duration_in_secs INT NULL,
    additional_duration_in_secs INT NULL,
    concurrency_limit INT NULL,
    policy_id VARCHAR(256) NULL,
    CONSTRAINT PK_CHANGE_MANAGEMENT_GROUPS PRIMARY KEY (id),
    CONSTRAINT FK_CHANGE_MANAGEMENT_GROUPS_SCHEDULES FOREIGN KEY (schedules_id)
        REFERENCES SCHEDULES (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE INDEX FK_SCHEDULES_IDX ON CHANGE_MANAGEMENT_GROUPS (schedules_id ASC);

CREATE UNIQUE INDEX CHANGE_MANAGEMENT_GROUP_ID_UNIQUE ON CHANGE_MANAGEMENT_GROUPS (schedules_id ASC, group_id ASC);


-- -----------------------------------------------------
-- Table CHANGE_MANAGEMENT_SCHEDULES
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS CHANGE_MANAGEMENT_SCHEDULES (
    id INT NOT NULL AUTO_INCREMENT,
    change_management_groups_id INT NULL,
    vnf_name VARCHAR(256) NOT NULL,
    vnf_id VARCHAR(256) NULL,
    status VARCHAR(45) NOT NULL,
    start_time BIGINT NULL,
    finish_time BIGINT NULL,
    mso_request_id VARCHAR(45) NULL,
    mso_status VARCHAR(45) NULL,
    mso_message MEDIUMTEXT NULL,
    mso_time BIGINT NULL,
    dispatcher_instance VARCHAR(128) NULL,
    dispatch_time BIGINT NULL,
    execution_completed_time BIGINT NULL,
    status_message MEDIUMTEXT NULL,
    tm_change_id VARCHAR(15) NULL,
    tm_approval_status VARCHAR(45) NULL,
    tm_status VARCHAR(45) NULL,
    CONSTRAINT PK_CHANGE_MANAGEMENT_SCHEDULES PRIMARY KEY (id),
    CONSTRAINT FK_CHANGE_MANAGEMENT_SCHEDULES_CHANGE_MANAGEMENT_GROUP FOREIGN KEY (change_management_groups_id)
        REFERENCES CHANGE_MANAGEMENT_GROUPS (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE INDEX FK_CHANGE_MANAGEMENT_GROUP_CHANGE_MANAGEMENT_SCHEDULES_IDX ON CHANGE_MANAGEMENT_SCHEDULES (change_management_groups_id ASC);

CREATE UNIQUE INDEX CHANGE_MANAGEMENT_SCHEDULES_VNF_NAME_UNIQUE ON CHANGE_MANAGEMENT_SCHEDULES (change_management_groups_id ASC, vnf_name ASC);


-- -----------------------------------------------------
-- Table CHANGE_MANAGEMENT_CHANGE_WINDOWS
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS CHANGE_MANAGEMENT_CHANGE_WINDOWS (
    id INT NOT NULL AUTO_INCREMENT,
    change_management_groups_id INT NOT NULL,
    start_time BIGINT NULL,
    finish_time BIGINT NULL,
    CONSTRAINT PK_CHANGE_MANAGEMENT_CHANGE_WINDOWS PRIMARY KEY (id),
    CONSTRAINT FK_CHANGE_WINDOWS_CHANGE_MANAGEMENT_GROUPS1 FOREIGN KEY (change_management_groups_id)
        REFERENCES CHANGE_MANAGEMENT_GROUPS (id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
)  ENGINE=INNODB;

CREATE INDEX FK_CHANGE_WINDOWS_CHANGE_MANAGEMENT_GROUPS1_IDX on CHANGE_MANAGEMENT_CHANGE_WINDOWS (change_management_groups_id ASC);




-- comment SQL_SAFE_UPDATES code in dev setup (testing)
-- uncomment in IT, IST, E2E Env's 
SET SQL_SAFE_UPDATES = 0;

DELETE FROM DOMAINS;

INSERT INTO DOMAINS (`domain`) VALUES ('ChangeManagement');


DELETE FROM APPROVAL_TYPES;

INSERT INTO APPROVAL_TYPES (`domain`, `approval_type`, `approval_count`) VALUES ('ChangeManagement', 'Tier 2', '1');