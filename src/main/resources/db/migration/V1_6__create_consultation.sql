CREATE TABLE CONSULTATION (
    consultation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    consultation_date TIMESTAMP WITHOUT TIME ZONE,

    objective_findings TEXT,
    subjective_notes TEXT,
    assesments TEXT,
    plan TEXT,

    appointment_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_consultation_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_consultation_appointment ON consultation(appointment_id);