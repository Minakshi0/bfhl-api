package com.chitkara.bfhl.service;

import com.chitkara.bfhl.dto.BfhlRequestDto;
import com.chitkara.bfhl.dto.BfhlResponseDto;

/**
 * Service interface defining the contract for BFHL data processing.
 */
public interface BfhlService {

    /**
     * Processes the input data array and returns a fully populated response DTO.
     *
     * @param request the incoming request containing the mixed data array
     * @return a {@link BfhlResponseDto} with categorised values and derived fields
     */
    BfhlResponseDto processData(BfhlRequestDto request);
}
