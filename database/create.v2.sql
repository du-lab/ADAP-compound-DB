create schema adapcompounddb collate utf8_general_ci;

create table SubmissionCategory
(
	Id bigint unsigned auto_increment
		primary key,
	Name text not null,
	Description text null,
	CategoryType varchar(30) not null
)
charset=latin1;

create table UserPrincipal
(
	Id bigint unsigned auto_increment
		primary key,
	Username varchar(30) not null,
	Email varchar(30) not null,
	HashedPassword binary(60) not null,
	constraint UserPrincipal_Username_uindex
		unique (Username)
);

create table Submission
(
	Id bigint unsigned auto_increment
		primary key,
	Name text not null,
	Description text null,
	DateTime timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
	UserPrincipalId bigint unsigned not null,
	Reference text null,
	constraint Submission_UserPrincipal_Id_fk
		foreign key (UserPrincipalId) references UserPrincipal (Id)
			on update cascade on delete cascade
);

create table File
(
	Id bigint unsigned auto_increment
		primary key,
	Name text not null,
	FileType varchar(30) not null,
	Content longblob not null,
	SubmissionId bigint unsigned not null,
	constraint File_Submission_Id_fk
		foreign key (SubmissionId) references Submission (Id)
			on update cascade on delete cascade
)
charset=latin1;

create index File_Submission_Id_fk_idx
	on File (SubmissionId);

create table Spectrum
(
	Id bigint unsigned auto_increment
		primary key,
	Name text null,
	Precursor double null,
	RetentionTime double null,
	Significance double null,
	ClusterId bigint unsigned null,
	Consensus tinyint(1) default '0' not null,
	Reference tinyint(1) default '0' not null,
	ChromatographyType varchar(30) not null,
	FileId bigint unsigned null,
	constraint Spectrum_File_Id_fk
		foreign key (FileId) references File (Id)
			on update cascade on delete cascade
);

create table Peak
(
	Id bigint unsigned auto_increment
		primary key,
	Mz double not null,
	Intensity double not null,
	SpectrumId bigint unsigned not null,
	constraint Peak_Spectrum_Id_fk
		foreign key (SpectrumId) references Spectrum (Id)
			on update cascade on delete cascade
);

create index Peak_Mz_index
	on Peak (Mz);

create index Peak_SpectrumId_Mz_Index
	on Peak (SpectrumId, Mz);

create index Peak_SpectrumId_index
	on Peak (SpectrumId);

create index Spectrum_CPR_index
	on Spectrum (ChromatographyType, Precursor, RetentionTime);

create index Spectrum_ClusterId_index
	on Spectrum (ClusterId);

create index Spectrum_Consensus_index
	on Spectrum (Consensus);

create index Spectrum_File_Id_fk_idx
	on Spectrum (FileId);

create table SpectrumCluster
(
	Id bigint unsigned auto_increment
		primary key,
	ConsensusSpectrumId bigint unsigned null,
	Diameter double not null,
	Size int not null,
	AveSignificance double null,
	MinSignificance double null,
	MaxSignificance double null,
	constraint SpectrumCluster_ConsensusSpectrumId_uindex
		unique (ConsensusSpectrumId),
	constraint SpectrumCluster_Spectrum_Id_fk
		foreign key (ConsensusSpectrumId) references Spectrum (Id)
			on update cascade on delete cascade
)
charset=latin1;

create table DiversityIndex
(
	ClusterId bigint unsigned not null,
	CategoryType varchar(30) charset utf8 not null,
	Diversity double not null,
	primary key (ClusterId, CategoryType),
	constraint DiversityIndex_SpectrumCluster_Id_fk
		foreign key (ClusterId) references SpectrumCluster (Id)
			on delete cascade
)
charset=latin1;

alter table Spectrum
	add constraint Spectrum_SpectrumCluster_Id_fk
		foreign key (ClusterId) references SpectrumCluster (Id)
			on update cascade on delete cascade;

create table SpectrumMatch
(
	Id bigint unsigned auto_increment
		primary key,
	QuerySpectrumId bigint unsigned not null,
	MatchSpectrumId bigint unsigned not null,
	Score double not null,
	constraint SpectrumMatch_Spectrum_Id_fk
		foreign key (QuerySpectrumId) references Spectrum (Id)
			on update cascade on delete cascade,
	constraint SpectrumMatch_Spectrum_Id_fk_2
		foreign key (MatchSpectrumId) references Spectrum (Id)
			on update cascade on delete cascade
)
charset=latin1;

create index SpectrumMatch_QuerySpectrumId_index
	on SpectrumMatch (QuerySpectrumId);

create table SpectrumProperty
(
	Id bigint unsigned auto_increment
		primary key,
	SpectrumId bigint unsigned not null,
	Name varchar(60) not null,
	Value text null,
	constraint SpectrumProperty_Spectrum_Id_fk
		foreign key (SpectrumId) references Spectrum (Id)
			on update cascade on delete cascade
);

create index SpectrumProperty_Name_index
	on SpectrumProperty (Name);

create index Submission_DateTime_Id_index
	on Submission (DateTime, Id);

create index Submission_UserPrincipalId_index
	on Submission (UserPrincipalId);

create table Submission2SubmissionCategory
(
	Id bigint unsigned auto_increment
		primary key,
	SubmissionId bigint unsigned not null,
	SubmissionCategoryId bigint unsigned not null,
	constraint Submission2SubmissionCategory_SubmissionCategory_Id_fk
		foreign key (SubmissionCategoryId) references SubmissionCategory (Id)
			on update cascade on delete cascade,
	constraint Submission2SubmissionCategory_Submission_Id_fk
		foreign key (SubmissionId) references Submission (Id)
			on update cascade on delete cascade
)
charset=latin1;

create index Submission2SubmissionCategory_SubmissionCategory_Id_fk_idx
	on Submission2SubmissionCategory (SubmissionCategoryId);

create index Submission2SubmissionCategory_Submission_Id_fk_idx
	on Submission2SubmissionCategory (SubmissionId);

create table SubmissionTag
(
	SubmissionId bigint unsigned not null,
	Name varchar(100) not null,
	primary key (SubmissionId, Name),
	constraint SubmissionTag_Submission_Id_fk
		foreign key (SubmissionId) references Submission (Id)
			on update cascade on delete cascade
);

create index SubmissionTag_Submission_Id_fk_idx
	on SubmissionTag (SubmissionId);

create table UserParameter
(
	Id bigint unsigned auto_increment
		primary key,
	UserPrincipalId bigint unsigned null,
	Identifier varchar(200) not null,
	Value text not null,
	Type varchar(30) not null,
	constraint UserParameter_UserPrincipal_Id_fk
		foreign key (UserPrincipalId) references UserPrincipal (Id)
			on update cascade on delete cascade
)
charset=latin1;

create index UserParameter_UserPrincipalId_Identifier_index
	on UserParameter (UserPrincipalId, Identifier);

create table UserRole
(
	userPrincipalId bigint unsigned not null,
	roleName varchar(15) not null,
	constraint user_role_user_unique_idx
		unique (userPrincipalId, roleName),
	constraint user_role_principal_fk
		foreign key (userPrincipalId) references userprincipal (Id)
)
charset=latin1;

create index user_role_user_principal_idx
	on UserRole (userPrincipalId);

create view clusterpage as select `sc`.`Id`                                                                              AS `id`,
         coalesce(sum((case when (`d`.`CategoryType` = 'SOURCE') then `d`.`Diversity` end)))    AS `source`,
         coalesce(sum((case when (`d`.`CategoryType` = 'SPECIMEN') then `d`.`Diversity` end)))  AS `specimen`,
         coalesce(sum((case when (`d`.`CategoryType` = 'TREATMENT') then `d`.`Diversity` end))) AS `treatment`
  from (`adapcompounddb`.`spectrumcluster` `sc` left join `adapcompounddb`.`diversityindex` `d` on ((`sc`.`Id` =
                                                                                                     `d`.`ClusterId`)))
  group by `sc`.`ConsensusSpectrumId`;

create view clusterspectrumpeakview as select `adapcompounddb`.`peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`spectrum`.`ChromatographyType` AS `ChromatographyType`
  from (`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`)
  where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and
         (`adapcompounddb`.`spectrum`.`Consensus` is false) and (`adapcompounddb`.`spectrum`.`Reference` is false));

create view searchspectrumpeakview as select `adapcompounddb`.`peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`spectrum`.`ChromatographyType` AS `ChromatographyType`
  from (`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`)
  where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and
         ((`adapcompounddb`.`spectrum`.`Consensus` is true) or (`adapcompounddb`.`spectrum`.`Reference` is true)));

create view searchspectrumpeakviewv2 as select `adapcompounddb`.`peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`spectrum`.`ChromatographyType` AS `ChromatographyType`,
         `adapcompounddb`.`submission`.`Id`               AS `SubmissionId`,
         `adapcompounddb`.`submissiontag`.`Name`          AS `SubmissionTagName`
  from ((((`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum` on ((`adapcompounddb`.`peak`.`SpectrumId` =
                                                                         `adapcompounddb`.`spectrum`.`Id`))) left join `adapcompounddb`.`file` on ((
    `adapcompounddb`.`spectrum`.`FileId` = `adapcompounddb`.`file`.`Id`))) left join `adapcompounddb`.`submission` on ((
    `adapcompounddb`.`file`.`SubmissionId` =
    `adapcompounddb`.`submission`.`Id`))) left join `adapcompounddb`.`submissiontag` on ((
    `adapcompounddb`.`submission`.`Id` = `adapcompounddb`.`submissiontag`.`SubmissionId`)))
  where ((`adapcompounddb`.`spectrum`.`Consensus` is true) or (`adapcompounddb`.`spectrum`.`Reference` is true));


