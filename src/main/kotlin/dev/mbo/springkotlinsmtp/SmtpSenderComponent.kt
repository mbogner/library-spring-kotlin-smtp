package dev.mbo.springkotlinsmtp

import dev.mbo.logging.logger
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class SmtpSenderComponent(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val sender: JavaMailSender, // enabled by spring.mail.host
    @Value("\${smtp.enabled:true}")
    private val enabled: Boolean,
    @Value("\${spring.mail.properties.mail.default-from}")
    private val defaultFrom: String,
    private val validator: Validator,
) {

    private val log = logger()

    fun send(mail: MailDto) {
        MDC.put("mail.id", mail.id.toString())
        try {
            validateMail(mail)
            if (enabled) {
                log.debug("Sending email to={}, subject={}", mail.to, mail.subject)
                log.trace("mail body:\n{}", mail.text)
                val mm = sender.createMimeMessage()
                val message = MimeMessageHelper(mm, false, StandardCharsets.UTF_8.displayName())

                message.setFrom(mail.from ?: defaultFrom)

                message.setTo(mail.to.toTypedArray())
                message.setCc(mail.cc.toTypedArray())
                message.setBcc(mail.bcc.toTypedArray())

                message.setSubject(mail.subject)
                message.setText(mail.text, mail.isHtml)

                sender.send(mm)
            } else {
                log.warn("mail not enabled: {}", mail)
            }
        } finally {
            MDC.remove("mail.id")
        }
    }

    private fun validateMail(mail: MailDto) {
        val violations = validator.validate(mail)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

}