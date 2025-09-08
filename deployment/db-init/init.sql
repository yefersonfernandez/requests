-- =========================================
-- TABLAS
-- =========================================

CREATE TABLE loan_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_amount NUMERIC(15,2) NOT NULL,
    max_amount NUMERIC(15,2) NOT NULL,
    interest_rate DOUBLE PRECISION NOT NULL,
    automatic_validation BOOLEAN NOT NULL
);

CREATE TABLE loan_state (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE loans (
    loan_id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL,
    term INTEGER NOT NULL,
    email VARCHAR(255),
    id_loan_state BIGINT NOT NULL,
    id_loan_type BIGINT NOT NULL
);

-- =========================================
-- DATOS DE PRUEBA
-- =========================================

INSERT INTO loan_state (name, description) VALUES
('PENDING_REVIEW', 'Pending Review'),
('MANUAL_REVIEW', 'Manual Review'),
('APPROVED', 'Approved'),
('REJECTED', 'Rejected');

INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, automatic_validation) VALUES
('Personal Loan', 500.00, 5000.00, 5.5, TRUE),
('Car Loan', 1000.00, 20000.00, 4.2, FALSE);

