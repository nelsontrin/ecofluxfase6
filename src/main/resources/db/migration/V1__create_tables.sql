CREATE TABLE tb_ponto_coleta (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR2(100) NOT NULL,
    endereco VARCHAR2(200) NOT NULL,
    capacidade_maxima NUMBER(10,2) NOT NULL,
    volume_atual NUMBER(10,2) NOT NULL,
    ativo NUMBER(1) NOT NULL
);

CREATE TABLE tb_descarte (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ponto_coleta_id NUMBER NOT NULL,
    tipo_residuo VARCHAR2(50) NOT NULL,
    volume NUMBER(10,2) NOT NULL,
    reciclavel NUMBER(1) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_descarte_ponto FOREIGN KEY (ponto_coleta_id) REFERENCES tb_ponto_coleta(id)
);

CREATE TABLE tb_coleta (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ponto_coleta_id NUMBER NOT NULL,
    empresa_responsavel VARCHAR2(100) NOT NULL,
    volume_coletado NUMBER(10,2) NOT NULL,
    destino_final VARCHAR2(100) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_coleta_ponto FOREIGN KEY (ponto_coleta_id) REFERENCES tb_ponto_coleta(id)
);

CREATE TABLE tb_alerta (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ponto_coleta_id NUMBER NOT NULL,
    mensagem VARCHAR2(255) NOT NULL,
    nivel VARCHAR2(20) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    resolvido NUMBER(1) NOT NULL,
    CONSTRAINT fk_alerta_ponto FOREIGN KEY (ponto_coleta_id) REFERENCES tb_ponto_coleta(id)
);

CREATE TABLE tb_usuario (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login VARCHAR2(100) NOT NULL UNIQUE,
    senha VARCHAR2(255) NOT NULL,
    role VARCHAR2(50) NOT NULL
);
