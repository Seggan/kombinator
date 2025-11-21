package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.CodeSource
import io.github.seggan.kombinator.lexer.Token

class ParseContext<TokenType : Any>(
    var index: Int,
    val tokens: List<Token<TokenType>>,
    val source: CodeSource,
    internal val errors: MutableList<ParsingError<TokenType>> = mutableListOf()
) {
    fun error(vararg errors: ParsingError<TokenType>): Nothing {
        this.errors.addAll(errors)
        throw ParseAbort()
    }
}