ALTER TABLE `adapcompounddb`.`Spectrum`
  ADD COLUMN `Significance` DOUBLE NULL DEFAULT NULL
  AFTER `RetentionTime`;

ALTER TABLE `adapcompounddb`.`SpectrumCluster`
  ADD COLUMN `AveSignificance` DOUBLE NULL DEFAULT NULL
  AFTER `Size`,
  ADD COLUMN `MinSignificance` DOUBLE NULL DEFAULT NULL
  AFTER `AveSignificance`,
  ADD COLUMN `MaxSignificance` DOUBLE NULL DEFAULT NULL
  AFTER `MinSignificance`;