package org.currency.DTO;

import java.math.BigDecimal;
import org.currency.model.Currencies;

public class ExchangedCurrenciesDTO {
    private Currencies baseCurrencyCode;
    private Currencies targetCurrencyCode;
    private BigDecimal amount;
    private BigDecimal rate;
    private BigDecimal convertedAmount;

    public ExchangedCurrenciesDTO(Currencies baseCurrencyCode, Currencies targetCurrencyCode, BigDecimal amount, BigDecimal rate, BigDecimal convertedAmount) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.amount = amount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
    }

    public Currencies getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public Currencies getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setBaseCurrencyCode(Currencies baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public void setTargetCurrencyCode(Currencies targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
