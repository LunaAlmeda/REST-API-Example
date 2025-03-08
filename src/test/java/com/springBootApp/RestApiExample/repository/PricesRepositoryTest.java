package com.springBootApp.RestApiExample.repository;

import com.springBootApp.RestApiExample.entity.Prices;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class PricesRepositoryTest {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  static final Prices prices_1 =
      new Prices(
          1L,
          1,
              LocalDateTime.parse("2020-06-14 00:00:00", DATE_FORMATTER),
              LocalDateTime.parse("2020-12-31 23:59:59", DATE_FORMATTER),
          1,
          35455L,
          0,
          BigDecimal.valueOf(35.50),
          "EUR");
  static final Prices prices_2 =
      new Prices(
          2L,
          1,
              LocalDateTime.parse("2020-06-14 15:00:00", DATE_FORMATTER),
              LocalDateTime.parse("2020-06-14 18:30:00", DATE_FORMATTER),
          2,
          35455L,
          1,
          BigDecimal.valueOf(25.45),
          "EUR");
  static final Prices prices_3 =
      new Prices(
          3L,
          1,
              LocalDateTime.parse("2020-06-15 00:00:00", DATE_FORMATTER),
              LocalDateTime.parse("2020-06-15 11:00:00", DATE_FORMATTER),
          3,
          35455L,
          1,
          BigDecimal.valueOf(30.50),
          "EUR");
  static final Prices prices_4 =
      new Prices(
          4L,
          1,
              LocalDateTime.parse("2020-06-15 16:00:00", DATE_FORMATTER),
              LocalDateTime.parse("2020-12-31 23:59:59", DATE_FORMATTER),
          4,
          35455L,
          1,
          BigDecimal.valueOf(38.95),
          "EUR");

  @Autowired private PricesRepository repository;

  static Stream<Arguments> givenParametersAndExpectedResult() {
    return Stream.of(
        Arguments.of(LocalDateTime.parse("2020-06-14 10:00:00", DATE_FORMATTER), List.of(prices_1)),
        Arguments.of(LocalDateTime.parse("2020-06-14 16:00:00", DATE_FORMATTER), List.of(prices_2, prices_1)),
        Arguments.of(LocalDateTime.parse("2020-06-14 21:00:00", DATE_FORMATTER), List.of(prices_1)),
        Arguments.of(LocalDateTime.parse("2020-06-15 10:00:00", DATE_FORMATTER), List.of(prices_3, prices_1)),
        Arguments.of(LocalDateTime.parse("2020-06-16 21:00:00", DATE_FORMATTER), List.of(prices_4, prices_1)),
        Arguments.of(LocalDateTime.parse("2025-06-16 21:00:00", DATE_FORMATTER), List.of()));
  }

  @ParameterizedTest
  @MethodSource("givenParametersAndExpectedResult")
  void checkExpectedEntity(LocalDateTime dateToCheck, List<Prices> expectedResult) {
    List<Prices> result = repository.findPriceRelatedWithProvidedParameter(1, 35455L, dateToCheck);
    assertThat(result).containsExactlyInAnyOrderElementsOf(expectedResult);
  }
}
