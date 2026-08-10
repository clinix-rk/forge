BEGIN;

-- Doctors Data
INSERT INTO doctors (name, case_no_prefix, total_patients) VALUES
('Dr. Yogesh Trivedi', 'Y', 0),
('Dr. Mehula Trivedi', 'M', 0),
('Dr. Vaidehi Trivedi', 'V', 0);

-- Medical Conditions Data
INSERT INTO medical_conditions (name) VALUES
('Blood Thinner'),
('Diabetes'),
('BP'),
('Heart Condition'),
('Kidney Disease'),
('Liver Disease'),
('Asthama'),
('Sinus Issue'),
('Epilepsy'),
('Thyroid'),
('History of Operations');

-- Drug Dosages Data
INSERT INTO drug_dosages (dosage) VALUES
('1-1-1'),
('1-0-1'),
('2-1-1'),
('1 HS');

-- Medicines Data
INSERT INTO medicines (name, type, instruction) VALUES
('Tab Dan-P', '', 'Before Meal'),
('Tab Diclogesic', '', 'Before Meal'),
('Tab Enzoflam', '', 'Before Meal'),
('Tab Keterol-DT 50 mg', '', 'Before Meal'),
('Tab Contramal DT 50 mg', '', 'Before Meal'),
('Tab Ultracet', '', 'Before Meal'),
('Tab Dolo-650', '', 'Before Meal'),
('Syrup Combiflam Syrup', '', 'Before Meal'),
('Syrup Diclogesic Syrup', '', 'Before Meal'),
('Tab Augmentin 625 mg', '', 'Before Meal'),
('Tab Zifi CV 200 mg', '', 'Before Meal'),
('Tab Metrogyl 400 mg', '', 'Before Meal'),
('Tab Merogyl 200 mg', '', 'Before Meal'),
('Tab Droxyl 500 mg', '', 'Before Meal'),
('Tab Droxyl 250 mg', '', 'Before Meal'),
('Cap PAN-D', '', 'Before Meal'),
('Tab Veloz-D', '', 'Before Meal'),
('Tab Rantac 150 mg', '', 'Before Meal'),
('Tab Spolac -DS 120 M', '', 'Before Meal'),
('Dologel CT', '', 'Before Meal'),
('Metrogyl 1% gel', '', 'Before Meal'),
('Metrogyl 2% Gel', '', 'Before Meal'),
('Conacort Paste', '', 'Before Meal'),
('Hexigel 1%', '', 'Before Meal'),
('Paste Thermoseal RA', '', 'Before Meal'),
('Sensofoam Gum Paint', '', 'Before Meal'),
('Stolin Gum Paint', '', 'Before Meal'),
('Enaflix Toothpaste', '', 'Before Meal'),
('Sensodyne Paste', '', 'Before Meal'),
('Mouthwash Betadine 2% Gargle Mint', '', 'Before Meal'),
('Hexidine Mouthwash', '', 'Before Meal'),
('Colgate maxFresh Plax antibactarial Mouthwash', '', 'Before Meal');

-- Complain Categories (Hierarchical Subquery Structure)
INSERT INTO complain_categories (name, parent_id) VALUES ('Pain', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Swelling', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Foul Smell', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Tarter', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Clicking sound in TMJ', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Irregular teeth', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Caries', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Missing Tooth', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Mobility in Teeth', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Fractured Teeth', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Trauma', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Bleeding Gums', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Asthetic Complain', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('General Check up', NULL);
INSERT INTO complain_categories (name, parent_id) VALUES ('Others', NULL);

-- Pain Subcategories
INSERT INTO complain_categories (name, parent_id) VALUES ('Continues', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Intermittent', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Dull', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Sharp', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Nocturnal', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Radiating', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Reffered', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Localized', (SELECT id FROM complain_categories WHERE name = 'Pain' AND parent_id IS NULL));

-- Reffered Sub-subcategories
INSERT INTO complain_categories (name, parent_id) VALUES ('Ear', (SELECT id FROM complain_categories WHERE name = 'Reffered'));
INSERT INTO complain_categories (name, parent_id) VALUES ('TMJ', (SELECT id FROM complain_categories WHERE name = 'Reffered'));
INSERT INTO complain_categories (name, parent_id) VALUES ('Maxillary Sinus', (SELECT id FROM complain_categories WHERE name = 'Reffered'));
INSERT INTO complain_categories (name, parent_id) VALUES ('Temporal Region', (SELECT id FROM complain_categories WHERE name = 'Reffered'));
INSERT INTO complain_categories (name, parent_id) VALUES ('Neck', (SELECT id FROM complain_categories WHERE name = 'Reffered'));
INSERT INTO complain_categories (name, parent_id) VALUES ('Other Arch', (SELECT id FROM complain_categories WHERE name = 'Reffered'));

-- Swelling Subcategories
INSERT INTO complain_categories (name, parent_id) VALUES ('Generalized', (SELECT id FROM complain_categories WHERE name = 'Swelling' AND parent_id IS NULL));

-- Tarter Subcategories
INSERT INTO complain_categories (name, parent_id) VALUES ('Unilateral', (SELECT id FROM complain_categories WHERE name = 'Tarter' AND parent_id IS NULL));
INSERT INTO complain_categories (name, parent_id) VALUES ('Bilateral', (SELECT id FROM complain_categories WHERE name = 'Tarter' AND parent_id IS NULL));

-- Treatment Categories (Hierarchical Subquery Structure)
INSERT INTO treatment_categories (name, parent_id) VALUES ('RCT', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('PROSTHESIS OF RCT', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('SC', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('DEEP SC', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('FILLING', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('FD', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('PD', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('XRAY', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('SCAN', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('CO', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('FOLLOW UP', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('EXTRACTION', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('GINGIVECTOMY', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('IMPLANT', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('ORTHODONTIA', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('NIGHT GUARD', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('PEDO. TREATMENT', NULL);
INSERT INTO treatment_categories (name, parent_id) VALUES ('Others', NULL);

-- RCT Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('RCO', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('BMP', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('RCD', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('RCF', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PRCF', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CAST CORE', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('TEMP. CAP', (SELECT id FROM treatment_categories WHERE name = 'RCT' AND parent_id IS NULL));

-- PROSTHESIS OF RCT Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('NI-CR FCC', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PFM FCC', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('FZ FCC', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PFZ FCC', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PFZ+FCC', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('LAVA', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('GOLD', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS OF RCT' AND parent_id IS NULL));

-- FILLING Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 1', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 2', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 3', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 4', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 5', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CLASS 6', (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL));

-- FILLING Substeps (MO, DO for CLASS 2 and CLASS 3)
INSERT INTO treatment_categories (name, parent_id) VALUES ('MO', (SELECT id FROM treatment_categories WHERE name = 'CLASS 2' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL)));
INSERT INTO treatment_categories (name, parent_id) VALUES ('DO', (SELECT id FROM treatment_categories WHERE name = 'CLASS 2' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL)));
INSERT INTO treatment_categories (name, parent_id) VALUES ('MO', (SELECT id FROM treatment_categories WHERE name = 'CLASS 3' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL)));
INSERT INTO treatment_categories (name, parent_id) VALUES ('DO', (SELECT id FROM treatment_categories WHERE name = 'CLASS 3' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'FILLING' AND parent_id IS NULL)));

-- FD Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('PI', (SELECT id FROM treatment_categories WHERE name = 'FD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('FI', (SELECT id FROM treatment_categories WHERE name = 'FD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('MR', (SELECT id FROM treatment_categories WHERE name = 'FD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('DD', (SELECT id FROM treatment_categories WHERE name = 'FD' AND parent_id IS NULL));

-- PD Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('PI', (SELECT id FROM treatment_categories WHERE name = 'PD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('FI', (SELECT id FROM treatment_categories WHERE name = 'PD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('MR', (SELECT id FROM treatment_categories WHERE name = 'PD' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('DD', (SELECT id FROM treatment_categories WHERE name = 'PD' AND parent_id IS NULL));

-- XRAY Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('IOPA', (SELECT id FROM treatment_categories WHERE name = 'XRAY' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('OPG', (SELECT id FROM treatment_categories WHERE name = 'XRAY' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('CBCT', (SELECT id FROM treatment_categories WHERE name = 'XRAY' AND parent_id IS NULL));

-- EXTRACTION Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('EXTRACTION', (SELECT id FROM treatment_categories WHERE name = 'EXTRACTION' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('DISIMPACTION', (SELECT id FROM treatment_categories WHERE name = 'EXTRACTION' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('SR', (SELECT id FROM treatment_categories WHERE name = 'EXTRACTION' AND parent_id IS NULL));

-- IMPLANT Steps
INSERT INTO treatment_categories (name, parent_id) VALUES ('DSL', (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('ISL', (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('NORMAL', (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PROSTHESIS', (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL));

-- IMPLANT PROSTHESIS Substeps
INSERT INTO treatment_categories (name, parent_id) VALUES ('TEMPORARY', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL)));
INSERT INTO treatment_categories (name, parent_id) VALUES ('PERMENENT', (SELECT id FROM treatment_categories WHERE name = 'PROSTHESIS' AND parent_id = (SELECT id FROM treatment_categories WHERE name = 'IMPLANT' AND parent_id IS NULL)));

COMMIT;
