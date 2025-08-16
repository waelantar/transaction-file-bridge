package wael_project.transaction_file_bridge;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import wael_project.transaction_file_bridge.model.Transaction;
import wael_project.transaction_file_bridge.processor.TransactionProcessor;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorPrivateMethodTest {

    @InjectMocks
    private TransactionProcessor transactionProcessor;

    @Test
    void parseTransaction_WithValidRow_CreatesTransaction() throws Exception {
        List<String> row = List.of("TXN001", "NL91ABNA0417164300", "NL91ABNA0417164301", "100.50", "EUR", "2023-05-20T10:30:00Z");

        Method parseTransactionMethod = TransactionProcessor.class.getDeclaredMethod("parseTransaction", List.class);
        parseTransactionMethod.setAccessible(true);

        Transaction transaction = (Transaction) parseTransactionMethod.invoke(transactionProcessor, row);

        assertNotNull(transaction);
        assertEquals("TXN001", transaction.getTransactionId());
        assertEquals("NL91ABNA0417164300", transaction.getAccountFrom());
        assertEquals("NL91ABNA0417164301", transaction.getAccountTo());
        assertEquals(new java.math.BigDecimal("100.50"), transaction.getAmount());
        assertEquals("EUR", transaction.getCurrency());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void parseTransaction_WithInvalidColumnCount_ThrowsException() throws Exception {
        List<String> row = List.of("TXN001", "NL91ABNA0417164300"); // Only 2 columns

        Method parseTransactionMethod = TransactionProcessor.class.getDeclaredMethod("parseTransaction", List.class);
        parseTransactionMethod.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            parseTransactionMethod.invoke(transactionProcessor, row);
        });

        assertTrue(exception.getCause().getMessage().contains("Expected 6 columns, got 2"));
    }
}
