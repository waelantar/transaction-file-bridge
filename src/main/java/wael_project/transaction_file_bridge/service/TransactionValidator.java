package wael_project.transaction_file_bridge.service;

import wael_project.transaction_file_bridge.model.Transaction;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service("transactionValidator")
public class TransactionValidator {

    private static final String IBAN_REGEX = "NL\\d{2}[A-Z]{4}\\d{10}";
    private static final String VALID_CURRENCY = "EUR";

    public void validate(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty or could not be parsed.");
        }

        for (Transaction tx : transactions) {
            if (tx.getTransactionId() == null || tx.getTransactionId().isBlank()) {
                throw new IllegalArgumentException("Missing transaction ID in a transaction record.");
            }

            if (tx.getAccountFrom() == null || tx.getAccountFrom().isBlank()) {
                throw new IllegalArgumentException("Missing 'From' account for transaction ID: " + tx.getTransactionId());
            }
            if (!tx.getAccountFrom().matches(IBAN_REGEX)) {
                throw new IllegalArgumentException("Invalid 'From' account format for transaction ID: " + tx.getTransactionId() +
                        ". Expected Dutch IBAN format.");
            }

            if (tx.getAccountTo() == null || tx.getAccountTo().isBlank()) {
                throw new IllegalArgumentException("Missing 'To' account for transaction ID: " + tx.getTransactionId());
            }
            if (!tx.getAccountTo().matches(IBAN_REGEX)) {
                throw new IllegalArgumentException("Invalid 'To' account format for transaction ID: " + tx.getTransactionId() +
                        ". Expected Dutch IBAN format.");
            }

            if (tx.getAmount() == null || tx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid amount for transaction ID: " + tx.getTransactionId() +
                        ". Amount must be greater than zero.");
            }

            if (tx.getCurrency() == null || !tx.getCurrency().equals(VALID_CURRENCY)) {
                throw new IllegalArgumentException("Invalid currency for transaction ID: " + tx.getTransactionId() +
                        ". Only EUR is supported.");
            }

            if (tx.getTimestamp() == null) {
                throw new IllegalArgumentException("Missing timestamp for transaction ID: " + tx.getTransactionId());
            }
        }
    }
}