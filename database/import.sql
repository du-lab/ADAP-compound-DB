LOAD DATA LOCAL INFILE '/Users/aleksandrsmirnov/Projects/du-lab/kdtree/data/matches_2020-06-01.filtered.csv' INTO TABLE SpectrumMatch FIELDS TERMINATED BY ',' IGNORE 1 LINES (QuerySpectrumId, MatchSpectrumId, Score);

-- You may need to execute the folloing lines first:
-- USE adapcompounddb;
-- SET GLOBAL local_infile=1;