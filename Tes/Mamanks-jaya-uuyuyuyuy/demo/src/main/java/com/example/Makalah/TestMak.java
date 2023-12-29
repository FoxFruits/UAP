package com.example.Makalah;

import java.util.Scanner;

public class TestMak {
        public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for input
        System.out.print("Enter the length of the rectangle: ");
        int length = scanner.nextInt();

        System.out.print("Enter the width of the rectangle: ");
        int width = scanner.nextInt();

        // Validate input
        if (length <= 0 || width <= 0) {
            System.out.println("Error: Please enter positive integers for length and width.");
        } else {
            // Calculate area
            int area = length * width;

            // Display the result
            System.out.println("The area of the rectangle is: " + area);
        }

        // Close the scanner
        scanner.close();
    }
}
