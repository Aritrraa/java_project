import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinanceTracker {
    private JFrame frame;
    private JTextField expenseField;
    private JComboBox<String> categoryBox;
    private JTextArea displayArea;
    private JButton exportButton, clearButton;
    private JSpinner dateSpinner;
    private Map<String, Map<String, Double>> expenses;
    private Map<String, Double> dailyTotalExpenses;
    private Map<String, Double> monthlyExpenses;

    public FinanceTracker() {
        expenses = new HashMap<>();
        dailyTotalExpenses = new HashMap<>();
        monthlyExpenses = new HashMap<>();
        createUI();
    }

    private void createUI() {
        frame = new JFrame("💰 Personal Finance Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 248, 255));

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBackground(new Color(224, 255, 255));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Delete Expense"));

        JLabel expenseLabel = new JLabel("Amount:");
        expenseField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryBox = new JComboBox<>(new String[]{"Food", "Rent", "Utilities", "Entertainment", "Transport", "Other"});

        JLabel dateLabel = new JLabel("Date:");
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);

        JButton addButton = new JButton("➕ Add Expense");
        JButton deleteButton = new JButton("❌ Delete Expense");
        exportButton = new JButton("📁 Export to File");
        clearButton = new JButton("🧹 Clear All");

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Button colors
        addButton.setBackground(new Color(144, 238, 144));
        deleteButton.setBackground(new Color(255, 160, 122));
        exportButton.setBackground(new Color(135, 206, 250));
        clearButton.setBackground(new Color(255, 182, 193));

        // Button actions
        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        exportButton.addActionListener(e -> exportToFile());
        clearButton.addActionListener(e -> clearAll());

        // Add components to panel
        inputPanel.add(expenseLabel);
        inputPanel.add(expenseField);
        inputPanel.add(categoryLabel);
        inputPanel.add(categoryBox);
        inputPanel.add(dateLabel);
        inputPanel.add(dateSpinner);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(exportButton);
        inputPanel.add(clearButton);

        // Add panels to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void addExpense() {
        String expenseText = expenseField.getText();
        String category = (String) categoryBox.getSelectedItem();
        String date = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String month = date.substring(0, 7);

        if (expenseText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an expense amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(expenseText);
            expenses.computeIfAbsent(date, k -> new HashMap<>()).merge(category, amount, Double::sum);
            dailyTotalExpenses.merge(date, amount, Double::sum);
            monthlyExpenses.merge(month, amount, Double::sum);
            expenseField.setText("");
            displayExpenses();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid amount.");
        }
    }

    private void deleteExpense() {
        String expenseText = expenseField.getText();
        String category = (String) categoryBox.getSelectedItem();
        String date = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
        String month = date.substring(0, 7);

        if (expenseText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter amount to delete.");
            return;
        }

        try {
            double amount = Double.parseDouble(expenseText);
            Map<String, Double> daily = expenses.get(date);
            if (daily != null && daily.containsKey(category)) {
                double current = daily.get(category);
                double updated = current - amount;
                if (updated <= 0) daily.remove(category);
                else daily.put(category, updated);

                dailyTotalExpenses.put(date, dailyTotalExpenses.get(date) - amount);
                monthlyExpenses.put(month, monthlyExpenses.get(month) - amount);
                expenseField.setText("");
                displayExpenses();
            } else {
                JOptionPane.showMessageDialog(frame, "Expense not found for deletion.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid amount.");
        }
    }

    private void exportToFile() {
        try (FileWriter writer = new FileWriter("FinanceReport.txt")) {
            writer.write(displayArea.getText());
            JOptionPane.showMessageDialog(frame, "Report exported to FinanceReport.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error exporting to file.");
        }
    }

    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Clear all data?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            expenses.clear();
            dailyTotalExpenses.clear();
            monthlyExpenses.clear();
            displayArea.setText("");
        }
    }

    private void displayExpenses() {
        StringBuilder sb = new StringBuilder("📅 Expense Report\n\n");

        for (String date : expenses.keySet()) {
            sb.append(date).append(":\n");
            Map<String, Double> daily = expenses.get(date);
            for (String cat : daily.keySet()) {
                sb.append("  ").append(cat).append(": ₹").append(String.format("%.2f", daily.get(cat))).append("\n");
            }
            sb.append("  Total: ₹").append(String.format("%.2f", dailyTotalExpenses.get(date))).append("\n\n");
        }

        sb.append("📈 Monthly Summary:\n");
        for (String month : monthlyExpenses.keySet()) {
            sb.append("  ").append(month).append(": ₹").append(String.format("%.2f", monthlyExpenses.get(month))).append("\n");
        }

        displayArea.setText(sb.toString());
    }

    // ✅ This is the entry point of your Java program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanceTracker());
    }
}
