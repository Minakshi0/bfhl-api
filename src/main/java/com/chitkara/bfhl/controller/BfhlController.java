package com.chitkara.bfhl.controller;

import com.chitkara.bfhl.dto.BfhlRequestDto;
import com.chitkara.bfhl.dto.BfhlResponseDto;
import com.chitkara.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the /bfhl endpoint.
 *
 * <p>Accepts a POST request with a JSON body, delegates all business logic to
 * {@link BfhlService}, and returns a 200 OK with the processed response.
 */
@RestController
@RequestMapping("/bfhl")
@RequiredArgsConstructor
public class BfhlController {

    private final BfhlService bfhlService;

    /**
     * POST /bfhl
     *
     * @param request validated request body containing the data array
     * @return 200 OK with a {@link BfhlResponseDto}
     */
    @PostMapping
    public ResponseEntity<BfhlResponseDto> processData(
            @Valid @RequestBody BfhlRequestDto request) {

        BfhlResponseDto response = bfhlService.processData(request);
        return ResponseEntity.ok(response);
    }
}
