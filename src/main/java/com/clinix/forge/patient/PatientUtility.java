package com.clinix.forge.patient;

import org.springframework.util.StringUtils;

/**
 * Utility class for common patient-related helper operations.
 */
public final class PatientUtility {

    private PatientUtility() {
        // Prevent instantiation
    }

    /**
     * Generates a patient case number based on the doctor's prefix and patient serial number.
     * Format: {prefix}{serial} (e.g. S101)
     *
     * @param prefix the doctor's case number prefix
     * @param serial the patient serial number
     * @return formatted case number
     */
    public static String generateCaseNo(String prefix, Integer serial) {
        if (!StringUtils.hasText(prefix)) {
            throw new IllegalArgumentException("Doctor case number prefix cannot be null or empty");
        }
        if (serial <= 0) {
            throw new IllegalArgumentException("Patient serial number must be greater than zero");
        }
        return prefix.trim().toUpperCase() + String.format("%05d", serial);
    }

    /**
     * Sanitizes a query parameter term for database searching.
     * Trims whitespace and returns null if the term is empty to ease JPQL optional matching.
     *
     * @param term the raw search term
     * @return sanitized term, or null if empty
     */
    public static String sanitizeSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            return null;
        }
        return term.trim();
    }

    /**
     * Validates search criteria parameters.
     *
     * @param name    the patient name search term
     * @param phoneNo the phone number search term
     * @param caseNo  the case number search term
     */
    public static void validateSearchParameters(String name, String phoneNo, String caseNo) {
        if (name != null && name.length() > 100) {
            throw new IllegalArgumentException("Name search term is too long (maximum 100 characters)");
        }
        if (caseNo != null && caseNo.length() > 50) {
            throw new IllegalArgumentException("Case number search term is too long (maximum 50 characters)");
        }
        if (phoneNo != null) {
            if (phoneNo.length() > 20) {
                throw new IllegalArgumentException("Phone number search term is too long (maximum 20 characters)");
            }
            // Simple validation to ensure phone search contains digits/plus/hyphens/spaces
            if (!phoneNo.matches("^[+0-9\\s-]*$")) {
                throw new IllegalArgumentException("Phone number search query contains invalid characters");
            }
        }
    }
}
