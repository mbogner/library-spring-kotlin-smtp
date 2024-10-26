package dev.mbo.springkotlinsmtp

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@TestConfiguration(proxyBeanMethods = false)
open class TestcontainersConfiguration {

    companion object {
        const val CONTAINER_PORT_SMTP = 1025
        const val CONTAINER_PORT_HTTP = 8025

        private val mailhogContainer: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("mailhog/mailhog:latest"))
                .withExposedPorts(CONTAINER_PORT_SMTP, CONTAINER_PORT_HTTP)
                .waitingFor(
                    Wait.forHttp("/api/v2/messages")
                        .forPort(CONTAINER_PORT_HTTP)
                        .withStartupTimeout(Duration.ofSeconds(30))
                )
                .waitingFor(Wait.forListeningPorts(CONTAINER_PORT_SMTP))

        init {
            mailhogContainer.start()
            System.setProperty("spring.mail.host", mailhogContainer.host)
            System.setProperty("spring.mail.port", mailhogContainer.getMappedPort(CONTAINER_PORT_SMTP).toString())
        }
    }

    @Bean
    @Qualifier("mailhog")
    open fun mailhogContainer(): GenericContainer<*> = mailhogContainer

}