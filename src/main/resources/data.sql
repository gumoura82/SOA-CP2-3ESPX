-- Usuário padrão para autenticação (senha: admin123)
INSERT INTO users (id, username, password, role) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN'),
(2, 'user', '$2a$10$ixlPY3AAd4ty1l6E2IsQ9OFZi2ba9ZQE0bP7RNcHTpk7rAGGfsnLe', 'USER');

-- Salas de reunião iniciais
INSERT INTO salas (id, nome, capacidade, localizacao) VALUES
(1, 'Sala Alfa', 10, 'Andar 1 - Ala Norte'),
(2, 'Sala Beta', 20, 'Andar 2 - Ala Sul'),
(3, 'Sala Gamma', 5, 'Andar 1 - Ala Sul'),
(4, 'Auditório Principal', 100, 'Térreo');
