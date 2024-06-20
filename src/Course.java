import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Course {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final Map<String, Map<String, String>> coursesMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Course::promptForPassword);
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
        String[] options = {"Add Course", "View Course", "Edit Course", "Delete Course", "Exit"};
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Choose an operation:", "Main Menu", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    addCourse();
                    break;
                case 1:
                    viewCourse();
                    break;
                case 2:
                    editCourse();
                    break;
                case 3:
                    deleteCourse();
                    break;
                case 4:
                    break;
            }
        } while (choice != 4);
    }

    private static void addCourse() {
        String id;
        boolean validId = false;
        
        do {
            id = JOptionPane.showInputDialog("Enter Course ID (3 letters followed by 3 numbers):");
            if (id != null && id.matches("[a-zA-Z]{3}\\d{3}")) {
                if (coursesMap.containsKey(id)) {
                    JOptionPane.showMessageDialog(null, "Course with ID " + id + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    validId = true;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid ID! Please enter 3 letters followed by 3 numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!validId);
    
        String name = JOptionPane.showInputDialog("Enter Course Name:");
        if (name != null) {
            String creditHoursStr = JOptionPane.showInputDialog("Enter Credit Hours:");
            if (creditHoursStr != null) {
                int creditHours;
                try {
                    creditHours = Integer.parseInt(creditHoursStr);
                    if (creditHours <= 0) {
                        throw new NumberFormatException();
                    }
                    String maxDegree = JOptionPane.showInputDialog("Enter Max Degree:");
                    if (maxDegree != null) {
                        addCourse(id, name, creditHours, maxDegree);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid credit hours! Please enter a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public static void viewCourse() {
        String courseId = JOptionPane.showInputDialog("Enter Course ID:");
        if (courseId != null) {
            boolean found = false;
            try (BufferedReader br = new BufferedReader(new FileReader("courses.csv"))) {
                String line;
                br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[0].equals(courseId)) {
                        String name = parts[1];
                        String creditHours = parts[2];
                        String maxDegree = parts[3];
                        JOptionPane.showMessageDialog(null, "ID: " + courseId + ", Name: " + name +
                                ", Credit Hours: " + creditHours + ", Max Degree: " + maxDegree, "View Course", JOptionPane.INFORMATION_MESSAGE);
                        found = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void editCourse() {
        String courseId = JOptionPane.showInputDialog("Enter Course ID:");
        if (courseId != null) {
            Map<String, String> course = coursesMap.get(courseId);
            if (course == null) {
                course = loadCourseFromCSV(courseId);
            }
    
            if (course != null) {
                String newName = JOptionPane.showInputDialog("Enter new name:", course.get("Name"));
                String newCreditHoursStr = JOptionPane.showInputDialog("Enter new credit hours:", course.get("CreditHours"));
                String newMaxDegree = JOptionPane.showInputDialog("Enter new max degree:", course.get("MaxDegree"));
                if (newName != null && newCreditHoursStr != null && newMaxDegree != null) {
                    boolean confirmed = confirmEdit(courseId, course.get("Name"), newName, newCreditHoursStr, newMaxDegree);
                    if (confirmed) {
                   
                        updateCourseInCSV(courseId, newName, newCreditHoursStr, newMaxDegree);
                        JOptionPane.showMessageDialog(null, "Course details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static void updateCourseInCSV(String courseId, String newName, String newCreditHours, String newMaxDegree) {
        try {
            File inputFile = new File("courses.csv");
            File tempFile = new File("temp.csv");
    
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
            String currentLine;
            String newLine;
    
            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split(",");
                if (parts.length >= 1 && parts[0].equals(courseId)) {
                   
                    newLine = courseId + "," + newName + "," + newCreditHours + "," + newMaxDegree;
                    writer.write(newLine + System.getProperty("line.separator"));
                } else {
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
            }
            writer.close();
            reader.close();
    
            if (!inputFile.delete()) {
                JOptionPane.showMessageDialog(null, "Error updating course. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!tempFile.renameTo(inputFile)) {
                JOptionPane.showMessageDialog(null, "Error updating course. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating course. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        
    }
    private static boolean confirmEdit(String courseId, String oldName, String newName, String newCreditHours, String newMaxDegree) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to edit the course:\nID: " + courseId + "\nOld Name: " + oldName + "\nNew Name: " + newName + "\nNew Credit Hours: " + newCreditHours + "\nNew Max Degree: " + newMaxDegree, "Confirm Edit", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }
    

    public static void deleteCourse() {
        String courseId = JOptionPane.showInputDialog("Enter Course ID:");
        if (courseId != null) {
            Map<String, String[]> courseMap = loadCoursesFromCSV();
            String[] course = courseMap.get(courseId);
            if (course != null) {
                boolean confirmed = confirmDeletion(courseId, course[1]); 
                if (confirmed) {
                    courseMap.remove(courseId);
                    writeToCSV(courseMap, "courses.csv");
                    JOptionPane.showMessageDialog(null, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private static boolean confirmDeletion(String courseId, String courseName) {
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the course:\nID: " + courseId + "\nName: " + courseName, "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }
    
    private static Map<String, String[]> loadCoursesFromCSV() {
        Map<String, String[]> courseMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("courses.csv"))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                courseMap.put(parts[0], parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseMap;
    }

   
    
    
    

   

    private static Map<String, String> loadCourseFromCSV(String courseId) {
        Map<String, String> course = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("courses.csv"))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(courseId)) {
                    course.put("Name", parts[1]);
                    course.put("CreditHours", parts[2]);
                    course.put("MaxDegree", parts[3]);
                    coursesMap.put(courseId, course);
                    return course;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
    public static void addCourse(String id, String name, int creditHours, String maxDegree) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("courses.csv", true))) {
            writer.write(id + "," + name + "," + creditHours + "," + maxDegree);
            writer.newLine();
            writer.flush();
            JOptionPane.showMessageDialog(null, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private static void writeToCSV(Map<String, String[]> courseMap, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("ID,Name,CreditHours,MaxDegree"); // Header
            for (String[] course : courseMap.values()) {
                writer.println(String.join(",", course));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
