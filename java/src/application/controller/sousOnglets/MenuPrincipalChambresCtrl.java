
/**
 * Controleur du sous onglet de l'onglet Menu Principal
 * 
 * @author Devan PRIGENT
 */

package application.controller.sousOnglets;

import application.controller.onglets.ControleurPrincipal;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class MenuPrincipalChambresCtrl implements Initializable{

	@FXML
	private final static MenuPrincipalChambresCtrl controleur = new MenuPrincipalChambresCtrl();
	
    @FXML
    private AnchorPane anchor;
    
    private int numero ;

	private MenuPrincipalChambresCtrl() {
	}
	
	public static MenuPrincipalChambresCtrl getControleurMenuPrincipalChambres() {
		return controleur;
	}
	
	public void setNumero(int numero) {
		this.numero = numero ;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		
		TableView<ObservableList<Object>> tab_suppr = new TableView();

		try {
			Connection conn = ControleurPrincipal.connect();
			PreparedStatement requete = conn.prepareStatement(
					"SELECT RC.numero, R.id_res,R.date_deb, R.date_fin,P.nom,P.prenom,P.tel,P.mail,R.id_employe FROM Reservation R JOIN Reservation_Chambre RC ON R.id_res = RC.id_res JOIN Personne P ON R.id_personne = P.id_personne WHERE RC.numero = ? ;");
			requete.setInt(1, this.numero);

			ResultSet resultat = requete.executeQuery();
			ObservableList<ObservableList<Object>> rep_tab = ControleurPrincipal.requeteTabNew(resultat,
					tab_suppr);
			tab_suppr.setItems(rep_tab);
			this.anchor.getChildren().add(tab_suppr);
		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
}
