-- Schema base (PostgreSQL)

CREATE TABLE IF NOT EXISTS cliente (
                                       id      BIGSERIAL PRIMARY KEY,
                                       cpf     VARCHAR(11)  NOT NULL,
    email   VARCHAR(100) NOT NULL,
    nome    VARCHAR(100) NOT NULL
    );

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uk_cliente_cpf'
  ) THEN
ALTER TABLE cliente ADD CONSTRAINT uk_cliente_cpf UNIQUE (cpf);
END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_email ON cliente(email);

CREATE TABLE IF NOT EXISTS moto (
                                    id          BIGSERIAL PRIMARY KEY,
                                    chassi      VARCHAR(17)  NOT NULL,
    imagem_path VARCHAR(255),
    modelo      VARCHAR(30)  NOT NULL,
    placa       VARCHAR(7)   NOT NULL,
    status      VARCHAR(30)  NOT NULL
    );

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uk_moto_placa'
  ) THEN
ALTER TABLE moto ADD CONSTRAINT uk_moto_placa UNIQUE (placa);
END IF;
END $$;

CREATE TABLE IF NOT EXISTS registro_moto_patio (
                                                   id                  BIGSERIAL PRIMARY KEY,
                                                   data_hora_registro  TIMESTAMP(3) NOT NULL,
    setor               VARCHAR(30)  NOT NULL,
    tipo                VARCHAR(10)  NOT NULL,
    vaga                VARCHAR(3)   NOT NULL,
    moto_id             BIGINT REFERENCES moto(id)
    );

CREATE TABLE IF NOT EXISTS locacao (
                                       id                  BIGSERIAL PRIMARY KEY,
                                       condicao_devolucao  VARCHAR(255),
    condicao_entrega    VARCHAR(255),
    data_devolucao      TIMESTAMP(3),
    data_saida          TIMESTAMP(3) NOT NULL,
    qr_code             VARCHAR(200),
    cliente_id          BIGINT REFERENCES cliente(id),
    moto_id             BIGINT REFERENCES moto(id)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id     BIGSERIAL PRIMARY KEY,
                                     ativo  BOOLEAN      NOT NULL,
                                     email  VARCHAR(180) NOT NULL,
    nome   VARCHAR(120) NOT NULL,
    role   VARCHAR(30)  NOT NULL,
    senha  VARCHAR(255) NOT NULL
    );

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uk_users_email'
  ) THEN
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
END IF;
END $$;

CREATE TABLE IF NOT EXISTS token (
                                     id       BIGSERIAL PRIMARY KEY,
                                     ativo    BOOLEAN,
                                     expirado BOOLEAN,
                                     token    VARCHAR(255),
    user_id  BIGINT REFERENCES users(id)
    );
