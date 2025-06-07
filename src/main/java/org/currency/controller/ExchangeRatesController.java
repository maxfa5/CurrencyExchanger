package org.currency.controller;

import org.currency.DTO.ExchangeRatesDTO;
import org.currency.service.ExchangeRatesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.currency.DTO.ExchangeRatesResponse;
import jakarta.validation.Valid;
import java.util.List;

import org.currency.model.ExchangeRates;

@RestController
@RequestMapping("/api/exchangeRates")
public class ExchangeRatesController {
    private final ExchangeRatesService exchangeRatesService;

    public ExchangeRatesController(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @PostMapping
    public ResponseEntity<ExchangeRatesResponse> createExchangeRates(@Valid @RequestBody ExchangeRatesDTO exchangeRates) {
        return ResponseEntity.ok(exchangeRatesService.createExchangeRates(exchangeRatesService.convertToEntity(exchangeRates)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeRates> getExchangeRatesById(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeRatesService.getExchangeRatesById(id));
    }

    @GetMapping("/{DoubleCode}")
        public ResponseEntity<ExchangeRates> getExchangeRatesByDoubleCode(@PathVariable String DoubleCode) {
        String from = DoubleCode.split(" ")[0];
        String to = DoubleCode.split(" ")[2];
        return ResponseEntity.ok(exchangeRatesService.getExchangeRatesFromTo(from, to));
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRates>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRatesService.getAllExchangeRates());   
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExchangeRates> updateExchangeRates(@PathVariable Long id, @RequestBody ExchangeRatesDTO exchangeRates) {
        return ResponseEntity.ok(exchangeRatesService.updateExchangeRates(id, exchangeRates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchangeRates(@PathVariable Long id) {
        exchangeRatesService.deleteExchangeRates(id);
        return ResponseEntity.ok().build();
    }


}
