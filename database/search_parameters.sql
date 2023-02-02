CREATE TABLE `adapcompounddb`.`searchparameters` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `value` TEXT NOT NULL,
    `UserPrimaryId` BIGINT UNSIGNED NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`UserPrimaryId`)
        REFERENCES `adapcompounddb`.`userprincipal` (`Id`));