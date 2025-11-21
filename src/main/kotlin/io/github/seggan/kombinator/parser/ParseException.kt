package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.KombinatorException
import io.github.seggan.kombinator.Span

class ParseException(message: String, span: Span) : KombinatorException(message, mutableListOf(span))

class ParseAbort : Exception()