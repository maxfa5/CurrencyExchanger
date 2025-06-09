package org.currency.mapper;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import org.currency.DTO.CurrenciesDTO;
import org.currency.DTO.CurrencyResponseDTO;
import org.currency.DTO.RateDTO;
import org.currency.model.Currencies;

@Component
public class CurrenciesMapper {
    private final RowMapper<Currencies> currenciesRowMapper;
    private final RowMapper<CurrenciesDTO> currencyRowMapper;

    CurrenciesMapper() {
        this.currenciesRowMapper = (rs, rowNum) -> {
            Currencies currencies = new Currencies();
            currencies.setID(rs.getLong("id"));
            currencies.setCode(rs.getString("code"));
            currencies.setFullName(rs.getString("fullName"));
            currencies.setSign(rs.getString("sign"));
            return currencies;
        };
        this.currencyRowMapper = (rs, rowNum) -> {
            CurrenciesDTO currency = new CurrenciesDTO();
            currency.setID(rs.getLong("id"));
            currency.setCode(rs.getString("code"));
            currency.setName(rs.getString("name"));
            currency.setSign(rs.getString("sign"));
            return currency;
        };
    }
    public RowMapper<Currencies> getCurrenciesRowMapper() {
        return currenciesRowMapper;
    }
    public RowMapper<CurrenciesDTO> getCurrencyRowMapper() {
        return currencyRowMapper;
    }
    public CurrenciesDTO mapToDTO(Currencies currency) {
        CurrenciesDTO dto = new CurrenciesDTO();
        dto.setID(currency.getID());
        dto.setCode(currency.getCode());
        dto.setName(currency.getFullName());
        dto.setSign(currency.getSign());
        return dto;
    }

    public Currencies mapToEntity(CurrenciesDTO dto) {
        Currencies currency = new Currencies();
        currency.setID(dto.getID());
        currency.setCode(dto.getCode());
        currency.setFullName(dto.getName());
        currency.setSign(dto.getSign());
        return currency;
    }
}
