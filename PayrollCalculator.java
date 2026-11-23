import java.util.Scanner;

public class PayrollCalculator {

    static final int MAX_EMPLOYEES = 100;
    static final String FILE_NAME = "employees.csv";
    static final double REGULAR_HOURS_LIMIT = 40.0;
    static final double OVERTIME_MULTIPLIER = 1.25;
    static final double TAX_RATE = 0.10;
    static final double CONTRIBUTION_RATE = 0.05;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] empIds = new String[MAX_EMPLOYEES];
        String[] empNames = new String[MAX_EMPLOYEES];
        String[] empPositions = new String[MAX_EMPLOYEES];
        double[] hourlyRates = new double[MAX_EMPLOYEES];
        double[] hoursWorked = new double[MAX_EMPLOYEES];

        System.out.println("            EMPLOYEE PAYROLL CALCULATOR SYSTEM            ");

        int recordCount = loadEmployeesFromCSV(
                FILE_NAME, empIds, empNames, empPositions, hourlyRates, hoursWorked
        );

        int choice;
        do {
            displayMenu();
            choice = getValidInt(scanner, "Enter your choice (1-7): ", 1, 7);

            switch (choice) {
                case 1:
                    viewAllEmployees(empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount);
                    break;
                case 2:
                    recordCount = addEmployee(
                            scanner, empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount
                    );
                    break;
                case 3:
                    searchEmployee(scanner, empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount);
                    break;
                case 4:
                    updateEmployee(scanner, empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount);
                    break;
                case 5:
                    recordCount = deleteEmployee(
                            scanner, empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount
                    );
                    break;
                case 6:
                    generatePayrollSummary(empIds, empNames, hourlyRates, hoursWorked, recordCount);
                    break;
                case 7:
                    boolean saved = saveEmployeesToCSV(
                            FILE_NAME, empIds, empNames, empPositions, hourlyRates, hoursWorked, recordCount
                    );
                    if (saved) {
                        System.out.println("\nAll records saved to " + FILE_NAME + ". Goodbye!");
                    } else {
                        System.out.println("\nExiting program (warning: final save not completed).");
                    }
                    break;
            }
        } while (choice != 7);

        scanner.close();
    }

    public static void displayMenu() {
        System.out.println("\n----------------------- MAIN MENU ------------------------");
        System.out.println("1. Check All Employee Payroll Records");
        System.out.println("2. Add New Employee Record");
        System.out.println("3. Search Employee & Print Payslip");
        System.out.println("4. Update Existing Employee Record");
        System.out.println("5. Delete Employee Record");
        System.out.println("6. Generate Payroll Summary Report");
        System.out.println("7. Save and Exit");
        System.out.println("----------------------------------------------------------");
    }

    public static int getValidInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[Invalid Input] Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("[Invalid Input] Please enter a valid whole number.");
            }
        }
    }

    // THIS SECTION IS FOR FILE HANDLING (CSV READ/WRITE) AND IS NOT YET IMPLEMENTED (San Jose)

    // TODO: Read CSV rows using BufferedReader, validate fields/numbers/duplicates, load arrays, return count.
    public static int loadEmployeesFromCSV(String fileName, String[] ids, String[] names,
        String[] positions, double[] rates, double[] hours) {

    int count = 0;

    try {
        File file = new File(fileName);

        if (file.exists()) {
            Scanner reader = new Scanner(file);
            reader.nextLine();

            while (reader.hasNextLine() && count < MAX_EMPLOYEES) {
                String[] data = reader.nextLine().split(",");

                if (data.length == 5) {
                    ids[count] = data[0];
                    names[count] = data[1];
                    positions[count] = data[2];
                    rates[count] = Double.parseDouble(data[3]);
                    hours[count] = Double.parseDouble(data[4]);
                    count++;
                }
            }
            reader.close();
        }
    } catch (Exception e) {
        System.out.println("Error loading records.");
    }

    return count;
}

public static boolean saveEmployeesToCSV(String fileName, String[] ids, String[] names,
        String[] positions, double[] rates, double[] hours, int count) {

    try {
        FileWriter writer = new FileWriter(fileName);
        writer.write("ID,Name,Position,Rate,Hours\n");

        for (int i = 0; i < count; i++) {
            writer.write(ids[i] + "," + names[i] + "," + positions[i] + ","
                    + rates[i] + "," + hours[i] + "\n");
        }

        writer.close();
        return true;

    } catch (Exception e) {
        return false;
    }
}

    // TODO: Write CSV header and all active records using PrintWriter & FileWriter; return true on success.
    public static boolean saveEmployeesToCSV(
            String fileName, String[] ids, String[] names,
            String[] positions, double[] rates, double[] hours, int count) {
        System.out.println("[TODO] saveEmployeesToCSV() not yet implemented.");
        return false;
    }

    // THIS SECTION IS FOR VALIDATION & SEARCH HELPERS (Cadahing)

    // TODO: Loop until user enters a valid double between min and max.
    public static double getValidDouble(Scanner scanner, String prompt, double min, double max) {
        return 0.0;
    }

    // TODO: Loop until user enters a non-empty string with no commas (',').
    public static String getValidString(Scanner scanner, String prompt) {
        return "";
    }

    // TODO: Linear search ids[0..count-1] ignoring case; return index or -1 if not found.
    public static int findEmployeeById(String[] ids, int count, String targetId) {
        return -1;
    }

    // THIS SECTION IS FOR PAYROLL CALCULATIONS (Saniel)

    // TODO: Compute gross pay (regular up to 40 hrs; excess hours at 1.25x rate).
    public static double computeGrossPay(double rate, double hours) {
        return 0.0;
    }

    // TODO: Compute total deductions = grossPay * (TAX_RATE + CONTRIBUTION_RATE).
    public static double computeDeductions(double grossPay) {
        return 0.0;
    }

    // TODO: Compute net pay = grossPay - deductions.
    public static double computeNetPay(double grossPay, double deductions) {
        return 0.0;
    }

    // THIS SECTION IS FOR VIEWING & ADDING EMPLOYEES (De Guzman)

    // TODO: Print all records with computed Gross, Deductions, and Net Pay in an aligned table.
    public static void viewAllEmployees(
            String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {
        System.out.println("[TODO] viewAllEmployees() not yet implemented.");
    }

    // TODO: Check capacity, validate unique ID + inputs, store in arrays, increment count, auto-save CSV.
    public static int addEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {
        System.out.println("[TODO] addEmployee() not yet implemented.");
        return count;
    }

    // THIS SECTION IS FOR SEARCHING, UPDATING, DELETING, AND SUMMARY REPORT (San Jose)

    // TODO: Find employee by ID and print an itemized payslip (regular, overtime, tax, contrib, net).
    public static void searchEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {
        System.out.println("[TODO] searchEmployee() not yet implemented.");
    }

    // TODO: Find employee by ID, show field edit menu (Name/Position/Rate/Hours), update array, auto-save CSV.
    public static void updateEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {
        System.out.println("[TODO] updateEmployee() not yet implemented.");
    }

    // THIS SECTION IS FOR DELETE & SUMMARY REPORT (Medellin)

    // TODO: Find by ID, confirm (Y/N), shift all 5 arrays left, clear last slot, decrement count, auto-save CSV.
    public static int deleteEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {
        System.out.println("[TODO] deleteEmployee() not yet implemented.");
        return count;
    }

    // TODO: Compute & display total employees, overtime count, total gross/deductions/net, average net, highest earner.
    public static void generatePayrollSummary(
            String[] ids, String[] names, double[] rates, double[] hours, int count) {
        System.out.println("[TODO] generatePayrollSummary() not yet implemented.");
    }
}
