CREATE PROCEDURE `getUniqueProviders` ()
NOT DETERMINISTIC
CONTAINS SQL
BEGIN
SELECT DISTINCT ctsadata.icd_full.npi FROM ctsadata.icd_full;
END