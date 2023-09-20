
/**
 * Controleur de l'onglet Administrateur
 * 
 * @author Charlie MORRISSEY sauf mention contraire
 */


package application.controller.onglets;

import application.Main;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;

public class ClientCtrl implements Initializable {

	@FXML
	private Button cli_b_liste;

	@FXML
	private Button cli_b_ajout;

	@FXML
	private Button cli_b_modif;

	@FXML
	private Button cli_b_supp;

	@FXML
	private Button cli_b_valider;

	@FXML
	private Label cli_label_resultat;

	@FXML
	private TableView<ObservableList<Object>> cli_tab;

	@FXML
	private TextField cli_text_nom;

	@FXML
	private TextField cli_text_prenom;

	@FXML
	private AnchorPane clientView;

////////////Methodes de l'onglet Clients ///////////

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listeClient();
	}

	@FXML
	public void ajoutClient() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/ClientAjout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setTitle("Ajouter un client");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	@FXML
	public void modifClient() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/ClientModif.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setTitle("Modifier client");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	
	/**
	 * Fonction supprimant le client selectionne dans le tableau
	 * 
	 * @author Devan PRIGENT 
	 */
	public void supprClient() {

		int id = 0;

		// On traite le cas où l'utilisateur n'a pas sélectionné de clients
		try {
			id = (int) cli_tab.getSelectionModel().getSelectedItem().get(0);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Erreur");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner une réservation.");
			alert.showAndWait();
			return;
		}

		// On demande une confirmation pour la suppression
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Suppression d'un client");
		alert.setHeaderText("Etes-vous certain de vouloir supprimer ce client ?");
		alert.setContentText("Identifiant : " + id);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == ButtonType.OK) {

			String[] infos = null;
			// Connection a la base de donnees
			Connection conn = ControleurPrincipal.connect();
			try {
				/// On recupere le nom et le prenom du client
				PreparedStatement verif = conn.prepareStatement("SELECT nom,prenom FROM Personne WHERE id_personne = ?");
				verif.setInt(1, id);
				// Execution de la requete
				ResultSet req = verif.executeQuery();
				String res = ControleurPrincipal.requestNew(req);
				infos = res.split(" ");

				/// On supprime le client de la BDD
				// Formulation de la requete sous forme de prepareStatement
				PreparedStatement requete = conn.prepareStatement("DELETE FROM Personne WHERE id_personne = ?");
				requete.setInt(1, id);

				// Execution de la requete
				requete.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
			
			/* Envoie de l'information au controleur principal et actualisation du tableau*/
			ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
			ctrl.actualiser();
			ctrl.ajoutEvenement("Client (" + infos[0] + " " + infos[1] + ") supprimé");
		}
	}

	public void listeClient() {
		Connection conn = ControleurPrincipal.connect();
		try {
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Personne WHERE Personne.id_personne NOT IN (SELECT Employe.id_personne FROM Employe);");

			ResultSet resultat = requete.executeQuery();
		
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat, cli_tab);
			cli_tab.setItems(rep_tab);
		}
		catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		}

// rechercher un client par : {nom}, {prenom}, {nom, prenom}
	public void rechercheClient() {

		try {
			String prenom = cli_text_prenom.getText();
			String nom = cli_text_nom.getText();
			PreparedStatement requete;
			Connection conn = ControleurPrincipal.connect();

			if (prenom.isEmpty()) {
				requete = conn.prepareStatement("SELECT * FROM Personne WHERE nom = ? AND id_personne NOT IN (SELECT Employe.id_personne FROM Employe)");
				requete.setString(1, nom);
			} else if (nom.isEmpty()) {
				requete = conn.prepareStatement("SELECT * FROM Personne WHERE prenom = ? AND id_personne NOT IN (SELECT Employe.id_personne FROM Employe)");
				requete.setString(1, prenom);
			} else {
				requete = conn.prepareStatement("SELECT * FROM Personne WHERE nom = ? AND prenom = ? AND id_personne NOT IN (SELECT Employe.id_personne FROM Employe)");
				requete.setString(1, nom);
				requete.setString(2, prenom);
			}
			System.out.println(requete);
			ResultSet resultat = requete.executeQuery();
			System.out.println(resultat);
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat, cli_tab);
			cli_tab.setItems(rep_tab);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
