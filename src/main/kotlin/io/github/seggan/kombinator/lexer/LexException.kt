package io.github.seggan.kombinator.lexer

import io.github.seggan.kombinator.KombinatorException
import io.github.seggan.kombinator.Span

class LexException(message: String, span: Span) : KombinatorException(message, mutableListOf(span))