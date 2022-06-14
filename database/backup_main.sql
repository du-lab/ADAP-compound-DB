-- MySQL dump 10.13  Distrib 8.0.26, for macos11 (x86_64)
--
-- Host: 18.221.34.82    Database: adapcompounddb
-- ------------------------------------------------------
-- Server version	8.0.27-0ubuntu0.20.04.1


DROP TABLE IF EXISTS `Adduct`;

CREATE TABLE `Adduct` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `NumMolecules` int NOT NULL,
  `Mass` double NOT NULL,
  `Charge` int NOT NULL,
  `Chromatography` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`)
)  AUTO_INCREMENT=36;


--
-- Table structure for table `DiversityIndex`
--

DROP TABLE IF EXISTS `DiversityIndex`;

CREATE TABLE `DiversityIndex` (
  `ClusterId` bigint  not null ,
  `CategoryType` varchar(30) NOT NULL,
  `Diversity` double NOT NULL,
  PRIMARY KEY (`ClusterId`,`CategoryType`)
 -- -- Constraint`DiversityIndex_SpectrumCluster_Id_fk` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE CASCADE
) ;



DROP TABLE IF EXISTS `File`;

CREATE TABLE `File` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `FileType` varchar(30) NOT NULL,
  `Content` longtext NOT NULL,
  `SubmissionId` bigint  NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `File_Submission_Id_fk_idx` (`SubmissionId`)
-- -- Constraint`File_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=391 ;

DROP TABLE IF EXISTS `Feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Feedback` (
  `Id` int unsigned NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `Email` text NOT NULL,
  `Affiliation` text NOT NULL,
  `Message` text NOT NULL,
  `SubmitDate` datetime DEFAULT NULL,
  `ReadFlag` tinyint NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=1311 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;


DROP TABLE IF EXISTS `Identifier`;

CREATE TABLE `Identifier` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint, 
  `Type` varchar(30) NOT NULL,
  `Value` text NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Identifier_Spectrum_id_fk_idx` (`SpectrumId`)
-- -- Constraint`Identifier_Spectrum_id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=2054750;


DROP TABLE IF EXISTS `Isotope`;
CREATE TABLE `Isotope` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint  NOT NULL,
  `Index` int NOT NULL,
  `Intensity` double NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `SpectrumId` (`SpectrumId`)
) ;

DROP TABLE IF EXISTS `Peak`;

CREATE TABLE `Peak` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `Mz` double NOT NULL,
  `Intensity` double NOT NULL,
  `SpectrumId` bigint  NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Peak_SpectrumId_index` (`SpectrumId`),
  KEY `Peak_Mz_index` (`Mz`),
  KEY `Peak_SpectrumId_Mz_index` (`Mz`,`SpectrumId`)
  -- -- Constraint`Peak_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=127438124 ;


DROP TABLE IF EXISTS `Spectrum`;
CREATE TABLE `Spectrum` (
  `Id` bigint  AUTO_INCREMENT,
  `ExternalId` text,
  `Name` text,
  `Precursor` double DEFAULT NULL,
  `PrecursorType` text,
  `RetentionTime` double DEFAULT NULL,
  `RetentionIndex` double DEFAULT NULL,
  `Significance` double DEFAULT NULL,
  `ClusterId` bigint  DEFAULT NULL,
  `Consensus` tinyint(1) NOT NULL DEFAULT '0',
  `Reference` tinyint(1) NOT NULL DEFAULT '0',
  `InHouseReference` tinyint(1) NOT NULL DEFAULT '0',
  `Clusterable` tinyint NOT NULL DEFAULT '0',
  `ChromatographyType` varchar(30) NOT NULL,
  `FileId` bigint  not null,
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
  PRIMARY KEY (`Id`),
  KEY `Spectrum_ClusterId_index` (`ClusterId`),
  KEY `Spectrum_Consensus_index` (`Consensus`),
 
  KEY `Spectrum_Mass_index` (`Mass`),
  KEY `Spectrum_CPR_index` (`ChromatographyType`,`Consensus`,`Reference`,`Clusterable`,`Precursor`,`RetentionTime`,`RetentionIndex`),
-- FOREIGN KEY (FileId) REFERENCES File(Id) ON DELETE CASCADE ON UPDATE CASCADE,
 -- FOREIGN KEY (ClusterId) REFERENCES SpectrumCluster(Id) ON DELETE SET NULL
)  AUTO_INCREMENT=5575669;

DROP TABLE IF EXISTS `SpectrumCluster`;

CREATE TABLE `SpectrumCluster` (
  `Id` bigint AUTO_INCREMENT,
  `ConsensusSpectrumId` bigint  DEFAULT NULL,
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
  UNIQUE KEY `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId`)
  -- Constraint`SpectrumCluster_Spectrum_Id_fk` FOREIGN KEY (`ConsensusSpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=1324485;


DROP TABLE IF EXISTS `SpectrumMatch`;

CREATE TABLE `SpectrumMatch` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` bigint  NOT NULL,
  `MatchSpectrumId` bigint  NOT NULL,
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
  KEY `SpectrumMatch_Score_index` (`Score`,`QuerySpectrumId`,`MatchSpectrumId`)
  -- Constraint`SpectrumMatch_Spectrum_Id_fk` FOREIGN KEY (`QuerySpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  -- Constraint`SpectrumMatch_Spectrum_Id_fk_2` FOREIGN KEY (`MatchSpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=258776 ;


DROP TABLE IF EXISTS `SpectrumProperty`;

CREATE TABLE `SpectrumProperty` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint  NOT NULL,
  `Name` varchar(60) NOT NULL,
  `Value` text,
  PRIMARY KEY (`Id`),
  KEY `SpectrumProperty_Name_index` (`Name`),
  KEY `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId`)
-- Constraint`SpectrumProperty_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=53109608;


DROP TABLE IF EXISTS `Submission`;

CREATE TABLE `Submission` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `Name` text NOT NULL,
  `Description` text,
  `DateTime` timestamp NULL DEFAULT NULL,
  `UserPrincipalId` bigint ,
  `Reference` text,
  `MassSpectrometryType` varchar(30) NOT NULL,
  `ExternalId` text,
  `IsPrivate` tinyint NOT NULL DEFAULT '0',
  `Raw` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  KEY `Submission_DateTime_Id_index` (`DateTime`,`Id`),
  KEY `Submission_UserPrincipalId_index` (`UserPrincipalId`)
  -- Constraint`Submission_UserPrincipal_Id_fk` FOREIGN KEY (`UserPrincipalId`) REFERENCES `UserPrincipal` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=382 ;


DROP TABLE IF EXISTS `Submission2SubmissionCategory`;

CREATE TABLE `Submission2SubmissionCategory` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `SubmissionId` bigint  NOT NULL,
  `SubmissionCategoryId` bigint ,
  PRIMARY KEY (`Id`),
  KEY `Submission2SubmissionCategory_Submission_Id_fk_idx` (`SubmissionId`),
  KEY `Submission2SubmissionCategory_SubmissionCategory_Id_fk_idx` (`SubmissionCategoryId`) 
  -- Constraint`Submission2SubmissionCategory_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE,
  -- Constraint`Submission2SubmissionCategory_SubmissionCategory_Id_fk` FOREIGN KEY (`SubmissionCategoryId`) REFERENCES `SubmissionCategory` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ;


DROP TABLE IF EXISTS `SubmissionCategory`;

CREATE TABLE `SubmissionCategory` (
  `Id` bigint ,
  `Name` text NOT NULL,
  `Description` text,
  `CategoryType` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`)
) ;


DROP TABLE IF EXISTS `SubmissionTag`;

CREATE TABLE `SubmissionTag` (
  `Id` bigint NOT NULL AUTO_INCREMENT,
  `SubmissionId` bigint  NOT NULL,
  `TagKey` varchar(100) NOT NULL,
  `TagValue` text,
  PRIMARY KEY (`Id`),
  KEY `SubmissionTag_Submission_Id_fk_idx` (`SubmissionId`)
  -- Constraint`SubmissionTag_Submission_Id_fk` FOREIGN KEY (`SubmissionId`) REFERENCES `Submission` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=2423;

DROP TABLE IF EXISTS `Synonym`;

CREATE TABLE `Synonym` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `SpectrumId` bigint  NOT NULL,
  `Name` text NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `Synonym_Spectrum_Id_fk_idx` (`SpectrumId`)
  -- Constraint`Synonym_Spectrum_Id_fk` FOREIGN KEY (`SpectrumId`) REFERENCES `Spectrum` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=86424;

DROP TABLE IF EXISTS `TagDistribution`;

CREATE TABLE `TagDistribution` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `ClusterId` bigint  DEFAULT NULL,
  `Label` varchar(256) NOT NULL,
  
  `Distribution` text NOT NULL,
  `PValue` double DEFAULT NULL,
  `MassSpectrometryType` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `ClusterId` (`ClusterId`)
  -- Constraint`TagDistribution_ibfk_1` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
)  AUTO_INCREMENT=3333774;


DROP TABLE IF EXISTS `UserParameter`;

CREATE TABLE `UserParameter` (
  `Id` bigint  NOT NULL AUTO_INCREMENT,
  `UserPrincipalId` bigint  DEFAULT NULL,
  `Identifier` varchar(200) NOT NULL,
  `Value` text NOT NULL,
  `Type` varchar(30) NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `UserParameter_UserPrincipalId_Identifier_index` (`UserPrincipalId`,`Identifier`)
  -- Constraint`UserParameter_UserPrincipal_Id_fk` FOREIGN KEY (`UserPrincipalId`) REFERENCES `UserPrincipal` (`Id`) ON DELETE CASCADE ON UPDATE CASCADE
) ;

DROP TABLE IF EXISTS `UserPrincipal`;

CREATE TABLE `UserPrincipal` (
  `Id` bigint  auto_increment,
  `Username` varchar(30) NOT NULL,
  `Email` varchar(30) NOT NULL,
  `HashedPassword` binary(60) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `UserPrincipal_Username_uindex` (`Username`)
)  AUTO_INCREMENT=35 ;


DROP TABLE IF EXISTS `UserRole`;
CREATE TABLE `UserRole` (
  `userPrincipalId` bigint ,
  `roleName` varchar(15) NOT NULL,
  UNIQUE KEY `user_role_user_unique_idx` (`userPrincipalId`,`roleName`),
  KEY `user_role_user_principal_idx` (`userPrincipalId`)
  -- Constraint`user_role_principal_fk` FOREIGN KEY (`userPrincipalId`) REFERENCES `UserPrincipal` (`Id`)
) ;

DROP TABLE IF EXISTS `clusterspectrumpeakview`;
DROP VIEW IF EXISTS `clusterspectrumpeakview`;


CREATE VIEW `clusterspectrumpeakview` AS SELECT 
 1 AS `Id`,
 1 AS `Mz`,
 1 AS `Intensity`,
 1 AS `SpectrumId`,
 1 AS `Precursor`,
 1 AS `RetentionTime`,
 1 AS `ChromatographyType`;


DROP TABLE IF EXISTS `searchspectrumpeakview`;
DROP VIEW IF EXISTS `searchspectrumpeakview`;

CREATE VIEW `searchspectrumpeakview` AS SELECT 
 1 AS `Id`,
 1 AS `Mz`,
 1 AS `Intensity`,
 1 AS `SpectrumId`,
 1 AS `Precursor`,
 1 AS `RetentionTime`,
 1 AS `ChromatographyType`;


DROP TABLE IF EXISTS `searchspectrumpeakviewv2`;


DROP VIEW IF EXISTS `searchspectrumpeakviewv2`;
CREATE VIEW `searchspectrumpeakviewv2` AS SELECT 
 1 AS `Id`,
 1 AS `Mz`,
 1 AS `Intensity`,
 1 AS `SpectrumId`,
 1 AS `Precursor`,
 1 AS `RetentionTime`,
 1 AS `ChromatographyType`,
 1 AS `SubmissionId`;



DROP VIEW IF EXISTS `clusterspectrumpeakview`;

CREATE VIEW `clusterspectrumpeakview` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`;

DROP VIEW IF EXISTS `searchspectrumpeakview`;

CREATE VIEW `searchspectrumpeakview` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`;



DROP VIEW IF EXISTS `searchspectrumpeakviewv2`;
CREATE VIEW `searchspectrumpeakviewv2` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`,1 AS `SubmissionId`;

