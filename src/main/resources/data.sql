-- Create the 'racks' table
CREATE TABLE IF NOT EXISTS racks (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     capacity INT NOT NULL,
                                     version INT NOT NULL
);

-- Create the 'samples' table
CREATE TABLE IF NOT EXISTS samples (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       patient_age INT NOT NULL,
                                       patient_company VARCHAR(255),
    patient_city_district VARCHAR(255),
    patient_vision_defect VARCHAR(255),
    rack_id BIGINT,
    version INT NOT NULL,
    FOREIGN KEY (rack_id) REFERENCES racks(id)
    );

-- Populate racks:
-- Insert data into the 'racks' table
INSERT INTO racks (capacity, version) VALUES (20, 0);
INSERT INTO racks (capacity, version) VALUES (20, 0);
INSERT INTO racks (capacity, version) VALUES (20, 0);
INSERT INTO racks (capacity, version) VALUES (50, 0);
INSERT INTO racks (capacity, version) VALUES (50, 0);

INSERT INTO samples (patient_age, patient_company, patient_city_district, patient_vision_defect, rack_id, version)
VALUES (33, 'ECB', 'Trojkat Bermudzki', 'ASTIGMATISM', 1, 0);
INSERT INTO samples (patient_age, patient_company, patient_city_district, patient_vision_defect, rack_id, version)
VALUES (35, 'Metroplan', 'Osiedle Utopia', 'BLIND', 1, 0);
INSERT INTO samples (patient_age, patient_company, patient_city_district, patient_vision_defect, rack_id, version)
VALUES (33, 'Metroplan', 'Jawor', 'BLIND', 2, 0);
INSERT INTO samples (patient_age, patient_company, patient_city_district, patient_vision_defect, rack_id, version)
VALUES (21, 'Mrowka PSB', 'Jaworzno', 'ASTIGMATISM', 2, 0);
INSERT INTO samples (patient_age, patient_company, patient_city_district, patient_vision_defect, rack_id, version)
VALUES (21, 'EY', 'Wroclaw Biskupin', 'ASTIGMATISM', 3, 0);