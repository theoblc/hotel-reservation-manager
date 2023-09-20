
/**
 * Controleur du sous onglet de l'onglet Réservation
 * 
 * @author Pauline SPINGA
 */

package application.controller.sousOnglets;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import application.controller.onglets.ControleurPrincipal;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReservationModifCtrl implements Initializable {

	@FXML
	private TableView<ObservableList<Object>> res_tabSupp;

	@FXML
	private TableView<ObservableList<Object>> res_tabNoSupp;

	@FXML
	private TableView<ObservableList<Object>> chambre_reserv;

	@FXML
	private TextField res_numReserv;

	@FXML
	private Button button_valider;

	@FXML
	private Button button_supprCh;

	@FXML
	private Button button_supprSupplement;

	@FXML
	private Button button_ajouterSupp;

	@FXML
	private Button button_ajouterCh;

	@FXML
	private TableView<ObservableList<Object>> list_supp;

	@FXML
	private TableView<ObservableList<Object>> chambreDispo;

	@FXML
	private Spinner<Integer> NbPers;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initSpinner();
	}

	private void initSpinner() {
		NbPers.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
	}

	@FXML
	private TextField nbChambreDispo;

	// Méthodes
	public void info_reserv() {
		int id = Integer.valueOf(res_numReserv.getText());

		ResultSet res;
		Connection conn = ControleurPrincipal.connect();
		listChambreDispo(); // liste des chambres disponibles restantes aux dates de la réservation
		chambreReserv(); // liste des chambres réservées
		affichSupplement();

		// affichage des infos si la réservation a des options
		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement req = conn.prepareStatement(
					"SELECT P.nom,P.prenom,P.tel,P.mail,R.date_deb, R.date_fin, numero, R.prix, R.id_res,R.id_employe, nom_sup FROM Personne as P NATURAL JOIN Reservation as R NATURAL JOIN Reservation_Chambre NATURAL JOIN Chambre_Supplement NATURAL JOIN Supplement Where id_res = ? ;");
			req.setInt(1, id);
			res = req.executeQuery();

			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res, res_tabSupp);
			res_tabSupp.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement req = conn.prepareStatement(
					"Select P.nom,P.prenom,P.tel,P.mail,R.date_deb, R.date_fin, numero, R.prix, R.id_res,R.id_employe FROM Personne as P NATURAL JOIN Reservation as R NATURAL JOIN Reservation_Chambre Where id_res = ? AND numero NOT IN (Select numero From Personne NATURAL JOIN Reservation NATURAL JOIN Reservation_Chambre NATURAL JOIN Chambre_Supplement Where id_res = ? );");
			req.setInt(1, id);
			req.setInt(2, id);
			res = req.executeQuery();

			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res, res_tabNoSupp);
			res_tabNoSupp.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void supprSupplement() {
		Connection conn = ControleurPrincipal.connect();

		int idReserv = Integer.valueOf(res_numReserv.getText());

		SimpleDateFormat formater = null;
		formater = new SimpleDateFormat("yyyy-MM-dd");

		double prix = prixSupp();
		int id = idSupp();
		int ch = (int) res_tabSupp.getSelectionModel().getSelectedItem().get(6);

		Date debut = (Date) res_tabSupp.getSelectionModel().getSelectedItem().get(4);
		String debutstr = formater.format(debut);

		Date fin = (Date) res_tabSupp.getSelectionModel().getSelectedItem().get(5);
		String finstr = formater.format(fin);

		double new_prix = 0;

		// maj prix de la réservation
		new_prix = majPrix(prix);
		try {

			// maj table Chambre_Supplement
			PreparedStatement req = conn.prepareStatement(
					"DELETE FROM Chambre_Supplement Where numero = ? AND id_sup= ? AND date_deb = ? AND date_fin = ? ;");
			req.setInt(1, ch);
			req.setInt(2, id);
			req.setString(3, debutstr);
			req.setString(4, finstr);
			req.executeUpdate();

			// maj du prix
			setPrixReserv(new_prix, idReserv); // maj prix de la réservation
			// maj de l'affichage
			info_reserv();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

	// récupérer l'id d'un supplément
	public int idSupp() {
		Connection conn = ControleurPrincipal.connect();
		String option = (String) res_tabSupp.getSelectionModel().getSelectedItem().get(10);
		String r = "";
		try {
			PreparedStatement req = conn.prepareStatement("SELECT id_sup FROM Supplement Where nom_sup= ? ;");
			req.setString(1, option);
			ResultSet res = req.executeQuery();
			r = ControleurPrincipal.requestNew(res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return Integer.valueOf(r.trim());
	}

	// récupérer le prix d'un supplément
	public double prixSupp() {
		Connection conn = ControleurPrincipal.connect();
		String option = (String) res_tabSupp.getSelectionModel().getSelectedItem().get(10);
		String r = "";
		try {
			PreparedStatement req = conn.prepareStatement("SELECT prix_sup FROM Supplement Where nom_sup= ? ;");
			req.setString(1, option);
			ResultSet res = req.executeQuery();
			r = ControleurPrincipal.requestNew(res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return Double.valueOf(r.trim());

	}

	// récupérer le prix de la réservation
	public double prixReserv() {
		int id = Integer.valueOf(res_numReserv.getText());
		Connection conn = ControleurPrincipal.connect();
		String r = "";
		try {
			PreparedStatement req = conn.prepareStatement("SELECT prix FROM Reservation Where id_res = ? ;");
			req.setInt(1, id);
			ResultSet res = req.executeQuery();
			r = ControleurPrincipal.requestNew(res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return Double.valueOf(r);

	}

	// maj prix réservation
	public double majPrix(double p) {
		double prix = prixReserv();
		double maj = prix - p;
		return maj;
	}

	public void setPrixReserv(double prix, int id) {
		Connection conn = ControleurPrincipal.connect();
		PreparedStatement requete;
		try {
			requete = conn.prepareStatement("UPDATE Reservation " + "SET  prix=? " + "WHERE id_res = ?");
			requete.setDouble(1, prix);
			requete.setInt(2, id);
			requete.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void supprChambre() {
		Connection conn = ControleurPrincipal.connect();

		int ch = (int) res_tabNoSupp.getSelectionModel().getSelectedItem().get(6);
		int id = Integer.valueOf(res_numReserv.getText()); // id de la réservation
		int duree = duree(); // durée de la réservation
		double prix_unitaire = prixCh(); // prix de la chambre

		double prix_ch_reserv = duree * prix_unitaire; // prix de la chambre le temps de la réservation
		double prix_reserv = majPrix(prix_ch_reserv); // calculer le nouveau prix de la réservation

		setPrixReserv(prix_reserv, id); // maj prix de la réservation

		PreparedStatement req;
		try {
			req = conn.prepareStatement("DELETE FROM Reservation_Chambre Where numero = ? AND id_res = ?;");
			req.setInt(1, ch);
			req.setInt(2, id);
			req.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		info_reserv();

	}

	public double prixCh() {

		Connection conn = ControleurPrincipal.connect();

		int ch = (int) res_tabNoSupp.getSelectionModel().getSelectedItem().get(6);
		double prix_unitaire = 0.0;
		try {
			PreparedStatement req = conn.prepareStatement("SELECT prix FROM Chambre Where numero = ?;");
			req.setInt(1, ch);
			ResultSet res = req.executeQuery();
			prix_unitaire = Double.valueOf(ControleurPrincipal.requestNew(res));
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		return prix_unitaire;

	}

	public int duree() {
		Connection conn = ControleurPrincipal.connect();
		int id = Integer.valueOf(res_numReserv.getText());
		String debut;
		String fin;
		java.util.Date dateDeb;
		java.util.Date dateFin;
		long duree = 0;

		try {
			PreparedStatement req = conn.prepareStatement("SELECT date_deb FROM Reservation Where id_res = ?;");
			req.setInt(1, id);
			ResultSet res = req.executeQuery();
			debut = ControleurPrincipal.requestNew(res);

			req = conn.prepareStatement("SELECT date_fin FROM Reservation Where id_res = ?;");
			req.setInt(1, id);
			res = req.executeQuery();
			fin = ControleurPrincipal.requestNew(res);

			try {

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				dateDeb = sdf.parse(debut.trim());
				dateFin = sdf.parse(fin.trim());

				long diff = dateFin.getTime() - dateDeb.getTime();

				TimeUnit time = TimeUnit.DAYS;
				duree = time.convert(diff, TimeUnit.MILLISECONDS);

			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return (int) duree;

	}

	public void listChambreDispo() {

		Connection conn = ControleurPrincipal.connect();
		int id = Integer.valueOf(res_numReserv.getText());
		String debut = null;
		String fin = null;
		int rNbPersonne = NbPers.getValue();

		PreparedStatement req;
		try {
			req = conn.prepareStatement("SELECT date_deb FROM Reservation Where id_res = ?;");
			req.setInt(1, id);
			ResultSet res = req.executeQuery();
			debut = ControleurPrincipal.requestNew(res);
			req = conn.prepareStatement("SELECT date_fin FROM Reservation Where id_res = ?;");
			req.setInt(1, id);
			res = req.executeQuery();
			fin = ControleurPrincipal.requestNew(res);
			debut = debut.trim();
			fin = fin.trim();

			// nombre de chambres dispos
			req = conn.prepareStatement(
					"select Count(numero) from Chambre where nb_lits_simples + 2*nb_lits_doubles >= ? AND statut <> 1 AND numero not in ( \n"
							+ "     SELECT C.numero FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
							+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) "
							+ "    OR (date_deb>=? AND date_fin <=? ) "
							+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
							+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

			req.setInt(1, rNbPersonne);

			req.setString(2, debut);
			req.setString(3, fin);

			req.setString(4, debut);
			req.setString(5, fin);

			req.setString(6, debut);
			req.setString(7, fin);
			req.setString(8, fin);

			req.setString(9, debut);
			req.setString(10, debut);
			req.setString(11, fin);

			res = req.executeQuery();
			String nbDispo = ControleurPrincipal.requestNew(res);
			nbChambreDispo.setText(nbDispo);

			// liste des chambres disponible
			PreparedStatement req2 = conn.prepareStatement(
					"select * from Chambre where nb_lits_simples + 2*nb_lits_doubles >= ? AND statut <> 1 AND numero not in ( \n"
							+ "     SELECT C.numero FROM Reservation_Chambre as RC JOIN Reservation as R on (R.id_res = RC.id_res) JOIN Chambre as C on (C.numero=RC.numero)"
							+ "    Where (" + "    (date_deb<= ? AND date_fin>= ? ) "
							+ "    OR (date_deb>=? AND date_fin <=? ) "
							+ "    OR (date_deb >= ? AND date_deb < ? AND date_fin >= ? ) "
							+ "    OR (date_deb <= ? AND date_fin > ? AND date_fin <= ?)" + "     )" + "     );");

			req2.setInt(1, rNbPersonne);

			req2.setString(2, debut);
			req2.setString(3, fin);

			req2.setString(4, debut);
			req2.setString(5, fin);

			req2.setString(6, debut);
			req2.setString(7, fin);
			req2.setString(8, fin);

			req2.setString(9, debut);
			req2.setString(10, debut);
			req2.setString(11, fin);

			ResultSet res2 = req2.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res2, chambreDispo);
			chambreDispo.setItems(rep_tab);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void ajouterCh() {
		Connection conn = ControleurPrincipal.connect();
		String r="";

		// récupération des champs nécessaires
		int numCh = (int) chambreDispo.getSelectionModel().getSelectedItem().get(0);
		int id = Integer.valueOf(res_numReserv.getText());

		// calcul du prix de la chambre à ajouter en fonction de son prix unitaire et de
		// sa durée
		double prix_unitaire = (double) chambreDispo.getSelectionModel().getSelectedItem().get(2);
		int duree = duree();
		double prix_ajout = prix_unitaire * duree;
		
		//maj du prix de la réservation
		try {
			PreparedStatement req = conn.prepareStatement("SELECT prix FROM Reservation Where id_res= ? ;");
			req.setInt(1, id);
			ResultSet res = req.executeQuery();
			r = ControleurPrincipal.requestNew(res);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		prix_ajout += Double.valueOf(r);
		
		setPrixReserv(prix_ajout, id);

		
		PreparedStatement req;

		// maj table reservation_Chambre
		try {
			req = conn.prepareStatement("INSERT INTO Reservation_Chambre (id_res,numero) VALUES (?,?);");
			req.setInt(1, id);
			req.setInt(2, numCh);
			req.executeUpdate();

			info_reserv();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}// fin ajouter chambre

	//liste des chambres concernées par la réservation
	public void chambreReserv() {

		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ResultSet res;
		int id = Integer.valueOf(res_numReserv.getText());

		try {
			req = conn.prepareStatement(
					"SELECT numero FROM Reservation NATURAL JOIN Reservation_Chambre Where id_res = ?;");
			req.setInt(1, id);
			req.executeQuery();
			res = req.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res, chambre_reserv);
			chambre_reserv.setItems(rep_tab);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void affichSupplement() {
		// String[] tabsupp;
		Connection conn = ControleurPrincipal.connect();
		PreparedStatement req;
		ResultSet res;

		try {
			req = conn.prepareStatement("SELECT * FROM Supplement;"); // rechercher la liste des suppléments
			res = req.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(res, list_supp);
			list_supp.setItems(rep_tab);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void chambreSelect() {
		Connection conn = ControleurPrincipal.connect();
		int numCh = (int) chambre_reserv.getSelectionModel().getSelectedItem().get(0);
		int idSupp = (int) list_supp.getSelectionModel().getSelectedItem().get(0);
		double prixSupp = (double) list_supp.getSelectionModel().getSelectedItem().get(2);
		int idReserv = Integer.valueOf(res_numReserv.getText());

		double prix_reserv = prixReserv();
		double new_prix = prix_reserv + prixSupp;
		setPrixReserv(new_prix, idReserv);

		String debut = null;
		String fin = null;

		PreparedStatement req;
		try {
			req = conn.prepareStatement("SELECT date_deb FROM Reservation Where id_res = ?;");
			req.setInt(1, idReserv);
			ResultSet res = req.executeQuery();
			debut = ControleurPrincipal.requestNew(res);

			req = conn.prepareStatement("SELECT date_fin FROM Reservation Where id_res = ?;");
			req.setInt(1, idReserv);
			res = req.executeQuery();
			fin = ControleurPrincipal.requestNew(res);

			debut = debut.trim();
			fin = fin.trim();

			// maj Chambre_Supplement
			PreparedStatement reqOp = conn.prepareStatement(
					"INSERT INTO Chambre_Supplement (numero,id_sup, date_deb, date_fin) VALUES (?,?, ?, ?);");
			reqOp.setInt(1, numCh);
			reqOp.setInt(2, idSupp);
			reqOp.setString(3, debut);
			reqOp.setString(4, fin);
			reqOp.executeUpdate();

			info_reserv();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
