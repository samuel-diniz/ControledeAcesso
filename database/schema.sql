-- =============================================================
-- Schema: Sistema de Controle de Acesso com QR Code
-- =============================================================

CREATE TABLE admin (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE evento (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(200) NOT NULL,
    descricao   TEXT,
    data        TIMESTAMP NOT NULL,
    local       VARCHAR(200),
    capacidade  INTEGER NOT NULL CHECK (capacidade > 0),
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE participante (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(200) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    telefone    VARCHAR(20),
    criado_em   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ingresso (
    id              BIGSERIAL PRIMARY KEY,
    evento_id       BIGINT NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    participante_id BIGINT NOT NULL REFERENCES participante(id) ON DELETE CASCADE,
    token           UUID NOT NULL UNIQUE,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
                        CHECK (status IN ('PENDENTE', 'USADO')),
    criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE leitura (
    id          BIGSERIAL PRIMARY KEY,
    ingresso_id BIGINT REFERENCES ingresso(id) ON DELETE SET NULL,
    token_lido  VARCHAR(36) NOT NULL,
    resultado   VARCHAR(20) NOT NULL
                    CHECK (resultado IN ('VALIDO', 'INVALIDO', 'JA_USADO', 'LOTADO')),
    lido_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    dispositivo VARCHAR(100)
);

-- Índices para performance
CREATE INDEX idx_ingresso_token       ON ingresso(token);
CREATE INDEX idx_ingresso_evento      ON ingresso(evento_id);
CREATE INDEX idx_ingresso_status      ON ingresso(evento_id, status);
CREATE INDEX idx_leitura_ingresso     ON leitura(ingresso_id);

-- Admin padrão (senha: admin123 — trocar em produção)
INSERT INTO admin (username, password_hash)
VALUES ('admin', '$2a$10$7sJkR9UmB1Q5yL8vX3fUxOqN2pW0cD4hE6mZ9tK1bI3nL5gP7qS');
