package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.Span

data class ParsingError<TokenType : Any>(
    val found: TokenType,
    val expected: Set<TokenType>,
    val span: Span
)