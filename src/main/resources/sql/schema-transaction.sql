CREATE TABLE transaction (

                             time_transaction CHARACTER VARYING NOT NULL,

                             from_bank VARCHAR(100),
                             from_account VARCHAR(30),

                             to_bank VARCHAR(100),
                             to_account VARCHAR(30),

                             amount_received DECIMAL(15, 2),
                             receiving_currency VARCHAR(10),

                             amount_paid DECIMAL(15, 2),
                             payment_currency VARCHAR(10),

                             payment_format VARCHAR(50)

);
