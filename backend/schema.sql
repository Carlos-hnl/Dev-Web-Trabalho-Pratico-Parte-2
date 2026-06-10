-- Criando o banco
-- CREATE DATABASE taskmanager;

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id         SERIAL PRIMARY KEY,
    nome       VARCHAR(100)  NOT NULL,
    email      VARCHAR(150)  UNIQUE NOT NULL,
    senha      VARCHAR(255)  NOT NULL,       -- hash BCrypt
    criado_em  TIMESTAMP     DEFAULT NOW()
);

-- Tabela de Tarefas
CREATE TABLE IF NOT EXISTS tarefas (
    id          SERIAL PRIMARY KEY,
    usuario_id  INT           NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    titulo      VARCHAR(200)  NOT NULL,
    descricao   TEXT,
    concluida   BOOLEAN       DEFAULT FALSE,
    criado_em   TIMESTAMP     DEFAULT NOW(),
    atualizado_em TIMESTAMP   DEFAULT NOW()
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_tarefas_usuario_id ON tarefas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_usuarios_email     ON usuarios(email);

-- Função para atualizar atualizado_em automaticamente
CREATE OR REPLACE FUNCTION atualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_tarefas_atualizado
BEFORE UPDATE ON tarefas
FOR EACH ROW EXECUTE FUNCTION atualizar_timestamp();
