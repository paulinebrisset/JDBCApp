import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class JDBCAppGui extends JFrame {
    private JTextField urlField, loginField, passwordField, queryField;
    private JTextArea resultArea;
    private JButton connectButton, executeButton;

    private Connection connection;
    private Statement statement;

    public JDBCAppGui() {
        // Setting up the JFrame
        super("JDBC App GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Creating components
        urlField = new JTextField("jdbc:mysql://localhost:3306/beocler", 20);
        loginField = new JTextField("root", 20);
        passwordField = new JPasswordField(20);
        queryField = new JTextField("SELECT * FROM item", 30);
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        connectButton = new JButton("Connect");
        executeButton = new JButton("Execute Query");

        // Adding components to the JFrame
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("URL:"));
        inputPanel.add(urlField);
        inputPanel.add(new JLabel("Login:"));
        inputPanel.add(loginField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Query:"));
        inputPanel.add(queryField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(connectButton);
        buttonPanel.add(executeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Adding action listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });
    }

    private void connect() {
        try {
            String url = urlField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();

            // Establishing the connection
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, password);
            statement = connection.createStatement();
            resultArea.setText("Connected to the database.");
        } catch (Exception ex) {
            resultArea.setText("Error connecting to the database: " + ex.getMessage());
        }
    }

    private void executeQuery() {
        try {
            String query = queryField.getText();
            ResultSet resultSet = statement.executeQuery(query);
            displayResults(resultSet);
        } catch (SQLException ex) {
            resultArea.setText("Error executing query: " + ex.getMessage());
        }
    }

    private void displayResults(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        StringBuilder buffer = new StringBuilder();

        int cols = meta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            buffer.append(meta.getColumnLabel(i)).append("\t");
        }
        buffer.append("\n");

        while (resultSet.next()) {
            for (int i = 1; i <= cols; i++) {
                buffer.append(resultSet.getString(i)).append("\t");
            }
            buffer.append("\n");
        }

        resultArea.setText(buffer.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JDBCAppGui().setVisible(true);
            }
        });
    }
}
