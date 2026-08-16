package com.clinix.forge.core.utils;

public class TextFormatter {
    /**
     * Converts a string into Title Case ("Make First Upper Case And Other Lower").
     *
     * @param input The raw input string.
     * @return Transformed string in title case, or empty string if input is null/blank.
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String[] words = input.trim().split("\\s+");
        StringBuilder titleCase = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return titleCase.toString().trim();
    }
}
