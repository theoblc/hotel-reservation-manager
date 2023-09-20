
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
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AdministrateurModifChambreCtrl implements Initializable {

	@FXML
	private Button boutonModif;

	@FXML
	private Button boutonValider;

	@FXML
	private Label label_erreur;

	@FXML
	private TextField modif_txt_double;

	@FXML
	private TextField modif_txt_genre;

	@FXML
	private TextField modif_txt_numero;

	@FXML
	private TextField modif_txt_prix;

	@FXML
	private TextField modif_txt_simple;

	@FXML
	private TextField modif_txt_statut;

	@FXML
	private TableView<ObservableList<Object>> tab_modif;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement("SELECT * FROM Chambre;");

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
		int numero = (int) tab_modif.getSelectionModel().getSelectedItem().get(0);
		String genre = (String) tab_modif.getSelectionModel().getSelectedItem().get(1);
		double prix = (double) tab_modif.getSelectionModel().getSelectedItem().get(2);
		int statut = (int) tab_modif.getSelectionModel().getSelectedItem().get(3);
		int nb_lits_simples = (int) tab_modif.getSelectionModel().getSelectedItem().get(4);
		int nb_lits_doubles = (int) tab_modif.getSelectionModel().getSelectedItem().get(5);

		modif_txt_numero.setText(Integer.toString(numero));
		modif_txt_genre.setText(genre);
		modif_txt_prix.setText(Double.toString(prix));
		modif_txt_statut.setText(Integer.toString(statut));
		modif_txt_simple.setText(Integer.toString(nb_lits_simples));
		modif_txt_double.setText(Integer.toString(nb_lits_doubles));

	}

	public void Valider(ActionEvent event) {

		// Recuperation des champs remplis par l'utilisateur
		String numero = modif_txt_numero.getText();
		String genre = modif_txt_genre.getText();
		String prix = modif_txt_prix.getText();
		String statut = modif_txt_statut.getText();
		String nb_lits_simples = modif_txt_simple.getText();
		String nb_lits_doubles = modif_txt_double.getText();

		/* On controle les donnees saisies dans le formulaire */
		try {
			int inumero = Integer.parseInt(numero);
			double iprix = Double.parseDouble(prix);
			double inb_lits_simples = Double.parseDouble(nb_lits_simples);
			double inb_lits_doubles = Double.parseDouble(nb_lits_doubles);
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

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn.prepareStatement("UPDATE Chambre "
					+ "SET genre=?, prix=?, statut=?, nb_lits_simples=?," + " nb_lits_doubles=?" + "WHERE numero = ?");
			requete.setString(1, genre);
			requete.setString(2, prix);
			requete.setString(3, statut);
			requete.setString(4, nb_lits_simples);
			requete.setString(5, nb_lits_doubles);
			requete.setString(6, numero);

			// Execution de la requete
			requete.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		/* Envoie de l'information au controleur principal et actualisation du tableau*/
		ControleurPrincipal ctrl = ControleurPrincipal.getControleurPrincipal();
		ctrl.actualiser();
		ctrl.ajoutEvenement("Chambre numéro (" + numero + ") modifiée");

		/* Ferme la fenetre */
		Stage stage = (Stage) boutonValider.getScene().getWindow();
		stage.close();
	}

}
