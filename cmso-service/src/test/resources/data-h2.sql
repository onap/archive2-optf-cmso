
DELETE FROM DOMAINS;

INSERT INTO DOMAINS (`domain`) VALUES ('ChangeManagement');

DELETE FROM APPROVAL_TYPES;

INSERT INTO APPROVAL_TYPES (`domain`, `approval_type`, `approval_count`) VALUES ('ChangeManagement', 'Tier 2', '1');