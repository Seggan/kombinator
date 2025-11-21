package io.github.seggan.kombinator.lexer

import io.github.seggan.kombinator.parser.ParseContext
import io.github.seggan.kombinator.parser.ParserNode
import io.github.seggan.kombinator.parser.ParsingError

interface TokenMatcher<TokenType : Any> : ParserNode<TokenType, Token<TokenType>> {

    val type: TokenType

    fun match(input: CharSequence): Int

    override fun parse(ctx: ParseContext<TokenType>): Token<TokenType> {
        val token = ctx.tokens[ctx.index]
        if (token.type != type) {
            ctx.error(ParsingError(
                found = token.type,
                expected = setOf(type),
                span = token.span
            ))
        }
        ctx.index++
        return token
    }

    class Literal<T : Any>(override val type: T, private val literal: String) : TokenMatcher<T> {
        override fun match(input: CharSequence): Int {
            return if (input.startsWith(literal)) literal.length else 0
        }
    }

    class Regex<T : Any>(override val type: T, pattern: kotlin.text.Regex) : TokenMatcher<T> {
        private val pattern = "^${pattern.pattern}".toRegex(pattern.options)
        override fun match(input: CharSequence): Int {
            return pattern.find(input)?.value?.length ?: 0
        }
    }

    class CString<T : Any>(override val type: T) : TokenMatcher<T> {
        override fun match(input: CharSequence): Int {
            if (input.isEmpty() || input[0] != '"') return 0
            var i = 1
            while (i < input.length) {
                when (input[i]) {
                    '"' -> return i + 1
                    '\\' -> i++
                }
                i++
            }
            return 0
        }
    }
}