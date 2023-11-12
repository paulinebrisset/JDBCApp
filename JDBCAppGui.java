import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class JDBCAppGui extends JFrame {
    // Colors
    private static Color BACKGROUND_COLOR = Color.decode("#FFFFFF");
    private static Color BUTTONS_COLOR = Color.decode("#D5D4E5");
    private static Color BOTTOM_COLOR = Color.decode("#A1B3D1");
    private static Color BUTTONS_BACKGROUND_COLOR = Color.decode("#D7B4DF");
    private static Color INPUT_BACKGROUND_COLOR = Color.decode("#A1B3D1");

    private JTextField urlField, loginField, passwordField, queryField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton connectButton, executeButton;

    private Connection connection;
    private Statement statement;

    public JDBCAppGui() {
        // Setting up the JFrame
        super("JDBC App GUI");
        // Set a reasonable initial size
        setSize(800, 700);
        // Let the layout manager handle the sizing
        setPreferredSize(new Dimension(800, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Creating components for input panel
        urlField = new JTextField("jdbc:mysql://localhost:3306/boardgames", 30);
        loginField = new JTextField("root", 20);
        passwordField = new JPasswordField(20);
        queryField = new JTextField("SELECT ? FROM GAME", 30);

        // Buttons
        connectButton = new JButton("Connect");
        connectButton.setBackground(BUTTONS_COLOR);
        executeButton = new JButton("Execute query");
        executeButton.setBackground(BUTTONS_COLOR);

        // Result Area
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBackground(BACKGROUND_COLOR);

        // Draw the input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(INPUT_BACKGROUND_COLOR);

        GridBagConstraints ipc = new GridBagConstraints();
        // Add margin at the bottom with an empty label
        ipc.gridx = 0;
        ipc.gridy = 0;
        ipc.anchor = GridBagConstraints.WEST;
        ipc.insets = new Insets(0, 0, 8, 0);
        inputPanel.add(new JLabel(), ipc);
        ipc.gridx = 0;
        ipc.gridy = 1;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("URL:"), ipc);

        ipc.gridx = 1;
        ipc.gridy = 1;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(urlField, ipc);

        ipc.gridx = 0;
        ipc.gridy = 2;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Login:"), ipc);

        ipc.gridx = 1;
        ipc.gridy = 2;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(loginField, ipc);

        ipc.gridx = 0;
        ipc.gridy = 3;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Password:"), ipc);

        ipc.gridx = 1;
        ipc.gridy = 3;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(passwordField, ipc);

        ipc.gridx = 0;
        ipc.gridy = 4;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Query:"), ipc);

        ipc.gridx = 1;
        ipc.gridy = 4;
        ipc.anchor = GridBagConstraints.WEST;
        inputPanel.add(queryField, ipc);

        // Add margin at the bottom with an empty label
        ipc.gridx = 0;
        ipc.gridy = 5;
        ipc.anchor = GridBagConstraints.WEST;
        ipc.insets = new Insets(0, 0, 8, 0);
        inputPanel.add(new JLabel(), ipc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(connectButton);
        buttonPanel.add(executeButton);
        buttonPanel.setBackground(BUTTONS_BACKGROUND_COLOR);
        executeButton.setEnabled(false);

        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(BOTTOM_COLOR);
        JPanel inputButtonPanel = new JPanel(new BorderLayout());
        inputButtonPanel.add(inputPanel, BorderLayout.NORTH);
        inputButtonPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adding components to the content pane
        add(inputButtonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER); // Placing inputButtonPanel between inputPanel and resultTable
        add(emptyPanel, BorderLayout.SOUTH);

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, password);
            statement = connection.createStatement();

            // Show connected status
            JOptionPane.showMessageDialog(this, "Connected to the database.");

            // Enable the executeButton since the connection is established
            executeButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage());

            // Disable the executeButton if there is an error in connection
            executeButton.setEnabled(false);
        }
    }

    private void executeQuery() {
        try {
            String query = queryField.getText();

            // Check if the query contains following '??' cause I don't handle it
            if (query.contains("??")) {
                // Parameters must be seperated with comas
                JOptionPane.showMessageDialog(this, "Invalid query: Please replace '??' with '?,?'");
                return;
            }

            // Always use PreparedStatement for consistency
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Check if the query contains parameters
                if (query.contains("?")) {
                    // Show a dialog to input parameters
                    showParameterInputDialog(query);
                } else {
                    executeQueryWithPreparedStatement(preparedStatement);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error executing query: " + e.getMessage());
        }
    }

    private void showParameterInputDialog(String query) {
        // Show a dialog to input parameters
        String input = JOptionPane.showInputDialog(this,
                "Enter parameters (one parameter per ?, separate it with commas):");
        if (input != null) {
            String[] parameters = input.split(",");
            try {
                String modifiedQuery = replaceParameters(query, parameters);
                executeQueryWithModifiedQuery(modifiedQuery);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error executing query with parameters: " + e.getMessage());
            }
        }
    }

    private String replaceParameters(String query, String[] parameters) {
        for (String parameter : parameters) {
            // Remove quotes from the parameter value
            String parameterValue = parameter.trim().replaceAll("'", "");

            // Replace the first "?" in the query with the parameter value
            query = query.replaceFirst("\\?", parameterValue);
        }
        return query;
    }

    private void executeQueryWithModifiedQuery(String modifiedQuery) throws SQLException {
        // Execute the modified query
        ResultSet resultSet = statement.executeQuery(modifiedQuery);

        // Clear the table and set column names
        tableModel.setDataVector(new Vector<Vector<Object>>(), getColumnNames(resultSet));

        // Fill the table model with data
        displayResults(resultSet);

        // Adjust column widths
        adjustColumnWidths();
    }

    private void executeQueryWithPreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        // Execute the query with or without parameters
        ResultSet resultSet = preparedStatement.executeQuery();

        // Clear the table and set column names
        tableModel.setDataVector(new Vector<Vector<Object>>(), getColumnNames(resultSet));

        // Fill the table model with data
        displayResults(resultSet);

        // Adjust column widths
        adjustColumnWidths();
    }

    private Vector<String> getColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int cols = meta.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= cols; i++) {
            columnNames.add(meta.getColumnLabel(i).toUpperCase());
        }
        return columnNames;
    }

    private void displayResults(ResultSet resultSet) throws SQLException {
        // Fill the table model with data
        while (resultSet.next()) {
            Vector<Object> rowData = new Vector<>();
            ResultSetMetaData meta = resultSet.getMetaData();
            int cols = meta.getColumnCount();

            for (int i = 1; i <= cols; i++) {
                rowData.add(resultSet.getString(i));
            }
            tableModel.addRow(rowData);
        }
    }

    private void adjustColumnWidths() {
        for (int column = 0; column < resultTable.getColumnCount(); column++) {
            int maxWidth = 0;

            for (int row = 0; row < resultTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = resultTable.getCellRenderer(row, column);
                Object value = resultTable.getValueAt(row, column);
                Component cellComponent = cellRenderer.getTableCellRendererComponent(resultTable, value, false, false,
                        row, column);
                maxWidth = Math.max(maxWidth, cellComponent.getPreferredSize().width);
            }

            TableColumn tableColumn = resultTable.getColumnModel().getColumn(column);
            tableColumn.setPreferredWidth(maxWidth + 10); // Add some padding
        }
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
