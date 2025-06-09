package org.currency.service;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.currency.exception.CurrencyNotFoundException;
import org.currency.model.ExchangeRates;
import org.currency.DTO.ExchangeRatesDTO;
import org.currency.DTO.ExchangeRatesResponse;
import org.currency.DTO.ExchangedCurrenciesDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.currency.exception.ExchangeRateNotFoundException;

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
    public ExchangeRatesResponse convertToResponse(ExchangeRates exchangeRates) {
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
    private ExchangeRates convertToEntity(ExchangeRatesResponse exchangeRatesResponse) {
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

    public ExchangeRatesResponse getExchangeRatesFromTo(String from, String to) {
        // Проверяем существование валют
        String sqlCheckCurrency = "SELECT COUNT(*) FROM Currencies WHERE code = ?";
        Integer countFrom = jdbcTemplate.queryForObject(sqlCheckCurrency, Integer.class, from);
        Integer countTo = jdbcTemplate.queryForObject(sqlCheckCurrency, Integer.class, to);
        
        if (countFrom == null || countFrom == 0) {
            throw new CurrencyNotFoundException("Currency not found: " + from);
        }
        if (countTo == null || countTo == 0) {
            throw new CurrencyNotFoundException("Currency not found: " + to);
        }
        System.out.println( jdbcTemplate.queryForObject("SELECT id FROM Currencies WHERE code = ?",Integer.class, from));
        System.out.println( jdbcTemplate.queryForObject("SELECT id FROM Currencies WHERE code = ?",Integer.class, to));

        // Ищем прямой курс
        String sqlDirect = "SELECT * from ExchangeRates WHERE baseCurrencyId = (SELECT id FROM Currencies WHERE code = ?) AND targetCurrencyId = (SELECT id FROM Currencies WHERE code = ?)";
        try {
            ExchangeRates directRate = jdbcTemplate.queryForObject(sqlDirect, exchangeRatesRowMapper, from, to);
            System.out.println(directRate);
            if (directRate != null) {
                return convertToResponse(directRate);
            }
        } catch (Exception e) {
            // Игнорируем ошибку и продолжаем поиск
        }

        // Ищем обратный курс
        try {
            ExchangeRates reverseRate = jdbcTemplate.queryForObject(sqlDirect, exchangeRatesRowMapper, to, from);
            if (reverseRate != null) {
                ExchangeRates reversedRate = new ExchangeRates(
                    reverseRate.getTargetCurrencyId(),
                    reverseRate.getBaseCurrencyId(),
                    reverseRate.getRate()
                );
                return createExchangeRates(reversedRate);
            }
        } catch (Exception e) {
            throw new CurrencyNotFoundException("Currency " + to + " not found");
        }
        List<ExchangeRates> path = new ArrayList<>();
        path = (getExchangeRatesMultycast(from, to, path));
        if (!path.isEmpty()) {
            BigDecimal rate = calculatePathRate(path);
            return createExchangeRates(new ExchangeRates(path.get(0).getBaseCurrencyId(), path.get(path.size() - 1).getTargetCurrencyId(), rate));
        }
        
        throw new ExchangeRateNotFoundException("Exchange rate not found for pair: " + from + "/" + to);
    }
    
    private List<ExchangeRates> getExchangeRatesMultycast(String from, String to, List<ExchangeRates> pathToFind) {
        if (pathToFind.size() > 5) { // Ограничиваем глубину поиска
            return List.of();
        }
        System.out.println(pathToFind.toString());

        // Ищем прямые курсы
        String sqlDirect = "SELECT * from ExchangeRates WHERE baseCurrencyId = (SELECT id FROM Currencies WHERE code = ?) AND targetCurrencyId = (SELECT id FROM Currencies WHERE code = ?)";
        try {
            ExchangeRates directRate = jdbcTemplate.queryForObject(sqlDirect, exchangeRatesRowMapper, from, to);
            if (directRate != null) {
                pathToFind.add(directRate);
                return pathToFind;
            }
        } catch (Exception e) {
            // Игнорируем ошибку и продолжаем поиск
        }

        // Ищем обратные курсы
        try {
            ExchangeRates reverseRate = jdbcTemplate.queryForObject(sqlDirect, exchangeRatesRowMapper, to, from);
            if (reverseRate != null) {
                ExchangeRates reversedRate = new ExchangeRates(
                    reverseRate.getTargetCurrencyId(),
                    reverseRate.getBaseCurrencyId(),
                    reverseRate.getRate()
                );
                pathToFind.add(reversedRate);
                System.out.println(reversedRate.getTargetCurrencyId());
                return pathToFind;
            }
        } catch (Exception e) {
            // Игнорируем ошибку и продолжаем поиск
        }

        // Ищем промежуточные курсы
        String sqlFindAll = "SELECT * from ExchangeRates WHERE baseCurrencyId = (SELECT id FROM Currencies WHERE code = ?)";
        List<ExchangeRates> exchangeRates = jdbcTemplate.query(sqlFindAll, exchangeRatesRowMapper, from);
        
        for (ExchangeRates exchangeRate : exchangeRates) {
            if (pathToFind.contains(exchangeRate)) {
                continue; // Пропускаем уже использованные курсы
            }

            String targetCode = jdbcTemplate.queryForObject(
                "SELECT code FROM Currencies WHERE id = ?", 
                String.class, 
                exchangeRate.getTargetCurrencyId()
            );
            System.out.println(targetCode);

            pathToFind.add(exchangeRate);
            List<ExchangeRates> result = getExchangeRatesMultycast(targetCode, to, pathToFind);
            
            if (result.isEmpty()) {
                pathToFind.remove(exchangeRate);
            } else {
                // Проверяем, есть ли последний элемент в пути
                String lastTargetCode = jdbcTemplate.queryForObject(
                    "SELECT code FROM Currencies WHERE id = ?", 
                    String.class, 
                    result.get(result.size() - 1).getTargetCurrencyId()
                );
                
                if (!lastTargetCode.equals(to)) {
                    // Если последний элемент не ведет к целевой валюте, добавляем его
                    try {
                        ExchangeRates finalRate = jdbcTemplate.queryForObject(
                            sqlDirect, 
                            exchangeRatesRowMapper, 
                            lastTargetCode, 
                            to
                        );
                        if (finalRate != null) {
                            result.add(finalRate);
                        }   
                    } catch (Exception e) {
                        // Игнорируем ошибку
                    }
                }
                return result;
            }
        }
        return List.of();
    }

    private BigDecimal calculatePathRate(List<ExchangeRates> path) {
        if (path.isEmpty()) return BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal rate = BigDecimal.ONE;
        for (ExchangeRates exchangeRate : path) {
            rate = rate.multiply(exchangeRate.getRate());
        }
        return rate.setScale(6, RoundingMode.HALF_UP);
    }

    public ExchangeRatesResponse updateExchangeRatesFromTo(String from, String to, BigDecimal rate) {
        String sql = "UPDATE ExchangeRates SET rate = ? WHERE baseCurrencyId = (SELECT id FROM Currencies WHERE code = ?) AND targetCurrencyId = (SELECT id FROM Currencies WHERE code = ?)";
        jdbcTemplate.update(sql, rate, from, to);
        return getExchangeRatesFromTo(from, to);
    }

    public ExchangedCurrenciesDTO convertByExchangeRates(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        ExchangeRatesResponse exchangeRatesResponse = getExchangeRatesFromTo(baseCurrencyCode, targetCurrencyCode);
        BigDecimal rate = exchangeRatesResponse.getRate();
        BigDecimal result = amount.multiply(rate);
        return new ExchangedCurrenciesDTO(exchangeRatesResponse.getBaseCurrency(), exchangeRatesResponse.getTargetCurrency(), amount, rate, result);
    }
    
    
    
    
}
