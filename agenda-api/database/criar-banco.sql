-- Executar como administrador no banco postgres, uma unica vez.
CREATE ROLE agenda LOGIN PASSWORD 'agenda_local';
CREATE DATABASE agenda OWNER agenda;
-- Depois conectar ao banco agenda como agenda e executar schema.sql.
