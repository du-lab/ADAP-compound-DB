CREATE TABLE IF NOT EXISTS `adapcompounddb`.`TempSpectrumMatch` (
  `Id`                BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `SessionId`         VARCHAR(128)        NOT NULL,
  `FileIndex`         INT                 NOT NULL,
  `SpectrumIndex`     INT                 NOT NULL,
  `QuerySpectrumName` VARCHAR(512)        NULL,
  `MatchSpectrumId`   BIGINT(20) UNSIGNED NULL,
  `Score`             DOUBLE              NULL,
  `IsotopicSimilarity` DOUBLE             NULL,
  `PrecursorError`    DOUBLE              NULL,
  `PrecursorErrorPPM` DOUBLE              NULL,
  `MassError`         DOUBLE              NULL,
  `MassErrorPPM`      DOUBLE              NULL,
  `RetTimeError`      DOUBLE              NULL,
  `RetIndexError`     DOUBLE              NULL,
  `OntologyLevel`     VARCHAR(45)         NULL,
  `QueryPeakMzs`      BLOB               NULL,
  `LibraryPeakMzs`    BLOB               NULL,
  PRIMARY KEY (`Id`),
  INDEX `idx_temp_session` (`SessionId`),
  INDEX `idx_temp_session_file_spectrum` (`SessionId`, `FileIndex`, `SpectrumIndex`),
  CONSTRAINT `TempSpectrumMatch_MatchSpectrum_fk`
  FOREIGN KEY (`MatchSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)

