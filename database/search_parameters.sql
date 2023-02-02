CREATE TABLE `adapcompounddb`.`searchparameters` (
    `name` VARCHAR(45) NOT NULL,
    `value` TEXT NOT NULL,
    `UserPrimaryId` BIGINT UNSIGNED NULL,
    INDEX `Id_idx` (`UserPrimaryId` ASC) VISIBLE,
    CONSTRAINT `Id`
        FOREIGN KEY (`UserPrimaryId`)
            REFERENCES `adapcompounddb`.`userprincipal` (`Id`)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION);
ALTER TABLE `adapcompounddb`.`searchparameters`
    ADD COLUMN `id` VARCHAR(45) NOT NULL AFTER `UserPrimaryId`,
ADD PRIMARY KEY (`id`);
;