CREATE TABLE credit_card_invoice
(
    id          VARCHAR(36) PRIMARY KEY NOT NULL,
    credit_card VARCHAR(36)             NOT NULL,
    month       INT                     NOT NULL,
    year        INT                     NOT NULL,
    value       DOUBLE                  NOT NULL,
    created_at  DATETIME                NOT NULL,
    paid_at     DATETIME
);