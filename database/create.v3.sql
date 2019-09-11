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

-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `adapcompounddb` DEFAULT CHARACTER SET utf8;
USE `adapcompounddb`;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`TagDistribution`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`TagDistribution`
(
    `Id`                  BIGINT(20)       UNSIGNED NOT NULL AUTO_INCREMENT,
    `ClusterId`           BIGINT(20)       UNSIGNED NULL DEFAULT NULL,
    `TagKey`              VARCHAR(256)     NOT NULL,
    `TagDistribution`     TEXT             NOT NULL,
    `PValue`              DOUBLE           NULL DEFAULT NULL,
    PRIMARY KEY (`Id`),
	FOREIGN KEY (`ClusterId`)
            REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 11
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserPrincipal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserPrincipal`
(
    `Id`             BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Username`       VARCHAR(30)         NOT NULL,
    `Email`          VARCHAR(30)         NOT NULL,
    `HashedPassword` BINARY(60)          NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE INDEX `UserPrincipal_Username_uindex` (`Username` ASC)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 17
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission`
(
    `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Name`            TEXT                NOT NULL,
    `Description`     TEXT                NULL     DEFAULT NULL,
    `DateTime`        TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    AUTO_INCREMENT = 113
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`File`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`File`
(
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
    AUTO_INCREMENT = 235
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Spectrum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Spectrum`
(
    `Id`                 BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Name`               TEXT                NULL     DEFAULT NULL,
    `Precursor`          DOUBLE              NULL     DEFAULT NULL,
    `RetentionTime`      DOUBLE              NULL     DEFAULT NULL,
    `Significance`       DOUBLE              NULL     DEFAULT NULL,
    `ClusterId`          BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
    `Consensus`          TINYINT(1)          NOT NULL DEFAULT '0',
    `Reference`          TINYINT(1)          NOT NULL DEFAULT '0',
    `ChromatographyType` VARCHAR(30)         NOT NULL,
    `FileId`             BIGINT(20) UNSIGNED NULL     DEFAULT NULL,
    PRIMARY KEY (`Id`),
    INDEX `Spectrum_ClusterId_index` (`ClusterId` ASC),
    INDEX `Spectrum_Consensus_index` (`Consensus` ASC),
    INDEX `Spectrum_File_Id_fk_idx` (`FileId` ASC),
    INDEX `Spectrum_CPR_index` (`ChromatographyType` ASC, `Precursor` ASC, `RetentionTime` ASC),
    CONSTRAINT `Spectrum_File_Id_fk`
        FOREIGN KEY (`FileId`)DiseasePVa
            REFERENCES `adapcompounddb`.`File` (`Id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `Spectrum_SpectrumCluster_Id_fk`
        FOREIGN KEY (`ClusterId`)
            REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
            ON DELETE SET NULL
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 241574
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumCluster`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumCluster`
(
    `Id`                  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `ConsensusSpectrumId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
    `Diameter`            DOUBLE              NOT NULL,
    `Size`                INT(11)             NOT NULL,
    `AveSignificance`     DOUBLE              NULL DEFAULT NULL,
    `MinSignificance`     DOUBLE              NULL DEFAULT NULL,
    `MaxSignificance`     DOUBLE              NULL DEFAULT NULL,
    `AveDiversity`        DOUBLE              NULL DEFAULT NULL,
    `MinDiversity`        DOUBLE              NULL DEFAULT NULL,
    `MaxDiversity`        DOUBLE              NULL DEFAULT NULL,
    `MinPValue`           DOUBLE              NULL DEFAULT NULL,
    `DiseasePValue`       DOUBLE              NULL DEFAULT NULL,
    `SpeciesPValue`       DOUBLE              NULL DEFAULT NULL,
    `SampleSourcePValue`  DOUBLE              NULL DEFAULT NULL,
    `ChromatographyType`  VARCHAR(30)         NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE INDEX `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId` ASC),
    CONSTRAINT `SpectrumCluster_Spectrum_Id_fk`
        FOREIGN KEY (`ConsensusSpectrumId`)
            REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 25173
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`DiversityIndex`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`DiversityIndex`
(
    `ClusterId`    BIGINT(20) UNSIGNED NOT NULL,
    `CategoryType` VARCHAR(30)         NOT NULL,
    `Diversity`    DOUBLE              NOT NULL,
    PRIMARY KEY (`ClusterId`, `CategoryType`),
    CONSTRAINT `DiversityIndex_SpectrumCluster_Id_fk`
        FOREIGN KEY (`ClusterId`)
            REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
            ON DELETE CASCADE
            ON UPDATE NO ACTION
)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Feedback`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Feedback`
(
    `Id`          INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Name`        TEXT             NOT NULL,
    `Email`       TEXT             NOT NULL,
    `Affiliation` TEXT             NOT NULL,
    `Message`     TEXT             NOT NULL,
    `SubmitDate`  DATETIME         NULL DEFAULT NULL,
    `ReadFlag`    TINYINT(4)       NOT NULL,
    PRIMARY KEY (`Id`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 8
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Peak`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Peak`
(
    `Id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Mz`         DOUBLE              NOT NULL,
    `Intensity`  DOUBLE              NOT NULL,
    `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
    PRIMARY KEY (`Id`),
    INDEX `Peak_Mz_index` (`Mz` ASC),
    INDEX `Peak_SpectrumId_index` (`SpectrumId` ASC),
    INDEX `Peak_SpectrumId_Mz_index` (`SpectrumId` ASC, `Mz` ASC),
    CONSTRAINT `Peak_Spectrum_Id_fk`
        FOREIGN KEY (`SpectrumId`)
            REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 14603055
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumMatch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumMatch`
(
    `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `QuerySpectrumId` BIGINT(20) UNSIGNED NOT NULL,
    `MatchSpectrumId` BIGINT(20) UNSIGNED NOT NULL,
    `Score`           DOUBLE              NOT NULL,
    PRIMARY KEY (`Id`),
    INDEX `SpectrumMatch_Spectrum_Id_fk_2` (`MatchSpectrumId` ASC),
    INDEX `SpectrumMatch_QuerySpectrumId_index` (`QuerySpectrumId` ASC),
    INDEX `SpectrumMatch_Score_index` (`Score` ASC, `QuerySpectrumId` ASC, `MatchSpectrumId` ASC),
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
    AUTO_INCREMENT = 1921836
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumProperty`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumProperty`
(
    `Id`         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
    `Name`       VARCHAR(60)         NOT NULL,
    `Value`      TEXT                NULL DEFAULT NULL,
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
    AUTO_INCREMENT = 2198215
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionCategory`
(
    `Id`           BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `Name`         TEXT                NOT NULL,
    `Description`  TEXT                NULL DEFAULT NULL,
    `CategoryType` VARCHAR(30)         NOT NULL,
    PRIMARY KEY (`Id`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 15
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission2SubmissionCategory`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission2SubmissionCategory`
(
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
    AUTO_INCREMENT = 71
    DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionTag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionTag`
(
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
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserParameter`
(
    `Id`              BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `UserPrincipalId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
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
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserRole`
(
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

USE `adapcompounddb`;

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`ClusterPage`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`ClusterPage`
(
    `id`        INT,
    `source`    INT,
    `specimen`  INT,
    `treatment` INT
);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`ClusterSpectrumPeakView`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`ClusterSpectrumPeakView`
(
    `Id`                 INT,
    `Mz`                 INT,
    `Intensity`          INT,
    `SpectrumId`         INT,
    `Precursor`          INT,
    `RetentionTime`      INT,
    `ChromatographyType` INT
);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`SearchSpectrumPeakView`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SearchSpectrumPeakView`
(
    `Id`                 INT,
    `Mz`                 INT,
    `Intensity`          INT,
    `SpectrumId`         INT,
    `Precursor`          INT,
    `RetentionTime`      INT,
    `ChromatographyType` INT
);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`SearchSpectrumPeakViewV2`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SearchSpectrumPeakViewV2`
(
    `Id`                 INT,
    `Mz`                 INT,
    `Intensity`          INT,
    `SpectrumId`         INT,
    `Precursor`          INT,
    `RetentionTime`      INT,
    `ChromatographyType` INT,
    `SubmissionId`       INT,
    `SubmissionTagName`  INT
);

-- -----------------------------------------------------
-- View `adapcompounddb`.`ClusterPage`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`ClusterPage`;
USE `adapcompounddb`;
CREATE OR REPLACE ALGORITHM = UNDEFINED DEFINER =`dulab`@`%` SQL SECURITY DEFINER VIEW `adapcompounddb`.`ClusterPage` AS
select `sc`.`Id`                                                                              AS `id`,
       coalesce(sum((case when (`d`.`CategoryType` = 'SOURCE') then `d`.`Diversity` end)))    AS `source`,
       coalesce(sum((case when (`d`.`CategoryType` = 'SPECIMEN') then `d`.`Diversity` end)))  AS `specimen`,
       coalesce(sum((case when (`d`.`CategoryType` = 'TREATMENT') then `d`.`Diversity` end))) AS `treatment`
from (`adapcompounddb`.`SpectrumCluster` `sc`
         left join `adapcompounddb`.`DiversityIndex` `d` on ((`sc`.`Id` = `d`.`ClusterId`)))
group by `sc`.`Id`;

-- -----------------------------------------------------
-- View `adapcompounddb`.`ClusterSpectrumPeakView`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`ClusterSpectrumPeakView`;
USE `adapcompounddb`;
CREATE OR REPLACE ALGORITHM = UNDEFINED DEFINER =`dulab`@`%` SQL SECURITY DEFINER VIEW `adapcompounddb`.`ClusterSpectrumPeakView` AS
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
-- View `adapcompounddb`.`SearchSpectrumPeakView`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`SearchSpectrumPeakView`;
USE `adapcompounddb`;
CREATE OR REPLACE ALGORITHM = UNDEFINED DEFINER =`dulab`@`%` SQL SECURITY DEFINER VIEW `adapcompounddb`.`SearchSpectrumPeakView` AS
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
-- View `adapcompounddb`.`SearchSpectrumPeakViewV2`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`SearchSpectrumPeakViewV2`;
USE `adapcompounddb`;
CREATE OR REPLACE ALGORITHM = UNDEFINED DEFINER =`dulab`@`%` SQL SECURITY DEFINER VIEW `adapcompounddb`.`SearchSpectrumPeakViewV2` AS
select `adapcompounddb`.`Peak`.`Id`                     AS `Id`,
       `adapcompounddb`.`Peak`.`Mz`                     AS `Mz`,
       `adapcompounddb`.`Peak`.`Intensity`              AS `Intensity`,
       `adapcompounddb`.`Peak`.`SpectrumId`             AS `SpectrumId`,
       `adapcompounddb`.`Spectrum`.`Precursor`          AS `Precursor`,
       `adapcompounddb`.`Spectrum`.`RetentionTime`      AS `RetentionTime`,
       `adapcompounddb`.`Spectrum`.`ChromatographyType` AS `ChromatographyType`,
       `adapcompounddb`.`Submission`.`Id`               AS `SubmissionId`,
       `adapcompounddb`.`SubmissionTag`.`Name`          AS `SubmissionTagName`
from ((((`adapcompounddb`.`Peak` join `adapcompounddb`.`Spectrum` on ((`adapcompounddb`.`Peak`.`SpectrumId` =
                                                                       `adapcompounddb`.`Spectrum`.`Id`))) left join `adapcompounddb`.`File` on ((`adapcompounddb`.`Spectrum`.`FileId` = `adapcompounddb`.`File`.`Id`))) left join `adapcompounddb`.`Submission` on ((
        `adapcompounddb`.`File`.`SubmissionId` = `adapcompounddb`.`Submission`.`Id`)))
         left join `adapcompounddb`.`SubmissionTag`
                   on ((`adapcompounddb`.`Submission`.`Id` = `adapcompounddb`.`SubmissionTag`.`SubmissionId`)))
where ((`adapcompounddb`.`Spectrum`.`Consensus` is true) or (`adapcompounddb`.`Spectrum`.`Reference` is true));
USE `adapcompounddb`;

DELIMITER $$
USE `adapcompounddb`$$
CREATE
    DEFINER =`dulab`@`%`
    TRIGGER `adapcompounddb`.`Feedback_BEFORE_INSERT`
    BEFORE INSERT
    ON `adapcompounddb`.`Feedback`
    FOR EACH ROW
BEGIN
    SET NEW.SubmitDate = sysdate();
END$$


DELIMITER ;

SET SQL_MODE = @OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS;
