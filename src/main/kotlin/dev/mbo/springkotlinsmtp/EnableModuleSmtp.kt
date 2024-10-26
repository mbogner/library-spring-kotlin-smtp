package dev.mbo.springkotlinsmtp

import org.springframework.context.annotation.Import

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Import(ModuleSmtpConfig::class)
annotation class EnableModuleSmtp
