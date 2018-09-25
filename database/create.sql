-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0;
SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0;
SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `adapcompounddb`;

-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `adapcompounddb`
  DEFAULT CHARACTER SET utf8;
USE `adapcompounddb`;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserPrincipal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserPrincipal` (
  `Id`             BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Username`       VARCHAR(30)         NOT NULL,
  `Email`          VARCHAR(30)         NOT NULL,
  `HashedPassword` BINARY(60)          NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `UserPrincipal_Username_uindex` (`Username` ASC)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 12
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission` (
  `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name`            TEXT                NOT NULL,
  `Description`     TEXT                NULL     DEFAULT NULL,
  `DateTime`        TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  `UserPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  `Reference`       TEXT                NULL     DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission_DateTime_Id_index` (`DateTime` ASC, `Id` ASC),
  INDEX `Submission_UserPrincipalId_index` (`UserPrincipalId` ASC),
  CONSTRAINT `Submission_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 72
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`File`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`File` (
  `Id`           BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name`         TEXT                NOT NULL,
  `FileType`     VARCHAR(30)         NOT NULL,
  `Content`      LONGBLOB            NOT NULL,
  `SubmissionId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `File_Submission_Id_fk_idx` (`SubmissionId` ASC),
  CONSTRAINT `File_Submission_Id_fk`
  FOREIGN KEY (`SubmissionId`)
  REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 50
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumCluster`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumCluster` (
  `Id`                  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ConsensusSpectrumId` BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
  `Diameter`            DOUBLE              NOT NULL,
  `Size`                INT(11)             NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId` ASC),
  CONSTRAINT `SpectrumCluster_Spectrum_Id_fk`
  FOREIGN KEY (`ConsensusSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 126
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`Spectrum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Spectrum` (
  `Id`                 BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name`               TEXT                NULL     DEFAULT NULL,
  `Precursor`          DOUBLE              NULL     DEFAULT NULL,
  `RetentionTime`      DOUBLE              NULL     DEFAULT NULL,
  `ClusterId`          BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
  `Consensus`          TINYINT(1)          NOT NULL DEFAULT '0',
  `Reference`          TINYINT(1)          NOT NULL DEFAULT '0',
  `ChromatographyType` VARCHAR(30)         NOT NULL,
  `FileId`             BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Spectrum_ClusterId_index` (`ClusterId` ASC),
  INDEX `Spectrum_Consensus_index` (`Consensus` ASC),
  INDEX `Spectrum_File_Id_fk_idx` (`FileId` ASC),
  CONSTRAINT `Spectrum_File_Id_fk`
  FOREIGN KEY (`FileId`)
  REFERENCES `adapcompounddb`.`File` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Spectrum_SpectrumCluster_Id_fk`
  FOREIGN KEY (`ClusterId`)
  REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 16883
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`Peak`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Peak` (
  `Id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Mz`         DOUBLE              NOT NULL,
  `Intensity`  DOUBLE              NOT NULL,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Peak_Mz_index` (`Mz` ASC),
  INDEX `Peak_SpectrumId_index` (`SpectrumId` ASC),
  CONSTRAINT `Peak_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1726894
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumMatch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumMatch` (
  `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `MatchSpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Score`           DOUBLE              NOT NULL,
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
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 4772
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumProperty`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumProperty` (
  `Id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Name`       VARCHAR(60)         NOT NULL,
  `Value`      TEXT                NULL     DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumProperty_Name_index` (`Name` ASC),
  INDEX `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId` ASC),
  CONSTRAINT `SpectrumProperty_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 89041
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionCategory` (
  `Id`           BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name`         TEXT                NOT NULL,
  `Description`  TEXT                NULL     DEFAULT NULL,
  `CategoryType` VARCHAR(30)         NOT NULL,
  PRIMARY KEY (`Id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission2SubmissionCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission2SubmissionCategory` (
  `Id`                   BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `SubmissionId`         BIGINT(20) UNSIGNED NOT NULL,
  `SubmissionCategoryId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission2SubmissionCategory_Submission_Id_fk_idx` (`SubmissionId` ASC),
  INDEX `Submission2SubmissionCategory_SubmissionCategory_Id_fk_idx` (`SubmissionCategoryId` ASC),
  CONSTRAINT `Submission2SubmissionCategory_SubmissionCategory_Id_fk`
  FOREIGN KEY (`SubmissionCategoryId`)
  REFERENCES `adapcompounddb`.`SubmissionCategory` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Submission2SubmissionCategory_Submission_Id_fk`
  FOREIGN KEY (`SubmissionId`)
  REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionTag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionTag` (
  `SubmissionId` BIGINT(20) UNSIGNED NOT NULL,
  `Name`         VARCHAR(100)        NOT NULL,
  PRIMARY KEY (`SubmissionId`, `Name`),
  INDEX `SubmissionTag_Submission_Id_fk_idx` (`SubmissionId` ASC),
  CONSTRAINT `SubmissionTag_Submission_Id_fk`
  FOREIGN KEY (`SubmissionId`)
  REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserParameter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserParameter` (
  `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserPrincipalId` BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
  `Identifier`      VARCHAR(200)        NOT NULL,
  `Value`           TEXT                NOT NULL,
  `Type`            VARCHAR(30)         NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `UserParameter_UserPrincipalId_Identifier_index` (`UserPrincipalId` ASC, `Identifier` ASC),
  CONSTRAINT `UserParameter_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserRole`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserRole` (
  `userPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  `roleName`        VARCHAR(15)         NOT NULL,
  UNIQUE INDEX `user_role_user_unique_idx` (`userPrincipalId` ASC, `roleName` ASC),
  INDEX `user_role_user_principal_idx` (`userPrincipalId` ASC),
  CONSTRAINT `user_role_principal_fk`
  FOREIGN KEY (`userPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8;

CREATE TABLE `DiversityIndex` (
  `ClusterId` bigint(20) unsigned NOT NULL,
  `CategoryType` varchar(30) NOT NULL,
  `Diversity` double NOT NULL,
  PRIMARY KEY (`ClusterId`,`CategoryType`),
  CONSTRAINT `DiversityIndex_SpectrumCluster_Id_fk` FOREIGN KEY (`ClusterId`) REFERENCES `SpectrumCluster` (`Id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARACTER SET = utf8;

USE `adapcompounddb`;

-- -----------------------------------------------------
-- View `adapcompounddb`.`clusterspectrumpeakview`
-- -----------------------------------------------------
USE `adapcompounddb`;
CREATE OR REPLACE VIEW `adapcompounddb`.`ClusterSpectrumPeakView` AS
  select `adapcompounddb`.`Peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`Peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`Peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`Peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`Spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`Spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`Spectrum`.`ChromatographyType` AS `ChromatographyType`
  from (`adapcompounddb`.`Peak`
      join `adapcompounddb`.`Spectrum`)
  where ((`adapcompounddb`.`Peak`.`SpectrumId` = `adapcompounddb`.`Spectrum`.`Id`) and
         (`adapcompounddb`.`Spectrum`.`Consensus` is false) and (`adapcompounddb`.`Spectrum`.`Reference` is false));

-- -----------------------------------------------------
-- View `adapcompounddb`.`searchspectrumpeakview`
-- -----------------------------------------------------
USE `adapcompounddb`;
CREATE OR REPLACE VIEW `adapcompounddb`.`SearchSpectrumPeakView` AS
  select `adapcompounddb`.`Peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`Peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`Peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`Peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`Spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`Spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`Spectrum`.`ChromatographyType` AS `ChromatographyType`
  from (`adapcompounddb`.`Peak`
      join `adapcompounddb`.`Spectrum`)
  where ((`adapcompounddb`.`Peak`.`SpectrumId` = `adapcompounddb`.`Spectrum`.`Id`) and
         ((`adapcompounddb`.`Spectrum`.`Consensus` is true) or (`adapcompounddb`.`Spectrum`.`Reference` is true)));

-- -----------------------------------------------------
-- View `adapcompounddb`.`searchspectrumpeakviewv2`
-- -----------------------------------------------------
USE `adapcompounddb`;
CREATE OR REPLACE VIEW `adapcompounddb`.`SearchSpectrumPeakViewV2` AS
  select `adapcompounddb`.`Peak`.`Id`                     AS `Id`,
         `adapcompounddb`.`Peak`.`Mz`                     AS `Mz`,
         `adapcompounddb`.`Peak`.`Intensity`              AS `Intensity`,
         `adapcompounddb`.`Peak`.`SpectrumId`             AS `SpectrumId`,
         `adapcompounddb`.`Spectrum`.`Precursor`          AS `Precursor`,
         `adapcompounddb`.`Spectrum`.`RetentionTime`      AS `RetentionTime`,
         `adapcompounddb`.`Spectrum`.`ChromatographyType` AS `ChromatographyType`,
         `adapcompounddb`.`Submission`.`Id`               AS `SubmissionId`,
         `adapcompounddb`.`SubmissionTag`.`Name`          AS `SubmissionTagName`
  from ((((`adapcompounddb`.`Peak`
      join `adapcompounddb`.`Spectrum` on ((`adapcompounddb`.`Peak`.`SpectrumId` =
                                            `adapcompounddb`.`Spectrum`.`Id`))) left join `adapcompounddb`.`File` on ((
    `adapcompounddb`.`Spectrum`.`FileId` = `adapcompounddb`.`File`.`Id`))) left join `adapcompounddb`.`Submission` on ((
    `adapcompounddb`.`File`.`SubmissionId` =
    `adapcompounddb`.`Submission`.`Id`))) left join `adapcompounddb`.`SubmissionTag` on ((
    `adapcompounddb`.`Submission`.`Id` = `adapcompounddb`.`SubmissionTag`.`SubmissionId`)))
  where ((`adapcompounddb`.`Spectrum`.`Consensus` is true) or (`adapcompounddb`.`Spectrum`.`Reference` is true));

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- View `adapcompounddb`.`clusterpage` view for cluster pagination
-- -----------------------------------------------------
create view ClusterPage as
    select 
        sc.id as id,
        coalesce(sum(case when d.CategoryType = "SOURCE" then Diversity end)) as "source",
        coalesce(sum(case when d.CategoryType = "SPECIMEN" then Diversity end)) as "specimen",
        coalesce(sum(case when d.CategoryType = "TREATMENT" then Diversity end)) as "treatment"
    from
       spectrumcluster sc
       left join diversityindex d on sc.id = d.ClusterId group by sc.ConsensusSpectrumId;