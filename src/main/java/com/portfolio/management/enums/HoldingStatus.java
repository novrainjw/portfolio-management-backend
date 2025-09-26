package com.portfolio.management.enums;

/**
 * Enumeration for Holding Status
 * Defines the various states a holding can be in within a portfolio
 */
public enum HoldingStatus {
    /**
     * Holding is active and represents a current position
     */
    ACTIVE("Active", "Holding is active with current position"),

    /**
     * Holding position is closed (quantity = 0)
     */
    CLOSED("Closed", "Holding position has been completely sold"),

    /**
     * Holding is temporarily inactive but not closed
     */
    INACTIVE("Inactive", "Holding is temporarily inactive"),

    /**
     * Holding is suspended due to market conditions or restrictions
     */
    SUSPENDED("Suspended", "Holding is suspended"),

    /**
     * Holding is being monitored but no position held (watchlist item)
     */
    WATCHING("Watching", "Monitoring symbol but no position held"),

    /**
     * Holding is archived for historical purposes
     */
    ARCHIVED("Archived", "Holding is archived for historical record"),

    /**
     * Position is being partially closed
     */
    PARTIAL_CLOSE("Partial Close", "Position is being partially closed"),

    /**
     * Position is being fully liquidated
     */
    LIQUIDATING("Liquidating", "Position is being fully liquidated"),

    /**
     * Holding has transfer pending (between accounts/brokers)
     */
    TRANSFER_PENDING("Transfer Pending", "Transfer to another account is pending"),

    /**
     * Holding is on hold pending some action
     */
    ON_HOLD("On Hold", "Holding is on hold pending action"),

    /**
     * Position has restrictions (cannot trade)
     */
    RESTRICTED("Restricted", "Position has trading restrictions"),

    /**
     * Stock is delisted but position still exists
     */
    DELISTED("Delisted", "Stock is delisted but position remains"),

    /**
     * Holding is under review
     */
    UNDER_REVIEW("Under Review", "Holding is under review"),

    /**
     * Position resulted from corporate action (split, dividend, etc.)
     */
    CORPORATE_ACTION("Corporate Action", "Position affected by corporate action"),

    /**
     * There's an error with this holding that needs attention
     */
    ERROR("Error", "Holding has an error that requires attention");

    private final String displayName;
    private final String description;

    HoldingStatus(String displayName, String description) {
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
     * Get HoldingStatus from string value (case-insensitive)
     */
    public static HoldingStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ACTIVE; // Default status
        }

        try {
            return valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid holding status: " + status);
        }
    }

    /**
     * Check if holding represents an active position
     */
    public boolean isActivePosition() {
        return this == ACTIVE || this == PARTIAL_CLOSE || this == LIQUIDATING ||
                this == TRANSFER_PENDING || this == ON_HOLD || this == CORPORATE_ACTION;
    }

    /**
     * Check if holding can be traded
     */
    public boolean isTradeable() {
        return this == ACTIVE;
    }

    /**
     * Check if holding allows new buy transactions
     */
    public boolean allowsBuying() {
        return this == ACTIVE || this == WATCHING;
    }

    /**
     * Check if holding allows sell transactions
     */
    public boolean allowsSelling() {
        return this == ACTIVE && hasPosition();
    }

    /**
     * Check if holding has an actual position (quantity > 0)
     */
    public boolean hasPosition() {
        return this != WATCHING && this != CLOSED && this != ARCHIVED;
    }

    /**
     * Check if holding is in a final state
     */
    public boolean isFinalStatus() {
        return this == CLOSED || this == ARCHIVED;
    }

    /**
     * Check if holding requires user attention
     */
    public boolean requiresAttention() {
        return this == ERROR || this == UNDER_REVIEW || this == ON_HOLD ||
                this == CORPORATE_ACTION || this == DELISTED;
    }

    /**
     * Check if holding is viewable in portfolio
     */
    public boolean isViewable() {
        return this != ARCHIVED;
    }

    /**
     * Check if holding contributes to portfolio value
     */
    public boolean contributesToPortfolioValue() {
        return isActivePosition() && this != WATCHING;
    }

    /**
     * Check if holding is being processed
     */
    public boolean isProcessing() {
        return this == PARTIAL_CLOSE || this == LIQUIDATING ||
                this == TRANSFER_PENDING || this == UNDER_REVIEW;
    }

    /**
     * Check if holding has trading restrictions
     */
    public boolean hasRestrictions() {
        return this == SUSPENDED || this == RESTRICTED || this == DELISTED ||
                this == ON_HOLD;
    }

    /**
     * Get the status to set when position is fully closed
     */
    public HoldingStatus getClosedStatus() {
        return CLOSED;
    }

    /**
     * Get the status to set when starting to liquidate
     */
    public HoldingStatus getLiquidatingStatus() {
        return LIQUIDATING;
    }

    /**
     * Check if status transition is valid
     */
    public boolean canTransitionTo(HoldingStatus newStatus) {
        if (this == newStatus) {
            return true; // Same status is always valid
        }

        if (isFinalStatus() && newStatus != ACTIVE) {
            return false; // Final statuses can only be reactivated
        }

        // Define valid transitions
        switch (this) {
            case ACTIVE:
                return true; // Active can transition to any status
            case WATCHING:
                return newStatus == ACTIVE || newStatus == ARCHIVED || newStatus == INACTIVE;
            case INACTIVE:
                return newStatus == ACTIVE || newStatus == ARCHIVED || newStatus == CLOSED;
            case SUSPENDED:
                return newStatus == ACTIVE || newStatus == INACTIVE || newStatus == RESTRICTED;
            case ON_HOLD:
                return newStatus == ACTIVE || newStatus == UNDER_REVIEW || newStatus == ERROR;
            case PARTIAL_CLOSE:
                return newStatus == ACTIVE || newStatus == CLOSED || newStatus == LIQUIDATING;
            case LIQUIDATING:
                return newStatus == CLOSED || newStatus == ACTIVE || newStatus == ERROR;
            case TRANSFER_PENDING:
                return newStatus == ACTIVE || newStatus == ERROR || newStatus == CLOSED;
            case RESTRICTED:
                return newStatus == ACTIVE || newStatus == SUSPENDED || newStatus == DELISTED;
            case DELISTED:
                return newStatus == CLOSED || newStatus == ARCHIVED;
            case UNDER_REVIEW:
                return newStatus == ACTIVE || newStatus == SUSPENDED || newStatus == ERROR;
            case CORPORATE_ACTION:
                return newStatus == ACTIVE || newStatus == UNDER_REVIEW;
            case ERROR:
                return newStatus == ACTIVE || newStatus == UNDER_REVIEW || newStatus == ON_HOLD;
            case CLOSED:
                return newStatus == ACTIVE || newStatus == ARCHIVED; // Can reopen or archive
            case ARCHIVED:
                return newStatus == ACTIVE; // Can only reactivate archived positions
            default:
                return false;
        }
    }

    /**
     * Get all statuses that represent active positions
     */
    public static HoldingStatus[] getActiveStatuses() {
        return new HoldingStatus[]{
                ACTIVE, PARTIAL_CLOSE, LIQUIDATING, TRANSFER_PENDING,
                ON_HOLD, CORPORATE_ACTION
        };
    }

    /**
     * Get all statuses that allow trading
     */
    public static HoldingStatus[] getTradeableStatuses() {
        return new HoldingStatus[]{ACTIVE};
    }

    /**
     * Get all statuses that don't represent actual positions
     */
    public static HoldingStatus[] getNonPositionStatuses() {
        return new HoldingStatus[]{WATCHING, CLOSED, ARCHIVED};
    }

    /**
     * Get all statuses that require attention
     */
    public static HoldingStatus[] getAttentionRequiredStatuses() {
        return new HoldingStatus[]{
                ERROR, UNDER_REVIEW, ON_HOLD, CORPORATE_ACTION, DELISTED
        };
    }

    /**
     * Get all final statuses
     */
    public static HoldingStatus[] getFinalStatuses() {
        return new HoldingStatus[]{CLOSED, ARCHIVED};
    }

    /**
     * Get all processing statuses
     */
    public static HoldingStatus[] getProcessingStatuses() {
        return new HoldingStatus[]{
                PARTIAL_CLOSE, LIQUIDATING, TRANSFER_PENDING, UNDER_REVIEW
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}