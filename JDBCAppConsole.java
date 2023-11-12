// package jdbcConsole;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe JDBCAppConsole
 * Exemple d'application console pour dialoguer avec une base de donnée MySQL
 * L'application comprend les commandes suivantes :
 * go : Envoie à la BD ce qui se trouve dans le tampon (buffer). Les commandes
 * se terminant par ; sont exécutées
 * quit : ferme les ressources et quitte l'application
 * reset : vide le tampon sans envoyer le contenu à la base
 * commit : envoie une validation à la BD, validant ainsi toutes les
 * transactions en attente
 * roolback : annule toutes les transactions non validées
 * version : affiche les informations de version du programme, de la BD et du
 * pilote JDBC (vie interface DatabaseMetaData)
 * @ author David GROSSER
 * Mise à jour : 28 octobre 2019
 */
// https://www.javatpoint.com/example-to-connect-to-the-mysql-database
public class JDBCAppConsole {
	// Paramètres de connection
	// Ce que je veux utiliser : mysql -h 127.0.0.1 -P 3306 -u root boardgames
	String url = "jdbc:mysql://localhost:3306/boardgames"; // URL de connexion
	String login = "root"; // user
	String password = ""; // pwd
	String driverName = "com.mysql.jdbc.Driver";
	Connection connection;
	DriverPropertyInfo[] required;
	Properties props = new Properties();
	Driver driver;

	// Gestion des commandes
	BufferedReader input;
	StringBuffer buffer = new StringBuffer();
	int line = 1; // La ligne d'entrée courante
	int row_count = 0; // Nombre de ligne a afficher lors de l'affichage d'un select
	boolean connected = false;
	boolean autocommit = true;

	/**
	 * Constructeurs sans paramètres
	 */
	public JDBCAppConsole() {
		input = new BufferedReader(new InputStreamReader(System.in));
		saisieConnect(); // Saisie des paramètres de connexion
		if (connected)
			saisie(); // entrer dans la boucle de saisie
	}

	/**
	 * Constructeurs avec paramètres de connexion
	 */
	public JDBCAppConsole(String url) {
		this.url = url;
		input = new BufferedReader(new InputStreamReader(System.in));
		try {
			connect();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (connected)
			saisie(); // entrer dans la boucle de saisie
	}

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("Syntaxe : <java -Djdbc.drivers=DRIVER_NAME" + "TerminalMonitor JDBC_URL");
			new JDBCAppConsole();
		} else {
			new JDBCAppConsole(args[0]);
		}
	}

	/**
	 * Saisie des paramètres de connexion
	 */
	public void saisieConnect() {
		String tmp;
		while (!connected) {
			if (url != null)
				System.out.print("URL (" + url + ") > ");
			else
				System.out.print("URL > ");
			// System.out.flush();
			try {
				tmp = input.readLine();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return;
			}
			if (!tmp.equals(""))
				url = tmp.trim(); // enlever les espaces en trop dans la commande
			// else if(url != null) System.out.println("URL : "+url);

			if (login != null)
				System.out.print("Login (" + login + ") > ");
			else
				System.out.print("Login > ");
			try {
				tmp = input.readLine();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return;
			}
			if (!tmp.equals(""))
				login = tmp;

			System.out.print("Password > ");
			try {
				password = input.readLine();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return;
			}

			if (driverName != null)
				System.out.print("JDBC Driver (" + driverName + ") > ");
			else
				System.out.print("JDBC Driver > ");
			try {
				tmp = input.readLine();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return;
			}
			if (!tmp.equals(""))
				driverName = tmp.trim();

			try {
				connect();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialisation de la connection à la base de données
	 */
	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName(driverName);
		driver = DriverManager.getDriver(url);
		required = driver.getPropertyInfo(url, props);
		// connection = DriverManager.getConnection(url, props);
		connection = DriverManager.getConnection(url, login, password);
		connected = true;
		System.out.println("Connecté à " + url);

	}

	/**
	 * gestion de l'interprétation des entrées
	 */
	public void saisie() {
		// input = new BufferedReader(new InputStreamReader(System.in));
		while (connected) {
			String tmp, cmd;
			// Affiche invite
			if (line == 1)
				System.out.print("SQL > ");
			else {
				System.out.print(line + " -> ");
			}
			System.out.flush();
			// Ligne suivante
			try {
				tmp = input.readLine();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				return;
			}
			// Enlever les espaces en trop dans la commande
			cmd = tmp.trim();
			boolean exec = false;
			if (cmd.equals("autocommit")) {
				autoCommit();
				exec = true;
			}
			if (cmd.equals("commit")) {
				commit();
				exec = true;
			}
			if (cmd.equals("go")) {
				go();
				exec = true;
			}
			if (cmd.equals("reset")) {
				reset();
				exec = true;
			}
			if (cmd.equals("quit")) {
				quit();
				exec = true;
			}
			if (cmd.equals("rollback")) {
				rollback();
				exec = true;
			}
			if (cmd.equals("version")) {
				showVersion();
				exec = true;
			}
			if (cmd.indexOf(";") != -1) {// La commande se termine par ;
				buffer.append(" " + tmp);
				line++;
				go();
				exec = true;
			}
			if (!exec) {
				buffer.append(" " + tmp);
				line++;
			}
		}
		try {
			connection.close();
		} catch (Exception e) {
			System.out.println("Erreur de fermeture de la connection " + e.getMessage());
		}
		System.out.println("Connection fermée");
	}

	// Commit
	public void commit() {
		try {
			connection.commit();
			System.out.println("Commit réussi");
		} catch (SQLException e) {
			System.out.println("Erreur : " + e.getMessage());
		} finally {
			reset();
		}
	}

	// Active ou desactive le mode autocommit
	public void autoCommit() {
		try {
			connection.setAutoCommit(!autocommit);
			autocommit = !autocommit;
			System.out.println("Mode autocommit : " + autocommit);
		} catch (SQLException e) {
			System.out.println("Erreur : " + e.getMessage());
		} finally {
			reset();
		}
	}

	// Rollback
	public void rollback() {
		try {
			connection.rollback();
			System.out.println("Rollback réussi");
		} catch (SQLException e) {
			System.out.println("Erreur : " + e.getMessage());
		} finally {
			reset();
		}
	}

	// Exécution du tampon courant
	public void go() {
		if (!buffer.equals("")) {
			try {
				executeStatement(buffer);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				reset();
			}
		}
		reset();
	}

	// Reset
	public void reset() {
		buffer = new StringBuffer();
		line = 1;
	}

	// Affichage des meta données sur la base
	public void showVersion() {
		try {
			DatabaseMetaData meta = connection.getMetaData();
			System.out.println("TerminalMonitor v2.0");
			System.out.println("DBMS : " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
			System.out.println("JDBC Driver : " + meta.getDriverName() + " " + meta.getDriverVersion());
		} catch (SQLException e) {
			System.out.println("Impossible d'obtenir les info de version :" + e.getMessage());
		} finally {
			reset();
		}
	}

	// Quit
	public void quit() {
		connected = false;
	}

	public void executeStatement(StringBuffer buff) throws SQLException {
		String sql = buff.toString();
		System.out.println("Exec " + sql);
		Statement statement = null;
		try {
			statement = connection.createStatement();
			if (statement.execute(sql)) { // true si retourne un ResultSet
				processResults(statement.getResultSet());
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	// Affichage formatté du ResultSet
	public void processResults(ResultSet results) throws SQLException {
		try {
			ResultSetMetaData meta = results.getMetaData();
			StringBuffer bar = new StringBuffer();
			StringBuffer buffer = new StringBuffer();
			int cols = meta.getColumnCount();
			row_count = 0; // Initialisation du nombre de lignes a afficher
			int i, width = 0;

			for (i = 1; i <= cols; i++) {
				width += meta.getColumnDisplaySize(i);
			}
			width += 1 + cols;
			for (i = 0; i < width; i++) {
				bar.append('-');
			}
			bar.append('\n');
			buffer.append(bar.toString() + "|");
			buffer.append(affEnTete(meta)); // Affichage de la ligne d'en-tete
			buffer.append("\n" + bar.toString());
			buffer.append(affLignes(results, cols));
			buffer = new StringBuffer(row_count + " lignes.\n" + buffer.toString() + bar.toString());
			System.out.println(buffer.toString());
			System.out.flush();
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				results.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * Affichage de l'en-tête
	 */
	public StringBuffer affEnTete(ResultSetMetaData meta) throws SQLException {
		StringBuffer result = new StringBuffer();
		StringBuffer filler; // Pour remplir avec de sespaces blancs
		String label; // Label de la colonne fomatté
		int size; // Taille max en nombre de caractères de la colonne
		int cols = meta.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			filler = new StringBuffer();
			label = meta.getColumnLabel(i);
			size = meta.getColumnDisplaySize(i);
			// System.out.println(label+" "+size);
			int x;
			// Si le titre est plus long que la largeur de la colonne
			if (label.length() > size) {
				label = label.substring(0, size);
			}
			// Si le titre est moins long que la largeur de la colonne
			if (label.length() < size) {
				int j;
				x = (size - label.length()) / 2;
				for (j = 0; j < x; j++) {
					filler.append(' ');
				}
				label = filler + label + filler;
				if (label.length() > size) {
					label = label.substring(0, size);
				} else {
					while (label.length() < size) {
						label += " ";
					}
				}
			}
			result.append(label + "|");
		} // fin for
		return result;
	} // fin affEnTete

	/**
	 * Formatter chaque ligne de l'ensemble résultat
	 */
	public StringBuffer affLignes(ResultSet results, int cols) throws SQLException {
		ResultSetMetaData meta = results.getMetaData();
		StringBuffer buffer = new StringBuffer();
		while (results.next()) {
			row_count++;
			buffer.append('|');
			// Formatter chaque colonne de la ligne
			for (int i = 1; i <= cols; i++) {
				StringBuffer filler = new StringBuffer();
				Object value = results.getObject(i);
				int size = meta.getColumnDisplaySize(i);
				String str;
				if (results.wasNull())
					str = "NULL";
				else
					str = value.toString();
				if (str.length() > size)
					str = str.substring(0, size);
				if (str.length() < size) {
					int x = (size - str.length()) / 2;
					for (int j = 0; j < x; j++)
						filler.append(' ');
					str = filler + str + filler;
					if (str.length() > size)
						str = str.substring(0, size);
					else
						while (str.length() < size)
							str += " ";
				}
				buffer.append(str + "|");
			}
			buffer.append("\n");
		}
		return buffer;
	}

} // Fin class