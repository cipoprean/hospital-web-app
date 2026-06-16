CREATE TABLE notification
(
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subject         VARCHAR(255) NOT NULL,
    recipient       VARCHAR(255) NOT NULL,
    message         TEXT,
    type            VARCHAR(50)  NOT NULL,

    user_id         UUID         NOT NULL UNIQUE,

    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES app_user (user_id)
                ON DELETE CASCADE
);