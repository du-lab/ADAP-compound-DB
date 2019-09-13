USE adapcompounddb;
SET SQL_SAFE_UPDATES = 0;
UPDATE Spectrum
SET IntegerMz = (SELECT MIN(ceil(Peak.Mz)=Peak.Mz) > 0 FROM Peak WHERE Peak.SpectrumId = Spectrum.Id);