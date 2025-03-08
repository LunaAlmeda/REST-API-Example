package com.springBootApp.RestApiExample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springBootApp.RestApiExample.dto.PricesRequestDto;
import com.springBootApp.RestApiExample.dto.PricesResponseDto;
import com.springBootApp.RestApiExample.service.PricesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricesQueryController.class)
class PricesQueryControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private PricesService pricesService;

  static Stream<Arguments> pricesRequestTestData() {
    return Stream.of(
        Arguments.of(
            PricesRequestDto.builder().productId(35455L).brandId(1).build(),
            "Missing dateToCheck parameter"),
        Arguments.of(
            PricesRequestDto.builder()
                .dateToCheck(
                    LocalDateTime.parse(
                        "2025-03-06-12.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
                .brandId(1)
                .build(),
            "Missing productId parameter"),
        Arguments.of(
            PricesRequestDto.builder()
                .dateToCheck(
                    LocalDateTime.parse(
                        "2025-03-06-12.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
                .productId(35455L)
                .build(),
            "Missing brandId parameter"));
  }

  @ParameterizedTest
  @MethodSource("pricesRequestTestData")
  void whenMissingParameter_thenReturnsBadRequest(PricesRequestDto request, String error)
      throws Exception {
    mockMvc
        .perform(
            post("/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(error))
        .andExpect(jsonPath("$.code").value("400"))
        .andExpect(jsonPath("$.error").value("Bad Request"));
  }

  @Test
  void testGetPriceByGetRequest_invalidDateToCheck() throws Exception {
    String invalidDateToCheck = "2025-03-08-25.00.00";
    Long productId = 1L;
    Integer brandId = 1;

    mockMvc
        .perform(
            get("/prices")
                .param("dateToCheck", invalidDateToCheck)
                .param("productId", productId.toString())
                .param("brandId", brandId.toString()))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid date format")))
        .andReturn();
  }

  @Test
  void testGetPriceByGetRequest_HappyFlow() throws Exception {
    String dateToCheck = "2025-03-07-11.00.00";
    Long productId = 100L;
    Integer brandId = 1;

    PricesResponseDto responseDto =
        PricesResponseDto.builder()
            .productId(100L)
            .brandId(1)
            .price(BigDecimal.valueOf(199.99))
            .fromDate(
                LocalDateTime.parse(
                    "2025-03-07-10.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .toDate(
                LocalDateTime.parse(
                    "2025-03-07-12.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .build();

    when(pricesService.getPriceFromRequestDto(any(PricesRequestDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            get("/prices")
                .param("dateToCheck", dateToCheck)
                .param("productId", String.valueOf(productId))
                .param("brandId", String.valueOf(brandId)))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
        .andReturn();

    verify(pricesService, times(1)).getPriceFromRequestDto(any(PricesRequestDto.class));
  }

  @Test
  void testGetPriceByPostRequest_HappyFlow() throws Exception {
    PricesRequestDto requestDto =
        PricesRequestDto.builder()
            .dateToCheck(
                LocalDateTime.parse(
                    "2025-03-07-11.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .productId(100L)
            .brandId(1)
            .build();

    PricesResponseDto responseDto =
        PricesResponseDto.builder()
            .productId(100L)
            .brandId(1)
            .price(BigDecimal.valueOf(199.99))
            .fromDate(
                LocalDateTime.parse(
                    "2025-03-07-10.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .toDate(
                LocalDateTime.parse(
                    "2025-03-07-12.00.00", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
            .build();

    when(pricesService.getPriceFromRequestDto(any(PricesRequestDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            post("/prices")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
        .andReturn();

    verify(pricesService, times(1)).getPriceFromRequestDto(any(PricesRequestDto.class));
  }
}
