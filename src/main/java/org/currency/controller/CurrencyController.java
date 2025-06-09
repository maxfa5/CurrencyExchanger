package org.currency.controller;

import org.currency.DTO.CurrenciesDTO;
import org.currency.DTO.CurrencyResponseDTO;
import org.currency.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public ResponseEntity<CurrencyResponseDTO> createCurrency(@Valid @RequestBody CurrenciesDTO currency) {
        return ResponseEntity.ok(new CurrencyResponseDTO(currencyService.createCurrency(currency)));
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyResponseDTO> getCurrencyByCode(@PathVariable String code) {
        return ResponseEntity.ok(new CurrencyResponseDTO(currencyService.getCurrenciesByCode(code)));
    }

    @GetMapping
    public ResponseEntity<List<CurrencyResponseDTO>> getAllCurrencies() {
        List<CurrencyResponseDTO> response = currencyService.getAllCurrencies().stream()
            .map(CurrencyResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
        }

    @PutMapping("/{id}")
    public ResponseEntity<CurrencyResponseDTO> updateCurrency(@PathVariable Long id, @Valid @RequestBody CurrenciesDTO currency) {
        return ResponseEntity.ok(new CurrencyResponseDTO(currencyService.updateCurrencies(id, currency)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        currencyService.deleteCurrencies(id);
        return ResponseEntity.ok().build();
    }
}