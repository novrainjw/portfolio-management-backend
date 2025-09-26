package com.portfolio.management.enums;

/**
 * Enumeration for Portfolio Status
 * Defines the various states a portfolio can be in
 */
public enum PortfolioStatus {
    /**
     * Portfolio is active and can be used for transactions
     */
    ACTIVE("Active", "Portfolio is active and operational"),

    /**
     * Portfolio is temporarily inactive but not deleted
     */
    INACTIVE("Inactive", "Portfolio is temporarily inactive"),

    /**
     * Portfolio is archived and read-only
     */
    ARCHIVED("Archived", "Portfolio is archived and read-only"),

    /**
     * Portfolio is suspended due to issues
     */
    SUSPENDED("Suspended", "Portfolio is suspended"),

    /**
     * Portfolio is being closed and positions are being liquidated
     */
    CLOSING("Closing", "Portfolio is in the process of being closed"),

    /**
     * Portfolio is closed with no active positions
     */
    CLOSED("Closed", "Portfolio is closed with no active positions"),

    /**
     * Portfolio is pending approval or setup
     */
    PENDING("Pending", "Portfolio is pending approval or setup"),

    /**
     * Portfolio is under review
     */
    UNDER_REVIEW("Under Review", "Portfolio is under review");

    private final String displayName;
    private final String description;

    PortfolioStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get PortfolioStatus from string value (case-insensitive)
     */
    public static PortfolioStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ACTIVE; // Default status
        }

        try {
            return valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid portfolio status: " + status);
        }
    }

    /**
     * Check if portfolio status allows transactions
     */
    public boolean allowsTransactions() {
        return this == ACTIVE;
    }

    /**
     * Check if portfolio status allows modifications
     */
    public boolean allowsModifications() {
        return this == ACTIVE || this == INACTIVE || this == PENDING;
    }

    /**
     * Check if portfolio is in a final state (cannot be changed)
     */
    public boolean isFinalStatus() {
        return this == CLOSED || this == ARCHIVED;
    }

    /**
     * Check if portfolio is viewable by the user
     */
    public boolean isViewable() {
        return this != SUSPENDED;
    }

    /**
     * Get all active statuses (portfolios that are operational)
     */
    public static PortfolioStatus[] getActiveStatuses() {
        return new PortfolioStatus[]{ACTIVE, INACTIVE, PENDING};
    }

    /**
     * Get all non-active statuses
     */
    public static PortfolioStatus[] getNonActiveStatuses() {
        return new PortfolioStatus[]{ARCHIVED, SUSPENDED, CLOSING, CLOSED, UNDER_REVIEW};
    }

    @Override
    public String toString() {
        return displayName;
    }
}