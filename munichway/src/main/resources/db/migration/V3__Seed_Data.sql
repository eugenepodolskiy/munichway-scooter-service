INSERT INTO users (name, email, password, role, balance, deleted)
VALUES
('Nastya Admin', 'admin@munichway.com', '$2a$10$/UsFFdtzLXtuImwRE6.zX.iI0QmtO4ejhwxfxqpmrReIZwWvjYwvu', 'ROLE_ADMIN', 1000.0, false),
('Test Driver', 'user@test.com', '$2a$10$MqmbJFaiYBgUppfWcpSwCO9SdVEQH/L5Tor7.mL1SifF71EEpzHA.', 'ROLE_USER', 10.0, false);


INSERT INTO scooters (model_name, location, battery_level, available, deleted)
VALUES ('Xiaomi Pro 2', 'Marienplatz', 100, true, false),
       ('Xiaomi Pro 2', 'Odeonsplatz', 85, true, false),
       ('Ninebot G30', 'Hauptbahnhof', 15, true, false),
       ('Ninebot G30', 'Stachus', 50, false, false);