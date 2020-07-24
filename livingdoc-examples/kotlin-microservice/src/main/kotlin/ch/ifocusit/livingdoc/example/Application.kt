package ch.ifocusit.livingdoc.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinMavenApplication

fun main(args: Array<String>) {
	runApplication<KotlinMavenApplication>(*args)
}
