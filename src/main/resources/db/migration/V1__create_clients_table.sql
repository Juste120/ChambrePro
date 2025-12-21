CREATE TABLE IF NOT EXISTS clients (
                                       id BIGSERIAL PRIMARY KEY,
                                       nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN'))
    );

-- Index sur email pour recherches rapides
CREATE UNIQUE INDEX IF NOT EXISTS idx_client_email ON clients(email);

-- Commentaires
COMMENT ON TABLE clients IS 'Table des clients utilisateurs du système';
COMMENT ON COLUMN clients.mot_de_passe IS 'Mot de passe hashé avec BCrypt';
COMMENT ON COLUMN clients.role IS 'Rôle de l''utilisateur : USER ou ADMIN';
