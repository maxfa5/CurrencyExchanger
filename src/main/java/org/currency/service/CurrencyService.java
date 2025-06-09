package org.currency.service;

import org.currency.model.Currencies;
import org.currency.DTO.CurrenciesDTO;
import org.currency.mapper.CurrenciesMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private final JdbcTemplate jdbcTemplate;
    private final CurrenciesMapper currenciesMapper;

    public CurrencyService(JdbcTemplate jdbcTemplate, CurrenciesMapper currenciesMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.currenciesMapper = currenciesMapper;
        initTable();
    }

    // Инициализация таблицы при старте приложения
    private void initTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS Currencies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                code TEXT NOT NULL UNIQUE,
                fullName TEXT NOT NULL UNIQUE,
                sign TEXT NOT NULL UNIQUE
            )
        """);
    }

    // Создание нового пользователя
    public CurrenciesDTO createCurrency(CurrenciesDTO currencyDTO) {
        Currencies currency = currenciesMapper.mapToEntity(currencyDTO);
        String sql = "INSERT INTO Currencies (code, fullName, sign) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
            currency.getCode(), 
            currency.getFullName(), 
            currency.getSign()
        );
        
        if (id != null) {
            currency.setID(id);
        } else {
            throw new DataAccessException("Failed to get ID for created currency") {};
        }
        return currenciesMapper.mapToDTO(currency);
    }

    // Получение пользователя по ID
    public CurrenciesDTO getCurrenciesByCode(String code) {
        String sql = "SELECT * FROM Currencies WHERE code = ?";
        Currencies currency = jdbcTemplate.queryForObject(sql, currenciesMapper.getCurrenciesRowMapper(), code);
        return currenciesMapper.mapToDTO(currency);
    }

    public Currencies getCurrenciesById(Long id) {
        String sql = "SELECT * FROM Currencies WHERE id = ?";
        Currencies currency = jdbcTemplate.queryForObject(sql, currenciesMapper.getCurrenciesRowMapper(), id);
        return currency;
    }

    // Получение всех пользователей
    public List<CurrenciesDTO> getAllCurrencies() {
        String sql = "SELECT * FROM Currencies";
        List<Currencies> currencies = jdbcTemplate.query(sql, currenciesMapper.getCurrenciesRowMapper());
        return currencies.stream()
            .map(currenciesMapper::mapToDTO)
            .collect(Collectors.toList());
    }

    public CurrenciesDTO updateCurrencies(Long id, CurrenciesDTO currencyDTO) {
        Currencies currency = currenciesMapper.mapToEntity(currencyDTO);
        String sql = "UPDATE Currencies SET code = ?, fullName = ?, sign = ? WHERE id = ?";
        jdbcTemplate.update(sql, 
            currency.getCode(), 
            currency.getFullName(), 
            currency.getSign(), 
            id
        );
        currency.setID(id);
        return currenciesMapper.mapToDTO(currency);
    }

    // Удаление пользователя
    public void deleteCurrencies(Long id) {
        String sql = "DELETE FROM Currencies WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

} 