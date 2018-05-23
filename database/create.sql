-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `adapcompounddb` DEFAULT CHARACTER SET latin1 ;
USE `adapcompounddb` ;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumCluster`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumCluster` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ConsensusSpectrumId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Diameter` DOUBLE NOT NULL,
  `Size` INT(11) NOT NULL,
  `ChromatographyType` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId` ASC),
  CONSTRAINT `SpectrumCluster_Spectrum_Id_fk`
  FOREIGN KEY (`ConsensusSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 534
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserPrincipal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserPrincipal` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Username` VARCHAR(30) NOT NULL,
  `Email` VARCHAR(30) NOT NULL,
  `HashedPassword` BINARY(60) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `UserPrincipal_Username_uindex` (`Username` ASC))
  ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionCategory` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(200) NOT NULL,
  `Description` TEXT NOT NULL,
  `UserPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SubmissionCategory_UserPrincipal_Id_fk` (`UserPrincipalId` ASC),
  CONSTRAINT `SubmissionCategory_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(60) NOT NULL,
  `Description` TEXT NOT NULL,
  `DateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Filename` VARCHAR(100) NOT NULL,
  `FileType` VARCHAR(30) NOT NULL,
  `File` LONGBLOB NOT NULL,
  `ChromatographyType` VARCHAR(30) NOT NULL,
  `SampleSourceType` VARCHAR(30) NOT NULL,
  `SubmissionCategoryId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `UserPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission_DateTime_Id_index` (`DateTime` ASC, `Id` ASC),
  INDEX `Submission_UserPrincipalId_index` (`UserPrincipalId` ASC),
  INDEX `Submission_SubmissionCategory_Id_fk` (`SubmissionCategoryId` ASC),
  INDEX `Submission_ChromatographyType_index` (`ChromatographyType` ASC),
  INDEX `Submission_SampleSourceType_index` (`SampleSourceType` ASC),
  CONSTRAINT `Submission_SubmissionCategory_Id_fk`
  FOREIGN KEY (`SubmissionCategoryId`)
  REFERENCES `adapcompounddb`.`SubmissionCategory` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Submission_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Spectrum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Spectrum` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NULL DEFAULT NULL,
  `SubmissionId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `ClusterId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Consensus` TINYINT(1) NOT NULL DEFAULT '0',
  `Precursor` DOUBLE NULL DEFAULT NULL,
  `RetentionTime` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Spectrum_SubmissionId_index` (`SubmissionId` ASC),
  INDEX `Spectrum_ClusterId_index` (`ClusterId` ASC),
  INDEX `Spectrum_Consensus_index` (`Consensus` ASC),
  CONSTRAINT `Spectrum_SpectrumCluster_Id_fk`
  FOREIGN KEY (`ClusterId`)
  REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Spectrum_Submission_Id_fk`
  FOREIGN KEY (`SubmissionId`)
  REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 2387
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Peak`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Peak` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Mz` DOUBLE NOT NULL,
  `Intensity` DOUBLE NOT NULL,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Peak_Mz_index` (`Mz` ASC),
  INDEX `Peak_SpectrumId_index` (`SpectrumId` ASC),
  CONSTRAINT `Peak_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 146278
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumMatch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumMatch` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `MatchSpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Score` DOUBLE NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumMatch_Spectrum_Id_fk_2` (`MatchSpectrumId` ASC),
  INDEX `SpectrumMatch_QuerySpectrumId_index` (`QuerySpectrumId` ASC),
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk`
  FOREIGN KEY (`QuerySpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk_2`
  FOREIGN KEY (`MatchSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3080
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumProperty`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumProperty` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Name` VARCHAR(60) NOT NULL,
  `Value` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumProperty_Name_index` (`Name` ASC),
  INDEX `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId` ASC),
  CONSTRAINT `SpectrumProperty_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 12671
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserParameter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserParameter` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserPrincipalId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Identifier` VARCHAR(200) NOT NULL,
  `Value` TEXT NOT NULL,
  `Type` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `UserParameter_UserPrincipalId_Identifier_index` (`UserPrincipalId` ASC, `Identifier` ASC),
  CONSTRAINT `UserParameter_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARACTER SET = latin1;

USE `adapcompounddb` ;

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`peakview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`peakview` (`Id` INT, `Mz` INT, `Intensity` INT, `SpectrumId` INT, `SubmissionId` INT, `Consensus` INT, `Precursor` INT, `RetentionTime` INT, `ChromatographyType` INT);

-- -----------------------------------------------------
-- View `adapcompounddb`.`peakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`peakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`peakview` AS select `adapcompounddb`.`peak`.`Id` AS `Id`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`spectrum`.`SubmissionId` AS `SubmissionId`,`adapcompounddb`.`spectrum`.`Consensus` AS `Consensus`,`adapcompounddb`.`spectrum`.`Precursor` AS `Precursor`,`adapcompounddb`.`spectrum`.`RetentionTime` AS `RetentionTime`,`adapcompounddb`.`submission`.`ChromatographyType` AS `ChromatographyType` from ((`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) join `adapcompounddb`.`submission`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and (`adapcompounddb`.`spectrum`.`SubmissionId` = `adapcompounddb`.`submission`.`Id`));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
