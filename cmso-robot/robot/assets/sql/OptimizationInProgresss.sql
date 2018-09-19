SET SQL_SAFE_UPDATES = 0;
update schedules set status = 'Pending Schedule' where status = 'Optimization in Progress';
SET SQL_SAFE_UPDATES = 1;