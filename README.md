# spring-kotlin-smtp

Mail sending API for Kotlin and Spring Boot.

## Release

see https://github.com/mbogner/spring-boot-bom for more details.

### Build

Local:
```shell
./gradlew clean signMavenPublication publishToMavenLocal
```

see `~/.m2/repository/dev/mbo/spring-kotlin-smtp` for the created content

Release:
```shell
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

All in One:
```shell
./gradlew clean signMavenPublication publishToMavenLocal publishToSonatype closeAndReleaseSonatypeStagingRepository
```

By running this you don't need to use the web interface to close and release the library.

see https://s01.oss.sonatype.org/content/groups/public/dev/mbo/

### Web Process

https://s01.oss.sonatype.org