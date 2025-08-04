
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
 class MoneyManager extends JFrame {
    private JTextField transactionNameField, amountField, budgetField;
    private JComboBox<String> typeComboBox, categoryComboBox;
    private JButton addTransactionButton, setBudgetButton, deleteTransactionButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel totalIncomeLabel, totalExpenseLabel, balanceLabel, budgetLabel, remainingBudgetLabel;
    private double totalIncome = 0.0, totalExpense = 0.0, balance = 0.0, budget = 0.0, remainingBudget = 0.0;

    private Connection connection;

    public MoneyManager() {
        setTitle("Money Manager App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        initializeDatabase();

        // Main UI Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel for Adding Transactions
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Transaction Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Transaction Name:"), gbc);
        gbc.gridx = 1;
        transactionNameField = new JTextField(15);
        formPanel.add(transactionNameField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        formPanel.add(amountField, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense"});
        formPanel.add(typeComboBox, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(new String[]{"Food", "Transportation", "Entertainment", "Bills", "Other"});
        formPanel.add(categoryComboBox, gbc);

        // Add Transaction Button
        gbc.gridx = 1;
        gbc.gridy = 4;
        addTransactionButton = new JButton("Add Transaction");
        formPanel.add(addTransactionButton, gbc);

        // Delete Transaction Button
        deleteTransactionButton = new JButton("Delete Transaction");
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(deleteTransactionButton, gbc);

        // Budget Panel
        JPanel budgetPanel = new JPanel(new GridBagLayout());
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Set Budget"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        budgetPanel.add(new JLabel("Monthly Budget:"), gbc);
        gbc.gridx = 1;
        budgetField = new JTextField(15);
        budgetPanel.add(budgetField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        setBudgetButton = new JButton("Set Budget");
        budgetPanel.add(setBudgetButton, gbc);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.add(formPanel, BorderLayout.CENTER);
        inputPanel.add(budgetPanel, BorderLayout.SOUTH);

        // Transaction Table
        String[] columnNames = {"Name", "Type", "Category", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(transactionTable);

        // Summary Panel
        JPanel totalsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        totalIncomeLabel = new JLabel("Total Income: $0.00");
        totalExpenseLabel = new JLabel("Total Expense: $0.00");
        balanceLabel = new JLabel("Balance: $0.00");
        budgetLabel = new JLabel("Monthly Budget: $0.00");
        remainingBudgetLabel = new JLabel("Remaining Budget: $0.00");

        totalsPanel.add(totalIncomeLabel);
        totalsPanel.add(totalExpenseLabel);
        totalsPanel.add(balanceLabel);
        totalsPanel.add(budgetLabel);
        totalsPanel.add(remainingBudgetLabel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(totalsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Button Actions
        setBudgetButton.addActionListener(e -> setBudget());
        addTransactionButton.addActionListener(e -> addTransaction());
        deleteTransactionButton.addActionListener(e -> deleteTransaction());

        // Load Data
        loadBudget();
        loadTransactions();
    }

    private void initializeDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/money_manager";
            String user = "root";
            String password = "root";

            connection = DriverManager.getConnection(url, user, password);

            if (connection != null) {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Budget (id INT PRIMARY KEY, monthly_budget DOUBLE, remaining_budget DOUBLE)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Transactions (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), type VARCHAR(50), category VARCHAR(50), amount DOUBLE)");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setBudget() {
        try {
            budget = Double.parseDouble(budgetField.getText());
            remainingBudget = budget - totalExpense;

            PreparedStatement ps = connection.prepareStatement("REPLACE INTO Budget (id, monthly_budget, remaining_budget) VALUES (1, ?, ?)");
            ps.setDouble(1, budget);
            ps.setDouble(2, remainingBudget);
            ps.executeUpdate();

            budgetLabel.setText("Monthly Budget: $" + budget);
            remainingBudgetLabel.setText("Remaining Budget: $" + remainingBudget);
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Invalid budget input.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTransaction() {
        try {
            String name = transactionNameField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String type = (String) typeComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();

            PreparedStatement ps = connection.prepareStatement("INSERT INTO Transactions (name, type, category, amount) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, category);
            ps.setDouble(4, amount);
            ps.executeUpdate();

            if (type.equals("Income")) {
                totalIncome += amount;
                balance += amount;
            } else {
                totalExpense += amount;
                balance -= amount;
                remainingBudget = budget - totalExpense;
            }

            tableModel.addRow(new Object[]{name, type, category, "$" + amount});
            updateTotals();
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding transaction.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String type = (String) tableModel.getValueAt(selectedRow, 1);
        double amount = Double.parseDouble(((String) tableModel.getValueAt(selectedRow, 3)).replace("$", ""));

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Transactions WHERE name = ? AND type = ? AND amount = ? LIMIT 1");
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();

            if (type.equals("Income")) {
                totalIncome -= amount;
                balance -= amount;
            } else {
                totalExpense -= amount;
                balance += amount;
                remainingBudget = budget - totalExpense;
            }

            tableModel.removeRow(selectedRow);
            updateTotals();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting transaction.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBudget() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT monthly_budget, remaining_budget FROM Budget WHERE id = 1");
            if (rs.next()) {
                budget = rs.getDouble("monthly_budget");
                remainingBudget = rs.getDouble("remaining_budget");

                budgetLabel.setText("Monthly Budget: $" + budget);
                remainingBudgetLabel.setText("Remaining Budget: $" + remainingBudget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, type, category, amount FROM Transactions");
            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");

                tableModel.addRow(new Object[]{name, type, category, "$" + amount});

                if (type.equals("Income")) {
                    totalIncome += amount;
                    balance += amount;
                } else {
                    totalExpense += amount;
                    balance -= amount;
                }
            }
            updateTotals();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotals() {
        totalIncomeLabel.setText("Total Income: $" + totalIncome);
        totalExpenseLabel.setText("Total Expense: $" + totalExpense);
        balanceLabel.setText("Balance: $" + balance);
        remainingBudgetLabel.setText("Remaining Budget: $" + remainingBudget);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MoneyManager app = new MoneyManager();
            app.setVisible(true);
        });
    }
}