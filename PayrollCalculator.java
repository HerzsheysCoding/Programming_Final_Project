import java.io.File;
import java.io.FileWriter;
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

        System.out.println("==========================================================");
        System.out.println("            EMPLOYEE PAYROLL CALCULATOR SYSTEM            ");
        System.out.println("==========================================================");

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

    // ==================== FILE HANDLING (San Jose) ====================

    public static int loadEmployeesFromCSV(String fileName, String[] ids, String[] names,
            String[] positions, double[] rates, double[] hours) {

        int count = 0;
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("[Notice] '" + fileName + "' not found. Starting with an empty list.");
            return 0;
        }

        try {
            Scanner reader = new Scanner(file);
            if (reader.hasNextLine()) {
                reader.nextLine(); // Skip CSV header row
            }

            while (reader.hasNextLine() && count < MAX_EMPLOYEES) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 5) {
                    try {
                        String id = data[0].trim();
                        String name = data[1].trim();
                        String position = data[2].trim();
                        double rate = Double.parseDouble(data[3].trim());
                        double hrs = Double.parseDouble(data[4].trim());

                        if (!id.isEmpty() && !name.isEmpty() && !position.isEmpty()
                                && rate > 0 && hrs >= 0 && findEmployeeById(ids, count, id) == -1) {
                            ids[count] = id;
                            names[count] = name;
                            positions[count] = position;
                            rates[count] = rate;
                            hours[count] = hrs;
                            count++;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[Warning] Skipped invalid row in CSV.");
                    }
                }
            }
            reader.close();
            System.out.println("[Success] Loaded " + count + " record(s) from " + fileName + ".");
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

    // ==================== VALIDATION & SEARCH HELPERS (Cadahing) ====================

    public static double getValidDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Invalid input. Enter a value between " + min + " and " + max + ".");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    public static String getValidString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Invalid input. Input cannot be empty.");
            } else if (input.contains(",")) {
                System.out.println("Invalid input. Commas are not allowed.");
            } else {
                return input;
            }
        }
    }

    public static int findEmployeeById(String[] ids, int count, String targetId) {
        for (int i = 0; i < count; i++) {
            if (ids[i].equalsIgnoreCase(targetId)) {
                return i;
            }
        }
        return -1;
    }

    // ==================== PAYROLL CALCULATIONS (Saniel) ====================

    public static double computeGrossPay(double rate, double hours) {
        if (!Double.isFinite(rate) || !Double.isFinite(hours) || rate <= 0 || hours <= 0) {
            return 0.0;
        }

        double regularHours = Math.min(hours, REGULAR_HOURS_LIMIT);
        double overtimeHours = Math.max(0.0, hours - REGULAR_HOURS_LIMIT);
        double regularPay = rate * regularHours;
        double overtimePay = overtimeHours * rate * OVERTIME_MULTIPLIER;

        return regularPay + overtimePay;
    }

    public static double computeDeductions(double grossPay) {
        if (!Double.isFinite(grossPay) || grossPay <= 0) {
            return 0.0;
        }
        return grossPay * (TAX_RATE + CONTRIBUTION_RATE);
    }

    public static double computeNetPay(double grossPay, double deductions) {
        if (!Double.isFinite(grossPay) || !Double.isFinite(deductions)) {
            return 0.0;
        }
        return grossPay - deductions;
    }

    // ==================== VIEWING & ADDING EMPLOYEES (De Guzman) ====================

    public static void viewAllEmployees(
            String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {

        if (count == 0) {
            System.out.println("\nNo employee records found.");
            return;
        }

        System.out.println("\n================ EMPLOYEE PAYROLL RECORDS ================");

        System.out.printf("%-12s %-30s %-25s %-12s %-12s %-12s %-12s %-12s%n",
                "EmployeeID", "Name", "Position", "HourlyRate",
                "HoursWorked", "Gross", "Deductions", "NetPay");

        for (int i = 0; i < count; i++) {
            double gross = computeGrossPay(rates[i], hours[i]);
            double deductions = computeDeductions(gross);
            double net = computeNetPay(gross, deductions);

            System.out.printf("%-12s %-30s %-25s %-12.2f %-12.2f %-12.2f %-12.2f %-12.2f%n",
                    ids[i], names[i], positions[i], rates[i],
                    hours[i], gross, deductions, net);
        }

        System.out.println("===========================================================");
    }

    public static int addEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {

        if (count >= MAX_EMPLOYEES) {
            System.out.println("\nEmployee capacity is full!");
            return count;
        }

        System.out.println("\n========== ADD NEW EMPLOYEE ==========");

        String id;
        while (true) {
            id = getValidString(scanner, "Enter Employee ID: ");

            if (findEmployeeById(ids, count, id) != -1) {
                System.out.println("Employee ID already exists. Try again.");
            } else {
                break;
            }
        }

        String name = getValidString(scanner, "Enter Employee Name: ");
        String position = getValidString(scanner, "Enter Employee Position: ");
        double rate = getValidDouble(scanner, "Enter Hourly Rate: ", 0.01, Double.MAX_VALUE);
        double workedHours = getValidDouble(scanner, "Enter Hours Worked: ", 0.0, Double.MAX_VALUE);

        ids[count] = id;
        names[count] = name;
        positions[count] = position;
        rates[count] = rate;
        hours[count] = workedHours;

        count++;

        System.out.println("\nEmployee record added successfully!");

        boolean saved = saveEmployeesToCSV(
                FILE_NAME, ids, names, positions, rates, hours, count);

        if (saved) {
            System.out.println("Employee records saved successfully!");
        } else {
            System.out.println("Warning: Unable to save employee records.");
        }

        return count;
    }

    // ==================== SEARCHING & UPDATING (San Jose) ====================

    public static void searchEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {

        if (count == 0) {
            System.out.println("\nNo employee records found.");
            return;
        }

        String targetId = getValidString(scanner, "Enter Employee ID to search: ");
        int index = findEmployeeById(ids, count, targetId);

        if (index == -1) {
            System.out.println("Employee ID not found.");
            return;
        }

        double regularHours = Math.min(hours[index], REGULAR_HOURS_LIMIT);
        double overtimeHours = Math.max(0.0, hours[index] - REGULAR_HOURS_LIMIT);
        double regularPay = regularHours * rates[index];
        double overtimePay = overtimeHours * rates[index] * OVERTIME_MULTIPLIER;
        double gross = computeGrossPay(rates[index], hours[index]);
        double tax = gross * TAX_RATE;
        double contrib = gross * CONTRIBUTION_RATE;
        double deductions = computeDeductions(gross);
        double net = computeNetPay(gross, deductions);

        System.out.println("\n--- EMPLOYEE PAYSLIP ---");
        System.out.println("ID: " + ids[index]);
        System.out.println("Name: " + names[index]);
        System.out.println("Position: " + positions[index]);
        System.out.printf("Hourly Rate: PHP %.2f%n", rates[index]);
        System.out.printf("Regular Pay (%.1f hrs): PHP %.2f%n", regularHours, regularPay);
        System.out.printf("Overtime Pay (%.1f hrs): PHP %.2f%n", overtimeHours, overtimePay);
        System.out.printf("Gross Pay: PHP %.2f%n", gross);
        System.out.printf("Tax (10%%): PHP %.2f%n", tax);
        System.out.printf("Contributions (5%%): PHP %.2f%n", contrib);
        System.out.printf("Total Deductions: PHP %.2f%n", deductions);
        System.out.printf("Net Pay: PHP %.2f%n", net);
    }

    public static void updateEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {

        if (count == 0) {
            System.out.println("\nNo employee records found.");
            return;
        }

        String targetId = getValidString(scanner, "Enter Employee ID to update: ");
        int index = findEmployeeById(ids, count, targetId);

        if (index == -1) {
            System.out.println("Employee ID not found.");
            return;
        }

        System.out.println("Updating: " + names[index]);
        System.out.println("1. Name");
        System.out.println("2. Position");
        System.out.println("3. Hourly Rate");
        System.out.println("4. Hours Worked");
        System.out.println("5. Cancel");

        int option = getValidInt(scanner, "Choose field to update (1-5): ", 1, 5);

        switch (option) {
            case 1:
                names[index] = getValidString(scanner, "Enter new Name: ");
                break;
            case 2:
                positions[index] = getValidString(scanner, "Enter new Position: ");
                break;
            case 3:
                rates[index] = getValidDouble(scanner, "Enter new Hourly Rate: ", 0.01, Double.MAX_VALUE);
                break;
            case 4:
                hours[index] = getValidDouble(scanner, "Enter new Hours Worked: ", 0.0, Double.MAX_VALUE);
                break;
            case 5:
                System.out.println("Update cancelled.");
                return;
        }

        saveEmployeesToCSV(FILE_NAME, ids, names, positions, rates, hours, count);
        System.out.println("Employee record updated!");
    }

    // ==================== DELETE & SUMMARY REPORT (Medellin) ====================

    public static int deleteEmployee(
            Scanner scanner, String[] ids, String[] names, String[] positions,
            double[] rates, double[] hours, int count) {

        if (count == 0) {
            System.out.println("No employee records to delete.");
            return count;
        }

        String targetId = getValidString(scanner, "Enter Employee ID to delete: ");
        int index = findEmployeeById(ids, count, targetId);

        if (index == -1) {
            System.out.println("Employee ID not found.");
            return count;
        }

        System.out.print("Delete " + names[index] + "? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return count;
        }

        for (int i = index; i < count - 1; i++) {
            ids[i] = ids[i + 1];
            names[i] = names[i + 1];
            positions[i] = positions[i + 1];
            rates[i] = rates[i + 1];
            hours[i] = hours[i + 1];
        }

        count--;
        saveEmployeesToCSV(FILE_NAME, ids, names, positions, rates, hours, count);
        System.out.println("Employee deleted successfully!");
        return count;
    }

    public static void generatePayrollSummary(
            String[] ids, String[] names, double[] rates, double[] hours, int count) {

        if (count == 0) {
            System.out.println("No employee records to summarize.");
            return;
        }

        double totalGross = 0;
        double totalDeductions = 0;
        double totalNet = 0;
        int overtimeCount = 0;
        int highestIndex = 0;
        double highestNet = 0;

        for (int i = 0; i < count; i++) {
            double gross = computeGrossPay(rates[i], hours[i]);
            double deductions = computeDeductions(gross);
            double net = computeNetPay(gross, deductions);

            totalGross += gross;
            totalDeductions += deductions;
            totalNet += net;

            if (hours[i] > REGULAR_HOURS_LIMIT) {
                overtimeCount++;
            }

            if (i == 0 || net > highestNet) {
                highestNet = net;
                highestIndex = i;
            }
        }

        double averageNet = totalNet / count;

        System.out.println("\n--- PAYROLL SUMMARY REPORT ---");
        System.out.println("Total Employees: " + count);
        System.out.println("Employees with Overtime: " + overtimeCount);
        System.out.printf("Total Gross Pay: PHP %.2f%n", totalGross);
        System.out.printf("Total Deductions: PHP %.2f%n", totalDeductions);
        System.out.printf("Total Net Pay: PHP %.2f%n", totalNet);
        System.out.printf("Average Net Pay: PHP %.2f%n", averageNet);
        System.out.printf("Highest Earner: %s (%s) - PHP %.2f%n",
                names[highestIndex], ids[highestIndex], highestNet);
    }
}
