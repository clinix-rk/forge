INSERT INTO drug_dosages (dosage)
SELECT 'S-O-S'
WHERE NOT EXISTS (SELECT 1
                  FROM drug_dosages
                  WHERE dosage = 'S-O-S');
