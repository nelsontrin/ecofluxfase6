-- Senha do administrador: admin123
INSERT INTO tb_usuario (login, senha, role)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN'
);

-- Senha do operador: operador123
INSERT INTO tb_usuario (login, senha, role)
VALUES (
    'operador',
    '$2a$10$8K1p/a0dL1LXMIgoEDFrwOe7HwJVjXPJFSbJLTNdqjhJVLJkBbJOi',
    'USER'
);
