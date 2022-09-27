CREATE TABLE credit_card_operation (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    credit_card VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    value DOUBLE NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME NOT NULL
);