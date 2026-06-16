CREATE TABLE appointment (
    appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    meeting_link VARCHAR(512),
    purpose_of_consultation TEXT,
    initial_symptoms TEXT,
    appointment_status VARCHAR(50) NOT NULL,
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_appointment_doctor
        FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),

    CONSTRAINT fk_appointment_patient
        FOREIGN KEY (patient_id) REFERENCES patient(patient_id)
);

CREATE INDEX idx_appointment_doctor ON appointment(doctor_id);
CREATE INDEX idx_appointment_patient ON appointment(patient_id);