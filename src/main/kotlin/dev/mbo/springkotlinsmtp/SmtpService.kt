package dev.mbo.springkotlinsmtp

import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
open class SmtpService(
    private val sender: SmtpSenderComponent,
) {

    @Async
    @Throws(MailException::class)
    open fun sendAsync(
        mail: MailDto
    ): CompletableFuture<Void> {
        return try {
            sender.send(mail)
            CompletableFuture.completedFuture(null)
        } catch (ex: Exception) {
            val failed = CompletableFuture<Void>()
            failed.completeExceptionally(ex)
            failed
        }
    }

    @Throws(MailException::class)
    open fun send(
        mail: MailDto
    ) {
        sender.send(mail)
    }

}