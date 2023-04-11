ALTER TABLE adapcompounddb.userprincipal
    ADD COLUMN is_organization INT NULL DEFAULT 0 AFTER PeakNumber,
    ADD COLUMN OrganizationId BIGINT NULL DEFAULT NULL AFTER is_organization,
    ADD COLUMN OrganizationRequestToken VARCHAR(45) NULL DEFAULT NULL AFTER OrganizationId,
    ADD COLUMN OrganizationRequestExpirationDate TIMESTAMP NULL DEFAULT NULL AFTER OrganizationRequestToken;