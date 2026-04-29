CREATE TABLE app_user
(
    user_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255) NOT NULL,
    password            VARCHAR(255) NOT NULL,
    email               VARCHAR(255) UNIQUE,
    profile_picture_url VARCHAR(255)
);

CREATE TABLE role
(
    role_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. Create the Join Table for the Many-to-Many relationship
CREATE TABLE user_role
(
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role (role_id) ON DELETE CASCADE
);

CREATE TABLE password_reset
(
    password_reset_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                   VARCHAR(255) NOT NULL UNIQUE,
    user_id                UUID         NOT NULL,
    expiry_date            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    used                   BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES app_user (user_id) ON DELETE CASCADE,
    CONSTRAINT uq_password_reset_user UNIQUE (user_id)
);