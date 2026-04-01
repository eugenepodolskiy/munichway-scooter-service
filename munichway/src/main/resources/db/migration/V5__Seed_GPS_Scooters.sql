INSERT INTO scooters (model_name, battery_level, available, deleted, location)
VALUES
('Ninebot Max G30', 100, true, false, ST_SetSRID(ST_MakePoint(11.5756, 48.1371), 4326)),
('Xiaomi Mi Pro 2', 80, true, false, ST_SetSRID(ST_MakePoint(11.5776, 48.1413), 4326)),
('Tier 5', 42, true, false, ST_SetSRID(ST_MakePoint(11.5900, 48.1527), 4326));

INSERT INTO scooters (model_name, battery_level, available, deleted, location)
VALUES
('Lime Gen 4', 95, true, false, ST_SetSRID(ST_MakePoint(13.4050, 52.5200), 4326));