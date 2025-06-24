package com.sep.backend.account.balance;

import com.sep.backend.entity.TransactionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Transaction {

    @Schema(description = "The type of the transaction.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionType;

    @Schema(description = "The amount of the transaction.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;

    @Schema(description = "The sender of the transaction. Might be null if action was DEPOSIT or WITHDRAW.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String senderUsername;

    @Schema(description = "The recipient of the transaction. Might be null if action was DEPOSIT or WITHDRAW.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String recipientUsername;


    public static Transaction from(TransactionEntity entity) {
        var transaction = new Transaction();
        transaction.setAmount(entity.getAmount());
        String transactionType = entity.getTransactionType();
        if (TransactionTypes.TRANSFER.equals(transactionType)) {
            transaction.setSenderUsername(entity.getCustomer().getUsername());
            transaction.setRecipientUsername(entity.getDriver().getUsername());
        }
        transaction.setTransactionType(transactionType);
        return transaction;
    }
}
