package com.springBootApp.RestApiExample.service;

import com.springBootApp.RestApiExample.dto.PricesRequestDto;
import com.springBootApp.RestApiExample.dto.PricesResponseDto;
import com.springBootApp.RestApiExample.entity.Prices;
import com.springBootApp.RestApiExample.repository.PricesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricesServiceTest {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Mock PricesRepository pricesMockRepository;

  @InjectMocks private PricesService pricesService;

  static Stream<Arguments> givenParametersAndExpectedResult() {
    return Stream.of(
        Arguments.of(
            PricesRequestDto.builder()
                .brandId(1)
                .productId(35455L)
                .dateToCheck(LocalDateTime.parse("2020-06-14 10:00:00", DATE_FORMATTER))
                .build(),
            List.of(
                givenFoundPrices(
                    "2020-06-14 00:00:00", "2020-12-31 23:59:59", 1, 0, BigDecimal.valueOf(35.50))),
            PricesResponseDto.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .price(BigDecimal.valueOf(35.50))
                .fromDate(LocalDateTime.parse("2020-06-14 00:00:00", DATE_FORMATTER))
                .toDate(LocalDateTime.parse("2020-12-31 23:59:59", DATE_FORMATTER))
                .build()),
        Arguments.of(
            PricesRequestDto.builder()
                .brandId(1)
                .productId(35455L)
                .dateToCheck(LocalDateTime.parse("2020-06-14 16:00:00", DATE_FORMATTER))
                .build(),
            List.of(
                givenFoundPrices(
                    "2020-06-14 15:00:00", "2020-06-14 18:30:00", 2, 1, BigDecimal.valueOf(25.45))),
            PricesResponseDto.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(2)
                .price(BigDecimal.valueOf(25.45))
                .fromDate(LocalDateTime.parse("2020-06-14 15:00:00", DATE_FORMATTER))
                .toDate(LocalDateTime.parse("2020-06-14 18:30:00", DATE_FORMATTER))
                .build()),
        Arguments.of(
            PricesRequestDto.builder()
                .brandId(1)
                .productId(35455L)
                .dateToCheck(LocalDateTime.parse("2020-06-14 21:00:00", DATE_FORMATTER))
                .build(),
            List.of(
                givenFoundPrices(
                    "2020-06-14 00:00:00", "2020-12-31 23:59:59", 1, 0, BigDecimal.valueOf(35.50))),
            PricesResponseDto.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .price(BigDecimal.valueOf(35.50))
                .fromDate(LocalDateTime.parse("2020-06-14 00:00:00", DATE_FORMATTER))
                .toDate(LocalDateTime.parse("2020-12-31 23:59:59", DATE_FORMATTER))
                .build()),
        Arguments.of(
            PricesRequestDto.builder()
                .brandId(1)
                .productId(35455L)
                .dateToCheck(LocalDateTime.parse("2020-06-15 10:00:00", DATE_FORMATTER))
                .build(),
            List.of(
                givenFoundPrices(
                    "2020-06-15 00:00:00", "2020-06-15 11:00:00", 3, 1, BigDecimal.valueOf(30.50))),
            PricesResponseDto.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(3)
                .price(BigDecimal.valueOf(30.50))
                .fromDate(LocalDateTime.parse("2020-06-15 00:00:00", DATE_FORMATTER))
                .toDate(LocalDateTime.parse("2020-06-15 11:00:00", DATE_FORMATTER))
                .build()),
        Arguments.of(
            PricesRequestDto.builder()
                .brandId(1)
                .productId(35455L)
                .dateToCheck(LocalDateTime.parse("2020-06-16 21:00:00", DATE_FORMATTER))
                .build(),
            List.of(
                givenFoundPrices(
                    "2020-06-15 16:00:00", "2020-12-31 23:59:59", 4, 1, BigDecimal.valueOf(38.95))),
            PricesResponseDto.builder()
                .productId(35455L)
                .brandId(1)
                .priceList(4)
                .price(BigDecimal.valueOf(38.95))
                .fromDate(LocalDateTime.parse("2020-06-15 16:00:00", DATE_FORMATTER))
                .toDate(LocalDateTime.parse("2020-12-31 23:59:59", DATE_FORMATTER))
                .build()));
  }

  private static Prices givenFoundPrices(
      String startDate, String endDate, Integer priceList, Integer priority, BigDecimal price) {
    return new Prices(
        1L,
        1,
        LocalDateTime.parse(startDate, DATE_FORMATTER),
        LocalDateTime.parse(endDate, DATE_FORMATTER),
        priceList,
        35455L,
        priority,
        price,
        "EUR");
  }

  @ParameterizedTest
  @MethodSource("givenParametersAndExpectedResult")
  void whenEntityIsConvertedToDto_thenCheckDtoResponse(
      PricesRequestDto request, List<Prices> foundPrices, PricesResponseDto expected) {
    // Mock return from the repository layer
    when(pricesMockRepository.findPriceRelatedWithProvidedParameter(
            eq(request.getBrandId()), eq(request.getProductId()), eq(request.getDateToCheck())))
        .thenReturn(foundPrices);
    // Check if expected Dto and response match
    assertEquals(expected, pricesService.getPriceFromRequestDto(request));
  }

  @Test
  void whenRepositoryReturnsNoResult_thenThrowException() {
    PricesRequestDto requestDto =
        PricesRequestDto.builder()
            .brandId(1)
            .productId(35455L)
            .dateToCheck(LocalDateTime.parse("2020-06-16 21:00:00", DATE_FORMATTER))
            .build();
    when(pricesMockRepository.findPriceRelatedWithProvidedParameter(any(), any(), any()))
        .thenReturn(List.of());

    assertThrows(
        ResponseStatusException.class, () -> pricesService.getPriceFromRequestDto(requestDto));
  }
}
