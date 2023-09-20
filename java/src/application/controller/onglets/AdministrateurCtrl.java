
/**
 * Controleur de l'onglet Administrateur
 * 
 * @author Théo BLANCHONNET sauf mention contraire
 */

package application.controller.onglets;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;

public class AdministrateurCtrl implements Initializable {

	@FXML
	private TableView<ObservableList<Object>> adm_tab_cha;

	@FXML
	private TableView<ObservableList<Object>> adm_tab_emp;

	@FXML
	private Button rafraichir;

	@FXML
	private AnchorPane administrateurView;

	@FXML
	private Button b_aj_chambre;

	@FXML
	private Button b_ajout_emp;

	@FXML
	private Button b_consult_chambre;

	@FXML
	private Button b_consult_emp;

	@FXML
	private Button b_modif_chambre;

	@FXML
	private Button b_modif_emp;

	@FXML
	private Button b_suppr_chambre;

	@FXML
	private Button b_suppr_emp;

	public void refresh() {
		listeChambre();
		listeEmp();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		refresh();
	}

	public void ajoutChambre() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/AdministrateurAjoutChambre.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Ajouter une chambre");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	public void modifChambre() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/AdministrateurModifChambre.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Modifier une chambre");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	/**
	 * Fonction supprimant la chambre selectionnee dans le tableau
	 * 
	 * @author Devan PRIGENT
	 */
	public void supprChambre() {

		int id = 0;

		// On traite le cas où l'utilisateur n'a pas sélectionné de chambres
		try {
			id = (int) adm_tab_cha.getSelectionModel().getSelectedItem().get(0);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Erreur");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner une chambre.");
			alert.showAndWait();
			return;
		}

		// On demande une confirmation pour la suppression
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Suppression d'une chambre");
		alert.setHeaderText("Etes-vous certain de vouloir supprimer cette chambre ?");
		alert.setContentText("Numéro : " + id);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == ButtonType.OK) {

			// Recuperation des champs remplis par l'utilisateur
			int numero = (int) adm_tab_cha.getSelectionModel().getSelectedItem().get(0);
			// Connection a la base de donnees
			Connection conn = ControleurPrincipal.connect();
			try {

				// Formulation de la requete sous forme de prepareStatement
				PreparedStatement requete_pre = conn.prepareStatement("SELECT * FROM Reservation_Chambre WHERE numero = ?");
				requete_pre.setInt(1, numero);
				ResultSet res = requete_pre.executeQuery();
				String reservations = ControleurPrincipal.requestNew(res);
				
				if (reservations != "") {
					Alert alert2 = new Alert(AlertType.WARNING);
					alert2.setTitle("Erreur");
					alert2.setHeaderText(null);
					alert2.setContentText("Opération impossible, des réservations concernent cette chambre");
					alert2.showAndWait();
					return;
				}
				// Execution de la requete
				
				
				// Formulation de la requete sous forme de prepareStatement
				PreparedStatement requete = conn.prepareStatement("DELETE FROM Chambre WHERE numero = ?");
				requete.setInt(1, numero);
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
			ctrl.ajoutEvenement("Chambre numéro (" + numero + ") supprimée");
		}
	}

	public void listeChambre() {
		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Chambre;");

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					this.adm_tab_cha);
			adm_tab_cha.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void ajoutEmp() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/AdministrateurAjoutEmp.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Ajouter un employé");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	public void modifEmp() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/AdministrateurModifEmp.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setTitle("Modifier un employé");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	/**
	 * Fonction supprimant l employe selectionnee dans le tableau
	 * 
	 * @author Devan PRIGENT
	 */
	public void supprEmp() {

		int id_employe = 0;
		int id_personne = 0;

		// On traite le cas où l'utilisateur n'a pas sélectionné de chambres
		try {
			// Recuperation des champs selectionnés dans le TableView
			id_employe = (int) adm_tab_emp.getSelectionModel().getSelectedItem().get(4);
			id_personne = (int) adm_tab_emp.getSelectionModel().getSelectedItem().get(5);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Erreur");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner un employé.");
			alert.showAndWait();
			return;
		}

		// On demande une confirmation pour la suppression
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Suppression d'un employé");
		alert.setHeaderText("Etes-vous certain de vouloir supprimer cet employé ?");
		alert.setContentText("Identifiant : " + id_employe);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == ButtonType.OK) {

			String[] infos = null;

			// Connection a la base de donnees
			Connection conn = ControleurPrincipal.connect();
			try {
				/// On recupere le nom et le prenom de l employe
				PreparedStatement verif = conn
						.prepareStatement("SELECT nom,prenom FROM Personne WHERE id_personne = ?");
				verif.setInt(1, id_personne);
				// Execution de la requete
				ResultSet req = verif.executeQuery();
				String res = ControleurPrincipal.requestNew(req);
				infos = res.split(" ");

				// Formulation de la requete sous forme de prepareStatement
				PreparedStatement requete1 = conn.prepareStatement("DELETE FROM Employe WHERE id_employe = ?");
				requete1.setInt(1, id_employe);

				// Execution de la requete
				requete1.executeUpdate();

				// Formulation de la requete sous forme de prepareStatement
				PreparedStatement requete2 = conn.prepareStatement("DELETE FROM Personne WHERE id_personne = ?");
				requete2.setInt(1, id_personne);

				// Execution de la requete
				requete2.executeUpdate();
			} catch (SQLException e) {
				Alert alert2 = new Alert(AlertType.WARNING);
				alert2.setTitle("Erreur");
				alert2.setHeaderText(null);
				alert2.setContentText("Cet employé a effectué des réservations");
				alert2.showAndWait();
				return;
			}

			/*
			 * Envoie de l'information au controleur principal et actualisation du tableau
			 */
			ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
			ctrl.actualiser();
			ctrl.ajoutEvenement("Employé (" + infos[0] + " " + infos[1] + ") supprimé");
		}
	}

	public void listeEmp() {

		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn
					.prepareStatement("SELECT P.nom, P.prenom, E.role, E.admin, E.id_employe, P.id_personne "
							+ "FROM Employe E JOIN Personne P ON E.id_personne = P.id_personne;");

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					this.adm_tab_emp);
			adm_tab_emp.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}