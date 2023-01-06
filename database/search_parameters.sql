CREATE TABLE `adapcompounddb_updated`.`searchparameters` (
    `name` VARCHAR(45) NOT NULL,
    `value` TEXT NOT NULL,
    `UserPrimaryId` BIGINT UNSIGNED NULL,
    INDEX `Id_idx` (`UserPrimaryId` ASC) VISIBLE,
    CONSTRAINT `Id`
        FOREIGN KEY (`UserPrimaryId`)
            REFERENCES `adapcompounddb_updated`.`userprincipal` (`Id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION);
ALTER TABLE `adapcompounddb_updated`.`searchparameters`
    ADD COLUMN `id` VARCHAR(45) NOT NULL AFTER `UserPrimaryId`,
ADD PRIMARY KEY (`id`);
;
