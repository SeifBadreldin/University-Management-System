import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StudentInfo {

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
            br.readLine(); // Skip header
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