CREATE TABLE doctor
(
    doctor_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name     VARCHAR(255) NOT NULL,
    last_name      VARCHAR(255) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(100) NOT NULL,

    user_id        UUID NOT NULL UNIQUE,

    created_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),

    CONSTRAINT fk_doctor_user
        FOREIGN KEY (user_id)
            REFERENCES app_user (user_id)
                ON DELETE CASCADE
);