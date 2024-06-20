import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Categories {

    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Categories::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Categories Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JButton adminButton = new JButton("Admin Role");
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCategoriesMenu(frame, true);
            }
        });

        JButton studentButton = new JButton("Student Role");
        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel studentFunctionalityPanel = new JPanel(new GridLayout(0, 1)); 
                JButton financialDuesButton = new JButton("University Fees");
                financialDuesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        UniversityPayment.showMainMenu();
                    }
                });

                JButton studentProfileButton = new JButton("Locker Fees");
                studentProfileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LockerManagement.showMainMenu();
                    }
                });

                JButton studentInfoButton = new JButton("Student Info");
                studentInfoButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        StudentInfo.showMainMenu();
                    }
                });

                JButton exitButton = new JButton("Exit");
                exitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                studentFunctionalityPanel.add(financialDuesButton);
                studentFunctionalityPanel.add(studentProfileButton);
                studentFunctionalityPanel.add(studentInfoButton);
                studentFunctionalityPanel.add(exitButton);

                frame.getContentPane().removeAll();
                frame.add(studentFunctionalityPanel, BorderLayout.CENTER);
                frame.revalidate();
            }
        });

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rolePanel.add(adminButton);
        rolePanel.add(studentButton);

        frame.add(rolePanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static void showCategoriesMenu(JFrame frame, boolean isAdmin) {
        if (isAdmin) {
            JPasswordField passwordField = new JPasswordField(10);
            int option = JOptionPane.showConfirmDialog(frame, passwordField, "Enter admin password:", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String password = new String(passwordField.getPassword());
                if (password.equals(ADMIN_PASSWORD)) {
                    frame.getContentPane().removeAll();
                    frame.setLayout(new GridLayout(5, 1));

                    JButton studentButton = new JButton("Student Category");
                    studentButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Student.showMainMenu();
                        }
                    });

                    JButton courseButton = new JButton("Course Category");
                    courseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Course.showMainMenu();
                        }
                    });

                    JButton resultsButton = new JButton("Results Category");
                    resultsButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Result.showMainMenu();
                        }
                    });

                    JButton purchaseButton = new JButton("Purchase Category");
                    purchaseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Purchase.showMainMenu();
                        }
                    });

                    JButton exitButton = new JButton("Exit");
                    exitButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });

                    frame.getContentPane().add(studentButton);
                    frame.getContentPane().add(courseButton);
                    frame.getContentPane().add(resultsButton);
                    frame.getContentPane().add(purchaseButton);
                    frame.getContentPane().add(exitButton);

                    frame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}

class StudentInfo {

    private static final String CSV_FILE = "students.csv";
    private static final Map<String, String[]> studentData = new HashMap<>();

    public static void showMainMenu() {
        loadStudentDataFromCSV();
        String studentId = JOptionPane.showInputDialog("Enter Student ID:");
        if (studentId != null && !studentId.isEmpty()) {
            String[] studentInfo = studentData.get(studentId);
            if (studentInfo != null) {
                StringBuilder info = new StringBuilder("Student Information:\n");
                for (String data : studentInfo) {
                    info.append(data).append("\n");
                }
                JOptionPane.showMessageDialog(null, info.toString(), "Student Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No student record found for Student ID " + studentId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void loadStudentDataFromCSV() {
        studentData.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    studentData.put(parts[0], parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading student data from file.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}