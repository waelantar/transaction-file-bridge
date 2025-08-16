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
        @SuppressWarnings("unchecked")
        List<List<String>> csvData = exchange.getIn().getBody(List.class);

        List<Transaction> transactions = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 1; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);

            try {
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
        transaction.setTimestamp(java.time.OffsetDateTime.parse(row.get(5).trim()));

        return transaction;
    }
}
