package dev.mbo.springkotlinsmtp

import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.ExecutionException

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@ActiveProfiles("test")
class SmtpServiceFailureTest {

    @MockitoBean
    lateinit var javaMailSender: JavaMailSender

    @Autowired
    lateinit var smtpService: SmtpService

    @Test
    fun send_shouldFailOnSmtpError() {
        val mimeMessage = mock(MimeMessage::class.java)
        `when`(javaMailSender.createMimeMessage()).thenReturn(mimeMessage)
        doThrow(MailSendException("SMTP failure")).`when`(javaMailSender).send(mimeMessage)

        val mail = MailDto(
            to = listOf("to@example.com"),
            subject = "subject",
            text = "text"
        )

        assertThrows<MailSendException> {
            smtpService.send(mail)
        }
    }

    @Test
    fun sendAsync_shouldFailOnSmtpError() {
        val mimeMessage = mock(MimeMessage::class.java)
        `when`(javaMailSender.createMimeMessage()).thenReturn(mimeMessage)
        doThrow(MailSendException("SMTP failure")).`when`(javaMailSender).send(mimeMessage)

        val mail = MailDto(
            to = listOf("to@example.com"),
            subject = "subject",
            text = "text"
        )

        val future = smtpService.sendAsync(mail)

        val ex = assertThrows<ExecutionException> {
            future.get()
        }
        assert(ex.cause is MailSendException)
    }
}