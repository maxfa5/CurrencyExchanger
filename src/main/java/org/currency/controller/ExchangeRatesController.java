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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.currency.DTO.ExchangeRatesResponse;
import org.currency.DTO.RateDTO;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import org.currency.model.ExchangeRates;

@RestController
@RequestMapping("/api")
public class ExchangeRatesController {
    private final ExchangeRatesService exchangeRatesService;

    public ExchangeRatesController(ExchangeRatesService exchangeRatesService) {
        this.exchangeRatesService = exchangeRatesService;
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRatesResponse> createExchangeRates(@Valid @RequestBody ExchangeRatesDTO exchangeRates) {
        return ResponseEntity.ok(exchangeRatesService.createExchangeRates(exchangeRatesService.convertToEntity(exchangeRates)));
    }

    @GetMapping("/exchangeRates/{id}")
    public ResponseEntity<ExchangeRates> getExchangeRatesById(@PathVariable Long id) {
        return ResponseEntity.ok(exchangeRatesService.getExchangeRatesById(id));
    }

    @GetMapping("/exchangeRate/{doubleCode}")
    public ResponseEntity<ExchangeRatesResponse> getExchangeRatesByDoubleCode(@PathVariable String doubleCode) {
        doubleCode = doubleCode.toUpperCase().replaceAll("(.{3})", "$1 ").trim();
        String from = doubleCode.split(" ")[0];
        String to = doubleCode.split(" ")[1];
        ExchangeRatesResponse response = exchangeRatesService.getExchangeRatesFromTo(from, to);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/exchangeRate/{doubleCode}")
    public ResponseEntity<ExchangeRatesResponse> updateExchangeRatesByDoubleCode(@PathVariable String doubleCode, @Valid @RequestBody RateDTO rateBody) {
        doubleCode = doubleCode.toUpperCase().replaceAll("(.{3})", "$1 ").trim();
        String from = doubleCode.split(" ")[0];
        String to = doubleCode.split(" ")[1];
        ExchangeRatesResponse response = exchangeRatesService.updateExchangeRatesFromTo(from, to, rateBody.getRate());
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRatesResponse>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRatesService.getAllExchangeRates().stream()
        .<ExchangeRatesResponse>map(exchangeRatesService::convertToResponse).collect(Collectors.toList()));   
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
