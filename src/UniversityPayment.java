import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UniversityPayment {

    private static final String CSV_FILE = "payment.csv";
    private static final Map<String, Double> studentPayments = new HashMap<>();
    private static double MAX_FEES = 50000.0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UniversityPayment::showMainMenu);
    }

    public static void showMainMenu() {
        loadStudentPaymentsFromCSV();
        String[] options = {"Add Payment", "View Payment", "Edit Payment", "Set Maximum Fees", "Exit"};
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Choose an operation:", "University Payment System", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    addPayment();
                    break;
                case 1:
                    viewPayment();
                    break;
                case 2:
                    editPayment();
                    break;
                case 3:
                    setMaximumFees();
                    break;
                case 4:
                    break;
            }
        } while (choice != 4);
    }

    private static void loadStudentPaymentsFromCSV() {
        studentPayments.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        double amountPaid = Double.parseDouble(parts[1]);
                        studentPayments.put(parts[0], amountPaid);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid entry: " + line);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading payment data from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private static void addPayment() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null && !studentId.isEmpty()) {
            String amountStr = JOptionPane.showInputDialog("Enter Payment Amount:");
            if (amountStr != null && !amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    double totalAmount = studentPayments.getOrDefault(studentId, 0.0) + amount;
                    if (totalAmount > MAX_FEES) {
                        JOptionPane.showMessageDialog(null, "Total payment exceeds the maximum allowed fees of $" + MAX_FEES, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        studentPayments.put(studentId, totalAmount);
                        writeToCSV();
                        JOptionPane.showMessageDialog(null, "Payment added successfully! Total amount paid: $" + totalAmount, "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static void viewPayment() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null && !studentId.isEmpty()) {
            Double amountPaid = studentPayments.get(studentId);
            if (amountPaid != null) {
                JOptionPane.showMessageDialog(null, "Total amount paid by Student ID " + studentId + ": $" + amountPaid, "View Payment", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No payment record found for Student ID " + studentId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void editPayment() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null && !studentId.isEmpty()) {
            Double currentAmount = studentPayments.get(studentId);
            if (currentAmount != null) {
                String newAmountStr = JOptionPane.showInputDialog("Enter new total amount for Student ID " + studentId + ":");
                if (newAmountStr != null && !newAmountStr.isEmpty()) {
                    try {
                        double newAmount = Double.parseDouble(newAmountStr);
                        if (newAmount > MAX_FEES) {
                            JOptionPane.showMessageDialog(null, "Total payment exceeds the maximum allowed fees of $" + MAX_FEES, "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            studentPayments.put(studentId, newAmount);
                            writeToCSV();
                            JOptionPane.showMessageDialog(null, "Payment updated successfully! New total amount paid: $" + newAmount, "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No payment record found for Student ID " + studentId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void setMaximumFees() {
        String maxFeesStr = JOptionPane.showInputDialog("Enter new maximum fees:");
        if (maxFeesStr != null && !maxFeesStr.isEmpty()) {
            try {
                double newMaxFees = Double.parseDouble(maxFeesStr);
                MAX_FEES = newMaxFees;
                JOptionPane.showMessageDialog(null, "Maximum fees updated successfully! New maximum fees: $" + MAX_FEES, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void writeToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println("StudentID,AmountPaid");
            for (Map.Entry<String, Double> entry : studentPayments.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing payment data to file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


}