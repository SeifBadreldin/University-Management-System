import javax.swing.*;
import java.io.*;
import java.util.*;

public class Purchase {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final Map<String, PurchaseItem> lockersMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Purchase::promptForPassword);
    }

    private static void promptForPassword() {
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {"Enter admin password:", passwordField};
        int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            if (password.equals(ADMIN_PASSWORD)) {
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                promptForPassword();
            }
            Arrays.fill(passwordChars, '0');
        } else {
            System.exit(0);
        }
    }

    public static void showMainMenu() {
        String[] options = {"Add Purchase", "View Purchases", "Edit Purchase", "Delete Purchase", "Exit"};
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Choose an operation:", "Main Menu", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    addPurchase();
                    break;
                case 1:
                    viewPurchases();
                    break;
                case 2:
                    editPurchase();
                    break;
                case 3:
                    deletePurchase();
                    break;
                case 4:
                    break;
            }
        } while (choice != 4);
    }

    private static void addPurchase() {
        String[] purchaseOptions = {"Lockers", "University Fees"};

        String description = (String) JOptionPane.showInputDialog(null, "Select Purchase Type:", "Select Purchase", JOptionPane.QUESTION_MESSAGE, null, purchaseOptions, purchaseOptions[0]);
        if (description == null) {
            return;
        }

        try {
            if (description.equals("Lockers")) {
                addLockerPurchase();
            } else if (description.equals("University Fees")) {
                addUniversityFeesPurchase();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid purchase type!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void addLockerPurchase() {
        int quantity = getValidQuantity("Enter Available Quantity:");
        if (quantity == -1) return;

        for (int i = 0; i < quantity; i++) {
            String lockerNumber;
            boolean lockerExists;
            do {
                lockerNumber = JOptionPane.showInputDialog("Enter Locker Number " + (i + 1) + " of " + quantity + ":");
                if (lockerNumber == null || lockerNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Locker number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lockerExists = lockersMap.containsKey(lockerNumber) || lockerNumberExistsInCSV(lockerNumber);
                if (lockerExists) {
                    JOptionPane.showMessageDialog(null, "Locker number already exists! Please enter a different one.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (lockerExists);

            String paymentTypeForLocker = choosePaymentType("Select Payment Type for Locker " + (i + 1) + ":");
            if (paymentTypeForLocker == null) {
                return;
            }

            double amountForLocker = getValidAmount("Enter Amount for Locker " + (i + 1) + ":");
            if (amountForLocker == -1) return;

            PurchaseItem purchaseItem = new PurchaseItem("Locker", amountForLocker, paymentTypeForLocker, 1, lockerNumber);
            lockersMap.put(lockerNumber, purchaseItem);
        }
        appendToCSV(lockersMap, "lockers.csv");
        JOptionPane.showMessageDialog(null, "Locker purchase added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static double getValidAmount(String prompt) {
        String input = JOptionPane.showInputDialog(prompt);
        if (input == null) return -1;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return getValidAmount(prompt);
        }
    }

    private static String choosePaymentType(String prompt) {
        String[] paymentOptions = {"Cash", "Visa", "Both cash & Visa are available"};
        return (String) JOptionPane.showInputDialog(null, prompt, "Select Payment Type", JOptionPane.QUESTION_MESSAGE, null, paymentOptions, paymentOptions[0]);
    }

    private static int getValidQuantity(String prompt) {
        String input = JOptionPane.showInputDialog(prompt);
        if (input == null) return -1;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return getValidQuantity(prompt);
        }
    }

    private static void addUniversityFeesPurchase() {
     
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId == null || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Student ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
       
        if (studentIdExistsInUniversityFees(studentId)) {
            JOptionPane.showMessageDialog(null, "Student ID already exists in university fees data!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        
        if (!studentIdExists(studentId)) {
            JOptionPane.showMessageDialog(null, "Student ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
       
        String paymentType = choosePaymentType("Select Payment Type for University Fees:");
        if (paymentType == null) return;
    
        double amountForUniversityFees = getValidAmount("Enter Amount for University Fees:");
        if (amountForUniversityFees == -1) return;
    
   
        appendToUniversityFeesCSV(studentId, amountForUniversityFees, paymentType);
        JOptionPane.showMessageDialog(null, "University fees purchase added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private static boolean studentIdExistsInUniversityFees(String studentId) {
        String csvFile = "university_fees.csv";
        String line;
        String cvsSplitBy = ",";
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
    
                if (data.length >= 1 && data[0].equals(studentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    private static boolean studentIdExists(String studentId) {
        String csvFile = "students.csv";
        String line;
        String cvsSplitBy = ",";
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
    
                if (data.length >= 1 && data[0].equals(studentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static void appendToUniversityFeesCSV(String studentId, double amount, String paymentType) {
        String csvFile = "university_fees.csv";
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            
            File file = new File(csvFile);
            if (file.length() == 0) {
                bw.write("Student ID,Amount,Payment Type\n"); 
            }
    
            String data = studentId + "," + amount + "$," + paymentType + "\n"; 
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error appending to university_fees.csv", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    

    private static void viewPurchases() {
        String[] options = {"Lockers", "University Fees"};
        String description = (String) JOptionPane.showInputDialog(null, "Select Purchase Type:", "Select Purchase", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (description == null) {
            return;
        }
    
        if (description.equals("Lockers")) {
            String lockerNumber = JOptionPane.showInputDialog(null, "Enter locker number:");
            if (lockerNumber != null && !lockerNumber.isEmpty()) {
                displayLockerData(lockerNumber);
            }
        }else if (description.equals("University Fees")) {
    String studentCode = JOptionPane.showInputDialog(null, "Enter student code:");
    if (studentCode != null && !studentCode.isEmpty()) {
        boolean studentFound = checkStudentExistence(studentCode);
        if (studentFound) {
            try (BufferedReader br = new BufferedReader(new FileReader("university_fees.csv"))) {
                String line;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 2 && data[0].trim().equals(studentCode.trim())) {
                        
                        JOptionPane.showMessageDialog(null, "Student Code: " + data[0].trim() + "\nAmount: " + data[1].trim() + "\nType: " + data[2].trim(), "University Fees for Student Code " + studentCode, JOptionPane.INFORMATION_MESSAGE);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "University fees data not found for student code " + studentCode, "Data Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading university fees data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Student with ID " + studentCode + " not found!", "Student Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }
} else {
    JOptionPane.showMessageDialog(null, "Invalid purchase type!", "Error", JOptionPane.ERROR_MESSAGE);
}
    }
    
    private static boolean checkStudentExistence(String studentCode) {
        try (BufferedReader br = new BufferedReader(new FileReader("university_fees.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].trim().equals(studentCode.trim())) {
                    return true; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading students data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; 
    }
    
    private static void displayLockerData(String lockerNumber) {
        StringBuilder purchasesText = new StringBuilder("Locker Purchases:\n");
        boolean lockerExists = false;

        try (BufferedReader br = new BufferedReader(new FileReader("lockers.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[3].trim().equals(lockerNumber.trim())) {
                    lockerExists = true;
                    purchasesText.append("Description: ").append(parts[0]).append(", Amount: ").append(parts[1]).append(", Payment Type: ").append(parts[2]).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!lockerExists) {
            JOptionPane.showMessageDialog(null, "Locker number does not exist.", "View Locker Purchases", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, purchasesText.toString(), "View Locker Purchases", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private static void editPurchase() {
        String[] purchaseOptions = {"Lockers", "University Fees"};
        String description = (String) JOptionPane.showInputDialog(null, "Select Purchase Type:", "Select Purchase", JOptionPane.QUESTION_MESSAGE, null, purchaseOptions, purchaseOptions[0]);
        if (description == null) {
            return;
        }

        if (description.equals("Lockers")) {
            editLockerPurchase();
        } else if (description.equals("University Fees")) {
            editUniversityFeesPurchase();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid purchase type!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void editLockerPurchase() {
        String lockerNumber = JOptionPane.showInputDialog("Enter Locker Number to Edit:");
        if (lockerNumber != null && !lockerNumber.isEmpty()) {
            boolean lockerExists = false;
            String csvFileName = "lockers.csv";

            try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[3].trim().equals(lockerNumber.trim())) {
                        lockerExists = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error reading CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (lockerExists) {

                double newAmount = getValidAmount("Enter new amount:");
                if (newAmount == -1) {
                    return;
                }

                String newPaymentType = choosePaymentType("Select Payment Type:");
                if (newPaymentType == null) {
                    return;
                }

                updateLockerCSV(csvFileName, lockerNumber, newAmount, newPaymentType);
                JOptionPane.showMessageDialog(null, "Purchase edited successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Locker number not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void updateLockerCSV(String csvFileName, String lockerNumber, double newAmount, String newPaymentType) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
            StringBuilder newData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[3].trim().equals(lockerNumber.trim())) {
                    newData.append(parts[0]).append(",").append(newAmount).append(",").append(newPaymentType).append(",").append(parts[3]).append("\n");
                } else {
                    newData.append(line).append("\n");
                }
            }

            try (FileWriter writer = new FileWriter(csvFileName)) {
                writer.write(newData.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void editUniversityFeesPurchase() {
        String studentCode = JOptionPane.showInputDialog(null, "Enter student code:");
        if (studentCode != null && !studentCode.isEmpty()) {
            boolean studentFound = checkStudentExistence(studentCode);
            if (studentFound) {
                try {
                    
                    List<String> lines = new ArrayList<>();
                    File file = new File("university_fees.csv");
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        lines.add(scanner.nextLine());
                    }
                    scanner.close();
    
                    
                    boolean found = false;
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        String[] data = line.split(",");
                        if (data.length >= 2 && data[0].trim().equals(studentCode.trim())) {
                           
                            found = true;
    
                            
                            String newAmountStr = JOptionPane.showInputDialog(null, "Enter new amount:");
                            String newType = choosePaymentType("Select new payment type:");
    
                            if (newAmountStr != null && newType != null) {
                                double newAmount = Double.parseDouble(newAmountStr);
                                lines.set(i, studentCode + "," + newAmount + "," + newType);
                               
                                FileWriter writer = new FileWriter(file);
                                for (String updatedLine : lines) {
                                    writer.write(updatedLine + "\n");
                                }
                                writer.close();
                                JOptionPane.showMessageDialog(null, "University fees purchase edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        }
                    }
                    if (!found) {
                        JOptionPane.showMessageDialog(null, "University fees data not found for student code " + studentCode, "Data Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading/writing university fees data!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Student with ID " + studentCode + " not found!", "Student Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    

    private static void deletePurchase() {
        String[] purchaseOptions = {"Lockers", "University Fees"};
        String description = (String) JOptionPane.showInputDialog(null, "Select Purchase Type:", "Select Purchase", JOptionPane.QUESTION_MESSAGE, null, purchaseOptions, purchaseOptions[0]);
        if (description == null) {
            return;
        }

        if (description.equals("Lockers")) {
            deleteLockerPurchase();
        } else if (description.equals("University Fees")) {
            deleteUniversityFeesPurchase();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid purchase type!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void deleteLockerPurchase() {
        String lockerNumber = JOptionPane.showInputDialog("Enter Locker Number of the Purchase to Delete:");
        if (lockerNumber != null && !lockerNumber.isEmpty()) {
            int confirmDelete = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the purchase associated with locker number " + lockerNumber + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmDelete == JOptionPane.YES_OPTION) {
                boolean purchaseFound = false;
                String csvFileName = "lockers.csv";

                try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 4 && parts[3].trim().equals(lockerNumber.trim())) {
                            purchaseFound = true;
                        } else {
                            lines.add(line);
                        }
                    }

                    try (FileWriter writer = new FileWriter(csvFileName)) {
                        for (String l : lines) {
                            writer.write(l + "\n");
                        }
                    }

                    if (purchaseFound) {
                        JOptionPane.showMessageDialog(null, "Purchase deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Purchase not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting purchase.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Deletion cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static void deleteUniversityFeesPurchase() {
        String studentCode = JOptionPane.showInputDialog(null, "Enter student code:");
        if (studentCode != null && !studentCode.isEmpty()) {
            boolean studentFound = checkStudentExistence(studentCode);
            if (studentFound) {
                try {
                    
                    List<String> lines = new ArrayList<>();
                    File file = new File("university_fees.csv");
                    Scanner scanner = new Scanner(file);
                    boolean found = false;
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] data = line.split(",");
                        if (data.length >= 2 && data[0].trim().equals(studentCode.trim())) {
                           
                            found = true;
                        } else {
                            lines.add(line);
                        }
                    }
                    scanner.close();
    
                    if (found) {
                        FileWriter writer = new FileWriter(file);
                        for (String updatedLine : lines) {
                            writer.write(updatedLine + "\n");
                        }
                        writer.close();
                        JOptionPane.showMessageDialog(null, "University fees purchase deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "University fees data not found for student code " + studentCode, "Data Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error reading/writing university fees data!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Student with ID " + studentCode + " not found!", "Student Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    

    private static void appendToCSV(Map<String, PurchaseItem> map, String fileName) {
        String csvFile = fileName;
        String cvsSplitBy = ",";
        String titles = "Description,Amount,Payment Type,Locker Number\n";
        Set<String> uniqueEntries = new HashSet<>();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            File file = new File(csvFile);
            if (file.length() == 0) {
                bw.write(titles);
            }

            for (Map.Entry<String, PurchaseItem> entry : map.entrySet()) {
                PurchaseItem item = entry.getValue();
                String entryString = item.getDescription() + item.getAmount() + item.getPaymentType() + item.getLockerNumber();
                if (!uniqueEntries.contains(entryString)) {
                    String data = item.getDescription() + cvsSplitBy + item.getAmount() + "$" + cvsSplitBy + item.getPaymentType() + cvsSplitBy + item.getLockerNumber() + "\n";
                    bw.write(data);
                    uniqueEntries.add(entryString);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean lockerNumberExistsInCSV(String lockerNumber) {
        String csvFile = "lockers.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                if (data.length >= 4 && data[3].equals(lockerNumber)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static class PurchaseItem {
        private String description;
        private double amount;
        private String paymentType;
        private int quantity;
        private String lockerNumber;

        public PurchaseItem(String description, double amount, String paymentType, int quantity, String lockerNumber) {
            this.description = description;
            this.amount = amount;
            this.paymentType = paymentType;
            this.quantity = quantity;
            this.lockerNumber = lockerNumber;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getLockerNumber() {
            return lockerNumber;
        }

        public void setLockerNumber(String lockerNumber) {
            this.lockerNumber = lockerNumber;
        }
    }
}
