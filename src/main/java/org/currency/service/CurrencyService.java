package org.currency.service;

import org.currency.model.Currencies;
import org.currency.DTO.CurrenciesDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    private final JdbcTemplate jdbcTemplate;

    // Маппер для преобразования строки результата в объект User
    private final RowMapper<Currencies> currenciesRowMapper = (rs, rowNum) -> {
        Currencies currencies = new Currencies();
        currencies.setID(rs.getLong("id"));
        currencies.setCode(rs.getString("code"));
        currencies.setFullName(rs.getString("fullName"));
        currencies.setSign(rs.getString("sign"));
        return currencies;
    };

    public CurrencyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.currencyRowMapper = (rs, rowNum) -> {
            CurrenciesDTO currency = new CurrenciesDTO();
            currency.setID(rs.getLong("id"));
            currency.setCode(rs.getString("code"));
            currency.setName(rs.getString("name"));
            currency.setSign(rs.getString("sign"));
            return currency;
        };
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
        Currencies currency = mapToEntity(currencyDTO);
        String sql = "INSERT INTO Currencies (code, fullName, sign) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
            currency.getCode(), 
            currency.getFullName(), 
            currency.getSign()
        );
        
        if (id != null) {
            currency.setID(id);
            System.out.println("Created currency with ID: " + id);
        } else {
            System.out.println("Failed to get ID for created currency");
            throw new DataAccessException("Failed to get ID for created currency") {};
        }
        return mapToDTO(currency);
    }

    // Получение пользователя по ID
    public CurrenciesDTO getCurrenciesByCode(String code) {
        String sql = "SELECT * FROM Currencies WHERE code = ?";
        Currencies currency = jdbcTemplate.queryForObject(sql, currenciesRowMapper, code);
        return mapToDTO(currency);
    }

    // Получение всех пользователей
    public List<CurrenciesDTO> getAllCurrencies() {
        String sql = "SELECT * FROM Currencies";
        List<Currencies> currencies = jdbcTemplate.query(sql, currenciesRowMapper);
        return currencies.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public CurrenciesDTO updateCurrencies(Long id, CurrenciesDTO currencyDTO) {
        Currencies currency = mapToEntity(currencyDTO);
        String sql = "UPDATE Currencies SET code = ?, fullName = ?, sign = ? WHERE id = ?";
        jdbcTemplate.update(sql, 
            currency.getCode(), 
            currency.getFullName(), 
            currency.getSign(), 
            id
        );
        currency.setID(id);
        return mapToDTO(currency);
    }

    // Удаление пользователя
    public void deleteCurrencies(Long id) {
        String sql = "DELETE FROM Currencies WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private CurrenciesDTO mapToDTO(Currencies currency) {
        CurrenciesDTO dto = new CurrenciesDTO();
        dto.setID(currency.getID());
        dto.setCode(currency.getCode());
        dto.setName(currency.getFullName());
        dto.setSign(currency.getSign());
        return dto;
    }

    private Currencies mapToEntity(CurrenciesDTO dto) {
        Currencies currency = new Currencies();
        currency.setID(dto.getID());
        currency.setCode(dto.getCode());
        currency.setFullName(dto.getName());
        currency.setSign(dto.getSign());
        return currency;
    }
} 