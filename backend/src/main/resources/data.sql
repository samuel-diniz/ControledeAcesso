-- Remove constraints legadas que conflitam com os status atuais do sistema.
-- Executado automaticamente pelo Spring Boot a cada inicialização (IF EXISTS é seguro).
ALTER TABLE IF EXISTS leitura  DROP CONSTRAINT IF EXISTS leitura_resultado_check;
ALTER TABLE IF EXISTS ingresso DROP CONSTRAINT IF EXISTS ingresso_status_check;
