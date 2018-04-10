-- Example --
USE adapcompounddb;

SELECT SpectrumId, SUM(Score) AS Score FROM (
	SELECT SpectrumId, Intensity * 100 AS Score FROM Peak WHERE ABS(Peak.mz - 100) < 0.1
	UNION
    SELECT SpectrumId, Intensity * 200 As Score FROM Peak WHERE ABS(Peak.mz - 110) < 0.1
) AS Result
GROUP BY SpectrumId
ORDER BY Score DESC
LIMIT 10;