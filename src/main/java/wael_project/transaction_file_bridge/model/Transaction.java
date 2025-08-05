package wael_project.transaction_file_bridge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.index.qual.Positive;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Transaction {
    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("account_from")
    @Pattern("^[A-Z]{2}\\d{2}[A-Z0-9]{4}\\d{10}$")
    private String accountFrom;

    @JsonProperty("account_to")
    @Pattern("^[A-Z]{2}\\d{2}[A-Z0-9]{4}\\d{10}$")
    private String accountTo;

    @JsonProperty("amount")
    @Positive
    private BigDecimal amount;

    @JsonProperty("currency")
    @NotNull
    private String currency;



    @JsonProperty("timestamp")
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssX")
    private OffsetDateTime timestamp;



    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
