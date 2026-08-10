CREATE TABLE doctors
(
    id             BIGSERIAL PRIMARY KEY,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    case_no_prefix VARCHAR(1)   NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    total_patients INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    username   VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL
);

CREATE TABLE drug_allergies
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE medical_conditions
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    name       VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE drug_dosages
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    dosage     VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE medicine_instruction
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    instruction VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE medicines
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    CONSTRAINT uq_medicine_name_type UNIQUE (name, type)
);

CREATE TABLE complain_categories
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    name       VARCHAR(100) NOT NULL,
    parent_id  BIGINT,
    CONSTRAINT uq_complain_category_name_parent UNIQUE (name, parent_id)
);

CREATE TABLE treatment_categories
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    name       VARCHAR(100) NOT NULL,
    parent_id  BIGINT,
    CONSTRAINT uq_treatment_category_name_parent UNIQUE (name, parent_id)
);

CREATE TABLE patients
(
    id            BIGSERIAL PRIMARY KEY,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    case_no       VARCHAR(255) NOT NULL UNIQUE,
    serial        INTEGER      NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    date_of_birth DATE,
    gender        VARCHAR(20),
    email         VARCHAR(100),
    address       TEXT,
    city          VARCHAR(50),
    pincode       VARCHAR(10),
    doctor_id     BIGINT       NOT NULL,
    referred_by   VARCHAR(50),
    CONSTRAINT uq_doctor_serial UNIQUE (doctor_id, serial)
);

CREATE TABLE phone_numbers
(
    id           BIGSERIAL PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    phone_number VARCHAR(20)  NOT NULL,
    type         VARCHAR(255) NOT NULL,
    patient_id   BIGINT       NOT NULL,
    CONSTRAINT uq_patient_phone_type UNIQUE (patient_id, type)
);

CREATE TABLE files
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    patient_id BIGINT       NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL UNIQUE,
    location   VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE treatments
(
    id                    BIGSERIAL PRIMARY KEY,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    details               TEXT,
    date                  DATE        NOT NULL,
    treatment_category_id BIGINT      NOT NULL,
    patient_id            BIGINT      NOT NULL
);

CREATE TABLE payments
(
    id                        BIGSERIAL PRIMARY KEY,
    created_at                TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    patient_id                BIGINT           NOT NULL,
    doctor_identity_character VARCHAR(1)       NOT NULL,
    financial_year            VARCHAR(50)      NOT NULL,
    serial                    INTEGER          NOT NULL,
    treatment_id              BIGINT           NOT NULL UNIQUE,
    amount                    DOUBLE PRECISION NOT NULL,
    method                    VARCHAR(50)      NOT NULL,
    reference                 VARCHAR(255)     NOT NULL DEFAULT ''
);

CREATE INDEX idx_payment_treatment ON payments (treatment_id);

CREATE TABLE complains
(
    id                   BIGSERIAL PRIMARY KEY,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    date                 DATE        NOT NULL,
    details              TEXT,
    complain_category_id BIGINT      NOT NULL,
    patient_id           BIGINT      NOT NULL
);

CREATE TABLE suggestions
(
    id                     BIGSERIAL PRIMARY KEY,
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    date                   DATE         NOT NULL,
    suggestion_category_id BIGINT       NOT NULL,
    details                TEXT,
    cost                   INTEGER      NOT NULL,
    status                 VARCHAR(50)  NOT NULL DEFAULT 'SUGGESTED',
    patient_id             BIGINT       NOT NULL
);

CREATE TABLE prescriptions
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    patient_id BIGINT      NOT NULL,
    date       DATE        NOT NULL,
    details    TEXT        NOT NULL
);

CREATE TABLE prescription_medicines
(
    id              BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    prescription_id BIGINT      NOT NULL,
    medicine_id     BIGINT      NOT NULL,
    dosage_id       BIGINT      NOT NULL,
    instruction_id  BIGINT,
    quantity        INTEGER     NOT NULL
);

CREATE INDEX idx_presc_med ON prescription_medicines (prescription_id, medicine_id);

CREATE TABLE patient_medical_conditions
(
    patient_id   BIGINT NOT NULL,
    condition_id BIGINT NOT NULL,
    PRIMARY KEY (patient_id, condition_id)
);

CREATE TABLE patient_drug_allergies
(
    patient_id BIGINT NOT NULL,
    allergy_id BIGINT NOT NULL,
    PRIMARY KEY (patient_id, allergy_id)
);

-- Foreign key constraints

ALTER TABLE patients
    ADD CONSTRAINT fk_patients_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id);

ALTER TABLE complain_categories
    ADD CONSTRAINT fk_complain_categories_parent FOREIGN KEY (parent_id) REFERENCES complain_categories (id);

ALTER TABLE treatment_categories
    ADD CONSTRAINT fk_treatment_categories_parent FOREIGN KEY (parent_id) REFERENCES treatment_categories (id);

ALTER TABLE phone_numbers
    ADD CONSTRAINT fk_phone_numbers_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE files
    ADD CONSTRAINT fk_files_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE treatments
    ADD CONSTRAINT fk_treatments_category FOREIGN KEY (treatment_category_id) REFERENCES treatment_categories (id),
    ADD CONSTRAINT fk_treatments_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    ADD CONSTRAINT fk_payments_treatment FOREIGN KEY (treatment_id) REFERENCES treatments (id);

ALTER TABLE complains
    ADD CONSTRAINT fk_complains_category FOREIGN KEY (complain_category_id) REFERENCES complain_categories (id),
    ADD CONSTRAINT fk_complains_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE suggestions
    ADD CONSTRAINT fk_suggestions_category FOREIGN KEY (suggestion_category_id) REFERENCES treatment_categories (id),
    ADD CONSTRAINT fk_suggestions_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE prescriptions
    ADD CONSTRAINT fk_prescriptions_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE prescription_medicines
    ADD CONSTRAINT fk_prescmed_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id),
    ADD CONSTRAINT fk_prescmed_medicine FOREIGN KEY (medicine_id) REFERENCES medicines (id),
    ADD CONSTRAINT fk_prescmed_dosage FOREIGN KEY (dosage_id) REFERENCES drug_dosages (id),
    ADD CONSTRAINT fk_prescmed_instruction FOREIGN KEY (instruction_id) REFERENCES medicine_instruction (id);

ALTER TABLE patient_medical_conditions
    ADD CONSTRAINT fk_pmc_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    ADD CONSTRAINT fk_pmc_condition FOREIGN KEY (condition_id) REFERENCES medical_conditions (id);

ALTER TABLE patient_drug_allergies
    ADD CONSTRAINT fk_pda_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    ADD CONSTRAINT fk_pda_allergy FOREIGN KEY (allergy_id) REFERENCES drug_allergies (id);

-- Seed Data

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

-- Medicine Instructions Data
INSERT INTO medicine_instruction (instruction) VALUES
('Before Meal'),
('After Meal');

-- Medicines Data
INSERT INTO medicines (name, type) VALUES
('Tab Dan-P', ''),
('Tab Diclogesic', ''),
('Tab Enzoflam', ''),
('Tab Keterol-DT 50 mg', ''),
('Tab Contramal DT 50 mg', ''),
('Tab Ultracet', ''),
('Tab Dolo-650', ''),
('Syrup Combiflam Syrup', ''),
('Syrup Diclogesic Syrup', ''),
('Tab Augmentin 625 mg', ''),
('Tab Zifi CV 200 mg', ''),
('Tab Metrogyl 400 mg', ''),
('Tab Merogyl 200 mg', ''),
('Tab Droxyl 500 mg', ''),
('Tab Droxyl 250 mg', ''),
('Cap PAN-D', ''),
('Tab Veloz-D', ''),
('Tab Rantac 150 mg', ''),
('Tab Spolac -DS 120 M', ''),
('Dologel CT', ''),
('Metrogyl 1% gel', ''),
('Metrogyl 2% Gel', ''),
('Conacort Paste', ''),
('Hexigel 1%', ''),
('Paste Thermoseal RA', ''),
('Sensofoam Gum Paint', ''),
('Stolin Gum Paint', ''),
('Enaflix Toothpaste', ''),
('Sensodyne Paste', ''),
('Mouthwash Betadine 2% Gargle Mint', ''),
('Hexidine Mouthwash', ''),
('Colgate maxFresh Plax antibactarial Mouthwash', '');

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
