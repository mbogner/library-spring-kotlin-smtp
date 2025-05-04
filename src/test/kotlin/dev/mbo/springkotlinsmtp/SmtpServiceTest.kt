package dev.mbo.springkotlinsmtp

import dev.mbo.springkotlinsmtp.TestcontainersConfiguration.Companion.CONTAINER_PORT_HTTP
import io.restassured.RestAssured.given
import io.restassured.specification.RequestSpecification
import jakarta.mail.internet.MimeMessage
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.GenericContainer
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@ActiveProfiles("test")
class SmtpServiceTest @Autowired constructor(
    private val smtpService: SmtpService,
    @Qualifier("mailpit") private val mailpitContainer: GenericContainer<*>,
    private val validator: Validator,
) {

    @Suppress("HttpUrlsUsage")
    private val mailpitRequest: RequestSpecification = given()
        .baseUri("http://${mailpitContainer.host}")
        .port(mailpitContainer.getMappedPort(CONTAINER_PORT_HTTP))
        .basePath("/api/v1")

    @AfterEach
    fun cleanupMailpit() {
        mailpitRequest.`when`().delete("/messages")
            .then().statusCode(200)
    }

    @Test
    fun send() {
        smtpService.send(
            MailDto(
                from = "from@example.com",
                to = listOf("to@example.com"),
                subject = "subj",
                text = "body"
            )
        )

        mailpitRequest.`when`().get("/messages")
            .then().body("total", equalTo(1))
    }

    @Test
    fun sendAsync() {
        val future = smtpService.sendAsync(
            MailDto(
                from = "from@example.com",
                to = listOf("to@example.com"),
                subject = "subj",
                text = "body"
            )
        )

        future.get(5, TimeUnit.SECONDS)

        mailpitRequest.`when`().get("/messages")
            .then().body("total", equalTo(1))
    }

    @Test
    fun sendBadMail() {
        assertThrows(ConstraintViolationException::class.java) {
            smtpService.send(
                MailDto(
                    from = "from@example.com",
                    subject = "subj",
                    text = "body"
                )
            )
        }
    }

    @Test
    fun sendBadMailAsync() {
        val future = smtpService.sendAsync(
            MailDto(
                from = "from@example.com",
                subject = "subj",
                text = "body"
            )
        )
        val ex = assertThrows(ExecutionException::class.java) {
            future.get() // this will rethrow the original exception wrapped in ExecutionException
        }
        assert(ex.cause is ConstraintViolationException)
    }

    @Test
    fun sendMail_smtpFailure() {
        val mockSender = Mockito.mock(JavaMailSender::class.java)
        val mimeMessage = Mockito.mock(MimeMessage::class.java)
        Mockito.`when`(mockSender.createMimeMessage()).thenReturn(mimeMessage)
        Mockito.doThrow(MailSendException("Simulated SMTP failure"))
            .`when`(mockSender).send(mimeMessage)

        val smtpSenderComponent = SmtpSenderComponent(
            sender = mockSender,
            enabled = true,
            defaultFrom = "default@example.com",
            validator = validator
        )

        val mail = MailDto(
            to = listOf("to@example.com"),
            subject = "subj",
            text = "body"
        )

        assertThrows<MailSendException> {
            smtpSenderComponent.send(mail)
        }
    }
}