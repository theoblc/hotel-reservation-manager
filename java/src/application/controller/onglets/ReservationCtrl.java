
/**
 * Controleur de l'onglet Administrateur
 * 
 * @author Pauline SPINGA sauf mention contraire
 */

package application.controller.onglets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import application.DataBase;
import application.Main;
import javafx.collections.ObservableList;

public class ReservationCtrl {

	@FXML
	private Button res_b_ajout;

	@FXML
	private Button res_b_liste;

	@FXML
	private Button res_b_liste2;

	@FXML
	private Button res_b_modif;

	@FXML
	private Button res_b_supp;

	@FXML
	private Button res_b_valider;

	@FXML
	private Label res_label_resultat;

	@FXML
	private TableView<ObservableList<Object>> res_tab;

	@FXML
	private TextField res_text_datedeb;

	@FXML
	private TextField res_text_datefin;

	@FXML
	private TextField res_text_nom;

	@FXML
	private TextField res_text_num;

	@FXML
	private AnchorPane reservationView;

	@FXML
	private DatePicker res_dp_dateDeb;

	@FXML
	private DatePicker res_dp_dateFin;

	@FXML
	private Button list_reserv;

	//////////// Lien vers la database ///////////

	String URLDBMYSQL = "jdbc:mysql://157.159.195.53:3306/";
	String DBNAME = "Hotel";
	String USER = "pro3600";
	String PASS = "pro3600BDD";

	String URLOPTIONS = "?useSSL=false&useUnicode=true"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false"
			+ "&serverTimezone=UTC&allowPublicKeyRetrieval=true";

////////////Methodes Utiles ///////////

// Methode qui prend en entree une requete INSERT et execute cette requete
	public void data_modify(String req) {
		try (DataBase dataBase = new DataBase(URLDBMYSQL, URLOPTIONS, DBNAME, USER, PASS)) {
			// Update avec la requete 'req'
			dataBase.insert(req);
			// Fermeture de la connection a la database
			dataBase.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

////////////Methodes de l'onglet Reservations ///////////
	@FXML
	public void initialize() {
		listeReservation();
	}

	public TableView<ObservableList<Object>> getTabReserv() {
		TableView<ObservableList<Object>> tab = res_tab;
		return tab;
	}

	public void ajoutReservation() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/ReservationAjout.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Formulaire de réservation");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void modifReservation() {

		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("fxml/sousOnglets/ReservationModif.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(page);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Formulaire de modification d'une réservation");
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void supprReservation() {

		int id = 0;

		// On traite le cas où l'utilisateur n'a pas sélectionné de réservations
		try {
			id = (int) res_tab.getSelectionModel().getSelectedItem().get(0);
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
		alert.setTitle("Suppression d'une réservation");
		alert.setHeaderText("Etes-vous certain de vouloir supprimer cette réservation ?");
		alert.setContentText("Identifiant : " + id);

		// option != null.
		Optional<ButtonType> option = alert.showAndWait();

		if (option.get() == ButtonType.OK) {

			// extraire les dates au format date et les formater en chaîne de caractère
			SimpleDateFormat formater = null;
			formater = new SimpleDateFormat("yyyy-MM-dd");

			Date LDrdateDeb = (Date) res_tab.getSelectionModel().getSelectedItem().get(6);
			String debut = formater.format(LDrdateDeb);

			Date LDrdateFin = (Date) res_tab.getSelectionModel().getSelectedItem().get(7);
			String fin = formater.format(LDrdateFin);

			String[] tabCh = null;
			ResultSet res;
			Connection conn = ControleurPrincipal.connect();

			try {

				// recherche des chambres concernées par la réservation
				PreparedStatement req = conn
						.prepareStatement("Select numero From Reservation_Chambre Where id_res = ? ;");
				req.setString(1, String.valueOf(id));
				res = req.executeQuery();
				String num = ControleurPrincipal.requestNew(res);
				tabCh = num.split("\n"); // convertir les numeros en un tableau

				// maj table Chambre_Supplement
				for (int i = 0; i < tabCh.length; i++) {
					PreparedStatement requete = conn.prepareStatement(
							"DELETE FROM Chambre_Supplement WHERE numero = ? AND date_deb = ? AND date_fin = ? ;");
					requete.setString(1, tabCh[i].trim());
					requete.setString(2, debut);
					requete.setString(3, fin);
					requete.executeUpdate();
				}

				// maj table Reservation
				PreparedStatement requete = conn.prepareStatement("DELETE FROM Reservation WHERE id_res = ? ;");
				requete.setInt(1, id);
				requete.executeUpdate();
				listeReservation(); // mise à jour de la liste des réservations

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

			 //Envoi de l'information au controleur principal et actualisation du tableau
			ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
			ctrl.actualiser();
			ctrl.ajoutEvenement("Réservation (" + id + ") supprimé");
		}
	}

	@FXML
	public void listeReservation() {
		ResultSet res;
		Connection conn = ControleurPrincipal.connect();

		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement req = conn.prepareStatement(
					"SELECT R.id_res, numero, P.nom,P.prenom,P.tel,P.mail,R.date_deb, R.date_fin, R.prix,R.id_employe FROM Personne as P NATURAL JOIN Reservation as R NATURAL JOIN Reservation_Chambre;");
			res = req.executeQuery();

			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void rechercheReservation() throws SQLException {

		// Recuperation des champs
		String idRes = res_text_num.getText();
		String nom = res_text_nom.getText();
		// chaine de caractère pour y stocker dates après conversion
		String dateDeb = null;
		String dateFin = null;

		// Récupération des DatePicker au format LocalDate
		LocalDate LDrdateDeb = res_dp_dateDeb.getValue();
		LocalDate LDrdateFin = res_dp_dateFin.getValue();

		// formatage des dates selon le pattern défini + conversion en chaine de
		// caractère
		if (LDrdateDeb != null) {
			dateDeb = LDrdateDeb.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (LDrdateFin != null) {
			dateFin = LDrdateFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		PreparedStatement ps;
		ResultSet res;
		ObservableList<ObservableList<Object>> rep_tab;

		Connection conn = ControleurPrincipal.connect();

		// ! La requete se fait soit sur l'id
		if (idRes.length() != 0) {

			ps = conn.prepareStatement(
					"SELECT nom,prenom,tel,mail,date_deb, date_fin, numero, prix, id_res, id_employe FROM Reservation NATURAL JOIN Personne NATURAL JOIN Reservation_Chambre Where id_res = ? ");
			ps.setInt(1, Integer.parseInt(idRes));
			res = ps.executeQuery();

			rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);

		}
		// ! La requete se fait soit sur le nom uniquement
		
		else if (nom.length() != 0  && idRes.length()==0 && dateDeb==null && dateDeb== null) {
			ps = conn.prepareStatement(
					"SELECT nom,prenom,tel,mail,date_deb, date_fin, numero, prix, id_res,id_employe FROM Reservation NATURAL JOIN Personne NATURAL JOIN Reservation_Chambre Where nom = ? ");
			ps.setString(1, nom);
			res = ps.executeQuery();
			rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);
		}
		// ! La requete se fait soit sur le nom et les dates de séjour
		else if (idRes.length() == 0 && nom.length() != 0 && dateDeb.length() != 0 && dateFin.length() != 0) {
			ps = conn.prepareStatement(
					"SELECT nom,prenom,tel,mail,date_deb, date_fin, numero, prix, id_res,id_employe FROM Reservation NATURAL JOIN Personne NATURAL JOIN Reservation_Chambre WHERE nom = ? AND date_deb= ? AND date_fin= ?");
			ps.setString(1, nom);
			ps.setString(2, dateDeb);
			ps.setString(3, dateFin);
			res = ps.executeQuery();
			rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);

		}
		// ! La requete se fait soit sur le nom et la date de fin de séjour
		else if (nom.length() != 0 && dateFin.length() != 0 && idRes.length() == 0 && dateDeb == null) {
			System.out.println("nom et datefin");
			ps = conn.prepareStatement(
					"SELECT nom,prenom,tel,mail,date_deb, date_fin, numero, prix, id_res,id_employe FROM Reservation NATURAL JOIN Personne  NATURAL JOIN Reservation_Chambre WHERE nom = ? AND date_fin= ?");
			ps.setString(1, nom);
			ps.setString(2, dateFin);
			res = ps.executeQuery();
			rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);

		}
		// ! La requete se fait soit sur le nom et la date de début de séjour
		else if (idRes.length() == 0 && nom.length() != 0 && dateDeb.length() != 0 && dateFin == null) {
			System.out.println("nom et datedeb");
			ps = conn.prepareStatement(
					"SELECT nom,prenom,tel,mail,date_deb, date_fin, numero, prix, id_res,id_employe FROM Reservation NATURAL JOIN Personne  NATURAL JOIN Reservation_Chambre WHERE nom = ? AND date_deb= ?");
			ps.setString(1, nom);
			ps.setString(2, dateDeb);
			res = ps.executeQuery();
			rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab);
			res_tab.setItems(rep_tab);

		}
	}


	public static ResultSet chambre_dispo_date(String debut, String fin) throws SQLException {
		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		req = conn.prepareStatement("select * from Chambre where statut <> 1 AND numero not in ( \n"
				+ "     SELECT C.numero  FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
				+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) " + "    OR (date_deb>=? AND date_fin <=? ) "
				+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
				+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

		req.setString(1, debut);
		req.setString(2, fin);

		req.setString(3, debut);
		req.setString(4, fin);

		req.setString(5, debut);
		req.setString(6, fin);
		req.setString(7, fin);

		req.setString(8, debut);
		req.setString(9, debut);
		req.setString(10, fin);

		ResultSet res = req.executeQuery();
		return res;

	}
}
