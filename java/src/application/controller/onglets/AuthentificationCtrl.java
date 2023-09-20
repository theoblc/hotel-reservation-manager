
/**
 * Controleur de l'onglet Administrateur
 * 
 * @author Théo BLANCHONNET sauf mention contraire
 */

package application.controller.onglets;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AuthentificationCtrl implements Initializable {

	@FXML
	private Button b_connect;

	@FXML
	private ProgressIndicator progress;

	@FXML
	private Label incorrect;

	@FXML
	private TextField id_field;

	@FXML
	private PasswordField password_field;

	@FXML
	private TextField unmask_field;

	@FXML
	private CheckBox check_pass;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// On laisse le textfield incorrect vide
		incorrect.setText("");
		// On n'affiche pas encore le unmask_field
		unmask_field.setVisible(false);
	}

	@FXML
	public void unmask() {
		// Si la case est cochée, on affiche le unmask_field
		// pour afficher le mot de passe
		if (check_pass.isSelected()) {
			unmask_field.setText(password_field.getText());
			unmask_field.setVisible(true);
			password_field.setVisible(false);
		}
		// Sinon, on copie colle ce qu'il y a dans le unmask_field pour le récupérer
		// dans le password_field
		else {
			password_field.setText(unmask_field.getText());
			password_field.setVisible(true);
			unmask_field.setVisible(false);
		}
	}

	@FXML
	public void onEnter(KeyEvent e) throws SQLException, IOException {
		if (e.getCode().toString().equals("ENTER")) {
			connect();
		}
	}

	@FXML
	public void connect() throws SQLException, IOException {

		incorrect.setText("");

		// On récupère les champs remplis par l'utilisateur
		String login = this.id_field.getText();
		String password = Integer.toString(this.password_field.getText().hashCode());
		// Connection à la base de donnée
		Connection conn = ControleurPrincipal.connect();

		try {

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("SELECT password,id_connexion,id_employe,admin FROM Employe;");
			// Execution de la requete
			ResultSet resultat = requete.executeQuery();

			/*
			 * Si personne n'est enregistré en tant qu'employé, l'id_connexion "admin" et
			 * password "admin" servent à initialiser l'application c'est-à-dire la base de
			 * données.
			 */
			if (password.equals("92668751") && login.equals("admin")) {
				if (!resultat.next()) {

					incorrect.setText("");

					FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/onglets/Main.fxml"));
					ControleurPrincipal controleur = ControleurPrincipal.getControleurPrincipal();
					loader.setController(controleur);
					Parent root = (Parent) loader.load();

					/*
					 * On récupère l'instance du controleurPrincipal utilisé afin de diffuser
					 * l'id_connexion et le password de l'employé actuellement connecté.
					 */
					Stage stage = new Stage();
					Scene scene = new Scene(root);
					stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres
					
					stage.setFullScreen(true);
					stage.setTitle("Gestionnaire d'Hôtel");
					stage.setScene(scene);
					stage.show();

					/* On transmet les informations de l'utilisateur connecté */
					controleur.setUser(login, password);

					// Ferme la fenetre
					Stage stage1 = (Stage) b_connect.getScene().getWindow();
					stage1.close();
				} else {
					incorrect.setText("Identifiant ou mot de passe incorrect");
				}
			}

			else {

				// On cherche le mot de passe qui convient
				boolean trouve = false;
				while (resultat.next() && !trouve) {
					if (resultat.getString(1).equals(password) && (resultat.getString(2).equals(login))) {
						trouve = true;

					}
				}

				if (trouve) {
					incorrect.setText("");
					// Si les champs match avec un utilisateur de la base de données
					// On lance le Main.fxml qui contient l'application

					FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/onglets/Main.fxml"));
					ControleurPrincipal controleur = ControleurPrincipal.getControleurPrincipal();
					loader.setController(controleur);
					Parent root = (Parent) loader.load();

					/*
					 * On récupère l'instance du controleurPrincipal utilisé afin de diffuser
					 * l'id_connexion et le password de l'employé actuellement connecté.
					 */
					controleur.setUser(login, password);

					Stage stage = new Stage();
					Scene scene = new Scene(root);
					stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

					stage.setFullScreen(true);
					stage.setTitle("Gestionnaire d'Hôtel");
					stage.setScene(scene);
					stage.show();

					// Ferme la fenetre
					Stage stage1 = (Stage) b_connect.getScene().getWindow();
					stage1.close();
				}

				else {
					incorrect.setText("Identifiant ou mot de passe incorrect");
				}
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}

}