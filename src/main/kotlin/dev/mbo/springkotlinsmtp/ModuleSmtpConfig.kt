package dev.mbo.springkotlinsmtp

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@Configuration
@ComponentScan(basePackageClasses = [ModuleSmtpConfig::class])
internal open class ModuleSmtpConfig