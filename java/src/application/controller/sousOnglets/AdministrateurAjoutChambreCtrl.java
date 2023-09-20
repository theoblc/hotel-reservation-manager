
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

public class AdministrateurAjoutChambreCtrl implements Initializable {

	@FXML
	private Button boutonValider;

	@FXML
	private GridPane grille;

	@FXML
	private Label label_erreur;

	@FXML
	private Label label_lits_doubles;

	@FXML
	private Label label_lits_simples;

	@FXML
	private Label labelnumero;

	@FXML
	private Label labelprix;

	@FXML
	private Label labelstatut;

	@FXML
	private Label labeltype;

	@FXML
	private TextField nb_lits_doubles;

	@FXML
	private TextField nb_lits_simples;

	@FXML
	private TextField numero;

	@FXML
	private TextField prix;

	@FXML
	private ComboBox<String> statut;

	@FXML
	private ComboBox<String> type;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		type.getItems().addAll("Simple", "Double","Suite");
		statut.getItems().addAll("0", "1");
	}

	//////////// Méthodes du controleur ///////////////

	@FXML
	void Valider(ActionEvent event) {

		// Recuperation des champs remplis par l'utilisateur
		String numero = this.numero.getText();
		String genre = this.type.getValue();
		String prix = this.prix.getText();
		String statut = this.statut.getValue();
		String nb_lits_simples = this.nb_lits_simples.getText();
		String nb_lits_doubles = this.nb_lits_doubles.getText();

		/* On controle les donnees saisies dans le formulaire */
		try {
			int inumero = Integer.parseInt(numero);
			int iprix = Integer.parseInt(prix);
			int inb_lits_simples = Integer.parseInt(nb_lits_simples);
			int inb_lits_doubles = Integer.parseInt(nb_lits_doubles);
			if ((iprix < 0) || (inumero < 0) || (inb_lits_simples < 0) || (inb_lits_doubles < 0)) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			label_erreur.setText("Argument(s) invalide(s)");
			return;
		}

		// Connection a la base de donnees
		Connection conn = ControleurPrincipal.connect();
		try {
			/// On commence par vérifier si le numero de chambre est deja attribue
			PreparedStatement verif = conn.prepareStatement("SELECT * FROM Chambre WHERE numero = ?");
			verif.setString(1, numero);
			// Execution de la requete
			ResultSet res = verif.executeQuery();
			String libre = ControleurPrincipal.requestNew(res);
			// Verification
			if (libre != "") {
				label_erreur.setText("Chambre déjà existante");
				return;
			}

			/// Ensuite on ajoute la nouvelle chambre
			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("INSERT INTO Chambre (numero,genre,prix,statut,nb_lits_simples"
							+ ",nb_lits_doubles) VALUES (?,?,?,?,?,?);");
			requete.setString(1, numero);
			requete.setString(2, genre);
			requete.setString(3, prix);
			requete.setString(4, statut);
			requete.setString(5, nb_lits_simples);
			requete.setString(6, nb_lits_doubles);
			// Execution de la requete
			requete.executeUpdate();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		/* Envoie de l'information au controleur principal et actualisation du tableau*/
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Chambre numéro (" + numero + ") ajoutée");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}
}
