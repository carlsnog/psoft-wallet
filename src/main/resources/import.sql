-- Clientes
INSERT INTO cliente (id, codigo_acesso, nome, plano, endereco) VALUES (1, '123456', 'Gustavo', 'NORMAL', 'Rua dos bobos, 0');
INSERT INTO cliente (id, codigo_acesso, nome, plano, endereco) VALUES (2, '654321', 'Nivea', 'PREMIUM', 'Rua dos Testes, 456');
INSERT INTO cliente (id, codigo_acesso, nome, plano, endereco) VALUES (3, '111111', 'Ewerton', 'PREMIUM', 'Av. Principal, 789');
INSERT INTO cliente (id, codigo_acesso, nome, plano, endereco) VALUES (4, '222222', 'Carlos', 'PREMIUM', 'Rua das Flores, 321');

-- Ativos (usando herança SINGLE_TABLE com dtype)
-- Ações
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Acao', 1, 'PETR4', 'Petrobras PN', 'DISPONIVEL', 30.50, 'ACAO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Acao', 2, 'VALE3', 'Vale S.A.', 'DISPONIVEL', 70.00, 'ACAO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Acao', 3, 'ITUB4', 'Itaú Unibanco PN', 'DISPONIVEL', 35.20, 'ACAO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Acao', 4, 'BBDC4', 'Bradesco PN', 'DISPONIVEL', 15.80, 'ACAO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Acao', 5, 'ABEV3', 'Ambev', 'DISPONIVEL', 12.45, 'ACAO');

-- Criptomoedas
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Cripto', 6, 'BTC', 'Bitcoin', 'DISPONIVEL', 250000.00, 'CRIPTO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Cripto', 7, 'ETH', 'Ethereum', 'DISPONIVEL', 15000.00, 'CRIPTO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Cripto', 8, 'Doge', 'Moeda dogecoin', 'DISPONIVEL', 0.50, 'CRIPTO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Cripto', 9, 'ADA', 'Cardano', 'DISPONIVEL', 2.30, 'CRIPTO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Cripto', 10, 'SOL', 'Solana', 'DISPONIVEL', 180.00, 'CRIPTO');

-- Tesouro
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Tesouro', 11, 'Selic', 'Tesouro Selic', 'DISPONIVEL', 100.00, 'TESOURO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Tesouro', 12, 'CDB', 'Certificado de Depósito Bancário', 'DISPONIVEL', 105.00, 'TESOURO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Tesouro', 13, 'Tesouro IPCA', 'Tesouro IPCA+', 'DISPONIVEL', 110.00, 'TESOURO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Tesouro', 14, 'Tesouro Prefixado', 'Tesouro Prefixado', 'DISPONIVEL', 108.00, 'TESOURO');
INSERT INTO ativo (dtype, id, nome, descricao, status, cotacao, tipo) VALUES ('Tesouro', 15, 'LCI', 'Letra de Crédito Imobiliário', 'DISPONIVEL', 102.00, 'TESOURO');
