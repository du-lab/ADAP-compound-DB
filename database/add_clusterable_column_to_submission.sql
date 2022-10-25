
SET SQL_SAFE_UPDATES = 0;
update submission s
    Inner join file f on f.submissionId = s.id
    inner join Spectrum sp on f.id = sp.fileId
    set s.clusterable = sp.clusterable;
SET SQL_SAFE_UPDATES = 1;