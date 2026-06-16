CREATE TABLE patient (

    patient_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,

    known_allergies TEXT,
    blood_group VARCHAR(10) NOT NULL,
    genotype VARCHAR(10) NOT NULL,

    user_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_patient_user
        FOREIGN KEY (user_id) REFERENCES app_user(user_id)

);