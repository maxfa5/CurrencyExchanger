package org.currency.mapper;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.currency.DTO.ExchangeRatesDTO;
import org.currency.DTO.ExchangeRatesResponseDTO;
import org.currency.model.ExchangeRates;
import org.currency.exception.CurrencyNotFoundException;

@Component
public class ExchangeRatesMapper {
    private final RowMapper<ExchangeRates> exchangeRatesRowMapper;
    private final RowMapper<ExchangeRatesDTO> exchangeRatesDTORowMapper;
    private final JdbcTemplate jdbcTemplate;
    ExchangeRatesMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.exchangeRatesRowMapper = (rs, rowNum) -> {
            ExchangeRates exchangeRates = new ExchangeRates();
            exchangeRates.setId(rs.getLong("id"));
            exchangeRates.setBaseCurrencyId(rs.getLong("baseCurrencyId"));
            exchangeRates.setTargetCurrencyId(rs.getLong("targetCurrencyId"));
            exchangeRates.setRate(rs.getBigDecimal("rate"));
            return exchangeRates;
        };
        this.exchangeRatesDTORowMapper = (rs, rowNum) -> {
            ExchangeRatesDTO exchangeRatesDTO = new ExchangeRatesDTO();
            exchangeRatesDTO.setBaseCurrencyCode(rs.getString("baseCurrencyCode"));
            exchangeRatesDTO.setTargetCurrencyCode(rs.getString("targetCurrencyCode"));
            exchangeRatesDTO.setRate(rs.getBigDecimal("rate"));
            return exchangeRatesDTO;
        };
    }
    public RowMapper<ExchangeRates> getExchangeRatesRowMapper() {
        return exchangeRatesRowMapper;
    }
    public RowMapper<ExchangeRatesDTO> getExchangeRatesDTORowMapper() {
        return exchangeRatesDTORowMapper;
    }
    public ExchangeRatesDTO convertToDTO(ExchangeRates exchangeRates) {
        ExchangeRatesDTO exchangeRatesDTO = new ExchangeRatesDTO();
        String baseCode = jdbcTemplate.queryForObject(
            "SELECT code FROM Currencies WHERE id = ?", 
            String.class, 
            exchangeRates.getBaseCurrencyId()
        );
        String targetCode = jdbcTemplate.queryForObject(
            "SELECT code FROM Currencies WHERE id = ?", 
            String.class, 
            exchangeRates.getTargetCurrencyId()
        );
        exchangeRatesDTO.setBaseCurrencyCode(baseCode);
        exchangeRatesDTO.setTargetCurrencyCode(targetCode);
        exchangeRatesDTO.setRate(exchangeRates.getRate());
        return exchangeRatesDTO;
    }
    public ExchangeRates convertToEntity(ExchangeRatesResponseDTO exchangeRatesResponse) {
        ExchangeRates exchangeRates = new ExchangeRates();
        exchangeRates.setBaseCurrencyId(exchangeRatesResponse.getBaseCurrency().getID());
        exchangeRates.setTargetCurrencyId(exchangeRatesResponse.getTargetCurrency().getID());
        exchangeRates.setRate(exchangeRatesResponse.getRate());
        return exchangeRates;
    }
    public ExchangeRates convertToEntity(ExchangeRatesDTO exchangeRatesDTO) {
        ExchangeRates exchangeRates = new ExchangeRates();
        try {
            Long baseId = jdbcTemplate.queryForObject(
                "SELECT id FROM Currencies WHERE code = ?", 
                Long.class, 
                exchangeRatesDTO.getBaseCurrencyCode()
            );
            try {
            Long targetId = jdbcTemplate.queryForObject(
            "SELECT id FROM Currencies WHERE code = ?", 
            Long.class, 
            exchangeRatesDTO.getTargetCurrencyCode()
            );
            exchangeRates.setBaseCurrencyId(baseId);
            exchangeRates.setTargetCurrencyId(targetId);
            exchangeRates.setRate(exchangeRatesDTO.getRate());
            return exchangeRates;
    } catch (Exception e) {
            throw new CurrencyNotFoundException("Currency "+ exchangeRatesDTO.getTargetCurrencyCode() +" not found");
        }
        }catch (CurrencyNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CurrencyNotFoundException("Currency "+ exchangeRatesDTO.getBaseCurrencyCode() +" not found");
        }
    }
}
