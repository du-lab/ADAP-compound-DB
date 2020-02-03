SELECT *
FROM (
         SELECT SpectrumCluster.Id, avg(Spectrum.Significance) as Average, count(*) as Count
         FROM SpectrumCluster
                  JOIN Spectrum ON Spectrum.ClusterId = SpectrumCluster.Id
                  JOIN File ON Spectrum.FileId = File.Id
                  JOIN Submission ON File.SubmissionId = Submission.Id
                  JOIN SubmissionTag ON SubmissionTag.SubmissionId = Submission.Id
         WHERE SubmissionTag.Name = "species (common) : mouse"
           AND SpectrumCluster.Size > 1
           AND Spectrum.Significance IS NOT NULL
         GROUP BY SpectrumCluster.Id) AS RESULTS
WHERE Count > 1
ORDER BY Average;