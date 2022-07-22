SET SQL_SAFE_UPDATES = 0;
UPDATE File SET Size = (SELECT Count(*) FROM Spectrum WHERE Spectrum.FileId = File.Id);
UPDATE Submission SET Size = (SELECT Count(*) FROM File JOIN Spectrum ON File.Id = Spectrum.FileId WHERE File.SubmissionId = Submission.Id);
SET SQL_SAFE_UPDATES = 1;