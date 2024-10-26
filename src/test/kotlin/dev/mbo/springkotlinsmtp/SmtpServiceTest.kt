package dev.mbo.springkotlinsmtp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SmtpServiceTest @Autowired constructor(
    private val smtpService: SmtpService,
) {

    @Test
    fun send() {
        assertThat(smtpService).isNotNull
    }
}