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

CREATE TABLE medicines
(
    id          BIGSERIAL PRIMARY KEY,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    instruction VARCHAR(255) NOT NULL,
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

-- Patients (references doctors)
CREATE TABLE patients
(
    id            BIGSERIAL PRIMARY KEY,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    case_no       VARCHAR(255) NOT NULL UNIQUE,
    serial        INTEGER      NOT NULL,
    name          VARCHAR(30)  NOT NULL,
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

-- Phone numbers
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

-- Files (one-to-one with patient)
CREATE TABLE files
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    patient_id BIGINT       NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL UNIQUE,
    location   VARCHAR(512) NOT NULL UNIQUE
);

-- Treatments
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

-- Payments (one-to-one with treatment)
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

-- Complains
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

-- Suggestions
CREATE TABLE suggestions
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    date       DATE         NOT NULL,
    category   VARCHAR(100) NOT NULL,
    details    TEXT,
    cost       INTEGER      NOT NULL,
    status     VARCHAR(50)  NOT NULL DEFAULT 'SUGGESTED',
    patient_id BIGINT       NOT NULL
);

-- Prescriptions
CREATE TABLE prescriptions
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    patient_id BIGINT      NOT NULL,
    date       DATE        NOT NULL,
    details    TEXT        NOT NULL
);

-- Prescription medicines (links prescriptions, medicines, dosages)
CREATE TABLE prescription_medicines
(
    id              BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    prescription_id BIGINT      NOT NULL,
    medicine_id     BIGINT      NOT NULL,
    dosage_id       BIGINT      NOT NULL,
    quantity        INTEGER     NOT NULL
);

CREATE INDEX idx_presc_med ON prescription_medicines (prescription_id, medicine_id);

-- Join tables for many-to-many relations
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
    ADD CONSTRAINT fk_suggestions_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE prescriptions
    ADD CONSTRAINT fk_prescriptions_patient FOREIGN KEY (patient_id) REFERENCES patients (id);

ALTER TABLE prescription_medicines
    ADD CONSTRAINT fk_prescmed_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id),
    ADD CONSTRAINT fk_prescmed_medicine FOREIGN KEY (medicine_id) REFERENCES medicines (id),
    ADD CONSTRAINT fk_prescmed_dosage FOREIGN KEY (dosage_id) REFERENCES drug_dosages (id);

ALTER TABLE patient_medical_conditions
    ADD CONSTRAINT fk_pmc_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    ADD CONSTRAINT fk_pmc_condition FOREIGN KEY (condition_id) REFERENCES medical_conditions (id);

ALTER TABLE patient_drug_allergies
    ADD CONSTRAINT fk_pda_patient FOREIGN KEY (patient_id) REFERENCES patients (id),
    ADD CONSTRAINT fk_pda_allergy FOREIGN KEY (allergy_id) REFERENCES drug_allergies (id);
