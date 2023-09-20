
/**
 * Controleur du sous onglet de l'onglet Réservation
 * 
 * @author Pauline SPINGA
 */

package application.controller.sousOnglets;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import application.controller.onglets.ControleurPrincipal;
import application.controller.onglets.ReservationCtrl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;

public class ReservationAjoutCtrl implements Initializable {

	@FXML
	private Button boutonValider;

	@FXML
	private DatePicker dateDeb;

	@FXML
	private DatePicker dateFin;

	@FXML
	private TextField email;

	@FXML
	private GridPane grille;

	@FXML
	private Label label_erreur;

	@FXML
	private Label numChambre;

	@FXML
	private Label labelTel;

	@FXML
	private Label labeldateDeb;

	@FXML
	private Label labeldateFin;

	@FXML
	private Label labelemail;

	@FXML
	private Label labelnomClient;

	@FXML
	private Label labelnomEmploye;

	@FXML
	private Label labelnumChambre;

	@FXML
	private Label labelprenomClient;

	@FXML
	private Label labelprix;

	@FXML
	private TextField prix_chambre;

	@FXML
	private TextField prix_options;

	@FXML
	private TextField prix_total;
	
	@FXML
	private TextField totalChambre;

	@FXML
	private TextField nomClient;

	@FXML
	private TextField nomEmploye;

	@FXML
	private TextField prenomClient;

	@FXML
	private TextField tel;

	@FXML
	private TextField nbChDispo;

	@FXML
	private Spinner<Integer> NbPers;

	@FXML
	private Label labelNbPers;

	@FXML
	private Button bouton_recherche;

	@FXML
	private TableView<ObservableList<Object>> res_tab_chambre;

	@FXML
	private Button bouton_calculer;

	@FXML
	private Button bouton_recherche_client;

	@FXML
	private MenuButton menu_button_supplements;

	@FXML
	private Button Option;

	@FXML
	private Button button_ajouter_chambre;

	// variable globale pour garder en mémoire la liste des suppléments
	List<CheckMenuItem> items;
	// ArrayList<CheckMenuItem> construct;
	String[] tabsupp;

	//////////// Lien vers la database ///////////

	String URLDBMYSQL = "jdbc:mysql://157.159.195.53:3306/";
	String DBNAME = "Hotel";
	String USER = "pro3600";
	String PASS = "pro3600BDD";

	String URLOPTIONS = "?useSSL=false&useUnicode=true"
			+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false"
			+ "&serverTimezone=UTC&allowPublicKeyRetrieval=true";

	/// Methodes specifiques au controleur

	public void initialize(URL arg0, ResourceBundle arg1) {
		initSpinner();
		initMenuButton();
		nomClient.setEditable(true);
		prenomClient.setEditable(true);
		tel.setEditable(true);
		email.setEditable(true);
		dateDeb.getEditor().setEditable(true);
		dateDeb.getEditor().setEditable(true);

		dateDeb.setDayCellFactory(param -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.compareTo(LocalDate.now()) < 0);
			}
		});

		dateFin.setDayCellFactory(param -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.compareTo(LocalDate.now()) < 0);
			}
		});
	}

	private void initSpinner() {
		NbPers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
	}

	// initialiser le menu avec l'ensemble des suppléments possibles
	private void initMenuButton() {
		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ResultSet res;
		String supp = null;

		try {
			req = conn.prepareStatement("SELECT nom_sup FROM Supplement;"); // rechercher la liste des suppléments
			res = req.executeQuery();
			supp = ControleurPrincipal.requestNew(res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		tabsupp = supp.split("\n"); // convertir les suppléments en un tableau
		items = Arrays.asList(new CheckMenuItem("Bar"), new CheckMenuItem("Salle de Sport"), new CheckMenuItem("Spa"));
		menu_button_supplements.getItems().addAll(items);
	}

	@FXML
	void Valider(ActionEvent event) {
		prix_total();
		/* Recuperation de l identifiant de l employe */
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		String idEmploye = ctrl.getEmploye();

		/* Recuperation des donnees du formulaire */
		String rnumChambre = numChambre.getText();
		String rnomClient = nomClient.getText();
		String rprenomClient = prenomClient.getText();
		String remail = email.getText();
		String rtel = tel.getText();
		String rprix = prix_total.getText().replaceFirst("€", "");
		ArrayList<String> selectedItems = optionChoisies();
		ArrayList<Integer> IdOption = idOption(selectedItems);
		String id_client = rechercher_client();
		String id_res = null;
		String rdateDeb = null;
		String rdateFin = null;

		/* On controle les donnees saisies dans le formulaire */
		try {
			if ((dateDeb.getValue() == null) || (dateFin.getValue() == null)) {
				label_erreur.setText("Dates invalides");
				return;
			}
			LocalDate LDrdateDeb = dateDeb.getValue();
			rdateDeb = LDrdateDeb.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			LocalDate LDrdateFin = dateFin.getValue();
			rdateFin = LDrdateFin.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

			if (LDrdateFin.isBefore(LDrdateDeb)) {
				label_erreur.setText("Dates incohérentes");
				return;
			}

			if ((rtel.length() != 0) && (rtel.length() != 10) || (!ControleurPrincipal.isValidEmailAddress(remail))) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			label_erreur.setText("Informations clients invalides");
			return;
		}

		/* Recuperation de l identifiant du client */
		Connection conn = ControleurPrincipal.connect();
		try {

			/* Ajout du client s il n existe pas et recuperation de son identifiant */
			if (id_client.length() == 0) {

				PreparedStatement reqClient = conn
						.prepareStatement("INSERT INTO Personne (nom,prenom,tel,mail) VALUES (?,?,?,?);");
				reqClient.setString(1, rnomClient);
				reqClient.setString(2, rprenomClient);
				reqClient.setString(3, rtel);
				reqClient.setString(4, remail);
				reqClient.executeUpdate();

				PreparedStatement request_id_client = conn.prepareStatement(
						"SELECT id_personne FROM Personne WHERE nom = ? AND prenom = ? AND mail = ? ;");
				request_id_client.setString(1, rnomClient);
				request_id_client.setString(2, rprenomClient);
				request_id_client.setString(3, remail);
				ResultSet resultat_id_client = request_id_client.executeQuery();
				id_client = ControleurPrincipal.requestNew(resultat_id_client);

			}

			if (IdOption.size() != 0) {
				for (int i = 0; i < IdOption.size(); i++) {
					PreparedStatement reqOp = conn.prepareStatement(
							"INSERT INTO Chambre_Supplement (numero,id_sup, date_deb, date_fin) VALUES (?,?, ?, ?);");
					reqOp.setString(1, rnumChambre);
					reqOp.setInt(2, IdOption.get(i));
					reqOp.setString(3, rdateDeb);
					reqOp.setString(4, rdateFin);
					reqOp.executeUpdate();
				}
			}

			/* Mise à jour de la table Reservation */
			PreparedStatement reqRes = conn.prepareStatement(
					"INSERT INTO Reservation (id_personne,id_employe,date_deb,date_fin, prix) VALUES (?,?,?,?,?);");
			reqRes.setInt(1, Integer.valueOf(id_client.trim()));
			reqRes.setString(2, idEmploye);
			reqRes.setString(3, rdateDeb);
			reqRes.setString(4, rdateFin);
			reqRes.setDouble(5, Double.valueOf(rprix));
			reqRes.executeUpdate();

			/* Recuperation de l identifiant de la reservation */
			PreparedStatement request_id_res = conn.prepareStatement(
					"SELECT id_res FROM Reservation WHERE id_personne = ? AND id_employe = ? AND date_deb = ? AND date_fin = ? ;");
			request_id_res.setString(1, id_client);
			request_id_res.setString(2, idEmploye);
			request_id_res.setString(3, rdateDeb);
			request_id_res.setString(4, rdateFin);
			ResultSet resultat_id_res = request_id_res.executeQuery();
			id_res = ControleurPrincipal.requestNew(resultat_id_res);

			/* Mise à jour de la table Reservation_Chambre */
			PreparedStatement reqResCh = conn
					.prepareStatement("INSERT INTO Reservation_Chambre (id_res,numero) VALUES (?,?);");
			reqResCh.setString(1, id_res);
			reqResCh.setInt(2, Integer.valueOf(rnumChambre));
			reqResCh.executeUpdate();

			// On rend indisponible les champs clients et dates de réservations
			nomClient.setEditable(false);
			prenomClient.setEditable(false);
			tel.setEditable(false);
			email.setEditable(false);
			// On nettoie le label affichant les erreurs
			label_erreur.setText("");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String id = id_res.replace("\n", "");
		/*
		 * Envoie de l'information au controleur principal et actualisation du tableau
		 */
		ctrl.ajoutEvenement("Réservation ( " + id + ") ajoutée");
		ctrl.actualiser();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Ajout d'une réservation");
		alert.setHeaderText(null);
		alert.setContentText("Ajout réussi ! Vous pouvez désormais quitter cet onglet ou ajouter d'autres chambres.");

		alert.showAndWait();
	}

	// méthode qui permet de rechercher une chambre à partir des champs saisis
	// (date; nb personne)
	public void rechercheDispo() {

		LocalDate LDrdateDeb = dateDeb.getValue();
		LocalDate LDrdateFin = dateFin.getValue();

		// debut comparaison des dates

		String rdateDeb = null;
		String rdateFin = null;
		int rNbPersonne = NbPers.getValue();

		if (LDrdateDeb != null) {
			rdateDeb = LDrdateDeb.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		if (LDrdateFin != null) {
			rdateFin = LDrdateFin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}

		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ObservableList<ObservableList<Object>> rep_tab;
		ResultSet res;

		if (LDrdateDeb != null && LDrdateFin != null && rNbPersonne == 0) {
			try {
				// liste des chambres dispos
				res = ReservationCtrl.chambre_dispo_date(rdateDeb, rdateFin);
				rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab_chambre);
				res_tab_chambre.setItems(rep_tab);

				// nombre de chambres dispos
				req = conn.prepareStatement("select Count(numero) from Chambre where statut <> 1 AND numero not in ( \n"
						+ "     SELECT C.numero  FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
						+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) "
						+ "    OR (date_deb>=? AND date_fin <=? ) "
						+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
						+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

				req.setString(1, rdateDeb);
				req.setString(2, rdateFin);

				req.setString(3, rdateDeb);
				req.setString(4, rdateFin);

				req.setString(5, rdateDeb);
				req.setString(6, rdateFin);
				req.setString(7, rdateFin);

				req.setString(8, rdateDeb);
				req.setString(9, rdateDeb);
				req.setString(10, rdateFin);

				res = req.executeQuery();
				String nbDispo = ControleurPrincipal.requestNew(res);
				nbChDispo.setText(nbDispo);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (LDrdateDeb != null && LDrdateFin != null && rNbPersonne != 0) {
			try {
				// liste des chambres disponibles
				req = conn.prepareStatement(
						"select * from Chambre where nb_lits_simples + 2*nb_lits_doubles >= ? AND statut <> 1 AND numero not in ( \n"
								+ "     SELECT C.numero FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
								+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) "
								+ "    OR (date_deb>=? AND date_fin <=? ) "
								+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
								+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

				req.setInt(1, rNbPersonne);

				req.setString(2, rdateDeb);
				req.setString(3, rdateFin);

				req.setString(4, rdateDeb);
				req.setString(5, rdateFin);

				req.setString(6, rdateDeb);
				req.setString(7, rdateFin);
				req.setString(8, rdateFin);

				req.setString(9, rdateDeb);
				req.setString(10, rdateDeb);
				req.setString(11, rdateFin);

				res = req.executeQuery();
				rep_tab = ControleurPrincipal.requeteTabNew(res, res_tab_chambre);
				res_tab_chambre.setItems(rep_tab);

				// nombre de chambres dispo
				req = conn.prepareStatement(
						"select Count(numero) from Chambre where nb_lits_simples + 2*nb_lits_doubles >= ? AND statut <> 1 AND numero not in ( \n"
								+ "     SELECT C.numero FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
								+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) "
								+ "    OR (date_deb>=? AND date_fin <=? ) "
								+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
								+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

				req.setInt(1, rNbPersonne);

				req.setString(2, rdateDeb);
				req.setString(3, rdateFin);

				req.setString(4, rdateDeb);
				req.setString(5, rdateFin);

				req.setString(6, rdateDeb);
				req.setString(7, rdateFin);
				req.setString(8, rdateFin);

				req.setString(9, rdateDeb);
				req.setString(10, rdateDeb);
				req.setString(11, rdateFin);

				res = req.executeQuery();
				String nbDispo = ControleurPrincipal.requestNew(res);
				nbChDispo.setText(nbDispo);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// méthode qui récupère les options cochées
	public ArrayList<String> optionChoisies() {
		ArrayList<String> selectedItems = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isSelected()) {
				selectedItems.add(tabsupp[i]);
			}
		}

		return selectedItems;

	}

	public ArrayList<Integer> idOption(ArrayList<String> options) {

		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ResultSet res;
		ArrayList<Integer> TabId = new ArrayList<>();

		for (int i = 0; i < options.size(); i++) {
			try {
				req = conn.prepareStatement("SELECT id_sup FROM Supplement Where nom_sup= ? ;");
				req.setString(1, options.get(i).trim());
				res = req.executeQuery();
				String r = ControleurPrincipal.requestNew(res);
				TabId.add(Integer.valueOf(r.trim()));

			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}

		return TabId;

	}

	public void calculer() {

		/* Recuperation de l identifiant de l employe */
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		String idEmploye = ctrl.getEmploye();
		nomEmploye.setText(String.valueOf(idEmploye));

		// récupérer date de séjour
		LocalDate LDrdateDeb = dateDeb.getValue();
		LocalDate LDrdateFin = dateFin.getValue();

		double prix_unitaire = 0;

		ArrayList<String> selectedItems = optionChoisies(); // récupère les suppléments choisis
		ArrayList<Double> prix_unitaire_option = new ArrayList<>();

		// récupération id chambre choisie
		// int id= Integer.parseInt(numChambre.getText());
		int id = (int) res_tab_chambre.getSelectionModel().getSelectedItem().get(0);
		numChambre.setText(String.valueOf(id));

		// calcul durée de séjour

		int duree = (int) ChronoUnit.DAYS.between(LDrdateDeb, LDrdateFin);
		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ResultSet res;

		// récupérer le prix de chaque option
		for (int i = 0; i < selectedItems.size(); i++) {
			try {
				req = conn.prepareStatement("SELECT prix_sup FROM Supplement Where nom_sup=?;");
				req.setString(1, selectedItems.get(i).trim());
				res = req.executeQuery();
				prix_unitaire_option.add(Double.valueOf(ControleurPrincipal.requestNew(res)));
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			req = conn.prepareStatement("SELECT prix FROM Chambre Where numero = ?;");
			req.setInt(1, id);
			res = req.executeQuery();
			prix_unitaire = Double.valueOf(ControleurPrincipal.requestNew(res));

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		// calcul du prix en fonction du prix unitaire et de la durée
		double prixChambre = prix_unitaire * duree;
		double prixOptions = 0;

		for (int i = 0; i < prix_unitaire_option.size(); i++) {
			prixOptions += prix_unitaire_option.get(i);
		}

		prix_chambre.setText(Double.toString(prixChambre) + "€");
		prix_options.setText(Double.toString(prixOptions) + "€");
		totalChambre.setText(Double.toString(prixChambre + prixOptions) + "€");
		//prix_total.setText(Double.toString(prixChambre + prixOptions));
	}
	
	public void prix_total() {
		
		Double prixtotch = Double.valueOf(totalChambre.getText().replaceFirst("€", ""));
		prix_total.setText(Double.toString(prixtotch) + "€");
	}

	public double maj_prix(String prix1) {
		Double total = 0.0;
		Double prix_reserv = Double.valueOf(prix_total.getText().replaceFirst("€", ""));
		total = prix_reserv + Double.valueOf(prix1);
		return total;
	}

	public String rechercher_client() {

		String rnomClient = nomClient.getText();
		String rprenomClient = prenomClient.getText();
		String remail = email.getText();
		String rtel = tel.getText();
		String id = "";

		nomClient.setStyle("-fx-background-color:#FFFFFF");
		prenomClient.setStyle("-fx-background-color:#FFFFFF");
		email.setStyle("-fx-background-color:#FFFFFF");
		tel.setStyle("-fx-background-color:#FFFFFF");

		Connection conn = ControleurPrincipal.connect();
		ResultSet res;
		PreparedStatement req;

		if (rnomClient.length() != 0 && rprenomClient.length() != 0 && remail.length() != 0 && rtel.length() == 0) {
			try {
				req = conn.prepareStatement(
						"SELECT id_personne FROM Personne WHERE nom = ? AND prenom = ? AND mail= ? ;");
				req.setString(1, rnomClient);
				req.setString(2, rprenomClient);
				req.setString(3, remail);
				res = req.executeQuery();
				id = ControleurPrincipal.requestNew(res);

				if (id != "") {
					nomClient.setStyle("-fx-background-color:#9DE57A");
					prenomClient.setStyle("-fx-background-color:#9DE57A");
					email.setStyle("-fx-background-color:#9DE57A");
				} else {
					nomClient.setStyle("-fx-background-color:#F73030");
					prenomClient.setStyle("-fx-background-color:#F73030");
					email.setStyle("-fx-background-color:#F73030");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (rnomClient.length() != 0 && rprenomClient.length() != 0 && remail.length() != 0 && rtel.length() != 0) {
			try {
				req = conn.prepareStatement(
						"SELECT id_personne FROM Personne WHERE nom = ? AND prenom = ? AND mail= ? AND tel= ?;");
				req.setString(1, rnomClient);
				req.setString(2, rprenomClient);
				req.setString(3, remail);
				req.setString(4, rtel);
				res = req.executeQuery();
				id = ControleurPrincipal.requestNew(res);

				if (id != "") {
					nomClient.setStyle("-fx-background-color:#9DE57A");
					prenomClient.setStyle("-fx-background-color:#9DE57A");
					email.setStyle("-fx-background-color:#9DE57A");
					tel.setStyle("-fx-background-color:#9DE57A");
				} else {
					nomClient.setStyle("-fx-background-color:#F73030");
					prenomClient.setStyle("-fx-background-color:#F73030");
					email.setStyle("-fx-background-color:#F73030");
					tel.setStyle("-fx-background-color:#F73030");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return id;

	}

	public void ajouter_chambre() {
		/* Recuperation de l identifiant de l employe */
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		String id_employe = ctrl.getEmploye().trim();
		nomEmploye.setText(id_employe);

		// récupération des données du formulaires
		String rnumChambre = numChambre.getText();

		ArrayList<String> selectedItems = optionChoisies();
		ArrayList<Integer> IdOption = idOption(selectedItems);

		LocalDate LDrdateDeb = dateDeb.getValue();
		String rdateDeb = LDrdateDeb.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		LocalDate LDrdateFin = dateFin.getValue();
		String rdateFin = LDrdateFin.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

		String id_client = rechercher_client();

		Connection conn = ControleurPrincipal.connect();

		try {
			// récupération id réservation
			PreparedStatement request_id_res = conn.prepareStatement(
					"SELECT id_res FROM Reservation WHERE id_personne = ? AND id_employe = ? AND date_deb = ? AND date_fin = ? ;");
			request_id_res.setString(1, id_client);
			request_id_res.setInt(2, Integer.valueOf(id_employe));
			request_id_res.setString(3, rdateDeb);
			request_id_res.setString(4, rdateFin);
			ResultSet resultat_id_res = request_id_res.executeQuery();
			String id_res = ControleurPrincipal.requestNew(resultat_id_res);

			// MAJ table chambre_supplement
			if (IdOption.size() != 0) {
				for (int i = 0; i < IdOption.size(); i++) {
					PreparedStatement reqOp = conn.prepareStatement(
							"INSERT INTO Chambre_Supplement (numero,id_sup, date_deb, date_fin) VALUES (?,?, ?, ?);");
					reqOp.setString(1, rnumChambre);
					reqOp.setInt(2, IdOption.get(i));
					reqOp.setString(3, rdateDeb);
					reqOp.setString(4, rdateFin);
					reqOp.executeUpdate();
				}
			}

			// MAJ table Reservation_Chambre
			PreparedStatement reqResCh = conn
					.prepareStatement("INSERT INTO Reservation_Chambre (id_res, numero) VALUES (?,?);");
			reqResCh.setString(1, id_res);
			reqResCh.setString(2, rnumChambre);
			reqResCh.executeUpdate();

			// affichage maj prix total
			prix_total.setText(String.valueOf(maj_prix(totalChambre.getText().replaceFirst("€", ""))) + " €");

			// maj du prix total de la réservation
			try {
				PreparedStatement requete = conn.prepareStatement("UPDATE Reservation SET prix=? WHERE id_res = ?");
				requete.setDouble(1, Double.valueOf(prix_total.getText().replaceFirst("€", "")));
				requete.setString(2, id_res);
				requete.executeUpdate();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
} // fin ctrl
