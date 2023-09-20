USE Hotel;


INSERT INTO Personne (nom,prenom,tel,mail) VALUES ("Villeneuve","Denis","0646597885","denis.villeneuve@gmail.com");
INSERT INTO Personne (nom,prenom, tel, mail) VALUES ("Wright","Edgar","06123654689", "edgar.wright@gmail.com");
INSERT INTO Personne (nom,prenom, tel, mail) VALUES ("Bigelow","Kathryn","0715686433", "kat.bigelow@orange.fr");
INSERT INTO Personne (nom,prenom, tel, mail) VALUES ("Aja","Alexandre", "0689976554", "alex.aja@hotmail.com");
INSERT INTO Personne (nom,prenom, tel, mail) VALUES ("Noe","Gaspard", "0788661423", "gaspard.noe@gmail.com");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Bong","Joon-Ho","bong@hotmail.fr");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Fargeat","Coralie","coralie@sfr.fr");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Fincher","David","fincher@gmail.com");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Scott","Ridley","ridley.scott@gmail.com");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Cruise","Tom","cruise@hotmail.fr");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Affleck","Ben","affleck@orange.fr");
INSERT INTO Personne (nom,prenom,mail) VALUES ("Oscar","Isaac","oscar.isaac@gmail.com");


INSERT INTO Employe (id_personne,admin,role,password,id_connexion) SELECT id_personne,FALSE,"Entretien","107961","baffleck" FROM Personne WHERE nom="Affleck" AND prenom="Ben";
INSERT INTO Employe (id_personne,admin,role,password,id_connexion) SELECT id_personne,FALSE,"Accueil","107961","ioscar" FROM Personne WHERE nom="Oscar" AND prenom="Isaac";
INSERT INTO Employe (id_personne,admin,role,password,id_connexion) SELECT id_personne,TRUE,"Accueil","107961","tcruise" FROM Personne WHERE nom="Cruise" AND prenom="Tom";

INSERT INTO Reservation (date_deb,date_fin,id_employe,id_personne)
SELECT "2022-06-15","2022-09-02",id_employe,(SELECT id_personne FROM Personne WHERE nom="Villeneuve" AND prenom="Denis")
FROM Employe as E JOIN Personne as P USING (id_personne) WHERE P.nom="Oscar" AND P.prenom="Isaac";

INSERT INTO Reservation (date_deb,date_fin,id_employe,id_personne)
SELECT "2022-05-20","2022-06-30",id_employe,(SELECT id_personne FROM Personne WHERE nom="Aja" AND prenom="Alexandre")
FROM Employe as E JOIN Personne as P USING (id_personne) WHERE P.nom="Cruise" AND P.prenom="Tom";

INSERT INTO Reservation (date_deb,date_fin,id_employe,id_personne)
SELECT "2022-07-16","2022-07-18",id_employe,(SELECT id_personne FROM Personne WHERE nom="Bong" AND prenom="Joon-Ho")
FROM Employe as E JOIN Personne as P USING (id_personne) WHERE P.nom="Oscar" AND P.prenom="Isaac";

INSERT INTO Reservation (date_deb,date_fin,id_employe,id_personne)
SELECT "2022-06-15","2022-06-21",id_employe,(SELECT id_personne FROM Personne WHERE nom="Fincher" AND prenom="David")
FROM Employe as E JOIN Personne as P USING (id_personne) WHERE P.nom="Oscar" AND P.prenom="Isaac";

INSERT INTO Reservation (date_deb,date_fin,id_employe,id_personne)
SELECT "2022-06-16","2022-07-01",id_employe,(SELECT id_personne FROM Personne WHERE nom="Fargeat" AND prenom="Coralie")
FROM Employe as E JOIN Personne as P USING (id_personne) WHERE P.nom="Oscar" AND P.prenom="Isaac";


INSERT INTO Chambre VALUES (001,"Simple",50,0,1,0);
INSERT INTO Chambre VALUES (002,"Simple",50,1,1,0);
INSERT INTO Chambre VALUES (101,"Double",70,0,2,0);
INSERT INTO Chambre VALUES (102,"Double",70,1,0,1);
INSERT INTO Chambre VALUES (201,"Simple",60,0,1,0);
INSERT INTO Chambre VALUES (202,"Double",90,0,0,1);



INSERT INTO Supplement (id_sup,nom_sup,prix_sup) VALUES (01,"bar",10);
INSERT INTO Supplement (id_sup,nom_sup,prix_sup) VALUES (02,"spa",20);
INSERT INTO Supplement (id_sup,nom_sup,prix_sup) VALUES (03,"salle de sport",15);


INSERT INTO Reservation_Chambre SELECT id_res,001 FROM Reservation WHERE date_deb="2022-06-15" AND date_fin="2022-09-02";
INSERT INTO Reservation_Chambre SELECT id_res,002 FROM Reservation WHERE date_deb="2022-05-20" AND date_fin="2022-06-30";
INSERT INTO Reservation_Chambre SELECT id_res,002 FROM Reservation WHERE date_deb="2022-07-16" AND date_fin="2022-07-18";
INSERT INTO Reservation_Chambre SELECT id_res,201 FROM Reservation WHERE date_deb="2022-06-15" AND date_fin="2022-06-21";
INSERT INTO Reservation_Chambre SELECT id_res,101 FROM Reservation WHERE date_deb="2022-06-16" AND date_fin="2022-07-01";

INSERT INTO Chambre_Supplement VALUES(101, 02, "2022-06-16", "2022-07-01");
INSERT INTO Chambre_Supplement VALUES(201, 03, "2022-06-15", "2022-06-30");
INSERT INTO Chambre_Supplement VALUES (002, 03, "2022-06-20", "2022-06-30");

INSERT INTO Chambre_Employe SELECT 001,id_employe,"2022-05-18","2022-05-19"
FROM Employe JOIN Personne USING (id_personne)
WHERE nom="Affleck" AND prenom="Ben" ;

INSERT INTO Chambre_Employe SELECT 002,id_employe,"2022-04-18","2022-04-19"
FROM Employe JOIN Personne USING (id_personne)
WHERE nom="Affleck" AND prenom="Ben" ;

INSERT INTO Chambre_Employe SELECT 101,id_employe,"2022-06-01","2022-06-02"
FROM Employe JOIN Personne USING (id_personne)
WHERE nom="Affleck" AND prenom="Ben" ;
