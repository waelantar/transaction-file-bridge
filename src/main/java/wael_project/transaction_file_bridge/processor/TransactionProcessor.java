package wael_project.transaction_file_bridge.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wael_project.transaction_file_bridge.model.Transaction;
import wael_project.transaction_file_bridge.service.TransactionValidator;
import java.util.List;
import java.util.ArrayList;

@Component
public class TransactionProcessor implements Processor {
    @Autowired
    private TransactionValidator validator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        // Store the original file content for error handling
        Object originalBody = exchange.getProperty("originalBody");

        @SuppressWarnings("unchecked")
        List<List<String>> csvData = exchange.getIn().getBody(List.class);
        List<Transaction> transactions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Skip header row (index 0)
        for (int i = 0; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            try {
                // Skip header row
                if (i == 0 && row.get(0).equals("TransactionId")) {
                    continue;
                }

                Transaction transaction = parseTransaction(row);
                transactions.add(transaction);
            } catch (Exception e) {
                errors.add("Error parsing row " + (i + 1) + ": " + e.getMessage());
            }
        }

        try {
            validator.validate(transactions);
        } catch (IllegalArgumentException e) {
            errors.add(e.getMessage());
        }

        if (!errors.isEmpty()) {
            // Restore the original body for error handling
            exchange.getIn().setBody(originalBody);
            exchange.setProperty("validationErrors", errors);
            throw new IllegalArgumentException("Validation errors found: " + String.join(", ", errors));
        }

        String jsonOutput = objectMapper.writeValueAsString(transactions);
        exchange.getIn().setBody(jsonOutput);
        exchange.setProperty("transactionCount", transactions.size());
    }

    private Transaction parseTransaction(List<String> row) throws Exception {
        if (row.size() != 6) {
            throw new IllegalArgumentException("Expected 6 columns, got " + row.size());
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(row.get(0).trim());
        transaction.setAccountFrom(row.get(1).trim());
        transaction.setAccountTo(row.get(2).trim());
        transaction.setAmount(new java.math.BigDecimal(row.get(3).trim()));
        transaction.setCurrency(row.get(4).trim());

        // Handle timestamp parsing with or without timezone
        String timestampStr = row.get(5).trim();
        try {
            // Try parsing with timezone first
            transaction.setTimestamp(java.time.OffsetDateTime.parse(timestampStr));
        } catch (java.time.format.DateTimeParseException e) {
            // If that fails, try parsing as LocalDateTime and convert to OffsetDateTime
            try {
                java.time.LocalDateTime localDateTime = java.time.LocalDateTime.parse(timestampStr);
                transaction.setTimestamp(localDateTime.atZone(java.time.ZoneId.systemDefault()).toOffsetDateTime());
            } catch (java.time.format.DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr + ". Expected format: yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd'T'HH:mm:ssX");
            }
        }

        return transaction;
    }
}