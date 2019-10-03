USE adapcompounddb;
SET SQL_SAFE_UPDATES = 0;
UPDATE Submission SET MassSpectrometryType = (
    SELECT IF(MIN(Spectrum.IntegerMz)=1, "LOW_RESOLUTION", "HIGH_RESOLUTION") FROM File JOIN Spectrum ON File.Id=Spectrum.FileId
    WHERE Submission.Id=File.SubmissionId
    GROUP BY Submission.Id
);