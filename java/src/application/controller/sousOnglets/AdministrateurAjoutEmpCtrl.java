
/**
 * Controleur du sous onglet de l'onglet Administrateur
 * 
 * @author Théo BLANCHONNET
 */

package application.controller.sousOnglets;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import application.controller.onglets.ControleurPrincipal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;

public class AdministrateurAjoutEmpCtrl implements Initializable {

	@FXML
	private Button boutonValider;

	@FXML
	private GridPane grille;

	@FXML
	private Label label_erreur;

	@FXML
	private Label labelEmploye;

	@FXML
	private Label labeladmin;

	@FXML
	private Label labelmail;

	@FXML
	private Label labelnom;

	@FXML
	private Label labelidentifiant;

	@FXML
	private Label labelpassword;

	@FXML
	private Label labelprenom;

	@FXML
	private Label labelrole;

	@FXML
	private Label labeltel;

	@FXML
	private TextField mail;

	@FXML
	private TextField tel;

	@FXML
	private TextField nom;

	@FXML
	private TextField password;

	@FXML
	private TextField identifiant;

	@FXML
	private TextField prenom;

	@FXML
	private ComboBox<String> admin;

	@FXML
	private ComboBox<String> role;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		role.getItems().addAll("Accueil", "Entretien");
		admin.getItems().addAll("0", "1");
	}

	//////////// Méthodes du controleur ///////////////

	@FXML
	void Valider(ActionEvent event) {

		// Recuperation des champs remplis par l'utilisateur
		String nom = this.nom.getText();
		String prenom = this.prenom.getText();
		String tel = this.tel.getText();
		String mail = this.mail.getText();
		String admin = this.admin.getValue();
		String role = this.role.getValue();
		String password = Integer.toString(this.password.getText().hashCode());
		String login = this.identifiant.getText();
		int id_personne = 0;

		/* On controle les donnees saisies dans le formulaire */
		try {
			Integer.parseInt(tel);
			Integer.parseInt(admin);
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

			// Creation du tuple dans la table Personne, le AUTO_INCREMENT
			// se charge de donner une valeur a id_personne
			PreparedStatement requete1 = conn
					.prepareStatement("INSERT INTO " + "Personne (nom,prenom,tel,mail) VALUES (?,?,?,?)");
			requete1.setString(1, nom);
			requete1.setString(2, prenom);
			requete1.setString(3, tel);
			requete1.setString(4, mail);

			requete1.executeUpdate();

			// Permet de recuperer le id_personne tout juste cree
			PreparedStatement requete2 = conn.prepareStatement(
					"SELECT id_personne " + "FROM Personne WHERE nom=? AND prenom=? AND tel=? AND mail=?");
			requete2.setString(1, nom);
			requete2.setString(2, prenom);
			requete2.setString(3, tel);
			requete2.setString(4, mail);
			ResultSet id_resultat = requete2.executeQuery();
			if (id_resultat.next()) {
				id_personne = id_resultat.getInt(1);
			}

			// Creation du tuple dans le table Employe
			PreparedStatement requete3 = conn.prepareStatement(
					"INSERT INTO Employe (id_personne,admin,role,password, id_connexion" + ") VALUES (?,?,?,?,?)");
			requete3.setInt(1, id_personne);
			requete3.setString(2, admin);
			requete3.setString(3, role);
			requete3.setString(4, password);
			requete3.setString(5, login);

			// Execution de la requete dans la table Employe
			requete3.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		/* Envoie de l'information au controleur principal et actualisation du tableau*/
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Employé (" + nom + " " + prenom + ") ajouté");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}
}
