/**
* Test unitaire :
* 		Test de l'ajout d'une chambre par requête SQL
*
* @file Hotel.JUnitPackage.ConnexionTest.java
*
* @version 1
* @author Théo BLANCHONNET
* @date 10 mai 2022
*/

package JUnitPackage;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import application.controller.onglets.ControleurPrincipal;

public class ConnexionTest {

	@Test
	public void testAjoutChambre() {

		int nbChambre1 = 0;
		int nbChambre2 = 0;

		Connection conn = ControleurPrincipal.connect();
		try {

			PreparedStatement requete1 = conn.prepareStatement("SELECT COUNT(*) FROM Chambre;");
			ResultSet resultat1 = requete1.executeQuery();

			// Sauvegarde du nombre de chambre
			if (resultat1.next()) {
				nbChambre1 = resultat1.getInt(1);
			}

			// Ajout d'une chambre
			String numero = "502";
			String genre = "Simple";
			String prix = "100.00";
			String statut = "0";
			String nb_lits_simples = "2";
			String nb_lits_doubles = "0";

			PreparedStatement requeteAj = conn
					.prepareStatement("INSERT INTO Chambre (numero,genre,prix,statut,nb_lits_simples"
							+ ",nb_lits_doubles) VALUES (?,?,?,?,?,?);");
			requeteAj.setString(1, numero);
			requeteAj.setString(2, genre);
			requeteAj.setString(3, prix);
			requeteAj.setString(4, statut);
			requeteAj.setString(5, nb_lits_simples);
			requeteAj.setString(6, nb_lits_doubles);
			// Execution de la requete
			requeteAj.executeUpdate();

			// Vérification du bon nombre de chambre après ajout
			PreparedStatement requete2 = conn.prepareStatement("SELECT COUNT(*) FROM Chambre;");
			ResultSet resultat2 = requete2.executeQuery();

			if (resultat2.next()) {
				nbChambre2 = resultat2.getInt(1);
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		// Test
		assertEquals(nbChambre1 + 1, nbChambre2);
	}

}