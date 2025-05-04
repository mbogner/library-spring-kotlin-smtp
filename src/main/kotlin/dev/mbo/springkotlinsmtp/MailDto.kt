package dev.mbo.springkotlinsmtp

import com.fasterxml.jackson.annotation.JsonProperty
import dev.mbo.springkotlinvalidation.Emails
import dev.mbo.springkotlinvalidation.OneNotEmpty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.Locale
import java.util.UUID

@OneNotEmpty(
    fields = [
        "to", "cc", "bcc"
    ]
)
data class MailDto(
    @field:JsonProperty("id")
    val id: UUID = UUID.randomUUID(),

    @field:JsonProperty("snd")
    @field:Email
    val from: String? = null,

    @field:JsonProperty("to")
    @field:Emails
    val to: List<String> = emptyList(),
    @field:JsonProperty("cc")
    @field:Emails
    val cc: List<String> = emptyList(),
    @field:JsonProperty("bcc")
    @field:Emails
    val bcc: List<String> = emptyList(),

    @field:JsonProperty("lng")
    @field:NotNull
    val locale: Locale = Locale.ENGLISH,

    @field:JsonProperty("sbj")
    @field:NotBlank
    val subject: String,
    @field:JsonProperty("txt")
    @field:NotBlank
    val text: String,

    @field:JsonProperty("ihy")
    val isHtml: Boolean = false,
)