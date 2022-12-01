alter table submission add column ChromatographyType varchar(30) NOT NULL;
alter table submission add column isReference tinyint(1) NOT NULL default 0;
alter table submission add column isInHouseReference tinyint(1) NOT NULL default 0;

SET SQL_SAFE_UPDATES = 0;
update submission s
    Inner join file f on f.submissionId = s.id
    inner join Spectrum sp on f.id = sp.fileId
    set s.ChromatographyType  = sp.ChromatographyType;

update submission s
    Inner join file f on f.submissionId = s.id
    inner join Spectrum sp on f.id = sp.fileId
    set s.isReference  = sp.reference;

update submission s
    Inner join file f on f.submissionId = s.id
    inner join Spectrum sp on f.id = sp.fileId
    set s.isInHouseReference  = sp.inHouseReference;
SET SQL_SAFE_UPDATES = 1;