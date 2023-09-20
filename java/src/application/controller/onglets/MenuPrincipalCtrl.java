
/**
 * Controleur de l'onglet Menu Principal
 * 
 * @author Devan PRIGENT
 */

package application.controller.onglets;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;

import application.Main;
import application.controller.sousOnglets.MenuPrincipalChambresCtrl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MenuPrincipalCtrl implements Initializable {

	@FXML
	private GridPane etage0;

	@FXML
	private GridPane etage1;

	@FXML
	private GridPane etage2;

	@FXML
	private GridPane etage3;

	@FXML
	private GridPane etage4;

	@FXML
	private GridPane etage5;

	@FXML
	private Label label_etage0;

	@FXML
	private Label label_etage1;

	@FXML
	private Label label_etage2;

	@FXML
	private Label label_etage3;

	@FXML
	private Label label_etage4;

	@FXML
	private Label label_etage5;

	@FXML
	private AnchorPane menuPrincipalView;

	@FXML
	private ProgressIndicator pb_etage0;

	@FXML
	private ProgressIndicator pb_etage1;

	@FXML
	private ProgressIndicator pb_etage2;

	@FXML
	private ProgressIndicator pb_etage3;

	@FXML
	private ProgressIndicator pb_etage4;

	@FXML
	private ProgressIndicator pb_etage5;

	@FXML
	private Button pri_b_histo;

	@FXML
	private Button b_disconnect;

	@FXML
	private Label pri_event;

	@FXML
	public VBox vboxEvent;

	@FXML
	private TableView<ObservableList<Object>> pri_tab_chambre;

	@FXML
	private TableView<ObservableList<Object>> pri_tab_event;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		demarrage();
	}

	@FXML
	public void disconnect() {
		ControleurPrincipal.disconnect(b_disconnect);
	}

	/**
	 * Methode affichant les indicateurs textuels du tableau de bord
	 */
	public void initIndicateurTextuel(Connection conn, int etage) {
		try {

			PreparedStatement req_disponibles = conn
					.prepareStatement("SELECT COUNT(numero) FROM Chambre WHERE numero>? AND numero<? AND statut=0;");
			PreparedStatement req_indisponibles = conn
					.prepareStatement("SELECT COUNT(numero) FROM Chambre WHERE numero>? AND numero<? AND statut=1;");

			int etage_inf = etage * 100 - 1;
			int etage_sup = etage * 100 + 99;
			req_disponibles.setInt(1, etage_inf);
			req_disponibles.setInt(2, etage_sup);
			req_indisponibles.setInt(1, etage_inf);
			req_indisponibles.setInt(2, etage_sup);

			ResultSet res_libres = req_disponibles.executeQuery();
			ResultSet res_indisponibles = req_indisponibles.executeQuery();

			String libres = ControleurPrincipal.requestNew(res_libres);
			String indisponibles = ControleurPrincipal.requestNew(res_indisponibles);

			String indicateur = libres.replace("\n", "") + " chambres disponibles \n";
			indicateur += indisponibles.replace("\n", "") + " chambres indisponibles";

			if (etage == 0) {
				label_etage0.setText(indicateur);
			} else if (etage == 1) {
				label_etage1.setText(indicateur);
			} else if (etage == 2) {
				label_etage2.setText(indicateur);
			} else if (etage == 3) {
				label_etage3.setText(indicateur);
			} else if (etage == 4) {
				label_etage4.setText(indicateur);
			} else if (etage == 5) {
				label_etage5.setText(indicateur);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methode affichant les indicateurs graphiques du tableau de bord
	 */
	public void initIndicateurGraphique(Connection conn, int etage) {
		try {

			PreparedStatement req_libres = conn
					.prepareStatement("SELECT COUNT(numero) FROM Chambre WHERE numero>? AND numero<? AND statut=0;");
			PreparedStatement req_total = conn
					.prepareStatement("SELECT COUNT(numero) FROM Chambre WHERE numero>? AND numero<?;");

			int etage_inf = etage * 100 - 1;
			int etage_sup = etage * 100 + 99;
			req_libres.setInt(1, etage_inf);
			req_libres.setInt(2, etage_sup);
			req_total.setInt(1, etage_inf);
			req_total.setInt(2, etage_sup);

			ResultSet res_libres = req_libres.executeQuery();
			ResultSet res_total = req_total.executeQuery();

			String libres = ControleurPrincipal.requestNew(res_libres);
			String total = ControleurPrincipal.requestNew(res_total);

			double indicateur = (double) Integer.parseInt(libres.replaceAll("\\s+", ""))
					/ Integer.parseInt(total.replaceAll("\\s+", "")) - 0.001;

			if ((etage == 0) && (etage0.getChildren().size() > 1)) {
				pb_etage0.setProgress(indicateur);
			} else if (etage == 1) {
				pb_etage1.setProgress(indicateur);
			} else if (etage == 2) {
				pb_etage2.setProgress(indicateur);
			} else if (etage == 3) {
				pb_etage3.setProgress(indicateur);
			} else if (etage == 4) {
				pb_etage4.setProgress(indicateur);
			} else if (etage == 5) {
				pb_etage5.setProgress(indicateur);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Methode affichant le tableau de bord
	 */
	public void demarrage() {

		// On remplit les tableaux du menu principal
		listeChambre();
		listeEvenements();

		// Connexion a la BDD
		Connection conn = ControleurPrincipal.connect();
		try {

			/// Affichage des chambres
			PreparedStatement resultat = conn.prepareStatement("SELECT numero FROM Chambre;");
			ResultSet chambres = resultat.executeQuery();
			int i0 = 0;
			int i1 = 0;
			int i2 = 0;
			int i3 = 0;
			int i4 = 0;
			int i5 = 0;

			// On supprime tous les boutons de la grille
			etage0.getChildren().clear();
			etage1.getChildren().clear();
			etage2.getChildren().clear();
			etage3.getChildren().clear();

			while (chambres.next()) {

				// recuperation du numero de la chambre
				int numero = chambres.getInt("numero");
				Button boutton;
				if (numero < 10) {
					String num = "00" + Integer.toString(numero);
					// creation du bouton representant la chambre dans le tableau de bord
					boutton = new Button(num);
				} else {
					// creation du bouton representant la chambre dans le tableau de bord
					boutton = new Button(Integer.toString(numero));
				}

				// Dimensions du boutton
				boutton.setPrefWidth(45);
				boutton.setPrefHeight(45);
				// On stocke le numero de la chambre dans le boutton
				boutton.setUserData(numero);
				// On associe la fonction qui va gerer le clic
				boutton.addEventHandler(MouseEvent.MOUSE_CLICKED, listeReservations);

				// Prise en compte du code couleur pour le type du bouton : on verifie si la
				// chambre est indisponible
				// puis s'il y a des reservations qui concerne la chambre a la date actuelle
				int statut = Integer.parseInt(estIndisponible(numero, conn).replaceAll("\\s+", ""));

				if (statut == 1) {
					boutton.setStyle("-fx-border-color: #0B0B0B; -fx-background-color: #F80000; ");
				} else if (estoccupee(numero, conn)) {
					boutton.setStyle("-fx-border-color: #0B0B0B; -fx-background-color: #FFF526; ");
				} else {
					boutton.setStyle("-fx-border-color: #0B0B0B; -fx-background-color: #39C600; ");
				}

				// on associe le bouton a son etage
				if (numero < 100) {
					etage0.add(boutton, i0, 0);
					i0 += 1;
				} else if (numero < 200) {
					etage1.add(boutton, i1, 0);
					i1 += 1;
				} else if (numero < 300) {
					etage2.add(boutton, i2, 0);
					i2 += 1;
				} else if (numero < 400) {
					etage3.add(boutton, i3, 0);
					i3 += 1;
				} else if (numero < 500) {
					etage4.add(boutton, i4, 0);
					i4 += 1;
				} else {
					etage5.add(boutton, i5, 0);
					i5 += 1;
				}
			}

			/// Affichage des indicateurs par etage
			for (int i = 0; i < 6; i++) {
				initIndicateurTextuel(conn, i);
				initIndicateurGraphique(conn, i);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gestionnaire d evenement lie aux boutons
	 */
	EventHandler<MouseEvent> listeReservations = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {

			// On recupere le numero de la chambre qui est contenu dans le boutton
			Object boutton = e.getSource();
			int numero = (Integer) ((Button) boutton).getUserData();

			try {

				FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/sousOnglets/MenuPrincipalChambresCtrl.fxml"));
				MenuPrincipalChambresCtrl controleur = MenuPrincipalChambresCtrl.getControleurMenuPrincipalChambres();
				controleur.setNumero(numero);
				loader.setController(controleur);
				AnchorPane page = (AnchorPane) loader.load();

				Stage stage = new Stage();
				Scene scene = new Scene(page);
				stage.initModality(Modality.APPLICATION_MODAL);

				stage.setTitle("Liste des réservations de la chambre " + numero);
				stage.setScene(scene);
				stage.show();

			} catch (IOException err) {
				System.err.println(err.getMessage());
				err.printStackTrace();
			}
		}
	};

	/**
	 * Methodes pour determiner le statut des chambres
	 */
	public String estIndisponible(int numero, Connection conn) {

		try {
			// on determine les reservations concernees par la chambre
			PreparedStatement reqStatut = conn.prepareStatement("SELECT statut FROM Chambre WHERE numero = ?;");
			reqStatut.setInt(1, numero);
			ResultSet res_statut = reqStatut.executeQuery();
			return ControleurPrincipal.requestNew(res_statut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1";
	}

	public boolean estoccupee(int numero, Connection conn) {

		try {
			// on determine les reservations concernees par la chambre
			PreparedStatement reqReservation = conn
					.prepareStatement("SELECT id_res FROM Reservation_Chambre WHERE numero = ?;");
			reqReservation.setInt(1, numero);
			ResultSet reservations = reqReservation.executeQuery();

			// pour chacune des reservations on verifie si elle est en cours aujourd hui
			while (reservations.next()) {
				int id_res = reservations.getInt("id_res");
				// on recupere les dates de la reservation
				PreparedStatement redDates = conn
						.prepareStatement("SELECT date_deb,date_fin FROM Reservation WHERE id_res = ?;");
				redDates.setInt(1, id_res);
				ResultSet dates = redDates.executeQuery();

				while (dates.next()) {
					String date_deb = dates.getString("date_deb");
					String date_fin = dates.getString("date_fin");

					// on recupere la date actuelle et on formate les dates de la reservation
					LocalDate adj = LocalDate.now();
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate debutRes = LocalDate.parse(date_deb, dtf);
					LocalDate finRes = LocalDate.parse(date_fin, dtf);

					// on les compare pour determiner si la chambre est occupee
					if ((adj.isAfter(debutRes)) && (adj.isBefore(finRes))) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Methodes remplissant les deux tableaux du Menu Principal
	 */
	public void listeEvenements() {
		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Historique ORDER BY id_event DESC;");

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					this.pri_tab_event);
			pri_tab_event.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void listeChambre() {
		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Chambre;");

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					this.pri_tab_chambre);
			pri_tab_chambre.setItems(rep_tab);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
