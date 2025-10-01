# Esquema y datos iniciales para la base de datos de personas
DROP DATABASE IF EXISTS dbpersonas;
# Elimina la base de datos existente para evitar conflictos
CREATE DATABASE dbpersonas;

USE dbpersonas;
CREATE TABLE dbpersonas.personas (
	personId INT auto_increment NOT NULL,
	firstName varchar(100) NOT NULL,
	lastName varchar(100) NOT NULL,
	birthDate varchar(100) NULL,
	CONSTRAINT personas_pk PRIMARY KEY (personId)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4;

# Datos iniciales de ejemplo
INSERT INTO personas (firstName, lastName, birthDate) VALUES
('John', 'Lennon', '1940-10-09'),
('Paul', 'McCartney', '1942-06-18'),
('George', 'Harrison', '1943-02-25'),
('Ringo', 'Starr', '1940-07-07');