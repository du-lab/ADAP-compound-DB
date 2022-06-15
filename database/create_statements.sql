-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `adapcompounddb` ;

-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `adapcompounddb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `adapcompounddb` ;

-- -----------------------------------------------------
-- Table `Adduct`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Adduct` ;

CREATE TABLE IF NOT EXISTS `Adduct` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `NumMolecules` INT NOT NULL,
  `Mass` DOUBLE NOT NULL,
  `Charge` INT NOT NULL,
  `Chromatography` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`))
ENGINE = InnoDB
AUTO_INCREMENT = 36
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `DiversityIndex`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DiversityIndex` ;

CREATE TABLE IF NOT EXISTS `DiversityIndex` (
  `ClusterId` BIGINT NOT NULL,
  `CategoryType` VARCHAR(30) NOT NULL,
  `Diversity` DOUBLE NOT NULL,
  PRIMARY KEY (`ClusterId`, `CategoryType`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Feedback`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Feedback` ;

CREATE TABLE IF NOT EXISTS `Feedback` (
  `Id` BIGINT NULL DEFAULT NULL,
  `Name` TEXT NULL DEFAULT NULL,
  `Email` TEXT NULL DEFAULT NULL,
  `Affiliation` TEXT NULL DEFAULT NULL,
  `Message` TEXT NULL DEFAULT NULL,
  `SubmitDate` TEXT NULL DEFAULT NULL,
  `ReadFlag` DOUBLE NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `File`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `File` ;

CREATE TABLE IF NOT EXISTS `File` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `FileType` VARCHAR(30) NOT NULL,
  `Content` LONGTEXT NOT NULL,
  `SubmissionId` BIGINT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `File_Submission_Id_fk_idx` (`SubmissionId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 391
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Identifier`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Identifier` ;

CREATE TABLE IF NOT EXISTS `Identifier` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT NULL DEFAULT NULL,
  `Type` VARCHAR(30) NOT NULL,
  `Value` TEXT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Identifier_Spectrum_id_fk_idx` (`SpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2054750
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Isotope`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Isotope` ;

CREATE TABLE IF NOT EXISTS `Isotope` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT NOT NULL,
  `Index` INT NOT NULL,
  `Intensity` DOUBLE NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumId` (`SpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Peak`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Peak` ;

CREATE TABLE IF NOT EXISTS `Peak` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `Mz` DOUBLE NOT NULL,
  `Intensity` DOUBLE NOT NULL,
  `SpectrumId` BIGINT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Peak_SpectrumId_index` (`SpectrumId` ASC) VISIBLE,
  INDEX `Peak_Mz_index` (`Mz` ASC) VISIBLE,
  INDEX `Peak_SpectrumId_Mz_index` (`Mz` ASC, `SpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 127438124
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Spectrum`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Spectrum` ;

CREATE TABLE IF NOT EXISTS `Spectrum` (
  `Id` BIGINT NULL DEFAULT NULL,
  `ExternalId` TEXT NULL DEFAULT NULL,
  `Name` TEXT NULL DEFAULT NULL,
  `Precursor` DOUBLE NULL DEFAULT NULL,
  `PrecursorType` TEXT NULL DEFAULT NULL,
  `RetentionTime` DOUBLE NULL DEFAULT NULL,
  `RetentionIndex` DOUBLE NULL DEFAULT NULL,
  `Significance` DOUBLE NULL DEFAULT NULL,
  `ClusterId` DOUBLE NULL DEFAULT NULL,
  `Consensus` DOUBLE NULL DEFAULT NULL,
  `Reference` DOUBLE NULL DEFAULT NULL,
  `InHouseReference` DOUBLE NULL DEFAULT NULL,
  `Clusterable` DOUBLE NULL DEFAULT NULL,
  `ChromatographyType` TEXT NULL DEFAULT NULL,
  `FileId` DOUBLE NULL DEFAULT NULL,
  `IntegerMz` DOUBLE NULL DEFAULT NULL,
  `Mass` DOUBLE NULL DEFAULT NULL,
  `Formula` TEXT NULL DEFAULT NULL,
  `CanonicalSMILES` TEXT NULL DEFAULT NULL,
  `InChi` TEXT NULL DEFAULT NULL,
  `InChiKey` TEXT NULL DEFAULT NULL,
  `TopMz1` DOUBLE NULL DEFAULT NULL,
  `TopMz2` DOUBLE NULL DEFAULT NULL,
  `TopMz3` DOUBLE NULL DEFAULT NULL,
  `TopMz4` DOUBLE NULL DEFAULT NULL,
  `TopMz5` DOUBLE NULL DEFAULT NULL,
  `TopMz6` DOUBLE NULL DEFAULT NULL,
  `TopMz7` DOUBLE NULL DEFAULT NULL,
  `TopMz8` DOUBLE NULL DEFAULT NULL,
  `TopMz9` DOUBLE NULL DEFAULT NULL,
  `TopMz10` DOUBLE NULL DEFAULT NULL,
  `TopMz11` DOUBLE NULL DEFAULT NULL,
  `TopMz12` DOUBLE NULL DEFAULT NULL,
  `TopMz13` DOUBLE NULL DEFAULT NULL,
  `TopMz14` DOUBLE NULL DEFAULT NULL,
  `TopMz15` DOUBLE NULL DEFAULT NULL,
  `TopMz16` DOUBLE NULL DEFAULT NULL,
  `OmegaFactor` DOUBLE NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `SpectrumCluster`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SpectrumCluster` ;

CREATE TABLE IF NOT EXISTS `SpectrumCluster` (
  `Id` BIGINT NULL DEFAULT NULL,
  `ConsensusSpectrumId` BIGINT NULL DEFAULT NULL,
  `Diameter` DOUBLE NULL DEFAULT NULL,
  `Size` BIGINT NULL DEFAULT NULL,
  `AveSignificance` DOUBLE NULL DEFAULT NULL,
  `MinSignificance` DOUBLE NULL DEFAULT NULL,
  `MaxSignificance` DOUBLE NULL DEFAULT NULL,
  `AveDiversity` DOUBLE NULL DEFAULT NULL,
  `MinDiversity` DOUBLE NULL DEFAULT NULL,
  `MaxDiversity` DOUBLE NULL DEFAULT NULL,
  `MinPValue` DOUBLE NULL DEFAULT NULL,
  `DiseasePValue` DOUBLE NULL DEFAULT NULL,
  `SpeciesPValue` DOUBLE NULL DEFAULT NULL,
  `SampleSourcePValue` DOUBLE NULL DEFAULT NULL,
  `ChromatographyType` TEXT NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `SpectrumMatch`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SpectrumMatch` ;

CREATE TABLE IF NOT EXISTS `SpectrumMatch` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` BIGINT NOT NULL,
  `MatchSpectrumId` BIGINT NOT NULL,
  `Score` DOUBLE NULL DEFAULT NULL,
  `MassError` DOUBLE NULL DEFAULT NULL,
  `MassErrorPPM` DOUBLE NULL DEFAULT NULL,
  `RetTimeError` DOUBLE NULL DEFAULT NULL,
  `PrecursorError` DOUBLE NULL DEFAULT NULL,
  `PrecursorErrorPPM` DOUBLE NULL DEFAULT NULL,
  `RetIndexError` DOUBLE NULL DEFAULT NULL,
  `IsotopicSimilarity` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumMatch_Spectrum_Id_fk_2` (`MatchSpectrumId` ASC) VISIBLE,
  INDEX `SpectrumMatch_QuerySpectrumId_index` (`QuerySpectrumId` ASC) VISIBLE,
  INDEX `SpectrumMatch_Score_index` (`Score` ASC, `QuerySpectrumId` ASC, `MatchSpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 258776
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `SpectrumProperty`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SpectrumProperty` ;

CREATE TABLE IF NOT EXISTS `SpectrumProperty` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT NOT NULL,
  `Name` VARCHAR(60) NOT NULL,
  `Value` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumProperty_Name_index` (`Name` ASC) VISIBLE,
  INDEX `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 53109608
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Submission`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Submission` ;

CREATE TABLE IF NOT EXISTS `Submission` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  `DateTime` TIMESTAMP NULL DEFAULT NULL,
  `UserPrincipalId` BIGINT NULL DEFAULT NULL,
  `Reference` TEXT NULL DEFAULT NULL,
  `MassSpectrometryType` VARCHAR(30) NOT NULL,
  `ExternalId` TEXT NULL DEFAULT NULL,
  `IsPrivate` TINYINT NOT NULL DEFAULT '0',
  `Raw` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`Id`),
  INDEX `Submission_DateTime_Id_index` (`DateTime` ASC, `Id` ASC) VISIBLE,
  INDEX `Submission_UserPrincipalId_index` (`UserPrincipalId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 382
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Submission2SubmissionCategory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Submission2SubmissionCategory` ;

CREATE TABLE IF NOT EXISTS `Submission2SubmissionCategory` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SubmissionId` BIGINT NOT NULL,
  `SubmissionCategoryId` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission2SubmissionCategory_Submission_Id_fk_idx` (`SubmissionId` ASC) VISIBLE,
  INDEX `Submission2SubmissionCategory_SubmissionCategory_Id_fk_idx` (`SubmissionCategoryId` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `SubmissionCategory`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SubmissionCategory` ;

CREATE TABLE IF NOT EXISTS `SubmissionCategory` (
  `Id` BIGINT NOT NULL,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  `CategoryType` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `SubmissionTag`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `SubmissionTag` ;

CREATE TABLE IF NOT EXISTS `SubmissionTag` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SubmissionId` BIGINT NOT NULL,
  `TagKey` VARCHAR(100) NOT NULL,
  `TagValue` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SubmissionTag_Submission_Id_fk_idx` (`SubmissionId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2423
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Synonym`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `Synonym` ;

CREATE TABLE IF NOT EXISTS `Synonym` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT NOT NULL,
  `Name` TEXT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Synonym_Spectrum_Id_fk_idx` (`SpectrumId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 86424
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `TagDistribution`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `TagDistribution` ;

CREATE TABLE IF NOT EXISTS `TagDistribution` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `ClusterId` BIGINT NULL DEFAULT NULL,
  `Label` VARCHAR(256) NOT NULL,
  `Distribution` TEXT NOT NULL,
  `PValue` DOUBLE NULL DEFAULT NULL,
  `MassSpectrometryType` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `ClusterId` (`ClusterId` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 3333774
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `UserParameter`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `UserParameter` ;

CREATE TABLE IF NOT EXISTS `UserParameter` (
  `Id` TEXT NULL DEFAULT NULL,
  `UserPrincipalId` TEXT NULL DEFAULT NULL,
  `Identifier` TEXT NULL DEFAULT NULL,
  `Value` TEXT NULL DEFAULT NULL,
  `Type` TEXT NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `UserPrincipal`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `UserPrincipal` ;

CREATE TABLE IF NOT EXISTS `UserPrincipal` (
  `Id` BIGINT NOT NULL AUTO_INCREMENT,
  `Username` VARCHAR(30) NOT NULL,
  `Email` VARCHAR(30) NOT NULL,
  `HashedPassword` BINARY(60) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `UserPrincipal_Username_uindex` (`Username` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 35
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `UserRole`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `UserRole` ;

CREATE TABLE IF NOT EXISTS `UserRole` (
  `userPrincipalId` BIGINT NULL DEFAULT NULL,
  `roleName` TEXT NULL DEFAULT NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

USE `adapcompounddb` ;

-- -----------------------------------------------------
-- View `clusterspectrumpeakview`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `clusterspectrumpeakview` ;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `clusterspectrumpeakview` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`;

-- -----------------------------------------------------
-- View `searchspectrumpeakview`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `searchspectrumpeakview` ;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `searchspectrumpeakview` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`;

-- -----------------------------------------------------
-- View `searchspectrumpeakviewv2`
-- -----------------------------------------------------
DROP VIEW IF EXISTS `searchspectrumpeakviewv2` ;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `searchspectrumpeakviewv2` AS select 1 AS `Id`,1 AS `Mz`,1 AS `Intensity`,1 AS `SpectrumId`,1 AS `Precursor`,1 AS `RetentionTime`,1 AS `ChromatographyType`,1 AS `SubmissionId`;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
