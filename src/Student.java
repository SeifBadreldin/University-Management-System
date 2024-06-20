import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Student {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final Map<String, Map<String, String>> studentsMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Student::promptForPassword);
    }

    private static void promptForPassword() {
        JTextField passwordField = new JPasswordField();
        Object[] message = {"Enter admin password:", passwordField};
        int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String password = passwordField.getText();
            if (password.equals(ADMIN_PASSWORD)) {
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                promptForPassword();
            }
        } else {
            System.exit(0);
        }
    }

    public static void showMainMenu() {
        String[] options = {"Add Student", "View Student", "Edit Student", "Delete Student", "Exit"};
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Choose an operation:", "Main Menu", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    addStudent();
                    break;
                case 1:
                    viewStudent();
                    break;
                case 2:
                    editStudent();
                    break;
                case 3:
                    deleteStudent();
                    break;
                case 4:
                    break;
            }
        } while (choice != 4);
    }

    private static void addStudent() {
        String id;
        boolean validId = false;
        boolean idExists = false;
        do {
            id = JOptionPane.showInputDialog("Enter 9-digit Student ID:");
            if (id != null && id.matches("\\d{9}")) {
                validId = true;
                if (studentsMap.containsKey(id)) {
                    JOptionPane.showMessageDialog(null, "Student with ID " + id + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    idExists = true;
                } else {
                    idExists = isIdInCSV(id);
                    if (idExists) {
                        JOptionPane.showMessageDialog(null, "Student with ID " + id + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid ID! Please enter a 9-digit integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!validId || idExists);

        if (!idExists) {
            String name = JOptionPane.showInputDialog("Enter Student Name:");
            if (name != null) {
                String birthdate = JOptionPane.showInputDialog("Enter Student Birthdate (YYYY-MM-DD):");
                if (birthdate != null) {
                    addStudent(id, name, birthdate);
                }
            }
        }
    }

    private static boolean isIdInCSV(String id) {
        try (BufferedReader br = new BufferedReader(new FileReader("students.csv"))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void viewStudent() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null) {
            boolean found = false;
            try (BufferedReader br = new BufferedReader(new FileReader("students.csv"))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[0].equals(studentId)) {
                        String name = parts[1];
                        String birthdate = parts[2];
                        JOptionPane.showMessageDialog(null, "ID: " + studentId + ", Name: " + name +
                                ", Birthdate: " + birthdate, "View Student", JOptionPane.INFORMATION_MESSAGE);
                        found = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   public static void editStudent() {
    String studentId = JOptionPane.showInputDialog("Enter Student ID:");
    if (studentId != null) {
        Map<String, String[]> studentMap = loadStudentsFromCSV();
        String[] student = studentMap.get(studentId);
        if (student != null) {
            String newName = JOptionPane.showInputDialog("Enter new name for student:");
            String newBirthdate = JOptionPane.showInputDialog("Enter new birthdate for student (YYYY-MM-DD):");
            if (newName != null && !newName.isEmpty() && newBirthdate != null ) {
                boolean confirmed = confirmEdit(studentId, student[1], student[2], newName, newBirthdate);
                if (confirmed) {
                    student[1] = newName; 
                    student[2] = newBirthdate; 
                    writeToCSV(studentMap, "students.csv");
                    JOptionPane.showMessageDialog(null, "Student edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid name or birthdate!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private static boolean confirmEdit(String studentId, String oldName, String oldBirthdate, String newName, String newBirthdate) {
    int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to edit the student:\nID: " + studentId + "\nOld Name: " + oldName + "\nOld Birthdate: " + oldBirthdate + "\nNew Name: " + newName + "\nNew Birthdate: " + newBirthdate, "Confirm Edit", JOptionPane.YES_NO_OPTION);
    return option == JOptionPane.YES_OPTION;
}




    
    
    
    

    public static void deleteStudent() {
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null) {
            Map<String, String[]> studentMap = loadStudentsFromCSV();
            String[] student = studentMap.get(studentId);
            if (student != null) {
                boolean confirmed = confirmDeletion(studentId, student[1]); 
                if (confirmed) {
                    studentMap.remove(studentId);
                    writeToCSV(studentMap, "students.csv");
                    JOptionPane.showMessageDialog(null, "Student deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static boolean confirmDeletion(String studentId, String studentName) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the student:\nID: " + studentId + "\nName: " + studentName, "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }

    private static Map<String, String[]> loadStudentsFromCSV() {
        Map<String, String[]> studentMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("students.csv"))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                studentMap.put(parts[0], parts); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return studentMap;
    }

    private static void writeToCSV(Map<String, String[]> studentMap, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Name,Birthdate"); 
            for (String[] student : studentMap.values()) {
                writer.println(String.join(",", student));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addStudent(String id, String name, String birthdate) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("students.csv", true))) {
            writer.write(id + "," + name + "," + birthdate);
            writer.newLine();
            writer.flush();
            JOptionPane.showMessageDialog(null, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
