package com.springBootApp.RestApiExample.service;

import com.springBootApp.RestApiExample.dto.PricesRequestDto;
import com.springBootApp.RestApiExample.dto.PricesResponseDto;
import com.springBootApp.RestApiExample.entity.Prices;
import com.springBootApp.RestApiExample.repository.PricesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PricesService {

  private final PricesRepository pricesRepository;

  public PricesService(PricesRepository pricesRepository) {
    this.pricesRepository = pricesRepository;
  }

  public PricesResponseDto getPriceFromRequestDto(PricesRequestDto requestDto) {
    List<Prices> pricesList =
        pricesRepository.findPriceRelatedWithProvidedParameter(
            requestDto.getBrandId(), requestDto.getProductId(), requestDto.getDateToCheck());

    return pricesList.stream()
        .findFirst()
        .map(
            prices ->
                PricesResponseDto.builder()
                    .productId(prices.getProductId())
                    .brandId(prices.getBrandId())
                    .price(prices.getPrice())
                    .priceList(prices.getPriceList())
                    .price(prices.getPrice())
                    .fromDate(prices.getStartDate())
                    .toDate(prices.getEndDate())
                    .build())
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Unable to find any price with the provided parameters"));
  }
}
