package org.currency.DTO;

public class CurrencyResponseDTO {
    private CurrenciesDTO currency;

    public CurrencyResponseDTO(CurrenciesDTO currency) {
        this.currency = currency;
    }

    public CurrenciesDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrenciesDTO currency) {
        this.currency = currency;
    }
} 