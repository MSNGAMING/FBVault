package net.milkbowl.vault.economy;

import org.jetbrains.annotations.Nullable;

/**
 * Standard return object for economy operations in Vault.
 * Contains information regarding transaction success, modified amount, new balance,
 * and error messages where applicable.
 */
public class EconomyResponse {

    /**
     * Enum indicating the outcome status of an economy operation.
     */
    public enum ResponseType {
        SUCCESS(1),
        FAILURE(2),
        NOT_IMPLEMENTED(3);

        private final int id;

        ResponseType(int id) {
            this.id = id;
        }

        int getId() {
            return this.id;
        }
    }

    /**
     * The amount modified by the calling transaction method.
     */
    public final double amount;

    /**
     * The new balance of the account after the operation.
     */
    public final double balance;

    /**
     * Outcome response type.
     */
    public final ResponseType type;

    /**
     * Error message if the operation was not successful (or null).
     */
    public final String errorMessage;

    /**
     * Constructs a new EconomyResponse.
     *
     * @param amount       amount modified
     * @param balance      resulting account balance
     * @param type         outcome status
     * @param errorMessage error description if failed, or null
     */
    public EconomyResponse(double amount, double balance, ResponseType type, @Nullable String errorMessage) {
        this.amount = amount;
        this.balance = balance;
        this.type = type;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns whether the operation was completely successful.
     *
     * @return true if ResponseType == SUCCESS
     */
    public boolean transactionSuccess() {
        return this.type == ResponseType.SUCCESS;
    }
}
