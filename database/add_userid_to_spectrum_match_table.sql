ALTER TABLE `adapcompounddb`.`SpectrumMatch`
    ADD COLUMN `UserPrincipalId` BIGINT UNSIGNED NULL,
    ADD COLUMN `Ontologylevel` VARCHAR(45) NULL;