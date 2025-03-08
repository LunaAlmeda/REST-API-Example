package com.springBootApp.RestApiExample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PricesResponseDto {

  private Long productId;
  private Integer brandId;
  private Integer priceList;
  @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss")
  private LocalDateTime fromDate;
  @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss")
  private LocalDateTime toDate;
  private BigDecimal price;
}
