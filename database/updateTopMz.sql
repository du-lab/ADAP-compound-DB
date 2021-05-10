SET SQL_SAFE_UPDATES = 0;
SET innodb_lock_wait_timeout = 1073741824;

UPDATE Spectrum
SET 
Spectrum.TopMz1 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 0),
Spectrum.TopMz2 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 1),
Spectrum.TopMz3 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 2),
Spectrum.TopMz4 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 3),
Spectrum.TopMz5 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 4),
Spectrum.TopMz6 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 5),
Spectrum.TopMz7 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 6),
Spectrum.TopMz8 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 7),
Spectrum.TopMz9 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 8),
Spectrum.TopMz10 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 9),
Spectrum.TopMz11 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 10),
Spectrum.TopMz12 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 11),
Spectrum.TopMz13 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 12),
Spectrum.TopMz14 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 13),
Spectrum.TopMz15 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 14),
Spectrum.TopMz16 = (SELECT Peak.Mz FROM adapcompounddb.Peak where Peak.SpectrumId = Spectrum.Id order by Peak.Intensity desc limit 1 offset 15);