
/**
 * Controleur du sous onglet de l'onglet Client
 * 
 * @author Charlie MORRISSEY
 */

package application.controller.sousOnglets;

import application.controller.onglets.ControleurPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;

public class ClientAjoutCtrl {

	@FXML
	private Button boutonValider;

	@FXML
	private TextField email;

	@FXML
	private GridPane grille;

	@FXML
	private TextField id_personne;

	@FXML
	private Label label_erreur;

	@FXML
	private Label labelTel;

	@FXML
	private Label labelemail;

	@FXML
	private Label labelnomClient;

	@FXML
	private Label labelnumChambre;

	@FXML
	private Label labelprenomClient;

	@FXML
	private TextField nomClient;

	@FXML
	private TextField prenomClient;

	@FXML
	private TextField tel;

	//////////// Lien vers la database ///////////

	String URLDBMYSQL = "jdbc:mysql://157.159.195.53:3306/";
	String DBNAME = "Hotel";
	String USER = "pro3600";
	String PASS = "pro3600BDD";

	String URLOPTIONS = "?useSSL=false&useUnicode=true"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false"
			+ "&serverTimezone=UTC&allowPublicKeyRetrieval=true";

	//////////// Methodes Utiles ///////////

	// Methode qui prend en entree une requete et renvoie le resultat de cette
	// requete

	// Methode specifique au controleur
	@FXML
	void Valider(ActionEvent event) {

		/* Recuperation des donnees du formulaire */
		String nom = nomClient.getText();
		String prenom = prenomClient.getText();
		String email = this.email.getText();
		String tel = this.tel.getText();

		/* On controle les donnees saisies dans le formulaire */
		try {
			Integer.parseInt(tel);
			if ((tel.length() != 0) && (tel.length() != 10) || (!ControleurPrincipal.isValidEmailAddress(email))) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			label_erreur.setText("Argument(s) invalide(s)");
			return;
		}

		/* Recuperation de l identifiant du client */
		Connection conn = ControleurPrincipal.connect();
		try {
			/// On commence par vérifier si le client a deja un compte
			PreparedStatement verif = conn.prepareStatement("SELECT * FROM Personne WHERE mail = ? AND tel = ?");
			verif.setString(1, email);
			verif.setString(2, tel);
			// Execution de la requete
			ResultSet res = verif.executeQuery();
			String libre = ControleurPrincipal.requestNew(res);
			// Verification
			if (libre != "") {
				label_erreur.setText("Email et tel déjà attribués");
				return;
			}

			PreparedStatement reqClient = conn
					.prepareStatement("INSERT INTO Personne (nom,prenom,tel,mail) VALUES (?,?,?,?);");
			reqClient.setString(1, nom);
			reqClient.setString(2, prenom);
			reqClient.setString(3, tel);
			reqClient.setString(4, email);
			reqClient.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Envoie de l'information au controleur principal et actualisation du tableau
		 */
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Client (" + nom + "  " + prenom + ") ajoutée");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}
}
