package com.chitkara.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for the /bfhl endpoint.
 * Contains all processed outputs derived from the input array.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BfhlResponseDto {

    /** Indicates whether the operation was successful. */
    @JsonProperty("is_success")
    private boolean isSuccess;

    /**
     * Unique user identifier in the format: full_name_ddmmyyyy
     * Example: john_doe_17091999
     */
    @JsonProperty("user_id")
    private String userId;

    /** Registered email of the user. */
    @JsonProperty("email")
    private String email;

    /** College roll number of the user. */
    @JsonProperty("roll_number")
    private String rollNumber;

    /** Even numeric strings extracted from the input array. */
    @JsonProperty("even_numbers")
    private List<String> evenNumbers;

    /** Odd numeric strings extracted from the input array. */
    @JsonProperty("odd_numbers")
    private List<String> oddNumbers;

    /** Alphabetic strings from the input array, converted to uppercase. */
    @JsonProperty("alphabets")
    private List<String> alphabets;

    /** Special character strings extracted from the input array. */
    @JsonProperty("special_characters")
    private List<String> specialCharacters;

    /**
     * Sum of all numeric values present in the input array, returned as a string.
     * Multi-digit numbers are treated as whole values (e.g., "334" → 334).
     */
    @JsonProperty("sum")
    private String sum;

    /**
     * All individual alphabetical characters collected from the input, listed in reverse
     * order with alternating caps starting from uppercase (rightmost char → uppercase).
     * Example: input chars [a, R] → reversed [R, a] → alternating caps → "Ra"
     */
    @JsonProperty("concat_string")
    private String concatString;
}
