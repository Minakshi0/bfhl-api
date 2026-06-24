package com.chitkara.bfhl;

import com.chitkara.bfhl.dto.BfhlRequestDto;
import com.chitkara.bfhl.dto.BfhlResponseDto;
import com.chitkara.bfhl.service.BfhlService;
import com.chitkara.bfhl.service.BfhlServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration + unit tests for the BFHL API.
 *
 * <p>Covers:
 * <ul>
 *   <li>Example A from the spec</li>
 *   <li>Example B from the spec</li>
 *   <li>Example C from the spec (pure alphabets, multi-char tokens)</li>
 *   <li>Empty data array edge case</li>
 *   <li>Validation – missing "data" field returns 400</li>
 *   <li>Service-layer unit tests (odd/even split, sum, concat)</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class BfhlApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Direct service reference for unit-level assertions
    private BfhlService service;

    @BeforeEach
    void setUp() throws Exception {
        BfhlServiceImpl impl = new BfhlServiceImpl();
        // Inject default property values via reflection
        setField(impl, "userName",   "john_doe");
        setField(impl, "userDob",    "17091999");
        setField(impl, "userEmail",  "john@xyz.com");
        setField(impl, "rollNumber", "ABCD123");
        service = impl;
    }

    // =========================================================================
    // Integration tests via MockMvc
    // =========================================================================

    @Test
    @DisplayName("Example A – mixed input: numbers, letters, special chars")
    void exampleA_returnsCorrectResponse() throws Exception {
        String body = """
                { "data": ["a", "1", "334", "4", "R", "$"] }
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers[0]").value("1"))
                .andExpect(jsonPath("$.even_numbers.length()").value(2))
                .andExpect(jsonPath("$.alphabets.length()").value(2))
                .andExpect(jsonPath("$.special_characters[0]").value("$"))
                .andExpect(jsonPath("$.sum").value("339"))
                .andExpect(jsonPath("$.concat_string").value("Ra"));
    }

    @Test
    @DisplayName("Example B – multiple special chars, multi-digit even numbers")
    void exampleB_returnsCorrectResponse() throws Exception {
        String body = """
                { "data": ["2","a","y","4","&","-","*","5","92","b"] }
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers[0]").value("5"))
                .andExpect(jsonPath("$.even_numbers.length()").value(3))
                .andExpect(jsonPath("$.alphabets.length()").value(3))
                .andExpect(jsonPath("$.special_characters.length()").value(3))
                .andExpect(jsonPath("$.sum").value("103"))
                .andExpect(jsonPath("$.concat_string").value("ByA"));
    }

    @Test
    @DisplayName("Example C – pure alphabetic multi-char tokens")
    void exampleC_returnsCorrectResponse() throws Exception {
        String body = """
                { "data": ["A","ABCD","DOE"] }
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.odd_numbers.length()").value(0))
                .andExpect(jsonPath("$.even_numbers.length()").value(0))
                .andExpect(jsonPath("$.alphabets.length()").value(3))
                .andExpect(jsonPath("$.special_characters.length()").value(0))
                .andExpect(jsonPath("$.sum").value("0"))
                .andExpect(jsonPath("$.concat_string").value("EoDdCbAa"));
    }

    @Test
    @DisplayName("Empty data array – returns zeros and empty arrays")
    void emptyData_returnsZeroSumAndEmptyArrays() throws Exception {
        String body = """
                { "data": [] }
                """;

        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_success").value(true))
                .andExpect(jsonPath("$.sum").value("0"))
                .andExpect(jsonPath("$.concat_string").value(""))
                .andExpect(jsonPath("$.even_numbers.length()").value(0))
                .andExpect(jsonPath("$.odd_numbers.length()").value(0));
    }

    @Test
    @DisplayName("Missing 'data' field – returns 400 Bad Request")
    void missingDataField_returns400() throws Exception {
        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }

    @Test
    @DisplayName("Malformed JSON – returns 400 Bad Request")
    void malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/bfhl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not valid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.is_success").value(false));
    }

    // =========================================================================
    // Service unit tests
    // =========================================================================

    @Test
    @DisplayName("Service: numbers are classified odd/even correctly")
    void service_oddEvenClassification() {
        BfhlRequestDto req = new BfhlRequestDto(List.of("1", "2", "3", "100", "999"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getOddNumbers()).containsExactly("1", "3", "999");
        assertThat(res.getEvenNumbers()).containsExactly("2", "100");
    }

    @Test
    @DisplayName("Service: sum is correct and returned as string")
    void service_sumAsString() {
        BfhlRequestDto req = new BfhlRequestDto(List.of("10", "20", "30"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getSum()).isEqualTo("60");
    }

    @Test
    @DisplayName("Service: alphabets converted to uppercase")
    void service_alphabetsUppercase() {
        BfhlRequestDto req = new BfhlRequestDto(List.of("abc", "XYZ", "Hello"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getAlphabets()).containsExactly("ABC", "XYZ", "HELLO");
    }

    @Test
    @DisplayName("Service: special characters are isolated correctly")
    void service_specialCharacters() {
        BfhlRequestDto req = new BfhlRequestDto(List.of("$", "&", "!", "1", "a"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getSpecialCharacters()).containsExactly("$", "&", "!");
    }

    @Test
    @DisplayName("Service: concat_string reversed alternating caps (Example A)")
    void service_concatStringExampleA() {
        // chars: a, R  → reversed: R, a  → alt caps: R (upper, idx 0), a (lower, idx 1) → "Ra"
        BfhlRequestDto req = new BfhlRequestDto(List.of("a", "1", "334", "4", "R", "$"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getConcatString()).isEqualTo("Ra");
    }

    @Test
    @DisplayName("Service: concat_string reversed alternating caps (Example B)")
    void service_concatStringExampleB() {
        // chars: a, y, b → reversed: b, y, a → alt caps: B, y, A → "ByA"
        BfhlRequestDto req = new BfhlRequestDto(
                List.of("2", "a", "y", "4", "&", "-", "*", "5", "92", "b"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getConcatString()).isEqualTo("ByA");
    }

    @Test
    @DisplayName("Service: concat_string reversed alternating caps (Example C)")
    void service_concatStringExampleC() {
        // chars: A,B,C,D,D,O,E → reversed: E,O,D,D,C,B,A,  → wait tokens: A → [A], ABCD → [A,B,C,D], DOE → [D,O,E]
        // full: A, A,B,C,D, D,O,E  reversed: E,O,D,D,C,B,A,A  → alt: E,o,D,d,C,b,A,a → "EoDdCbAa"
        BfhlRequestDto req = new BfhlRequestDto(List.of("A", "ABCD", "DOE"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getConcatString()).isEqualTo("EoDdCbAa");
    }

    @Test
    @DisplayName("Service: user_id follows full_name_ddmmyyyy format")
    void service_userIdFormat() {
        BfhlRequestDto req = new BfhlRequestDto(Collections.emptyList());
        BfhlResponseDto res = service.processData(req);

        assertThat(res.getUserId()).matches("[a-z_]+_\\d{8}");
    }

    @Test
    @DisplayName("Service: is_success is always true on valid input")
    void service_isSuccessTrue() {
        BfhlRequestDto req = new BfhlRequestDto(List.of("1", "a", "$"));
        BfhlResponseDto res = service.processData(req);

        assertThat(res.isSuccess()).isTrue();
    }

    // =========================================================================
    // Utility
    // =========================================================================

    /** Injects a value into a private field without requiring Spring context. */
    private static void setField(Object target, String fieldName, String value)
            throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
