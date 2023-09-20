
/**
 * Controleur du sous onglet de l'onglet Administrateur
 * 
 * @author Théo BLANCHONNET
 */

package application.controller.sousOnglets;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import application.controller.onglets.ControleurPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdministrateurModifEmpCtrl implements Initializable {
	@FXML
	private Button boutonModif;

	@FXML
	private Button boutonValider;

	@FXML
	private ComboBox<String> modif_combo_admin;

	@FXML
	private ComboBox<String> modif_combo_role;

	@FXML
	private Label label_erreur;

	@FXML
	private TextField modif_txt_nom;

	@FXML
	private TextField modif_txt_prenom;

	@FXML
	private TextField modif_txt_mail;

	@FXML
	private TextField modif_txt_mdp;

	@FXML
	private TextField modif_txt_tel;

	@FXML
	private TableView<ObservableList<Object>> tab_modif;
	
	private String password;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Initialisation des ComboBox
		modif_combo_role.getItems().addAll("Accueil", "Entretien");
		modif_combo_admin.getItems().addAll("0", "1");

		// Initialisation du TableView
		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement("SELECT P.nom, "
					+ "P.prenom, E.role, E.admin, P.tel, P.mail, E.password, E.id_connexion, E.id_personne, E.id_employe"
					+ " FROM Employe E JOIN Personne P ON " + "E.id_personne = P.id_personne;");

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

		// Recuperation des champs du TableView provenant de la ligne
		// séléctionnée
		String nom = (String) tab_modif.getSelectionModel().getSelectedItem().get(0);
		String prenom = (String) tab_modif.getSelectionModel().getSelectedItem().get(1);
		String role = (String) tab_modif.getSelectionModel().getSelectedItem().get(2);
		int admin = (int) tab_modif.getSelectionModel().getSelectedItem().get(3);
		String tel = (String) tab_modif.getSelectionModel().getSelectedItem().get(4);
		String mail = (String) tab_modif.getSelectionModel().getSelectedItem().get(5);
		String mdp = (String) tab_modif.getSelectionModel().getSelectedItem().get(6);

		modif_txt_nom.setText(nom);
		modif_txt_prenom.setText(prenom);
		modif_combo_admin.setValue(Integer.toString(admin));
		modif_combo_role.setValue(role);
		modif_txt_mdp.setText(mdp);
		modif_txt_tel.setText(tel);
		modif_txt_mail.setText(mail);

		this.password = mdp;
	}

	public void Valider(ActionEvent event) {

		// Recuperation des champs remplis par l'utilisateur
		String nom = modif_txt_nom.getText();
		String prenom = modif_txt_prenom.getText();
		String admin = modif_combo_admin.getValue();
		String role = modif_combo_role.getValue();
		String mdp = modif_txt_mdp.getText();
		String tel = modif_txt_tel.getText();
		String mail = modif_txt_mail.getText();
		// Recuperation id_personne et id_employe
		int id_personne = (int) tab_modif.getSelectionModel().getSelectedItem().get(8);
		int id_employe = (int) tab_modif.getSelectionModel().getSelectedItem().get(9);

		
		// Si le mdp est modifé on introduit le hashcode du mdp dans la BDD
		if (!this.password.equals(mdp)) {
			mdp = Integer.toString(mdp.hashCode());
		}
		
		/* On controle les donnees saisies dans le formulaire */
		try {
			Integer.parseInt(tel);
			if ((tel.length() != 10) || (!ControleurPrincipal.isValidEmailAddress(mail))) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			label_erreur.setText("Argument(s) invalide(s)");
			return;
		}

		// Connection a la base de donnees
		Connection conn = ControleurPrincipal.connect();
		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete1 = conn
					.prepareStatement("UPDATE Personne SET nom=?, prenom=?, tel=?, mail=? WHERE id_personne = ?");
			requete1.setString(1, nom);
			requete1.setString(2, prenom);
			requete1.setString(3, tel);
			requete1.setString(4, mail);
			requete1.setInt(5, id_personne);
			// Execution de la requete
			requete1.executeUpdate();

			PreparedStatement requete2 = conn
					.prepareStatement("UPDATE Employe " + "SET password=?, role=?, admin=? " + "WHERE id_employe = ?");
			requete2.setString(1, mdp);
			requete2.setString(2, role);
			requete2.setString(3, admin);
			requete2.setInt(4, id_employe);
			// Execution de la requete
			requete2.executeUpdate();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		/* Envoie de l'information au controleur principal et actualisation du tableau*/
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Employé (" + nom + "  " + prenom + ") modifié");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}

}
