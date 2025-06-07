package org.currency.service;

import java.util.List;

import org.currency.exception.CurrencyNotFoundException;
import org.currency.model.ExchangeRates;
import org.currency.DTO.ExchangeRatesDTO;
import org.currency.DTO.ExchangeRatesResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesService {
    private final JdbcTemplate jdbcTemplate;
    private final CurrencyService currencyService;
    private final RowMapper<ExchangeRates> exchangeRatesRowMapper = (rs, rowNum) -> {
        ExchangeRates exchangeRates = new ExchangeRates();
        exchangeRates.setId(rs.getLong("id"));
        exchangeRates.setBaseCurrencyId(rs.getLong("baseCurrencyId"));
        exchangeRates.setTargetCurrencyId(rs.getLong("targetCurrencyId"));
        exchangeRates.setRate(rs.getBigDecimal("rate"));
        return exchangeRates;
    };
    public ExchangeRatesService(JdbcTemplate jdbcTemplate, CurrencyService currencyService) {
        this.jdbcTemplate = jdbcTemplate;
        this.currencyService = currencyService;
        initTable();
    }

    private void initTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS ExchangeRates (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                baseCurrencyId INTEGER NOT NULL,
                targetCurrencyId INTEGER NOT NULL,
                rate DECIMAL(10, 4) NOT NULL,
                FOREIGN KEY (baseCurrencyId) REFERENCES Currencies(id),
                FOREIGN KEY (targetCurrencyId) REFERENCES Currencies(id)
            )
        """);
    }
    private ExchangeRatesResponse convertToResponse(ExchangeRates exchangeRates) {
        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        exchangeRatesResponse.setId(exchangeRates.getId());
        exchangeRatesResponse.setBaseCurrency(currencyService.getCurrenciesById(exchangeRates.getBaseCurrencyId()));
        exchangeRatesResponse.setTargetCurrency(currencyService.getCurrenciesById(exchangeRates.getTargetCurrencyId()));
        exchangeRatesResponse.setRate(exchangeRates.getRate());
        return exchangeRatesResponse;
    }

    public ExchangeRatesResponse createExchangeRates(ExchangeRates exchangeRates) {
        String sql = "INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, rate) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, exchangeRates.getBaseCurrencyId(), exchangeRates.getTargetCurrencyId(), exchangeRates.getRate());
        exchangeRates.setId(id);
        return convertToResponse(exchangeRates);
    }

    public ExchangeRates getExchangeRatesById(Long id) {
        String sql = "SELECT * FROM ExchangeRates WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, exchangeRatesRowMapper, id);
    }

    public List<ExchangeRates> getAllExchangeRates() {
        String sql = "SELECT * FROM ExchangeRates";
        return jdbcTemplate.query(sql, exchangeRatesRowMapper);
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


    public ExchangeRates updateExchangeRates(Long id, ExchangeRatesDTO exchangeRates) {
        ExchangeRates exchangeRatesEntity = convertToEntity(exchangeRates);
        String sql = "UPDATE ExchangeRates SET baseCurrencyId = ?, targetCurrencyId = ?, rate = ? WHERE id = ?";
        jdbcTemplate.update(sql, exchangeRatesEntity.getBaseCurrencyId(), exchangeRatesEntity.getTargetCurrencyId(), exchangeRatesEntity.getRate(), id);
        exchangeRatesEntity.setId(id);
        return exchangeRatesEntity;
    }

    public void deleteExchangeRates(Long id) {
        String sql = "DELETE FROM ExchangeRates WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public ExchangeRates getExchangeRatesFromTo(String from, String to) {
        String sql = "SELECT * FROM ExchangeRates WHERE baseCurrencyId = (SELECT id FROM Currencies WHERE code = ?) AND targetCurrencyId = (SELECT id FROM Currencies WHERE code = ?)";
        try {
            return jdbcTemplate.queryForObject(sql, exchangeRatesRowMapper, from, to);
        } catch (Exception e) {
            return jdbcTemplate.queryForObject(sql, exchangeRatesRowMapper, to, from);
        }
    }
    
    
    
    
    
}
