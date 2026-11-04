CREATE DATABASE IF NOT EXISTS bank
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bank;

-- Encja Klient -> tabela klienci
CREATE TABLE IF NOT EXISTS klienci (
    id_klienta     BIGINT NOT NULL AUTO_INCREMENT,
    numer_klienta  VARCHAR(10) NOT NULL,
    imie           VARCHAR(255) NOT NULL,
    nazwisko       VARCHAR(255) NOT NULL,
    mail           VARCHAR(255) NOT NULL,
    tel            VARCHAR(255) NOT NULL,
    haslo_hash     VARCHAR(150) NOT NULL,
    pesel          VARCHAR(255) NOT NULL,

    PRIMARY KEY (id_klienta),
    UNIQUE KEY uk_klienci_numer (numer_klienta),
    UNIQUE KEY uk_klienci_mail (mail),
    UNIQUE KEY uk_klienci_tel (tel),
    UNIQUE KEY uk_klienci_pesel (pesel)
) ENGINE=InnoDB;

-- Encja Konto -> tabela konta
CREATE TABLE IF NOT EXISTS konta (
    id_konta       BIGINT NOT NULL AUTO_INCREMENT,
    numer_konta    VARCHAR(255) NULL,
    saldo          DECIMAL(19,2) NULL DEFAULT 0.00,
    id_klienta     BIGINT NULL,
    typ_konta      VARCHAR(255) NULL,

    PRIMARY KEY (id_konta),
    UNIQUE KEY uk_konta_numer (numer_konta),
    KEY idx_konta_klient (id_klienta),
    CONSTRAINT fk_konta_klienci FOREIGN KEY (id_klienta)
        REFERENCES klienci (id_klienta)
) ENGINE=InnoDB;

-- Encja Tranzakcja -> tabela tranzakcje (pisownia zgodna z kodem Java)
CREATE TABLE IF NOT EXISTS tranzakcje (
    id_tranzakcji      BIGINT NOT NULL AUTO_INCREMENT,
    id_konta_nadawcy   BIGINT NOT NULL,
    id_konta_odbiorcy  BIGINT NOT NULL,
    czas_tranzakcji    DATETIME(6) NULL,
    kwota              DECIMAL(19,2) NULL,

    PRIMARY KEY (id_tranzakcji),
    KEY idx_tranzakcje_nadawca (id_konta_nadawcy),
    KEY idx_tranzakcje_odbiorca (id_konta_odbiorcy),
    CONSTRAINT fk_tranzakcje_nadawca FOREIGN KEY (id_konta_nadawcy)
        REFERENCES konta (id_konta),
    CONSTRAINT fk_tranzakcje_odbiorca FOREIGN KEY (id_konta_odbiorcy)
        REFERENCES konta (id_konta)
) ENGINE=InnoDB;
