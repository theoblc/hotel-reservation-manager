DROP DATABASE IF EXISTS Hotel;

CREATE DATABASE Hotel;
USE Hotel

CREATE TABLE Personne (
id_personne int PRIMARY KEY AUTO_INCREMENT, 
nom varchar(50) NOT NULL,
prenom varchar(50) NOT NULL, 
tel char(10),    
mail varchar(50) NOT NULL CONSTRAINT format_mail CHECK (mail LIKE "%@%")
);

CREATE TABLE Employe (
id_employe int PRIMARY KEY AUTO_INCREMENT, 
id_personne int NOT NULL, 
admin TINYINT NOT NULL, 
role varchar(15) NOT NULL CONSTRAINT valeurs_role CHECK (role IN ("Entretien","Accueil")), 
id_connexion varchar(50) NOT NULL,
password varchar(50) NOT NULL,
FOREIGN KEY (id_personne) REFERENCES Personne(id_personne)
);

CREATE TABLE Reservation (
id_res int PRIMARY KEY AUTO_INCREMENT, 
id_personne int NOT NULL,
id_employe int NOT NULL, 
date_deb date NOT NULL, 
date_fin date NOT NULL,
prix real CONSTRAINT format_prix_res CHECK (prix>=0),
FOREIGN KEY (id_employe) REFERENCES Employe(id_employe),
FOREIGN KEY (id_personne) REFERENCES Personne(id_personne)
);

CREATE TABLE Chambre (
numero int PRIMARY KEY, 
genre char(6) NOT NULL CONSTRAINT valeurs_genre CHECK (genre IN ("Simple","Double", "Suite")), 
prix real NOT NULL CONSTRAINT format_prix_chambre CHECK (prix>=0), 
statut int CONSTRAINT valeurs_statut CHECK (statut IN (0,1)), 
nb_lits_simples int, 
nb_lits_doubles int
);

CREATE TABLE Supplement (
id_sup int PRIMARY KEY AUTO_INCREMENT, 
nom_sup varchar(50) UNIQUE NOT NULL, 
prix_sup real NOT NULL CONSTRAINT format_prix_supplement CHECK (prix_sup>=0) 
);

CREATE TABLE Chambre_Supplement (
numero int, 
id_sup int,
date_deb date, 
date_fin date, 
PRIMARY KEY(numero,id_sup, date_deb, date_fin),
FOREIGN KEY (numero) REFERENCES Chambre(numero) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (id_sup) REFERENCES Supplement(id_sup) ON UPDATE CASCADE ON DELETE CASCADE
); 

CREATE TABLE Reservation_Chambre(
id_res int, 
numero int,
PRIMARY KEY(id_res,numero),
FOREIGN KEY (id_res) REFERENCES Reservation(id_res) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (numero) REFERENCES Chambre(numero) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Chambre_Employe (
numero int, 
id_employe int, 
date_deb date, 
date_fin date,
PRIMARY KEY(numero,id_employe,date_deb,date_fin),
FOREIGN KEY (numero) REFERENCES Chambre(numero) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY (id_employe) REFERENCES Employe(id_employe) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Historique (
id_event int PRIMARY KEY AUTO_INCREMENT, 
evenement varchar(200),
creation timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
id_employe int, 
FOREIGN KEY (id_employe) REFERENCES Employe(id_employe) ON UPDATE CASCADE ON DELETE CASCADE
);
