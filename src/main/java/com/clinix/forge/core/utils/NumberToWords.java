package com.clinix.forge.core.utils;

/**
 * Utility class for converting numbers to words in Indian English.
 * Used for receipt generation to display amounts in words using Indian numbering system.
 * <p>
 * Supports conversion of numbers from 0 to 999,999,999 (99 Crore 99 Lakh 99 Thousand).
 * Output format: space-separated words using Indian scales (Crore, Lakh, Thousand)
 * Examples:
 * 1,000 → "One Thousand"
 * 1,00,000 → "One Lakh"
 * 1,00,00,000 → "One Crore"
 */
public class NumberToWords {

    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] TEENS = {
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    /**
     * Converts a number to its word representation using Indian numbering system.
     * <p>
     * Indian number grouping: Crore (1,00,00,000), Lakh (1,00,000), Thousand (1,000)
     *
     * @param number the number to convert (0 to 999,999,999)
     * @return the word representation of the number
     * @throws IllegalArgumentException if number is outside the supported range
     */
    public static String convert(long number) {
        if (number < 0 || number > 999_999_999) {
            throw new IllegalArgumentException(
                    "Number must be between 0 and 999,999,999 (99 Crore 99 Lakh 99 Thousand). Received: " + number);
        }

        if (number == 0) {
            return "Zero";
        }

        return convertToIndianWords(number);
    }

    /**
     * Formats a number using Indian grouping (e.g. 10,00,000).
     * Last three digits are grouped together, then every two digits thereafter.
     *
     * @param number the number to format (non-negative)
     * @return the Indian-formatted number string
     */
    public static String formatIndian(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative. Received: " + number);
        }

        String digits = Long.toString(number);
        int length = digits.length();
        if (length <= 3) {
            return digits;
        }

        StringBuilder formatted = new StringBuilder();
        formatted.append(digits.substring(length - 3));

        int index = length - 3;
        while (index > 0) {
            int start = Math.max(0, index - 2);
            formatted.insert(0, digits.substring(start, index) + ",");
            index = start;
        }

        return formatted.toString();
    }

    /**
     * Internal method to handle the conversion logic using Indian numbering system.
     * Breaks down the number into groups: Crore (2 digits), Lakh (2 digits), Thousand (2 digits), Ones (3 digits)
     */
    private static String convertToIndianWords(long number) {
        StringBuilder words = new StringBuilder();

        // Extract Crore (1,00,00,000 place)
        long crore = number / 10_000_000;
        if (crore > 0) {
            words.append(convertTwoDigits(crore)).append(" Crore ");
        }

        // Extract Lakh (1,00,000 place)
        long remaining = number % 10_000_000;
        long lakh = remaining / 100_000;
        if (lakh > 0) {
            words.append(convertTwoDigits(lakh)).append(" Lakh ");
        }

        // Extract Thousand (1,000 place)
        remaining = remaining % 100_000;
        long thousand = remaining / 1_000;
        if (thousand > 0) {
            words.append(convertTwoDigits(thousand)).append(" Thousand ");
        }

        // Extract Hundreds and Ones (1-999)
        remaining = remaining % 1_000;
        if (remaining > 0) {
            words.append(convertHundreds(remaining)).append(" ");
        }

        return words.toString().trim();
    }

    /**
     * Converts a two-digit number (1-99) to words.
     * Used for Crore, Lakh, and Thousand places in Indian system.
     */
    private static String convertTwoDigits(long number) {
        if (number <= 0) {
            return "";
        }
        if (number < 10) {
            return ONES[(int) number];
        }
        if (number < 20) {
            return TEENS[(int) (number - 10)];
        }

        long tens = number / 10;
        long ones = number % 10;
        if (ones > 0) {
            return TENS[(int) tens] + " " + ONES[(int) ones];
        }
        return TENS[(int) tens];
    }

    /**
     * Converts a three-digit group (0-999) to words.
     */
    private static String convertHundreds(long number) {
        StringBuilder result = new StringBuilder();
        long hundreds = number / 100;
        long remainder = number % 100;

        if (hundreds > 0) {
            result.append(ONES[(int) hundreds]).append(" Hundred");
        }

        if (remainder > 0) {
            if (hundreds > 0) {
                result.append(" ");
            }
            if (remainder < 10) {
                result.append(ONES[(int) remainder]);
            } else if (remainder < 20) {
                result.append(TEENS[(int) (remainder - 10)]);
            } else {
                long tens = remainder / 10;
                long ones = remainder % 10;
                result.append(TENS[(int) tens]);
                if (ones > 0) {
                    result.append(" ").append(ONES[(int) ones]);
                }
            }
        }

        return result.toString().trim();
    }
}
