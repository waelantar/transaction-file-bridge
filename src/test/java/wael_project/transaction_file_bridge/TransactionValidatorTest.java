package wael_project.transaction_file_bridge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import wael_project.transaction_file_bridge.model.Transaction;
import wael_project.transaction_file_bridge.service.TransactionValidator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionValidatorTest {

    @InjectMocks
    private TransactionValidator transactionValidator;

    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        transactions = new ArrayList<>();
    }

    @Test
    void validate_WithNullTransactions_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(null);
        });

        assertEquals("CSV file is empty or could not be parsed.", exception.getMessage());
    }

    @Test
    void validate_WithEmptyTransactions_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(new ArrayList<>());
        });

        assertEquals("CSV file is empty or could not be parsed.", exception.getMessage());
    }

    @Test
    void validate_WithValidTransactions_PassesValidation() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        assertDoesNotThrow(() -> transactionValidator.validate(transactions));
    }

    @Test
    void validate_WithNullTransactionId_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Missing transaction ID"));
    }

    @Test
    void validate_WithBlankTransactionId_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Missing transaction ID"));
    }

    @Test
    void validate_WithNullAccountFrom_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Missing 'From' account"));
    }

    @Test
    void validate_WithInvalidAccountFromFormat_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("INVALID_IBAN");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid 'From' account format"));
    }

    @Test
    void validate_WithNullAccountTo_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Missing 'To' account"));
    }

    @Test
    void validate_WithInvalidAccountToFormat_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("INVALID_IBAN");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid 'To' account format"));
    }

    @Test
    void validate_WithNullAmount_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid amount"));
    }

    @Test
    void validate_WithZeroAmount_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid amount"));
    }

    @Test
    void validate_WithNegativeAmount_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("-50.00"));
        transaction.setCurrency("EUR");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid amount"));
    }

    @Test
    void validate_WithNullCurrency_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid currency"));
    }

    @Test
    void validate_WithInvalidCurrency_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("USD");
        transaction.setTimestamp(OffsetDateTime.now());

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Invalid currency"));
    }

    @Test
    void validate_WithNullTimestamp_ThrowsException() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAccountFrom("NL91ABNA0417164300");
        transaction.setAccountTo("NL91ABNA0417164301");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("EUR");

        transactions.add(transaction);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionValidator.validate(transactions);
        });

        assertTrue(exception.getMessage().contains("Missing timestamp"));
    }
}
