
/**
 * Controleur de l'onglet Administrateur
 * 
 * @author Théo BLANCHONNET et Devan PRIGENT
 */

package application.controller.onglets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import application.DataBase;
import application.Main;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ControleurPrincipal {

	@FXML
	private final static ControleurPrincipal controleurPrincipal = new ControleurPrincipal();

	@FXML
	private MenuPrincipalCtrl menuPrincipalViewController;

	@FXML
	private ReservationCtrl reservationViewController;

	@FXML
	private ClientCtrl clientViewController;

	@FXML
	private AdministrateurCtrl administrateurViewController;

	@FXML
	private TabPane tabpane;

	private String login;

	private String password;

	private String idEmploye;

	/**
	 * Methodes permettant de gerer le controleur principal
	 * 
	 * @author Devan PRIGENT
	 */
	private ControleurPrincipal() {
	}

	public static ControleurPrincipal getControleurPrincipal() {
		return controleurPrincipal;
	}

	public String getEmploye() {
		return idEmploye;
	}

	public void actualiser() {
		clientViewController.listeClient();
		administrateurViewController.refresh();
		reservationViewController.listeReservation();
	}

	/**
	 * Methode permettant d afficher les evenements
	 * 
	 * @author Devan PRIGENT
	 */
	public void ajoutEvenement(String event) {
		// Connection a la base de donnees
		Connection conn = ControleurPrincipal.connect();
		try {
			/// Ensuite on ajoute la nouvelle chambre
			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn.prepareStatement("INSERT INTO Historique (evenement,id_employe) VALUES (?,?);");
			requete.setString(1, event);
			requete.setString(2, this.idEmploye);
			// Execution de la requete
			requete.executeUpdate();

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		// On actualise le tableau
		menuPrincipalViewController.listeEvenements();
	}

	/**
	 * Methode permettant de gerer les requetes
	 * 
	 * @author Devan PRIGENT
	 */
	public static String requestNew(ResultSet reponse) {
		String URLDBMYSQL = "jdbc:mysql://157.159.195.53:3306/";
		String DBNAME = "Hotel";
		String USER = "pro3600";
		String PASS = "pro3600BDD";

		String URLOPTIONS = "?useSSL=false&useUnicode=true"
				+ "&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false"
				+ "&serverTimezone=UTC&allowPublicKeyRetrieval=true";
		try (DataBase dataBase = new DataBase(URLDBMYSQL, URLOPTIONS, DBNAME, USER, PASS)) {

			// On recupere le nombre de colonne du resultat
			int n = reponse.getMetaData().getColumnCount();

			String rep = "";
			while (reponse.next()) {
				// Pour chaque ligne
				for (int i = 1; i <= n; i++) {
					// Pour chaque colonne
					rep += reponse.getObject(i);
					rep += " ";
				}
				rep += " \n";
			}
			reponse.close();
			return rep;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Méthode qui prend le résultat de requete et renvoie un objet qui va remplir
	 * le tableau choisi passé en argument
	 * 
	 * @param reponse Resultat de la requete
	 * @param tab     TableView qui sert à afficher le resultat
	 * @throws SQLException exceptions pouvant se produire lors de la connexion à la
	 *                      base.
	 * @author Théo BLANCHONNET
	 */

	public static ObservableList<ObservableList<Object>> requeteTabNew(ResultSet reponse,
			TableView<ObservableList<Object>> tab) throws SQLException {
		// n: nombre de colonnes
		int n = reponse.getMetaData().getColumnCount();

		// data: données que l'on va envoyées à la fin au tableau
		ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();

		// On remet le tableau à zéro si il est déjà rempli
		int taille = tab.getColumns().size();
		if (taille > 0) {
			tab.getColumns().clear();
		}

		for (int i = 0; i < n; i++) {
			final int j = i;
			// Nouvelle colonne
			TableColumn<ObservableList<Object>, Object> col = new TableColumn<>(
					reponse.getMetaData().getColumnName(i + 1));

			// Passage difficile...
			col.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<ObservableList<Object>, Object>, ObservableValue<Object>>() {
						public ObservableValue<Object> call(CellDataFeatures<ObservableList<Object>, Object> param) {
							Object test = param.getValue().get(j);
							if (test == null) {
								return null;
							} else {
								return new SimpleObjectProperty<>(param.getValue().get(j).toString());
							}
						}
					});

			// On ajoute la colonne au tableau
			tab.getColumns().addAll(col);
		}

		// On parcoure la requete
		while (reponse.next()) {
			// Pour chaque ligne
			ObservableList<Object> row = FXCollections.observableArrayList();
			for (int i = 1; i <= reponse.getMetaData().getColumnCount(); i++) {
				// Pour chaque colonne
				row.add(reponse.getObject(i));
			}

			// On ajoute la ligne aux données finales
			data.add(row);

		}

		// Fermeture de la requete
		reponse.close();
		return data;
	}

	public static Connection connect() {
		Connection conn = null;
		String URLDBMYSQL = "jdbc:mysql://157.159.195.53:3306/";
		String DBNAME = "Hotel";
		String USER = "pro3600";
		String PASS = "pro3600BDD";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(URLDBMYSQL + DBNAME, USER, PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Methode pour verifier les emails (trouvee sur internet)
	 * 
	 */
	public static boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * Méthode qui fixe id et password. Cette méthode est appellée depuis le
	 * controleur d'authentification.
	 * 
	 * @param String id correspond à l'id_connexion entré par l'utilisateur
	 * 
	 * @param String password correspond au password entré par l'utilisateur
	 * 
	 * @author Théo BLANCHONNET
	 */
	public void setUser(String login, String password) {
		// On initialise les informations de l utilisateur
		this.login = login;
		this.password = password;

		/// On recupere l identifiant de l utilisateur
		// Connection à la base de donnée
		Connection conn = ControleurPrincipal.connect();
		try {
			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("SELECT id_employe FROM Employe WHERE id_connexion=? AND password=?;");
			// Execution de la requete
			requete.setString(1, login);
			requete.setString(2, password);
			ResultSet resultat = requete.executeQuery();
			this.idEmploye = requestNew(resultat);

		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		disableAdmin();
		disableEntretien();
	}

	/**
	 * Méthode qui enlève le droit d'utilisation de l'onglet Administrateur aux
	 * employés non admin
	 * 
	 * @author Théo BLANCHONNET
	 */
	public void disableAdmin() {

		// Connection à la base de donnée
		Connection conn = ControleurPrincipal.connect();

		try {
			int admin = 0;

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("SELECT admin FROM Employe WHERE id_connexion=? AND password=?;");
			// Execution de la requete
			requete.setString(1, this.login);
			requete.setString(2, this.password);
			ResultSet resultat = requete.executeQuery();

			if (resultat.next()) {
				admin = resultat.getInt(1);
			}
			if (admin == 0) {
				tabpane.getTabs().get(3).setDisable(true);
			}

		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Méthode qui enlève le droit d'utilisation des onglets Clients et Réservations
	 *  aux employés d'entretien
	 * 
	 * @author Théo BLANCHONNET
	 */
	public void disableEntretien() {

		// Connection à la base de donnée
		Connection conn = ControleurPrincipal.connect();

		try {
			String role = "";

			// Formulation de la requete sous forme de prepareStatement
			PreparedStatement requete = conn
					.prepareStatement("SELECT role FROM Employe WHERE id_employe=?;");
			// Execution de la requete
			requete.setString(1, this.idEmploye);
			ResultSet resultat = requete.executeQuery();

			if (resultat.next()) {
				role = resultat.getString(1);
			}
			if (role.equals("Entretien")) {
				tabpane.getTabs().get(1).setDisable(true);
				tabpane.getTabs().get(2).setDisable(true);			
			}
		}

		catch (SQLException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Méthode qui permet de se déconnecter de la session
	 * 
	 * @author Théo BLANCHONNET
	 */
	public static void disconnect(Button bouton) {

		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("fxml/onglets/Authentification.fxml"));
			Parent root = (Parent) loader.load();

			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.initModality(Modality.APPLICATION_MODAL);// on ne peut pas ecrire dans les autres fenetres

			stage.setTitle("Authentification");
			stage.setScene(scene);
			stage.show();

			// Ferme la fenetre
			Stage stage1 = (Stage) bouton.getScene().getWindow();
			stage1.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
