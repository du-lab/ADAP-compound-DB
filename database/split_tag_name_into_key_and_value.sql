SET SQL_SAFE_UPDATES = 0;
UPDATE SubmissionTag SET SubmissionTag.TagKey=TRIM(SUBSTRING(SubmissionTag.Name, 1, LOCATE(":",SubmissionTag.Name) - 1));
UPDATE SubmissionTag SET SubmissionTag.TagValue=TRIM(SUBSTRING(SubmissionTag.Name, LOCATE(":",SubmissionTag.Name) + 1));
SET SQL_SAFE_UPDATES = 1;