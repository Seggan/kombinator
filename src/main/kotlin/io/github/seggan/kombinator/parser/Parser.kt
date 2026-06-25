package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.CodeSource
import io.github.seggan.kombinator.lexer.Token
import kotlin.reflect.KProperty

abstract class Parser<TokenType : Any, AstType> {
    abstract val rootParser: ParserNode<TokenType, AstType>
    abstract val eofToken: TokenType

    var debugMode: Boolean = false

    fun parse(tokens: List<Token<TokenType>>, source: CodeSource): AstType {
        val ctx = ParseContext(0, tokens, source)
        try {
            return (rootParser then EofNode()).parse(ctx)
        } catch (_: ParseAbort) {
            throw createParseError(ctx)
        }
    }

    private inner class EofNode : ParserNode<TokenType, Unit> {
        override fun parse(ctx: ParseContext<TokenType>) {
            val token = ctx.tokens[ctx.index]
            if (token.type != eofToken) {
                ctx.error(
                    ParsingError(
                        found = token.type,
                        expected = setOf(eofToken),
                        span = token.span
                    )
                )
            }
        }
    }

    private fun createParseError(ctx: ParseContext<TokenType>): ParseException {
        val maxIndex = ctx.errors.maxOf { it.span.start }
        val error = ctx.errors.filter { it.span.start == maxIndex }
            .reduce { acc, err ->
                ParsingError(
                    found = err.found,
                    expected = acc.expected + err.expected,
                    span = err.span
                )
            }
        return if (error.found == eofToken) {
            ParseException(
                message = "Unexpected end of input, expected ${error.expected.joinToString(" or ")}",
                span = error.span
            )
        } else {
            ParseException(
                message = "Expected ${error.expected.joinToString(" or ")}, but found ${error.found}",
                span = error.span
            )
        }
    }

    protected inline fun <AstType> ref(crossinline block: () -> ParserNode<TokenType, AstType>) =
        object : ParserNode<TokenType, AstType> {
            private val node by lazy { block() }
            override fun parse(ctx: ParseContext<TokenType>): AstType = node.parse(ctx)
        }

    private var indent = 0

    protected infix fun <AstType> ParserNode<TokenType, AstType>.named(name: String) =
        ParserNode { ctx ->
            fun stringifyToken(token: Token<TokenType>?): String {
                return if (token == null) {
                    "null"
                } else {
                    "${token.type} '${token.span.text}'"
                }
            }
            if (debugMode) {
                println("${"  ".repeat(indent++)}Entering $name at index ${ctx.index} (${stringifyToken(
                    ctx.tokens.getOrNull(ctx.index)
                )})")
            }
            try {
                this@named.parse(ctx)
            } catch (e: ParseAbort) {
                if (debugMode) {
                    val error = createParseError(ctx)
                    println("${"  ".repeat(indent)}Failed $name at index ${ctx.index} (${stringifyToken(
                        ctx.tokens.getOrNull(ctx.index)
                    )}): ${error.message}")
                }
                throw e
            } finally {
                if (debugMode) {
                    println("${"  ".repeat(--indent)}Exiting $name")
                }
            }
        }

    protected operator fun <SubType> ParserNode<TokenType, SubType>.getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) : ParserNode<TokenType, SubType> = this named property.name
}
