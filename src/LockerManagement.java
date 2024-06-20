import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LockerManagement {

    private static final String CSV_FILE = "payment.csv";
    private static final Map<String, String> lockersMap = new HashMap<>();
    private static final int LOCKER_PRICE = 50;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LockerManagement::showMainMenu);
    }

    public static void showMainMenu() {
        loadLockersFromCSV();
        String[] options = {"View Available Lockers", "View Busy Lockers", "Buy Locker", "Exit"};
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Choose an operation:", "Locker Management", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    viewAvailableLockers();
                    break;
                case 1:
                    viewBusyLockers();
                    break;
                case 2:
                    buyLocker();
                    break;
                case 3:
                    break;
            }
        } while (choice != 3);
    }

    private static void loadLockersFromCSV() {
        lockersMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    lockersMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void viewAvailableLockers() {
        StringBuilder availableLockers = new StringBuilder("Available Lockers:\n");
        int count = 0;
        for (int i = 1; i <= 100; i++) {
            String lockerNumber = String.format("%03d", i);
            if ("available".equalsIgnoreCase(lockersMap.getOrDefault(lockerNumber, "available"))) {
                availableLockers.append(lockerNumber).append(" ");
                count++;
                if (count % 10 == 0) {
                    availableLockers.append("\n");
                }
            }
        }
        JOptionPane.showMessageDialog(null, availableLockers.toString(), "Available Lockers", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void viewBusyLockers() {
        StringBuilder busyLockers = new StringBuilder("Busy Lockers:\n");
        for (int i = 1; i <= 100; i++) {
            String lockerNumber = String.format("%03d", i);
            if ("busy".equalsIgnoreCase(lockersMap.getOrDefault(lockerNumber, "available"))) {
                busyLockers.append(lockerNumber).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, busyLockers.toString(), "Busy Lockers", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void buyLocker() {
        String lockerNumber = JOptionPane.showInputDialog("Enter Locker Number to Buy (1-100):");
        if (lockerNumber != null) {
            try {
                int lockerNum = Integer.parseInt(lockerNumber);
                if (lockerNum < 1 || lockerNum > 100) {
                    throw new NumberFormatException();
                }
                lockerNumber = String.format("%03d", lockerNum);
                if ("available".equalsIgnoreCase(lockersMap.getOrDefault(lockerNumber, "available"))) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Locker " + lockerNumber + " costs $" + LOCKER_PRICE + ". Do you want to buy it?", "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        lockersMap.put(lockerNumber, "busy");
                        writeToCSV();
                        JOptionPane.showMessageDialog(null, "Locker " + lockerNumber + " bought successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Locker " + lockerNumber + " is already busy!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid Locker Number! Please enter a number between 1 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void writeToCSV() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            writer.println("LockerNumber,Status");
            for (int i = 1; i <= 100; i++) {
                String lockerNumber = String.format("%03d", i);
                writer.println(lockerNumber + "," + lockersMap.getOrDefault(lockerNumber, "available"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}