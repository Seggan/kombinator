plugins {
    kotlin("jvm") version "2.2.20"
}

group = "io.github.seggan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/src")
        }
    }
}

tasks.compileKotlin {
    dependsOn(generateTuples, generateThenCombinators)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

val maxArity = 32
val generateTuples by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/src/io/github/seggan/kombinator/tuples")
    doLast {
        val outputDirFile = outputDir.get().asFile
        outputDirFile.mkdirs()
        for (arity in 2..maxArity) {
            val file = outputDirFile.resolve("Tuple$arity.kt")
            file.writeText(buildString {
                appendLine("package io.github.seggan.kombinator.tuples")
                appendLine()
                append("data class Tuple$arity<")
                append((1..arity).joinToString(", ") { "T$it" })
                appendLine(">(")
                append((1..arity).joinToString(",\n") { "    val item$it: T$it" })
                appendLine()
                appendLine(")")
            })
        }
    }
}

val generateThenCombinators by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/src/io/github/seggan/kombinator/parser")
    doLast {
        val outputDirFile = outputDir.get().asFile
        outputDirFile.mkdirs()
        val outputFile = outputDirFile.resolve("ThenCombinators.kt")
        // @formatter:off
        outputFile.writeText(buildString {
            appendLine("package io.github.seggan.kombinator.parser")
            appendLine()
            appendLine("import io.github.seggan.kombinator.tuples.*")
            appendLine()
            appendLine("""
                @JvmName("then1")
                infix fun <TokenType : Any, AstType1, AstType2> ParserNode<TokenType, AstType1>.then(
                    other: ParserNode<TokenType, AstType2>
                ) = ParserNode { ctx ->
                    val first = this@then.parse(ctx)
                    val second = other.parse(ctx)
                    Tuple2(first, second)
                }
                
                @JvmName("unitThen1")
                infix fun <TokenType : Any, AstType1> ParserNode<TokenType, AstType1>.then(
                    other: ParserNode<TokenType, Unit>
                ) = ParserNode { ctx ->
                    val first = this@then.parse(ctx)
                    other.parse(ctx)
                    first
                }
                
                @JvmName("reversedUnitThen1")
                infix fun <TokenType : Any, AstType2> ParserNode<TokenType, Unit>.then(
                    other: ParserNode<TokenType, AstType2>
                ) = ParserNode { ctx ->
                    this@then.parse(ctx)
                    other.parse(ctx)
                }
                
                @JvmName("bothUnitThen1")
                infix fun <TokenType : Any> ParserNode<TokenType, Unit>.then(
                    other: ParserNode<TokenType, Unit>
                ) = ParserNode { ctx ->
                    this@then.parse(ctx)
                    other.parse(ctx)
                }
            """.trimIndent())
            for (arity in 2 until maxArity) {
                appendLine("""
                    @JvmName("then$arity")
                    infix fun <TokenType : Any, ${ (1..arity + 1).joinToString(", ") { "AstType$it" } }> ParserNode<TokenType, Tuple$arity<${ (1..arity).joinToString(", ") {"AstType$it" } }>>.then(
                        other: ParserNode<TokenType, AstType${arity + 1}>
                    ) = ParserNode { ctx ->
                        val first = this@then.parse(ctx)
                        val second = other.parse(ctx)
                        Tuple${arity + 1}(${ (1..arity).joinToString(", ") { "first.item$it" } }, second)
                    }
                """.trimIndent())

                appendLine(
                    """
                    @JvmName("unitThen$arity")
                    infix fun <TokenType : Any, ${ (1..arity).joinToString(", ") { "AstType$it" } }> ParserNode<TokenType, Tuple$arity<${ (1..arity).joinToString(", ") {"AstType$it" } }>>.then(
                        other: ParserNode<TokenType, Unit>
                    ) = ParserNode { ctx ->
                        val first = this@then.parse(ctx)
                        other.parse(ctx)
                        first
                    }
                """.trimIndent())
            }
        })
        // @formatter:on
    }
}