package wael_project.transaction_file_bridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wael_project.transaction_file_bridge.processor.TransactionProcessor;
import wael_project.transaction_file_bridge.service.TransactionValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {

    @Mock
    private TransactionValidator validator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionProcessor transactionProcessor;

    @Test
    void process_WithValidTransactions_ProcessesSuccessfully() throws Exception {
        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);

        when(exchange.getIn()).thenReturn(message);

        List<List<String>> csvData = new ArrayList<>();
        csvData.add(List.of("TransactionId", "AccountFrom", "AccountTo", "Amount", "Currency", "Timestamp"));
        csvData.add(List.of("TXN001", "NL91ABNA0417164300", "NL91ABNA0417164301", "100.50", "EUR", "2023-05-20T10:30:00Z"));

        when(message.getBody(List.class)).thenReturn(csvData);
        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"transactionId\":\"TXN001\"}]");

        assertDoesNotThrow(() -> transactionProcessor.process(exchange));

        verify(message).setBody(anyString());
        verify(exchange).setProperty(eq("transactionCount"), anyInt());
    }

    @Test
    void process_WithInvalidTransactions_ThrowsException() throws Exception {
        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);

        when(exchange.getIn()).thenReturn(message);

        List<List<String>> csvData = new ArrayList<>();
        csvData.add(List.of("TransactionId", "AccountFrom", "AccountTo", "Amount", "Currency", "Timestamp")); // Header
        csvData.add(List.of("", "NL91ABNA0417164300", "NL91ABNA0417164301", "100.50", "EUR", "2023-05-20T10:30:00Z"));

        when(message.getBody(List.class)).thenReturn(csvData);
        doThrow(new IllegalArgumentException("Missing required fields")).when(validator).validate(anyList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionProcessor.process(exchange));
        assertTrue(exception.getMessage().contains("Validation errors found"));

        verify(exchange).setProperty(eq("validationErrors"), anyList());
    }

    @Test
    void process_WithParsingError_ThrowsException() throws Exception {
        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);

        when(exchange.getIn()).thenReturn(message);

        List<List<String>> csvData = new ArrayList<>();
        csvData.add(List.of("TransactionId", "AccountFrom", "AccountTo", "Amount", "Currency", "Timestamp"));
        csvData.add(List.of("TXN001", "NL91ABNA0417164300", "NL91ABNA0417164301", "invalid_amount", "EUR", "2023-05-20T10:30:00Z"));

        when(message.getBody(List.class)).thenReturn(csvData);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionProcessor.process(exchange));
        assertTrue(exception.getMessage().contains("Validation errors found"));

        verify(exchange).setProperty(eq("validationErrors"), anyList());
    }
}
