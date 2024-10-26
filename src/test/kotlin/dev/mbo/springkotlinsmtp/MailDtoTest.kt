package dev.mbo.springkotlinsmtp

import jakarta.validation.Validation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class MailDtoTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    @ParameterizedTest
    @MethodSource("addresses")
    fun test(from: String?, to: List<String>, subject: String, text: String, validationErrorCount: Int) {
        assertThat(
            validator.validate(
                MailDto(from = from, to = to, subject = subject, text = text)
            ).size
        ).isEqualTo(validationErrorCount)
    }

    companion object {
        @JvmStatic
        fun addresses(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("from@example.com", listOf("to@example.com"), "subj", "body", 0),
                Arguments.of("from@example.com", emptyList<String>(), "subj", "body", 1),
                Arguments.of("from@example.com", listOf("to@example.com"), "subj", "", 1), // empty body
                Arguments.of("from@example.com", listOf("to@example.com"), "", "body", 1), // empty subject
                Arguments.of("from@example.com", listOf("to"), "subj", "body", 1), // invalid to
                Arguments.of("from", listOf("to@example.com"), "subj", "body", 1), // invalid from
                Arguments.of("from", listOf("to"), "", "", 4), // from, to, subject, body invalid
            )
        }
    }

}