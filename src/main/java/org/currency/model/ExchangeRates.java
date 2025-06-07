package org.currency.model;

import java.math.BigDecimal;

public class ExchangeRates {
    private Long id;
    private long BaseCurrencyId;
    private long TargetCurrencyId;
    private BigDecimal rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public long getBaseCurrencyId() {
        return BaseCurrencyId;
    }

    public void setBaseCurrencyId(long baseCurrencyId) {
        BaseCurrencyId = baseCurrencyId;
    }

    public long getTargetCurrencyId() {
        return TargetCurrencyId;
    }

    public void setTargetCurrencyId(long targetCurrencyId) {
        TargetCurrencyId = targetCurrencyId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = new BigDecimal(rate.toString());
    }

 
}
