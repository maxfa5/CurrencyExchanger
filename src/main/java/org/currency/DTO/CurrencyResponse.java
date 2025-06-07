package org.currency.DTO;

public class CurrencyResponse {
    private CurrenciesDTO currency;

    public CurrencyResponse(CurrenciesDTO currency) {
        this.currency = currency;
    }

    public CurrenciesDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrenciesDTO currency) {
        this.currency = currency;
    }
} 