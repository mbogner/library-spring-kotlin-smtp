package dev.mbo.springkotlinsmtp

import dev.mbo.logging.logger
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*

@Service
open class SmtpService(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val sender: JavaMailSender, // enabled by spring.mail.host
    @Value("\${smtp.enabled:true}")
    private val enabled: Boolean,
    @Value("\${spring.mail.properties.mail.default-from}")
    private val defaultFrom: String,
    private val validator: Validator,
) {

    private val log = logger()

    @Async
    @Throws(MailException::class)
    open fun send(
        mail: MailDto
    ) {
        validateMail(mail)
        if (enabled) {
            log.debug(
                "sending mail {}",
                mail,
            )
            val mm = sender.createMimeMessage()
            val message = MimeMessageHelper(mm, false, StandardCharsets.UTF_8.displayName())

            message.setFrom(mail.from ?: defaultFrom)

            message.setTo(mail.to.toTypedArray())
            message.setCc(mail.cc.toTypedArray())
            message.setBcc(mail.bcc.toTypedArray())

            message.setSubject(mail.subject)
            message.setText(mail.text)

            sender.send(mm)
        } else {
            log.warn("mail not enabled: {}", mail)
        }
    }

    private fun validateMail(mail: MailDto) {
        val violations = validator.validate(mail)
        require(violations.isEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

}