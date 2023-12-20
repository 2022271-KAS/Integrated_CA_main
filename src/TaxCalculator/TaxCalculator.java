package TaxCalculator;

import ioutils.IOUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mainmenu.User;

public class TaxCalculator {

    // Define constants for the standard and higher income tax rates
    static final double STANDARD_RATE = 0.20; // Standard rate (20%)
    static final double HIGHER_RATE = 0.40;   // Higher rate (40%)

    // Define weekly rate bands for single and married individuals
    // reference Revenue: https://www.revenue.ie/en/personal-tax-credits-reliefs-and-exemptions/tax-relief-charts/index.aspx
    static final double SINGLE_RATE_BAND_WEEKLY = 769.23; // Rate band for single individuals
    static final double SINGLE_RATE_BAND_WEEKLY_CHILDREN = 846.15; // Rate band for single with children
    static final double MARRIED_ONE_EARNER_RATE_BAND_WEEKLY = 942.31; // Rate band for married (one earner)
    static final double MARRIED_BOTH_EARNERS_RATE_BAND_WEEKLY = 942.31; // Rate band for married (both earners)

    // Define weekly tax credits for single and married individuals
    static final double SINGLE_TAX_CREDIT_WEEKLY = 34.13; // Tax credit for single individuals
    static final double MARRIED_TAX_CREDIT_WEEKLY = 68.27; // Tax credit for married individuals
    static final double EMPLOYEE_TAX_CREDIT_WEEKLY = 34.13; // Employee tax credit

    // Constants for PRSI (Pay Related Social Insurance)
    // reference: https://www.gov.ie/en/publication/ffa563-prsi-class-a-rates/
    static final double PRSI_RATE = 0.04; // PRSI rate (4%)

    // Constants for USC (Universal Social Charge) bands and rates
    // reference: https://www.revenue.ie/en/jobs-and-pensions/usc/standard-rates-thresholds.aspx
    static final double USC_FIRST_BAND_WEEKLY = 230.62; // Lower income band for USC
    static final double USC_SECOND_BAND_WEEKLY = 440.92; // Middle income band for USC
    static final double USC_THIRD_BAND_WEEKLY = 1462.38; // Higher income band for USC
    static final double USC_FIRST_RATE = 0.005; // USC rate for lower income band (0.5%)
    static final double USC_SECOND_RATE = 0.02;  // USC rate for middle income band (2%)
    static final double USC_THIRD_RATE = 0.045;  // USC rate for higher income band (4.5%)
    static final double USC_BALANCE_RATE = 0.08; // USC rate for balance (8%)

    private static List<String> calculations = new ArrayList<>();
    private static Map<Integer, List<String>> userCalculations = new HashMap<>();

// Method to associate tax calculations with a user ID
    public static void storeCalculationsForUser(int userId) {
        if (userCalculations.containsKey(userId)) {
            List<String> userCalculationsList = userCalculations.get(userId);
            userCalculationsList.addAll(calculations);
        } else {
            List<String> userCalculationsList = new ArrayList<>(calculations);
            userCalculations.put(userId, userCalculationsList);
        }
    }

    // Method to run the tax calculator
    public static void runTaxCalculator() {
        IOUtils input = new IOUtils();

        // Infinite loop to allow for multiple calculations
        while (true) {
            // Present options to the user
            System.out.println("Select your marital status or exit:");
            // Get user choice
            int choice = input.getUserInt("1-Single \n2-Single with children \n3-Married (Both Spouses with Income) \n4-Married (One Spouse with Income) \n5-Exit", 1, 5);

            // Determine marital status based on user choice
            String maritalStatus = "";
            switch (choice) {
                case 1:
                    maritalStatus = "single";
                    break;

                case 2:
                    maritalStatus = "single with children";
                    break;

                case 3:
                    maritalStatus = "married_both_earners";
                    break;
                case 4:
                    maritalStatus = "married_one_earner";
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    return; // Exit the program
                default:
                    System.out.println("Invalid choice.");
                    continue; // Repeat the loop for invalid choices
            }

            // Get weekly income from the user
            double weeklyIncome = input.getUserDecimal("Enter your weekly income: ");

            // Calculate taxes and deductions
            double taxPayable = calculateIncomeTax(maritalStatus, weeklyIncome);
            double prsi = calculatePRSI(weeklyIncome);
            double usc = calculateUSC(weeklyIncome);
            double totalDeductions = taxPayable + prsi + usc;
            double netIncome = weeklyIncome - totalDeductions;

            // Display the results
            //System.out.println("User ID: " + User.getId());
            System.out.println("Weekly Income Tax: " + String.format("%.2f", taxPayable));
            System.out.println("Weekly PRSI: " + String.format("%.2f", prsi));
            System.out.println("Weekly USC: " + String.format("%.2f", usc));
            System.out.println("Total Weekly Deductions: " + String.format("%.2f", totalDeductions));
            System.out.println("Net Weekly Income: " + String.format("%.2f", netIncome));

            // Add the calculation to the list
            String calculationResult = String.format("Tax: %.2f, PRSI: %.2f, USC: %.2f, Total Deductions: %.2f, Net Income: %.2f",
                    taxPayable, prsi, usc, totalDeductions, netIncome);
            calculations.add(calculationResult);

            // Ask user if they want to perform another calculation or leave
            char repeat = input.getUserText("Do you want to perform another calculation? (y/n): ").toLowerCase().charAt(0);
            if (repeat != 'y') {
                System.out.println("Exiting the program.");
                break; // Exit the loop and thus the program

            }
        }
    }

    // Method to retrieve tax calculations for a specific user ID
    public static List<String> getCalculationsForUser(int userId) {
        return userCalculations.getOrDefault(userId, new ArrayList<>());
    }

    // Method to calculate income tax based on marital status and income
    private static double calculateIncomeTax(String maritalStatus, double weeklyIncome) {
        double rateBand = determineRateBand(maritalStatus);
        double taxCredits = maritalStatus.startsWith("married") ? MARRIED_TAX_CREDIT_WEEKLY + EMPLOYEE_TAX_CREDIT_WEEKLY : SINGLE_TAX_CREDIT_WEEKLY + EMPLOYEE_TAX_CREDIT_WEEKLY;

        double standardRateTax = Math.min(weeklyIncome, rateBand) * STANDARD_RATE;
        double higherRateTax = weeklyIncome > rateBand ? (weeklyIncome - rateBand) * HIGHER_RATE : 0;
        double grossTax = standardRateTax + higherRateTax;

        return Math.max(grossTax - taxCredits, 0);
    }

    // Method to determine the rate band based on marital status
    private static double determineRateBand(String maritalStatus) {
        if (maritalStatus.equals("single")) {
            return SINGLE_RATE_BAND_WEEKLY;
        } else if (maritalStatus.equals("single with children")) {
            return SINGLE_RATE_BAND_WEEKLY_CHILDREN;
        } else if (maritalStatus.equals("married_one_earner")) {
            return MARRIED_ONE_EARNER_RATE_BAND_WEEKLY;
        } else { // assuming married_both_earners
            return MARRIED_BOTH_EARNERS_RATE_BAND_WEEKLY + 596.15; // Additional €31,000 / 52 weeks
        }
    }

    // Method to calculate PRSI based on weekly income
    private static double calculatePRSI(double weeklyIncome) {
        return weeklyIncome * PRSI_RATE;
    }

    // Method to calculate USC based on weekly income
    private static double calculateUSC(double weeklyIncome) {
        double usc = 0;
        usc += Math.min(weeklyIncome, USC_FIRST_BAND_WEEKLY) * USC_FIRST_RATE;
        usc += Math.min(Math.max(weeklyIncome - USC_FIRST_BAND_WEEKLY, 0), USC_SECOND_BAND_WEEKLY - USC_FIRST_BAND_WEEKLY) * USC_SECOND_RATE;
        usc += Math.min(Math.max(weeklyIncome - USC_SECOND_BAND_WEEKLY, 0), USC_THIRD_BAND_WEEKLY - USC_SECOND_BAND_WEEKLY) * USC_THIRD_RATE;
        usc += Math.max(weeklyIncome - USC_THIRD_BAND_WEEKLY, 0) * USC_BALANCE_RATE;
        return usc;
    }
}
