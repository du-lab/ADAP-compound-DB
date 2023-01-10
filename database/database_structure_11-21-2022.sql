SET FOREIGN_KEY_CHECKS=0;
CREATE TABLE `Adduct` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `NumMolecules` int NOT NULL,
  `Mass` double NOT NULL,
  `Charge` int NOT NULL,
  `Chromatography` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `DiversityIndex` (
  `ClusterId` bigint unsigned NOT NULL,
  `CategoryType` varchar(30) NOT NULL,
  `Diversity` double NOT NULL,
  PRIMARY KEY (`ClusterId`,`CategoryType`),
  CONSTRAINT `DiversityIndex_SpectrumCluster_Id_fk` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Feedback` (
  `Id` int unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `Email` text NOT NULL,
  `Affiliation` text NOT NULL,
  `Message` text NOT NULL,
  `SubmitDate` datetime DEFAULT NULL,
  `ReadFlag` tinyint NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=1320 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `File` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `FileType` varchar(30) NOT NULL,
  `Content` longblob NOT NULL,
  `SubmissionId` bigint unsigned NOT NULL,
  `Size` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `File_Submission_Id_fk_idx` (`SubmissionId`),
  CONSTRAINT `File_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=516 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Identifier` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint unsigned NOT NULL,
  `Type` varchar(30) NOT NULL,
  `Value` text NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Identifier_Spectrum_id_fk_idx` (`SpectrumId`),
  CONSTRAINT `Identifier_Spectrum_id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2069460 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Isotope` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint unsigned NOT NULL,
  `Index` int NOT NULL,
  `Intensity` double NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `SpectrumId` (`SpectrumId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Peak` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Mz` double NOT NULL,
  `Intensity` double NOT NULL,
  `SpectrumId` bigint unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Peak_SpectrumId_index` (`SpectrumId`),
  KEY `Peak_Mz_index` (`Mz`),
  KEY `Peak_SpectrumId_Mz_index` (`Mz`,`SpectrumId`),
  CONSTRAINT `Peak_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=182575584 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Spectrum` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `ExternalId` text,
  `Name` text,
  `Precursor` double DEFAULT NULL,
  `PrecursorType` text,
  `RetentionTime` double DEFAULT NULL,
  `RetentionIndex` double DEFAULT NULL,
  `Significance` double DEFAULT NULL,
  `ClusterId` bigint unsigned DEFAULT NULL,
  `Consensus` tinyint(1) NOT NULL DEFAULT '0',
  `Reference` tinyint(1) NOT NULL DEFAULT '0',
  `InHouseReference` tinyint(1) NOT NULL DEFAULT '0',
  `Clusterable` tinyint NOT NULL DEFAULT '0',
  `ChromatographyType` varchar(30) NOT NULL,
  `FileId` bigint unsigned DEFAULT NULL,
  `IntegerMz` tinyint(1) NOT NULL DEFAULT '0',
  `Mass` double DEFAULT NULL,
  `Formula` text,
  `CanonicalSMILES` text,
  `InChi` text,
  `InChiKey` text,
  `TopMz1` double DEFAULT NULL,
  `TopMz2` double DEFAULT NULL,
  `TopMz3` double DEFAULT NULL,
  `TopMz4` double DEFAULT NULL,
  `TopMz5` double DEFAULT NULL,
  `TopMz6` double DEFAULT NULL,
  `TopMz7` double DEFAULT NULL,
  `TopMz8` double DEFAULT NULL,
  `TopMz9` double DEFAULT NULL,
  `TopMz10` double DEFAULT NULL,
  `TopMz11` double DEFAULT NULL,
  `TopMz12` double DEFAULT NULL,
  `TopMz13` double DEFAULT NULL,
  `TopMz14` double DEFAULT NULL,
  `TopMz15` double DEFAULT NULL,
  `TopMz16` double DEFAULT NULL,
  `OmegaFactor` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `Spectrum_ClusterId_index` (`ClusterId`),
  KEY `Spectrum_Consensus_index` (`Consensus`),
  KEY `Spectrum_File_Id_fk_idx` (`FileId`),
  KEY `Spectrum_Mass_index` (`Mass`),
  KEY `Spectrum_CPR_index` (`ChromatographyType`,`Consensus`,`Reference`,`Clusterable`,`Precursor`,`RetentionTime`,`RetentionIndex`),
  CONSTRAINT `Spectrum_File_Id_fk` FOREIGN KEY (`FileId`) REFERENCES `File` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Spectrum_SpectrumCluster_Id_fk` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7148700 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `SpectrumCluster` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `ConsensusSpectrumId` bigint unsigned DEFAULT NULL,
  `Diameter` double NOT NULL,
  `Size` int NOT NULL,
  `AveSignificance` double DEFAULT NULL,
  `MinSignificance` double DEFAULT NULL,
  `MaxSignificance` double DEFAULT NULL,
  `AveDiversity` double DEFAULT NULL,
  `MinDiversity` double DEFAULT NULL,
  `MaxDiversity` double DEFAULT NULL,
  `MinPValue` double DEFAULT NULL,
  `DiseasePValue` double DEFAULT NULL,
  `SpeciesPValue` double DEFAULT NULL,
  `SampleSourcePValue` double DEFAULT NULL,
  `ChromatographyType` varchar(30) NOT NULL DEFAULT 'GAS',
  PRIMARY KEY (`Id`),
  UNIQUE KEY `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId`),
  CONSTRAINT `SpectrumCluster_Spectrum_Id_fk` FOREIGN KEY (`ConsensusSpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1324485 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `SpectrumMatch` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` bigint unsigned NOT NULL,
  `MatchSpectrumId` bigint unsigned NOT NULL,
  `Score` double DEFAULT NULL,
  `MassError` double DEFAULT NULL,
  `MassErrorPPM` double DEFAULT NULL,
  `RetTimeError` double DEFAULT NULL,
  `PrecursorError` double DEFAULT NULL,
  `PrecursorErrorPPM` double DEFAULT NULL,
  `RetIndexError` double DEFAULT NULL,
  `IsotopicSimilarity` double DEFAULT NULL,
  PRIMARY KEY (`Id`),
  KEY `SpectrumMatch_Spectrum_Id_fk_2` (`MatchSpectrumId`),
  KEY `SpectrumMatch_QuerySpectrumId_index` (`QuerySpectrumId`),
  KEY `SpectrumMatch_Score_index` (`Score`,`QuerySpectrumId`,`MatchSpectrumId`),
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk` FOREIGN KEY (`QuerySpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk_2` FOREIGN KEY (`MatchSpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=258776 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `SpectrumProperty` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint unsigned NOT NULL,
  `Name` varchar(60) NOT NULL,
  `Value` text,
  PRIMARY KEY (`Id`),
  KEY `SpectrumProperty_Name_index` (`Name`),
  KEY `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId`),
  CONSTRAINT `SpectrumProperty_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=80533380 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Submission` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `Description` text,
  `DateTime` timestamp NULL DEFAULT NULL,
  `UserPrincipalId` bigint unsigned NOT NULL,
  `Reference` text,
  `MassSpectrometryType` varchar(30) NOT NULL,
  `ExternalId` text,
  `IsPrivate` tinyint NOT NULL DEFAULT '0',
  `Raw` tinyint(1) NOT NULL DEFAULT '0',
  `Size` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `Submission_DateTime_Id_index` (`DateTime`,`Id`),
  KEY `Submission_UserPrincipalId_index` (`UserPrincipalId`),
  CONSTRAINT `Submission_UserPrincipal_Id_fk` FOREIGN KEY (`UserPrincipalId`) REFERENCES `UserPrincipal` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=482 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Submission2SubmissionCategory` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `SubmissionId` bigint unsigned NOT NULL,
  `SubmissionCategoryId` bigint unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Submission2SubmissionCategory_Submission_Id_fk_idx` (`SubmissionId`),
  KEY `Submission2SubmissionCategory_SubmissionCategory_Id_fk_idx` (`SubmissionCategoryId`),
  CONSTRAINT `Submission2SubmissionCategory_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Submission2SubmissionCategory_SubmissionCategory_Id_fk` FOREIGN KEY (`SubmissionCategoryId`) REFERENCES `SubmissionCategory` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `SubmissionCategory` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `Description` text,
  `CategoryType` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `SubmissionTag` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `SubmissionId` bigint unsigned NOT NULL,
  `TagKey` varchar(100) NOT NULL,
  `TagValue` text,
  PRIMARY KEY (`Id`),
  KEY `SubmissionTag_Submission_Id_fk_idx` (`SubmissionId`),
  CONSTRAINT `SubmissionTag_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2533 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Synonym` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint unsigned NOT NULL,
  `Name` text NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Synonym_Spectrum_Id_fk_idx` (`SpectrumId`),
  CONSTRAINT `Synonym_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=228674 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `TagDistribution` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `ClusterId` bigint unsigned DEFAULT NULL,
  `Label` varchar(256) NOT NULL,
  `Distribution` text NOT NULL,
  `PValue` double DEFAULT NULL,
  `MassSpectrometryType` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `ClusterId` (`ClusterId`),
  CONSTRAINT `TagDistribution_ibfk_1` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3333774 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `UserParameter` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `UserPrincipalId` bigint unsigned DEFAULT NULL,
  `Identifier` varchar(200) NOT NULL,
  `Value` text NOT NULL,
  `Type` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `UserParameter_UserPrincipalId_Identifier_index` (`UserPrincipalId`,`Identifier`),
  CONSTRAINT `UserParameter_UserPrincipal_Id_fk` FOREIGN KEY (`UserPrincipalId`) REFERENCES `UserPrincipal` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `UserPrincipal` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `Username` varchar(30) NOT NULL,
  `Email` varchar(30) NOT NULL,
  `HashedPassword` binary(60) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UserPrincipal_Username_uindex` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb3;

CREATE TABLE `UserRole` (
  `userPrincipalId` bigint unsigned NOT NULL,
  `roleName` varchar(15) NOT NULL,
  UNIQUE KEY `user_role_user_unique_idx` (`userPrincipalId`,`roleName`),
  KEY `user_role_user_principal_idx` (`userPrincipalId`),
  CONSTRAINT `user_role_principal_fk` FOREIGN KEY (`userPrincipalId`) REFERENCES `UserPrincipal` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
SET FOREIGN_KEY_CHECKS=1;
alter table userprincipal add column PeakCapacity INT default  15000000;
alter table submission add column Clusterable TINYINT(4) NOT NULL DEFAULT 0;
SET SQL_SAFE_UPDATES = 0;
update submission s
    Inner join file f on f.submissionId = s.id
    inner join Spectrum sp on f.id = sp.fileId
    set s.clusterable = sp.clusterable;
SET SQL_SAFE_UPDATES = 1;
CREATE TABLE `filecontent` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `content` longblob,
  `fileid` bigint unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `filecontent_file_Id_fk` (`fileid`),
  CONSTRAINT `filecontent_file_Id_fk` FOREIGN KEY (`fileid`) REFERENCES `file` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
