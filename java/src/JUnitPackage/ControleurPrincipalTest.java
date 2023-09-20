/**
* Test unitaire :
* 		Test de la fonction de récupération du String
*  		résultant d'une requête SQL
* Test d'intégration :
*		Test de la fonction estOccupee 
*
* @file Hotel.JUnitPackage.ControleurPincipalTest.java
*
* @version 1
* @author Théo BLANCHONNET
* @date 11 mai 2022
*/
package JUnitPackage;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import application.controller.onglets.ControleurPrincipal;
import application.controller.onglets.MenuPrincipalCtrl;

public class ControleurPrincipalTest {

	@Test
	public void testRequestNew() {

		Connection conn = ControleurPrincipal.connect();
		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Chambre WHERE numero=600;");
			/*
			 * Cette chambre 600 n'existe pas, cela a pour unique but de fournir une requete
			 * vide. resultat doit donc être vide
			 */
			ResultSet resultat = requete.executeQuery();

			String res = ControleurPrincipal.requestNew(resultat);

			// Test
			assertEquals("", res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

	@Test
	public void testEstOccupee() throws SQLException {

		// Creation de l'instance du controleur
		MenuPrincipalCtrl controleur = new MenuPrincipalCtrl();

		// Connexion établie
		Connection conn = ControleurPrincipal.connect();

		// On teste si la chambre numero 1 est occupee
		boolean occupee = controleur.estoccupee(1, conn);

		// Test
		assertTrue(occupee);

	}
}