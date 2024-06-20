import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Result extends JFrame {

    private static final String ADMIN_PASSWORD = "admin123";
    private static final String STUDENTS_FILE = "students.csv";
    private static final String COURSES_FILE = "courses.csv";

    public Result() {
        super("Result Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel passwordLabel = new JLabel("Enter Admin Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");

        add(passwordLabel);
        add(passwordField);
        add(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                char[] input = passwordField.getPassword();
                String password = new String(input);
                if (password.equals(ADMIN_PASSWORD)) {
                    showMainMenu();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid password! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
               
                passwordField.setText("");
            }
        });

        setSize(300, 150);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void showMainMenu() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainMenuFrame mainMenu = new MainMenuFrame();
                mainMenu.setSize(400, 300); 
                mainMenu.setLocationRelativeTo(null); 
                mainMenu.setVisible(true);
            }
        });
    }

    private static class MainMenuFrame extends JFrame {
        private static final String[] OPTIONS = {"Add Result", "View Result", "Edit Result", "Delete Result", "Exit"};

        public MainMenuFrame() {
            setTitle("Main Menu");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(OPTIONS.length, 1));

            for (int i = 0; i < OPTIONS.length; i++) {
                JButton button = new JButton(OPTIONS[i]);
                button.addActionListener(new MainMenuActionListener(i));
                panel.add(button);
            }

            add(panel, BorderLayout.CENTER);
            pack();
        }
    }

    private static class MainMenuActionListener implements ActionListener {
        private final int option;

        public MainMenuActionListener(int option) {
            this.option = option;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (option) {
                case 0:
                    addResult();
                    break;
                case 1:
                    viewResult();
                    break;
                case 2:
                    editResult();
                    break;
                case 3:
                    deleteResult();
                    break;
                case 4:
                    break;
            }
        }
    }

    private static boolean isGradeAlreadyAdded(String studentID, String courseID) {
        String fileName = courseID + ".csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(studentID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void addResult() {
        String studentID = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentID != null) {
           
            if (!isStudentIDValid(studentID)) {
                JOptionPane.showMessageDialog(null, "Student ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String courseID = JOptionPane.showInputDialog("Enter Course ID:");
            if (courseID != null) {
              
                if (!isCourseIDValid(courseID)) {
                    JOptionPane.showMessageDialog(null, "Course ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (isGradeAlreadyAdded(studentID, courseID)) {
                    JOptionPane.showMessageDialog(null, "Grade for this course has already been added for the student.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String gradeStr = JOptionPane.showInputDialog("Enter Grade:");
                if (gradeStr != null) {
                    try {
                        int grade = Integer.parseInt(gradeStr);
                        if (grade >= 0 && grade <= 100) {
                            String gradeLetter = calculateGradeLetter(grade);
                            saveCourseToFile(courseID, studentID, gradeStr, gradeLetter);
                            JOptionPane.showMessageDialog(null, "Result added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid grade! Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid grade! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private static void editResult() {
        String studentID = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentID != null) {
            String courseID = JOptionPane.showInputDialog("Enter Course ID:");
            if (courseID != null) {
                
                String fileName = courseID + ".csv";
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    boolean found = false;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 1 && parts[0].trim().equals(studentID)) {
                            String currentGrade = parts[2].trim(); 

                        
                            JOptionPane.showMessageDialog(null, "Current Grade: " + currentGrade, "Edit Result", JOptionPane.INFORMATION_MESSAGE);

                            String newGradeStr = JOptionPane.showInputDialog("Enter new Grade (or leave blank to keep current):");
                            if (newGradeStr != null && !newGradeStr.isEmpty()) {
                                try {
                                    int newGrade = Integer.parseInt(newGradeStr);
                                    if (newGrade >= 0 && newGrade <= 100) {
                                        String newGradeLetter = calculateGradeLetter(newGrade);
                                        line = parts[0] + "," + parts[1] + "," + newGradeStr + "," + newGradeLetter;
                                        found = true;
                                        JOptionPane.showMessageDialog(null, "Result updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Invalid grade! Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(null, "Invalid grade! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "No changes made. Result remains unchanged.", "No Changes", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        fileContent.append(line).append("\n");
                    }

                    if (!found) {
                        JOptionPane.showMessageDialog(null, "Result not found for student ID: " + studentID + " and course ID: " + courseID, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                 
                    try (FileWriter writer = new FileWriter(fileName)) {
                        writer.write(fileContent.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void viewResult() {
        String studentID = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentID != null) {
            String courseID = JOptionPane.showInputDialog("Enter Course ID:");
            if (courseID != null) {
                
                String fileName = courseID + ".csv";
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 1 && parts[0].trim().equals(studentID)) {
                            String grade = parts[2].trim();
                            String gradeLetter = parts[3].trim(); 
                            JOptionPane.showMessageDialog(null, "Student ID: " + studentID + "\nCourse ID: " + courseID + "\nGrade: " + grade + "\nGrade Letter: " + gradeLetter, "View Result", JOptionPane.INFORMATION_MESSAGE);
                            return; 
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Result not found for student ID: " + studentID + " and course ID: " + courseID, "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void deleteResult() {
        String studentID = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentID != null) {
            String courseID = JOptionPane.showInputDialog("Enter Course ID:");
            if (courseID != null) {
               
                String fileName = courseID + ".csv";
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    boolean found = false;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 1 && parts[0].trim().equals(studentID)) {
                            found = true;
                            JOptionPane.showMessageDialog(null, "Result for student ID: " + studentID + " and course ID: " + courseID + " found. Deleting the record...", "Success", JOptionPane.INFORMATION_MESSAGE);
                            continue;
                        }
                        fileContent.append(line).append("\n");
                    }

                    if (!found) {
                        JOptionPane.showMessageDialog(null, "Result not found for student ID: " + studentID + " and course ID: " + courseID, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    
                    try (FileWriter writer = new FileWriter(fileName)) {
                        writer.write(fileContent.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String calculateGradeLetter(int grade) {
        if (grade >= 90) {
            return "A";
        } else if (grade >= 80) {
            return "B";
        } else if (grade >= 70) {
            return "C";
        } else if (grade >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    private static boolean isStudentIDValid(String studentID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(studentID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isCourseIDValid(String courseID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(courseID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void saveCourseToFile(String courseID, String studentID, String grade, String gradeLetter) {
        String fileName = courseID + ".csv"; 
        try (FileWriter writer = new FileWriter(fileName, true)) {
            File file = new File(fileName);
            if (file.length() == 0) { 
                writer.append("Student ID,Course Name,Grade,Grade Letter\n");
            }
            writer.append(studentID);
            writer.append(',');
            writer.append(courseID);
            writer.append(',');
            writer.append(grade);
            writer.append(',');
            writer.append(gradeLetter);
            writer.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Result();
            }
        });
    }
}
