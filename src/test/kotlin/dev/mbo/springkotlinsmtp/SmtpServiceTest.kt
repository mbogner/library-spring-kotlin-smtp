package dev.mbo.springkotlinsmtp

import dev.mbo.springkotlinsmtp.TestcontainersConfiguration.Companion.CONTAINER_PORT_HTTP
import io.restassured.RestAssured.given
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.GenericContainer

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@ActiveProfiles(value = ["test"])
class SmtpServiceTest @Autowired constructor(
    private val smtpService: SmtpService,
    @Qualifier("mailhog") private val mailhogContainer: GenericContainer<*>,
) {

    @Suppress("HttpUrlsUsage")
    private val mailhogRequest: RequestSpecification = given()
        .baseUri("http://${mailhogContainer.host}")
        .port(mailhogContainer.getMappedPort(CONTAINER_PORT_HTTP))
        .basePath("/api/v2")

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

        mailhogRequest.`when`().get("/messages")
            .then().body("total", equalTo(1))
    }
}