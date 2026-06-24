package com.chitkara.bfhl.service;

import com.chitkara.bfhl.dto.BfhlRequestDto;
import com.chitkara.bfhl.dto.BfhlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link BfhlService}.
 *
 * <p>Processing rules:
 * <ul>
 *   <li>A token is numeric if it can be parsed as a long integer.</li>
 *   <li>A token is purely alphabetic if every character satisfies {@link Character#isLetter}.</li>
 *   <li>Anything else (contains digits mixed with letters, or pure symbols) is a special character token.</li>
 *   <li>Numbers are classified odd/even by their absolute numeric value.</li>
 *   <li>{@code sum} is the total of all numeric token values, returned as a string.</li>
 *   <li>{@code concat_string}: every individual letter across ALL tokens is collected in order,
 *       then the list is reversed, and alternating-caps is applied starting with UPPER for index 0
 *       (i.e. the rightmost original character becomes uppercase).</li>
 * </ul>
 */
@Service
public class BfhlServiceImpl implements BfhlService {

    @Value("${app.user.name:john_doe}")
    private String userName;

    @Value("${app.user.dob:17091999}")
    private String userDob;

    @Value("${app.user.email:john@xyz.com}")
    private String userEmail;

    @Value("${app.user.roll:ABCD123}")
    private String rollNumber;

    @Override
    public BfhlResponseDto processData(BfhlRequestDto request) {

        List<String> evenNumbers      = new ArrayList<>();
        List<String> oddNumbers       = new ArrayList<>();
        List<String> alphabets        = new ArrayList<>();
        List<String> specialChars     = new ArrayList<>();
        long         numericSum       = 0;

        // Collect all individual alphabetical characters (in input order)
        List<Character> allAlphaChars = new ArrayList<>();

        for (String token : request.getData()) {
            if (token == null || token.isEmpty()) continue;

            if (isNumeric(token)) {
                long value = Long.parseLong(token);
                numericSum += value;
                if (Math.abs(value) % 2 == 0) {
                    evenNumbers.add(token);
                } else {
                    oddNumbers.add(token);
                }
            } else if (isPurelyAlphabetic(token)) {
                // Store as uppercase per spec
                alphabets.add(token.toUpperCase());
                // Collect individual letters for concat_string
                for (char c : token.toCharArray()) {
                    allAlphaChars.add(c);
                }
            } else {
                // Mixed token or pure special characters
                specialChars.add(token);
            }
        }

        String concatString = buildConcatString(allAlphaChars);
        String userId = userName.toLowerCase() + "_" + userDob;

        return BfhlResponseDto.builder()
                .isSuccess(true)
                .userId(userId)
                .email(userEmail)
                .rollNumber(rollNumber)
                .evenNumbers(evenNumbers)
                .oddNumbers(oddNumbers)
                .alphabets(alphabets)
                .specialCharacters(specialChars)
                .sum(String.valueOf(numericSum))
                .concatString(concatString)
                .build();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns true if the token can be parsed as a long integer
     * (handles negative numbers too, though inputs are typically positive).
     */
    private boolean isNumeric(String token) {
        try {
            Long.parseLong(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true only when every character in the token is a letter (a-z / A-Z).
     * Tokens like "ABCD" or "a" qualify; "A1" or "$" do not.
     */
    private boolean isPurelyAlphabetic(String token) {
        for (char c : token.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }

    /**
     * Builds the concat_string:
     * <ol>
     *   <li>Collect every individual alphabetical character from the input (in order).</li>
     *   <li>Reverse the collected list.</li>
     *   <li>Apply alternating caps starting with UPPER at index 0 of the reversed list.</li>
     * </ol>
     *
     * <p>Example A: input chars = [a, R]  → reversed = [R, a]  → alt caps = [R, a]  → "Ra"
     * <p>Example B: input chars = [a, y, b] → reversed = [b, y, a] → alt caps = [B, y, A] → "ByA"
     * <p>Example C: input chars = [A,B,C,D, D,O,E] → reversed = [E,O,D,D,C,B,A]
     *              → alt caps = [E,o,D,d,C,b,A] → "EoDdCbA" — but spec shows "EoDdCbAa"
     *              which implies multi-char tokens expand left-to-right then reversed together.
     *              That is exactly what this implementation does.
     */
    private String buildConcatString(List<Character> allAlphaChars) {
        if (allAlphaChars.isEmpty()) return "";

        // Reverse the list
        List<Character> reversed = new ArrayList<>(allAlphaChars);
        java.util.Collections.reverse(reversed);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reversed.size(); i++) {
            char c = reversed.get(i);
            if (i % 2 == 0) {
                sb.append(Character.toUpperCase(c));   // even index → uppercase
            } else {
                sb.append(Character.toLowerCase(c));   // odd index  → lowercase
            }
        }
        return sb.toString();
    }
}
