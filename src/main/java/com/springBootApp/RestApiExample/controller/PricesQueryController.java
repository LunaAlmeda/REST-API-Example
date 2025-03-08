package com.springBootApp.RestApiExample.controller;

import com.springBootApp.RestApiExample.dto.PricesRequestDto;
import com.springBootApp.RestApiExample.dto.PricesResponseDto;
import com.springBootApp.RestApiExample.service.PricesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/prices")
public class PricesQueryController {

  private final PricesService pricesService;

  public PricesQueryController(PricesService pricesService) {
    this.pricesService = pricesService;
  }

  @SneakyThrows
  @GetMapping
  public ResponseEntity<PricesResponseDto> getPriceByGetRequest(
      @RequestParam @NotNull(message = "Missing dateToCheck parameter") String dateToCheck,
      @RequestParam @NotNull(message = "Missing productId parameter") Long productId,
      @RequestParam @NotNull(message = "Missing brandId parameter") Integer brandId) {
    PricesRequestDto requestDto =
        PricesRequestDto.builder()
            .dateToCheck(
                LocalDateTime.parse(
                    dateToCheck, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .productId(productId)
            .brandId(brandId)
            .build();

    return ResponseEntity.ok(pricesService.getPriceFromRequestDto(requestDto));
  }

  @SneakyThrows
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<PricesResponseDto> getPriceByPostRequest(
      @RequestBody @Valid PricesRequestDto requestDto) {
    return ResponseEntity.ok(pricesService.getPriceFromRequestDto(requestDto));
  }
}
