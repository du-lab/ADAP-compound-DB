ALTER TABLE `adapcompounddb`.`Peak`
  ADD INDEX `Peak_SpectrumId_Mz_Index` (`SpectrumId` ASC, `Mz` ASC);
;

ALTER TABLE `adapcompounddb`.`Spectrum`
  ADD INDEX `Spectrum_CPR_index` (`ChromatographyType` ASC, `Precursor` ASC, `RetentionTime` ASC);
;