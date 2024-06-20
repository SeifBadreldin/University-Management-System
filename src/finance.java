import java.util.Scanner;

public class finance {

    

    private static double universityFees = 25000.0; 
    private static double lockersFees = 0.0; 
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        handlePurchaseOperation(scanner);
        scanner.close(); 
    }
    
    protected static void handlePurchaseOperation(Scanner scanner) {
        while (true) {
            System.out.println("1. Pay university fees");
            System.out.println("2. Pay lockers fees");
            System.out.println("3. Edit university fees");
            System.out.println("4. Edit lockers fees");
            System.out.println("5. View university fees");
            System.out.println("6. View lockers fees");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput(scanner);
            
            switch (choice) {
                case 1:
                    payUniversityFees(scanner);
                    break;
                case 2:
                    payLockersFees(scanner);
                    break;
                case 3:
                    editUniversityFees(scanner);
                    break;
                case 4:
                    editLockersFees(scanner);
                    break;
                case 5:
                    viewUniversityFees();
                    break;
                case 6:
                    viewLockersFees();
                    break;
                case 7:
                    System.out.println("Exiting Purchase Operation...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 7.");
            }
        }
    }
    
    private static int getIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); 
        }
        return scanner.nextInt();
    }
    
    private static void payUniversityFees(Scanner scanner) {
        System.out.println("You selected to pay university fees.");
        boolean continuePayment = true;
        
        while (continuePayment) {
            System.out.println("Your current university fees balance: $" + universityFees);
            System.out.print("Enter the amount you want to pay (0 to exit): $");
            double paymentAmount = scanner.nextDouble();
            
            if (paymentAmount == 0) {
                System.out.println("Exiting University Fees Payment System...");
                continuePayment = false;
            } else if (paymentAmount < 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
            } else if (paymentAmount > universityFees) {
                System.out.println("Payment amount exceeds the current balance. Please enter a smaller amount.");
            } else {
                universityFees -= paymentAmount;
                System.out.println("Payment of $" + paymentAmount + " processed successfully.");
            }
        }
    }
    
    private static void payLockersFees(Scanner scanner) {
        System.out.println("You selected to pay lockers fees.");
        System.out.print("Enter the amount to pay for lockers fees: $");
        double paymentAmount = scanner.nextDouble();
        
      
        lockersFees += paymentAmount;
        System.out.println("Lockers fees paid successfully.");
    }
    
    private static void editUniversityFees(Scanner scanner) {
        System.out.println("You selected to edit university fees.");
        System.out.print("Enter the new university fees: $");
        double newFees = scanner.nextDouble();
        universityFees = newFees;
        System.out.println("University fees updated successfully.");
    }
    
    private static void editLockersFees(Scanner scanner) {
        System.out.println("You selected to edit lockers fees.");
        System.out.print("Enter the new lockers fees: $");
        double newFees = scanner.nextDouble();
        lockersFees = newFees;
        System.out.println("Lockers fees updated successfully.");
    }
    
    private static void viewUniversityFees() {
        System.out.println("You selected to view university fees.");
        System.out.println("Current university fees: $" + universityFees);
    }
    
    private static void viewLockersFees() {
        System.out.println("You selected to view lockers fees.");
        System.out.println("Current lockers fees: $" + lockersFees);
    }
}