
/**
 * Controleur du sous onglet de l'onglet Client
 * 
 * @author Charlie MORRISSEY
 */

package application.controller.sousOnglets;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import application.controller.onglets.ControleurPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClientModifCtrl implements Initializable {

	@FXML
	private Button boutonModif;

	@FXML
	private Button boutonValider;

	@FXML
	private Label label_erreur;

	@FXML
	private TextField modif_txt_nom;

	@FXML
	private TextField modif_txt_prenom;

	@FXML
	private TextField modif_txt_tel;

	@FXML
	private TextField modif_txt_mail;

	@FXML
	private TableView<ObservableList<Object>> tab_modif;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement(
					"SELECT * FROM Personne WHERE Personne.id_personne NOT IN (SELECT Employe.id_personne FROM Employe)");

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					this.tab_modif);
			tab_modif.setItems(rep_tab);
		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	//////////// Méthodes du controleur ///////////////

	@FXML
	void Modifier(ActionEvent event) {

		// Recuperation des champs du tableau provenant de la ligne selectionnee
		String nom = (String) tab_modif.getSelectionModel().getSelectedItem().get(1);
		String prenom = (String) tab_modif.getSelectionModel().getSelectedItem().get(2);
		String tel = (String) tab_modif.getSelectionModel().getSelectedItem().get(3);
		String mail = (String) tab_modif.getSelectionModel().getSelectedItem().get(4);

		// Affichage dans le tableau
		modif_txt_nom.setText(nom);
		modif_txt_prenom.setText(prenom);
		modif_txt_tel.setText(tel);
		modif_txt_mail.setText(mail);

	}

	public void Valider(ActionEvent event) {

		// Recuperation des champs remplis par l'utilisateur
		String nom = modif_txt_nom.getText();
		String prenom = modif_txt_prenom.getText();
		String tel = modif_txt_tel.getText();
		String mail = modif_txt_mail.getText();
		// Recuperation id_personne
		int id_personne = (int) tab_modif.getSelectionModel().getSelectedItem().get(0);

		/* On controle les donnees saisies dans le formulaire */
		try {
			Integer.parseInt(tel);
			if ((tel.length() != 10) || (!ControleurPrincipal.isValidEmailAddress(mail))) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			label_erreur.setText("Argument(s) invalide(s)");
			;
			return;
		}

		// Connection a la base de donnees
		Connection conn = ControleurPrincipal.connect();
		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("UPDATE Personne SET nom=?, prenom=?, tel=?, mail=? WHERE id_personne = ?");
			requete.setString(1, nom);
			requete.setString(2, prenom);
			requete.setString(3, tel);
			requete.setString(4, mail);
			requete.setInt(5, id_personne);

			// Execution de la requete
			requete.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		/*
		 * Envoie de l'information au controleur principal et actualisation du tableau
		 */
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Client (" + nom + "  " + prenom + ") modifié");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}

}
