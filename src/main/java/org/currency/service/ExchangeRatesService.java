package org.currency.service;

import java.util.List;

import org.currency.model.ExchangeRates;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesService {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ExchangeRates> exchangeRatesRowMapper = (rs, rowNum) -> {
        ExchangeRates exchangeRates = new ExchangeRates();
        exchangeRates.setId(rs.getLong("id"));
        exchangeRates.setBaseCurrencyId(rs.getLong("baseCurrencyId"));
        exchangeRates.setTargetCurrencyId(rs.getLong("targetCurrencyId"));
        exchangeRates.setRate(rs.getBigDecimal("rate"));
        return exchangeRates;
    };
    public ExchangeRatesService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public ExchangeRates createExchangeRates(ExchangeRates exchangeRates) {
        String sql = "INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, rate) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, exchangeRates.getBaseCurrencyId(), exchangeRates.getTargetCurrencyId(), exchangeRates.getRate());
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        exchangeRates.setId(id);
        return exchangeRates;
    }

    public ExchangeRates getExchangeRatesById(Long id) {
        String sql = "SELECT * FROM ExchangeRates WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, exchangeRatesRowMapper, id);
    }

    public List<ExchangeRates> getAllExchangeRates() {
        String sql = "SELECT * FROM ExchangeRates";
        return jdbcTemplate.query(sql, exchangeRatesRowMapper);
    }

    public ExchangeRates updateExchangeRates(Long id, ExchangeRates exchangeRates) {
        String sql = "UPDATE ExchangeRates SET baseCurrencyId = ?, targetCurrencyId = ?, rate = ? WHERE id = ?";
        jdbcTemplate.update(sql, exchangeRates.getBaseCurrencyId(), exchangeRates.getTargetCurrencyId(), exchangeRates.getRate(), id);
        exchangeRates.setId(id);
        return exchangeRates;
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
