DROP DATABASE IF EXISTS boardgames;
CREATE DATABASE boardgames;
USE boardgames;

CREATE TABLE Author (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Publisher (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Game (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    id_author INT NOT NULL,
    id_publisher INT NOT NULL,
    box_price DECIMAL(10,2) NOT NULL,
    age_limit INT NOT NULL,
    duration INT NOT NULL,
    sold_units INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_author) REFERENCES Author(id),
    FOREIGN KEY (id_publisher) REFERENCES Publisher(id)
);

CREATE TABLE GameTranslation (
    id_game INT NOT NULL,
    language VARCHAR(2) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_game, language),
    FOREIGN KEY (id_game) REFERENCES Game(id)
);
INSERT INTO Author (name) VALUES
    ('Reiner Knizia'),
    ('Vlaada Chvátil'),
    ('Stefan Feld'),
    ('Uwe Rosenberg'),
    ('Jamey Stegmaier'),
    ('Eric Lang'),
    ('Elizabeth Hargrave'),
    ('Rob Daviau'),
    ('Matt Leacock'),
    ('Wolfgang Kramer'),
    ('Michael Kiesling'),
    ('Richard Garfield'),
    ('Bruno Cathala'),
    ('Antoine Bauza'),
    ('Phil Walker-Harding'),
    ('Alexander Pfister'),
    ('Leder Game'),
    ('Vital Lacerda'),
    ('Dávid Turczi'),
    ('Isaac Childres'),
    ('Ryan Laukat'),
    ('Splotter Spellen'),
    ('Corey Konieczka'),
    ('Eric M. Lang'),
    ('Prospero Hall'),
    ('Cole Wehrle'),
    ('Neil Gaiman'),
    ('Martin Wallace'),
    ('Ryan Cordell'),
    ('Kevin Wilson'),
    ('Michael Elliott');

INSERT INTO Publisher (name) VALUES
    ('Kosmos'),
    ('Czech Game Edition'),
    ('Queen Game'),
    ('HABA'),
    ('Stonemaier Game'),
    ('CMON'),
    ('Academy Game'),
    ('Z-Man Game'),
    ('Victory Point Game'),
    ('Mayfair Game'),
    ('Ravensburger'),
    ('Wizards of the Coast'),
    ('Asmodee'),
    ('Matagot'),
    ('Bezier Game'),
    ('Eagle-Gryphon Game'),
    ('Gamewright'),
    ('Repos Production'),
    ('Portal Game'),
    ('Greater Than Game'),
    ('GMT Game'),
    ('Ares Game'),
    ('Jamey Stegmaier Game'),
    ('Kickstarter'),
    ('North Star Game'),
    ('Marvel'),
    ('Ares Game'),
    ('Corvus Belli'),
    ('Renegade Game Studios');

INSERT INTO Game (name, id_author, id_publisher, box_price, age_limit, duration, sold_units) VALUES
    ('Catan', 1, 1, 50.00, 10, 60, 25000000),
    ('7 Wonders', 2, 3, 50.00, 10, 30, 2000000),
    ('Carcassonne', 1, 4, 30.00, 8, 30, 10000000),
    ('Ticket to Ride', 5, 6, 40.00, 8, 60, 10000000),
    ('Pandemic', 2, 7, 40.00, 10, 60, 5000000),
    ('Agricola', 1, 8, 50.00, 12, 90, 1000000),
    ('Wingspan', 9, 10, 50.00, 10, 40, 1000000),
    ('Root', 11, 12, 60.00, 10, 60, 500000),
    ('Spirit Island', 13, 14, 80.00, 14, 90, 250000),
    ('Gloomhaven', 15, 16, 140.00, 12, 120, 500000),
    ('Twilight Imperium: Fourth Edition', 17, 18, 150.00, 14, 240, 250000),
    ('Mage Knight', 15, 19, 100.00, 12, 120, 500000),
    ('Terraforming Mars', 18, 20, 70.00, 12, 120, 500000),
    ('Le Havre', 1, 8, 50.00, 12, 120, 500000),
    ('Great Western Trail', 19,  8, 50.00, 12, 120, 500000),
    ('Everdell', 11, 21, 60.00, 10, 40, 500000),
    ('Scythe', 22, 23, 70.00, 14, 90, 500000),
    ('Arkham Horror: The Card Game', 24, 25, 60.00, 14, 60, 500000),
    ('Food Chain Magnate', 26, 27, 100.00, 12, 120, 250000),
    ('Concordia', 1, 28, 50.00, 13, 60, 500000),
    ('The Castles of Burgundy', 29, 20, 50.00, 12, 90, 500000),
    ('Puerto Rico', 29, 20, 50.00, 12, 120, 500000),
    ('Terraforming Mars: Ares Expedition', 18, 20, 30.00, 12, 60, 500000),
    ('Wingspan: European Expansion', 9, 10, 30.00, 10, 40, 500000),
    ('Spirit Island: Jagged Earth', 13, 20, 60.00, 14, 120, 250000),
    ('Gloomhaven: Jaws of the Lion', 15, 16, 60.00, 12, 90, 500000),
    ('Codenames: Duet', 20, 20, 20.00, 10, 15, 500000);

INSERT INTO GameTranslation (id_game, language, name) VALUES
    (1, 'fr', 'Les Colons de Catane'),
    (1, 'es', 'Catan'),
    (2, 'fr', '7 Wonders'),
    (2, 'es', '7 Maravillas'),
    (3, 'fr', 'Carcassonne'),
    (3, 'es', 'Carcassonne'),
    (4, 'fr', 'Ticket to Ride'),
    (4, 'es', 'Trenes por Europa'),
    (5, 'fr', 'Pandémie'),
    (5, 'es', 'Pandemic'),
    (6, 'fr', 'Agricola'),
    (6, 'es', 'Agricola'),
    (7, 'fr', 'Wingspan'),
    (7, 'es', 'Wingspan'),
    (8, 'fr', 'Root'),
    (8, 'es', 'Root'),
    (9, 'fr', 'Île aux Esprits'),
    (9, 'es', 'Spirit Island'),
    (10, 'fr', 'Gloomhaven'),
    (10, 'es', 'Gloomhaven'),
    (11, 'fr', 'Twilight Imperium: Quatrième Édition'),
    (11, 'es', 'Twilight Imperium: Cuarta Edición'),
    (12, 'fr', 'Mage Knight'),
    (12, 'es', 'Mage Knight'),
    (13, 'fr', 'Terraforming Mars'),
    (13, 'es', 'Terraforming Mars'),
    (14, 'fr', 'Le Havre'),
    (14, 'es', 'El Havre'),
    (15, 'fr', 'Great Western Trail'),
    (15, 'es', 'Great Western Trail'),
    (16, 'fr', 'Everdell'),
    (16, 'es', 'Everdell'),
    (17, 'fr', 'Scythe'),
    (17, 'es', 'Scythe'),
    (18, 'fr', 'Arkham Horror: Le Jeu de Cartes'),
    (18, 'es', 'Arkham Horror: El Juego de Cartas'),
    (19, 'fr', 'Food Chain Magnate'),
    (19, 'es', 'Food Chain Magnate'),
    (20, 'fr', 'Concordia'),
    (20, 'es', 'Concordia'),
    (21, 'fr', 'Les Châteaux de Bourgogne'),
    (21, 'es', 'Los Castillos de Borgoña'),
    (22, 'fr', 'Puerto Rico'),
    (22, 'es', 'Puerto Rico'),
    (23, 'fr', 'Terraforming Mars: Expédition Ares'),
    (23, 'es', 'Terraforming Mars: Expedición Ares'),
    (24, 'fr', 'Wingspan: Expansion Europeenne'),
    (24, 'es', 'Wingspan: Expansión Europea'),
    (25, 'fr', 'Spirit Island: Jagged Earth'),
    (25, 'es', 'Spirit Island: Jagged Earth'),
    (26, 'fr', 'Gloomhaven: Les Mâchoires du Lion'),
    (26, 'es', 'Gloomhaven: Las Fauces del León'),
    (27, 'fr', 'Codenames: Duet'),
    (27, 'es', 'Codenames: Dúo');
