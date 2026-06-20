package io.github.seggan.kombinator.lexer

import io.github.seggan.kombinator.CodeSource
import io.github.seggan.kombinator.Span
import io.github.seggan.kombinator.StringView
import org.intellij.lang.annotations.Language
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class Lexer<TokenType : Any> {

    open val errorToken: TokenType? = null

    abstract val eofToken: TokenType

    private val matchers = mutableSetOf<TokenMatcher<TokenType>>()

    fun lex(source: CodeSource): List<Token<TokenType>> {
        var code: CharSequence = StringView(source.text)
        val tokens = mutableListOf<Token<TokenType>>()
        var pos = 0
        while (code.isNotEmpty()) {
            var longestMatch: Token<TokenType>? = null
            for (matcher in matchers) {
                val matchLength = matcher.match(code)
                if (matchLength != 0 && (longestMatch == null || matchLength >= longestMatch.span.length)) {
                    longestMatch = Token(
                        matcher.type,
                        Span(pos, pos + matchLength, source)
                    )
                }
            }
            if (longestMatch != null) {
                tokens.add(longestMatch)
                val length = longestMatch.span.length
                code = code.drop(length)
                pos += length
            } else if (errorToken == null) {
                throw LexException(
                    "Unexpected character: '${code[0]}'",
                    Span(pos, pos + 1, source)
                )
            } else {
                // No match, skip one character
                tokens.add(
                    Token(
                        type = errorToken!!,
                        span = Span(pos, pos + 1, source)
                    )
                )
                code = code.drop(1)
                pos++
            }
        }
        return tokens + Token(
            type = eofToken,
            span = Span(pos, pos, source)
        )
    }

    private inner class TokenMatcherProvider(private val matcher: TokenMatcher<TokenType>) :
        PropertyDelegateProvider<Lexer<TokenType>, ReadOnlyProperty<Lexer<TokenType>, TokenMatcher<TokenType>>> {
        override fun provideDelegate(
            thisRef: Lexer<TokenType>,
            property: KProperty<*>
        ): ReadOnlyProperty<Lexer<TokenType>, TokenMatcher<TokenType>> {
            matchers.add(matcher)
            return ReadOnlyProperty { _, _ -> matcher }
        }
    }

    protected fun literal(
        literal: String,
        type: TokenType
    ): PropertyDelegateProvider<Lexer<TokenType>, ReadOnlyProperty<Lexer<TokenType>, TokenMatcher<TokenType>>> {
        return TokenMatcherProvider(TokenMatcher.Literal(type, literal))
    }

    protected fun regex(
        @Language("RegExp") pattern: String,
        type: TokenType,
        vararg options: RegexOption
    ): PropertyDelegateProvider<Lexer<TokenType>, ReadOnlyProperty<Lexer<TokenType>, TokenMatcher<TokenType>>> {
        return TokenMatcherProvider(TokenMatcher.Regex(type, pattern.toRegex(options.toSet())))
    }

    protected fun cString(type: TokenType): PropertyDelegateProvider<Lexer<TokenType>, ReadOnlyProperty<Lexer<TokenType>, TokenMatcher<TokenType>>> {
        return TokenMatcherProvider(TokenMatcher.CString(type))
    }
}