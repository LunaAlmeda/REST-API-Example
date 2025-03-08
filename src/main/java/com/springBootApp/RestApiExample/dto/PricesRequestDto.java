package com.springBootApp.RestApiExample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PricesRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd-HH.mm.ss")
    @NotNull(message = "Missing dateToCheck parameter")
    private LocalDateTime dateToCheck;
    @NotNull(message = "Missing productId parameter")
    private Long productId;
    @NotNull(message = "Missing brandId parameter")
    private Integer brandId;
}
