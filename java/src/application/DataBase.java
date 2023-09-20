package application;


import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* Classe pour l'usage de MySQL */
/**
 * Gestion de base de données pour MySQL.
 * @author eric lallet
 *
 */
public class DataBase implements AutoCloseable {

	/**
	 * Référence sur la connexion au serveur.
	 */
	private Connection conn;
	/**
	 * Constructeur qui ouvre une connexion avec la base dbName du serveur MySql.
	 * @param urlMysql
	 *                url du serveur MySql
	 * @param urlOptions
	 *                options de l'url.
	 * @param dbName
	 *               nom de la base de données.
	 * @param user
	 *               id de l'utilisateur utilisé pour se connecter à la base.
	 * @param pass
	 *               mot de passe de l'utilisateur.
	 * @throws SQLException
	 *               exceptions pouvant se produire lors de la connexion  à la base. 
	 */
	public DataBase(final String urlMysql, final String urlOptions,
			final String dbName, final String user, final String pass) throws SQLException {
		super();
		String url =  urlMysql + dbName + urlOptions  ;

		this.conn = DriverManager.getConnection(url, user, pass);

	}


	/**
	 * Ferme la connexion avec la base.
	 * Automatiquement appelée si cette classe est utilisée dans le cadre d'un usage d'AutoCloseable.
	 */
	public void close() throws SQLException {
		conn.close();
	}

	/**
	 * Calcule le résultat d'une requête SQL.
	 * @param querySql
	 *        la requête à exécuter.
	 * @return
	 *        le résultat de la requête.
	 * @throws SQLException
	 *          Exceptions levées en cas d'erreur lors de la connexion ou l'exécution de la requête.
	 */
	public ResultSet query(final String querySql) throws SQLException {
		ResultSet result ;
		Statement stmt  = conn.createStatement();
		result   = stmt.executeQuery(querySql);

		return result;
	}


	/**
	 * Réalise une requête d'insertion. 
	 * @param insertSql
	 *        la requête d'insertion.
	 * @throws SQLException
	 *          Exceptions levées en cas d'erreur lors de la connexion ou l'insertion.
	 */
	public void insert(final String insertSql) throws SQLException {
		PreparedStatement insertStmt = conn.prepareStatement(insertSql); 
		insertStmt.executeUpdate();
	}
	
	
	/**
     * Réalise une requête de suppression.
     * @param deleteSql
     *        la requête de supression.
     * @throws SQLException
     *          Exceptions levées en cas d'erreur lors de la connexion ou la supression.
     */
    public void delete(final String deleteSql) throws SQLException {
        this.insert(deleteSql); // même code qu'insert
    }
    
    
    /**
     * Réalise une requête de modification.
     * @param updateSql
     * 			la requête de modification.
     * @throws SQLException
     * 			Exceptions levées en cas d'erreur lors de la connexion ou la modification.
     */
    public void update(final String updateSql) throws SQLException {
    	this.insert(updateSql); // même code qu'insert
    }
    
}


