package org.currency.controller;

import org.currency.DTO.ExchangeRatesDTO;
import org.currency.service.ExchangeRatesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.currency.DTO.ExchangeRatesResponseDTO;
import org.currency.DTO.ExchangedCurrenciesDTO;
import org.currency.DTO.RateDTO;
import org.currency.mapper.ExchangeRatesMapper;
import org.currency.model.ExchangeRates;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ExchangeRatesController {
    private final ExchangeRatesService exchangeRatesService;
    private final ExchangeRatesMapper exchangeRatesMapper;

    public ExchangeRatesController(ExchangeRatesService exchangeRatesService, ExchangeRatesMapper exchangeRatesMapper) {
        this.exchangeRatesService = exchangeRatesService;
        this.exchangeRatesMapper = exchangeRatesMapper;
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRatesResponseDTO> createExchangeRates(@Valid @RequestBody ExchangeRatesDTO exchangeRates) {
        return ResponseEntity.ok(exchangeRatesService.createExchangeRates(exchangeRatesMapper.convertToEntity(exchangeRates)));
    }

    @GetMapping("/exchangeRates/{id}")
    public ResponseEntity<ExchangeRates> getExchangeRatesById(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeRatesService.getExchangeRatesById(id));
    }

    @GetMapping("/exchangeRate/{doubleCode}")
    public ResponseEntity<ExchangeRatesResponseDTO> getExchangeRatesByDoubleCode(@PathVariable String doubleCode) {
        doubleCode = doubleCode.toUpperCase().replaceAll("(.{3})", "$1 ").trim();
        String from = doubleCode.split(" ")[0];
        String to = doubleCode.split(" ")[1];
        ExchangeRatesResponseDTO response = exchangeRatesService.getExchangeRatesFromTo(from, to);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/exchangeRate/{doubleCode}")
    public ResponseEntity<ExchangeRatesResponseDTO> updateExchangeRatesByDoubleCode(@PathVariable String doubleCode, @Valid @RequestBody RateDTO rateBody) {
        doubleCode = doubleCode.toUpperCase().replaceAll("(.{3})", "$1 ").trim();
        String from = doubleCode.split(" ")[0];
        String to = doubleCode.split(" ")[1];
        ExchangeRatesResponseDTO response = exchangeRatesService.updateExchangeRatesFromTo(from, to, rateBody.getRate());
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/exchange")
    public ResponseEntity<ExchangedCurrenciesDTO> getConvertByExchangeRates(@RequestParam (name = "from") String baseCurrencyCode, @RequestParam(name = "to") String targetCurrencyCode, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(exchangeRatesService.convertByExchangeRates(baseCurrencyCode, targetCurrencyCode, amount));
    }

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRatesResponseDTO>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRatesService.getAllExchangeRates().stream()
        .<ExchangeRatesResponseDTO>map(rate -> exchangeRatesMapper.convertToResponse(rate, exchangeRatesService.getCurrencyService())).collect(Collectors.toList()));   
    }

    @PutMapping("/exchangeRates/{id}")
    public ResponseEntity<ExchangeRates> updateExchangeRates(@PathVariable Long id, @RequestBody ExchangeRatesDTO exchangeRates) {
        return ResponseEntity.ok(exchangeRatesService.updateExchangeRates(id, exchangeRates));
    }

    @DeleteMapping("/exchangeRates/{id}")
    public ResponseEntity<Void> deleteExchangeRates(@PathVariable Long id) {
        exchangeRatesService.deleteExchangeRates(id);
        return ResponseEntity.ok().build();
    }


}
