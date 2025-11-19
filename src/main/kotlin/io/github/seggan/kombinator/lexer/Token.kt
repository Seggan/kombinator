package io.github.seggan.kombinator.lexer

import io.github.seggan.kombinator.Span

data class Token<TokenType : Any>(val type: TokenType, val span: Span)
