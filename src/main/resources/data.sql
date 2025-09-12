INSERT INTO products (id, name, description, price, status, created_at, updated_at) VALUES
('a1b2c3d4-e5f6-4a7b-8c9d-1e2f3a4b5c6d', 'Gaming Laptop', 'High performance gaming laptop', 2499.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b2c3d4e5-f6a7-4b8c-9d1e-2f3a4b5c6d7e', 'Wireless Mouse', 'Ergonomic wireless gaming mouse', 79.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('c3d4e5f6-a7b8-4c9d-1e2f-3a4b5c6d7e8f', 'Mechanical Keyboard', 'RGB mechanical gaming keyboard', 159.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('d4e5f6a7-b8c9-4d1e-2f3a-4b5c6d7e8f9a', 'Gaming Monitor', '27-inch 144Hz gaming monitor', 349.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('e5f6a7b8-c9d1-4e2f-3a4b-5c6d7e8f9a0b', 'Gaming Headset', '7.1 surround sound gaming headset', 129.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('f6a7b8c9-d1e2-4f3a-4b5c-6d7e8f9a0b1c', 'SSD Drive', '1TB NVMe SSD drive', 199.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('a7b8c9d1-e2f3-4a4b-5c6d-7e8f9a0b1c2d', 'Webcam', '4K streaming webcam', 89.99, 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('b8c9d1e2-f3a4-4b5c-6d7e-8f9a0b1c2d3e', 'Graphics Card', 'RTX gaming graphics card', 699.99, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO customers (id, birth_date, number, status, inactive_in, last_access, register_date, cpf, name, rg, street, zip_code) VALUES
('a1b2c3d4-e5f6-4a7b-8c9d-000000000001', '1990-05-15', 123, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '123.456.789-01', 'João Silva', '12.345.678-9', 'Rua das Flores', '01234-567'),
('b2c3d4e5-f6a7-4b8c-9d1e-000000000002', '1985-08-22', 456, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '234.567.890-02', 'Maria Santos', '23.456.789-0', 'Avenida Brasil', '12345-678'),
('c3d4e5f6-a7b8-4c9d-1e2f-000000000003', '1992-03-10', 789, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '345.678.901-03', 'Pedro Oliveira', '34.567.890-1', 'Praça da Sé', '23456-789'),
('d4e5f6a7-b8c9-4d1e-2f3a-000000000004', '1988-12-05', 321, 1, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '456.789.012-04', 'Ana Costa', '45.678.901-2', 'Rua Augusta', '34567-890'),
('e5f6a7b8-c9d1-4e2f-3a4b-000000000005', '1987-11-28', 654, 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '789.123.456-05', 'Carlos Ferreira', '78.912.345-6', 'Avenida Paulista', '24681-357');