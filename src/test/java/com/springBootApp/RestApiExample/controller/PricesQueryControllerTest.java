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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PricesQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PricesService pricesService;

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
    void testGetPriceByPostRequest_PriceNotFound() throws Exception {
        PricesRequestDto requestDto = getRequestDto("2020-03-07-11.00.00");
        mockMvc
                .perform(
                        post("/prices")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Unable to find any price with the provided parameters")))
                .andReturn();
    }

    @Test
    void testGetPriceByGetRequest_HappyFlow() throws Exception {
        String dateToCheck = "2020-06-14-10.00.00";
        Long productId = 35455L;
        Integer brandId = 1;

        PricesResponseDto responseDto = getResponseDto("2020-06-14-00.00.00", "2020-12-31-23.59.59", 35.50, 1);

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
    }


    static Stream<Arguments> givenParametersAndExpectedResult() {
        return Stream.of(
                Arguments.of(getRequestDto("2020-06-14-10.00.00"), getResponseDto("2020-06-14-00.00.00", "2020-12-31-23.59.59", 35.50, 1)),
                Arguments.of(getRequestDto("2020-06-14-16.00.00"), getResponseDto("2020-06-14-15.00.00", "2020-06-14-18.30.00", 25.45, 2)),
                Arguments.of(getRequestDto("2020-06-14-21.00.00"), getResponseDto("2020-06-14-00.00.00", "2020-12-31-23.59.59", 35.50, 1)),
                Arguments.of(getRequestDto("2020-06-15-10.00.00"), getResponseDto("2020-06-15-00.00.00", "2020-06-15-11.00.00", 30.50, 3)),
                Arguments.of(getRequestDto("2020-06-16-21.00.00"), getResponseDto("2020-06-15-16.00.00", "2020-12-31-23.59.59", 38.95, 4)));
    }

    @ParameterizedTest
    @MethodSource("givenParametersAndExpectedResult")
    void testGetPriceByPostRequest_HappyFlow(PricesRequestDto requestDto, PricesResponseDto responseDto) throws Exception {
        mockMvc
                .perform(
                        post("/prices")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)))
                .andReturn();
    }

    private static PricesRequestDto getRequestDto(String dateToCheck) {
        return
                PricesRequestDto.builder()
                        .dateToCheck(
                                LocalDateTime.parse(
                                        dateToCheck, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
                        .productId(35455L)
                        .brandId(1)
                        .build();
    }

    private static PricesResponseDto getResponseDto(String fromDate, String toDate, Double amount, Integer priceList) {
        return
                PricesResponseDto.builder()
                        .productId(35455L)
                        .brandId(1)
                        .price(BigDecimal.valueOf(amount))
                        .priceList(priceList)
                        .fromDate(
                                LocalDateTime.parse(
                                        fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
                        .toDate(
                                LocalDateTime.parse(
                                        toDate, DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss")))
                        .build();
    }

}
