package com.chitkara.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for the /bfhl endpoint.
 * Carries the input array of mixed strings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BfhlRequestDto {

    @NotNull(message = "data array must not be null")
    @JsonProperty("data")
    private List<String> data;
}
