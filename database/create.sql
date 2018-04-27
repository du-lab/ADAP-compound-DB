-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
DROP SCHEMA  IF EXISTS `adapcompounddb`;
CREATE SCHEMA IF NOT EXISTS `adapcompounddb` DEFAULT CHARACTER SET latin1 ;
USE `adapcompounddb` ;

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
AUTO_INCREMENT = 3
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
AUTO_INCREMENT = 4
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
  `UserPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  `SubmissionCategoryId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission_DateTime_Id_index` (`DateTime` ASC, `Id` ASC),
  INDEX `Submission_UserPrincipalId_index` (`UserPrincipalId` ASC),
  INDEX `Submission_SubmissionCategory_Id_fk` (`SubmissionCategoryId` ASC),
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
AUTO_INCREMENT = 30
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Spectrum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Spectrum` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` VARCHAR(60) NULL DEFAULT NULL,
  `SubmissionId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Spectrum_SubmissionId_index` (`SubmissionId` ASC),
  CONSTRAINT `Spectrum_Submission_Id_fk`
    FOREIGN KEY (`SubmissionId`)
    REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 3952
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
AUTO_INCREMENT = 351911
DEFAULT CHARACTER SET = utf8;


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
AUTO_INCREMENT = 7387
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
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`peakview` (`SpectrumId` INT, `Mz` INT, `Intensity` INT, `ChromatographyType` INT, `SubmissionCategoryId` INT);

-- -----------------------------------------------------
-- View `adapcompounddb`.`peakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`peakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`peakview` AS select `adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`submission`.`ChromatographyType` AS `ChromatographyType`,`adapcompounddb`.`submission`.`SubmissionCategoryId` AS `SubmissionCategoryId` from ((`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) join `adapcompounddb`.`submission`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and (`adapcompounddb`.`spectrum`.`SubmissionId` = `adapcompounddb`.`submission`.`Id`));


