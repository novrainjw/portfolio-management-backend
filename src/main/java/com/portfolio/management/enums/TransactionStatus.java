package com.portfolio.management.enums;

/**
 * Enumeration for Transaction Status
 * Defines the various states a transaction can be in throughout its lifecycle
 */
public enum TransactionStatus {
    /**
     * Transaction has been created but not yet processed
     */
    PENDING("Pending", "Transaction is pending execution"),

    /**
     * Transaction is being validated by the system
     */
    VALIDATING("Validating", "Transaction is being validated"),

    /**
     * Transaction has been submitted to broker/exchange
     */
    SUBMITTED("Submitted", "Transaction has been submitted for execution"),

    /**
     * Transaction is being executed
     */
    EXECUTING("Executing", "Transaction is currently being executed"),

    /**
     * Transaction has been executed successfully
     */
    EXECUTED("Executed", "Transaction has been executed successfully"),

    /**
     * Transaction is in settlement period
     */
    SETTLING("Settling", "Transaction is in settlement period"),

    /**
     * Transaction has been fully settled
     */
    SETTLED("Settled", "Transaction has been settled"),

    /**
     * Transaction was cancelled before execution
     */
    CANCELLED("Cancelled", "Transaction was cancelled"),

    /**
     * Transaction failed during execution
     */
    FAILED("Failed", "Transaction failed during execution"),

    /**
     * Transaction was rejected by broker/exchange
     */
    REJECTED("Rejected", "Transaction was rejected"),

    /**
     * Transaction timed out
     */
    TIMEOUT("Timeout", "Transaction timed out"),

    /**
     * Transaction is on hold pending manual review
     */
    ON_HOLD("On Hold", "Transaction is on hold"),

    /**
     * Transaction was partially filled
     */
    PARTIALLY_FILLED("Partially Filled", "Transaction was partially executed"),

    /**
     * Transaction is being reversed/rolled back
     */
    REVERSING("Reversing", "Transaction is being reversed"),

    /**
     * Transaction has been reversed
     */
    REVERSED("Reversed", "Transaction has been reversed"),

    /**
     * Transaction requires manual intervention
     */
    REQUIRES_ACTION("Requires Action", "Transaction requires manual action");

    private final String displayName;
    private final String description;

    TransactionStatus(String displayName, String description) {
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
     * Get TransactionStatus from string value (case-insensitive)
     */
    public static TransactionStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PENDING; // Default status
        }

        try {
            return valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction status: " + status);
        }
    }

    /**
     * Check if transaction is in a pending state (not yet final)
     */
    public boolean isPending() {
        return this == PENDING || this == VALIDATING || this == SUBMITTED ||
                this == EXECUTING || this == SETTLING || this == ON_HOLD ||
                this == PARTIALLY_FILLED || this == REVERSING;
    }

    /**
     * Check if transaction completed successfully
     */
    public boolean isCompleted() {
        return this == EXECUTED || this == SETTLED;
    }

    /**
     * Check if transaction failed or was cancelled
     */
    public boolean isFailed() {
        return this == CANCELLED || this == FAILED || this == REJECTED ||
                this == TIMEOUT || this == REVERSED;
    }

    /**
     * Check if transaction is in a final state (cannot be modified)
     */
    public boolean isFinalStatus() {
        return isCompleted() || isFailed();
    }

    /**
     * Check if transaction can be cancelled
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == VALIDATING || this == SUBMITTED || this == ON_HOLD;
    }

    /**
     * Check if transaction affects portfolio positions
     */
    public boolean affectsPositions() {
        return this == EXECUTED || this == SETTLED;
    }

    /**
     * Check if transaction requires user attention
     */
    public boolean requiresAttention() {
        return this == FAILED || this == REJECTED || this == ON_HOLD ||
                this == REQUIRES_ACTION || this == TIMEOUT;
    }

    /**
     * Check if transaction is being processed
     */
    public boolean isProcessing() {
        return this == VALIDATING || this == SUBMITTED || this == EXECUTING ||
                this == SETTLING || this == REVERSING;
    }

    /**
     * Get the next logical status for workflow progression
     */
    public TransactionStatus getNextStatus() {
        switch (this) {
            case PENDING:
                return VALIDATING;
            case VALIDATING:
                return SUBMITTED;
            case SUBMITTED:
                return EXECUTING;
            case EXECUTING:
                return EXECUTED;
            case EXECUTED:
                return SETTLING;
            case SETTLING:
                return SETTLED;
            case REVERSING:
                return REVERSED;
            default:
                return this; // Final states or special cases
        }
    }

    /**
     * Get all active/processing statuses
     */
    public static TransactionStatus[] getActiveStatuses() {
        return new TransactionStatus[]{
                PENDING, VALIDATING, SUBMITTED, EXECUTING, SETTLING,
                ON_HOLD, PARTIALLY_FILLED, REVERSING
        };
    }

    /**
     * Get all successful completion statuses
     */
    public static TransactionStatus[] getSuccessfulStatuses() {
        return new TransactionStatus[]{EXECUTED, SETTLED};
    }

    /**
     * Get all failure/cancellation statuses
     */
    public static TransactionStatus[] getFailureStatuses() {
        return new TransactionStatus[]{
                CANCELLED, FAILED, REJECTED, TIMEOUT, REVERSED
        };
    }

    /**
     * Get statuses that require attention
     */
    public static TransactionStatus[] getAttentionRequiredStatuses() {
        return new TransactionStatus[]{
                FAILED, REJECTED, ON_HOLD, REQUIRES_ACTION, TIMEOUT
        };
    }

    /**
     * Check if status transition is valid
     */
    public boolean canTransitionTo(TransactionStatus newStatus) {
        if (this == newStatus) {
            return true; // Same status is always valid
        }

        if (isFinalStatus() && newStatus != REVERSING) {
            return false; // Final statuses can only go to reversing
        }

        // Define valid transitions
        switch (this) {
            case PENDING:
                return newStatus == VALIDATING || newStatus == CANCELLED || newStatus == ON_HOLD;
            case VALIDATING:
                return newStatus == SUBMITTED || newStatus == REJECTED || newStatus == ON_HOLD;
            case SUBMITTED:
                return newStatus == EXECUTING || newStatus == CANCELLED || newStatus == TIMEOUT;
            case EXECUTING:
                return newStatus == EXECUTED || newStatus == PARTIALLY_FILLED ||
                        newStatus == FAILED || newStatus == TIMEOUT;
            case EXECUTED:
                return newStatus == SETTLING || newStatus == REVERSING;
            case SETTLING:
                return newStatus == SETTLED || newStatus == FAILED;
            case PARTIALLY_FILLED:
                return newStatus == EXECUTED || newStatus == CANCELLED || newStatus == FAILED;
            case ON_HOLD:
                return newStatus == VALIDATING || newStatus == CANCELLED || newStatus == REQUIRES_ACTION;
            case REVERSING:
                return newStatus == REVERSED || newStatus == FAILED;
            default:
                return false; // Final statuses
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}