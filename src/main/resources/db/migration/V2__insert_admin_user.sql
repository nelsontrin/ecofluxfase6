-- Inserção do usuário administrador padrão
-- Senha: admin123 (BCrypt hash)
INSERT INTO tb_usuario (login, senha, role)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);

-- Inserção de usuário operador padrão
-- Senha: operador123 (BCrypt hash)
INSERT INTO tb_usuario (login, senha, role)
VALUES (
    'operador',
    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOe7HwJVjXPJFSbJLTNdqjhJVLJkBbJOi',
    'USER'
);
