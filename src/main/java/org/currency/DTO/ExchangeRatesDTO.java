package org.currency.DTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ExchangeRatesDTO {
    @NotBlank(message = "Base currency code is required")
    @Size(min = 3, max = 3, message = "Base currency code must be exactly 3 characters long")
    private String baseCurrencyCode;
    
    @NotBlank(message = "Target currency code is required")
    @Size(min = 3, max = 3, message = "Target currency code must be exactly 3 characters long")
    private String targetCurrencyCode;
    private BigDecimal rate;

    public ExchangeRatesDTO() {
    }

    public ExchangeRatesDTO(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
