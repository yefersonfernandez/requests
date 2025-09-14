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


-- =========================================
-- TIPOS DE PRÉSTAMOS
-- =========================================
INSERT INTO loan_type (name, min_amount, max_amount, interest_rate, automatic_validation) VALUES
('Personal Loan', 500000.00, 5000000.00, 0.05, TRUE),
('Car Loan', 1000000.00, 20000000.00, 0.02, FALSE);


-- =========================================
-- PRÉSTAMOS PREVIOS ACTIVOS
-- =========================================
-- Cliente APROBADO: debe ~10% del sueldo (~300.000)
INSERT INTO loans (amount, term, email, id_loan_state, id_loan_type) VALUES
(150000.00, 6, 'centenohijomio@gmail.com', 3, 1),
(150000.00, 6, 'centenohijomio@gmail.com', 3, 1);

-- Cliente RECHAZADO: debe ~25% del sueldo (~500.000)
INSERT INTO loans (amount, term, email, id_loan_state, id_loan_type) VALUES
(300000.00, 12, 'pruebalambda1@gmail.com', 3, 1),
(200000.00, 6, 'pruebalambda1@gmail.com', 3, 1);
