CREATE TABLE TempTable AS SELECT SpectrumId, MAX(Intensity) AS MaxIntensity FROM Peak GROUP BY SpectrumIdTempTableTempTable;

CREATE INDEX PrimaryIndex ON TempTable (SpectrumId);

SET SQL_SAFE_UPDATES = 0;
UPDATE Peak set Peak.Intensity = (
    SELECT Peak.Intensity/TempTable.MaxIntensity FROM TempTable WHERE TempTable.SpectrumId = Peak.SpectrumId
);

UPDATE Spectrum SET OmegaFactor = (
    SELECT 1.0 / (SUM(Peak.Intensity) - 0.5) FROM Peak WHERE Peak.SpectrumId = Spectrum.Id
)