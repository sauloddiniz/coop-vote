ALTER TABLE pauta ADD aberta NUMBER(1, 0) DEFAULT 0 NOT NULL;
ALTER TABLE pauta ADD CONSTRAINT check_pauta_aberta CHECK (aberta IN (0, 1));