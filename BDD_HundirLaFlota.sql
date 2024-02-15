-- CREAMOS LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS BDD_HundirLaFlota;

-- SELECCIONAMOS LA BASE DE DATOS
use BDD_HundirLaFlota;

-- Borrar la tabla Disparos
DROP TABLE IF EXISTS Disparos;

-- Borrar la tabla Barcos
DROP TABLE IF EXISTS Barcos;

-- Borrar la tabla Partidas
DROP TABLE IF EXISTS Partidas;

-- Borrar la tabla Jugadores
DROP TABLE IF EXISTS Jugadores;

-- Tabla Jugadores
CREATE TABLE Jugadores (
    id_jugador INT AUTO_INCREMENT,
    nombre VARCHAR(10) NOT NULL,
	contraseña INT NOT NULL,
    PRIMARY KEY (id_jugador)
);

-- Tabla Partidas
CREATE TABLE Partidas (
    id_partida INT AUTO_INCREMENT,
    jugador_1 INT,
    jugador_2 INT,
    estado VARCHAR(1) NOT NULL,-- X -> Terminada, O -> En curso
    ganador INT,-- Id del jugador Ganador 
    ultimo_turno INT,-- Id del jugador del ultimo turno
    PRIMARY KEY (id_partida),
    FOREIGN KEY (jugador_1) REFERENCES Jugadores(id_jugador),
    FOREIGN KEY (jugador_2) REFERENCES Jugadores(id_jugador),
    FOREIGN KEY (ganador) REFERENCES Jugadores(id_jugador),
    FOREIGN KEY (ultimo_turno) REFERENCES Jugadores(id_jugador)
);


-- Tabla Barcos
CREATE TABLE Barcos (-- Solo 3 varco por jugador en cada partida
    id_barco INT AUTO_INCREMENT,
    id_partida INT,
    jugador_id INT,
    tamaño INT NOT NULL,-- Barco sea mayor que 2 y menor que 7
    posicion_x INT NOT NULL,
    posicion_y INT NOT NULL,
    orientacion VARCHAR(1) NOT NULL,-- V -> Vertical, H -> Horizontal
    PRIMARY KEY (id_barco),
    FOREIGN KEY (id_partida) REFERENCES Partidas(id_partida),
    FOREIGN KEY (jugador_id) REFERENCES Jugadores(id_jugador)
);

-- Tabla Disparos
CREATE TABLE Disparos (
    id_disparo INT AUTO_INCREMENT,
    id_partida INT,
    jugador_id INT,
    posicion_x INT NOT NULL,
    posicion_y INT NOT NULL,
    resultado VARCHAR(1) NOT NULL,-- A -> Agua,  T -> Tocado, H -> Hundido
    PRIMARY KEY (id_disparo),
    FOREIGN KEY (id_partida) REFERENCES Partidas(id_partida),
    FOREIGN KEY (jugador_id) REFERENCES Jugadores(id_jugador)
);