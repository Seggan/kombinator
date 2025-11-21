@file:Suppress("unused")

package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.lexer.Token

fun interface ParserNode<TokenType : Any, out AstType> {
    fun parse(ctx: ParseContext<TokenType>): AstType
}

data class NodeSpan<TokenType : Any>(val startToken: Token<TokenType>, val endToken: Token<TokenType>)

inline infix fun <TokenType : Any, AstType1, AstType2> ParserNode<TokenType, AstType1>.map(
    crossinline transform: NodeSpan<TokenType>.(AstType1) -> AstType2
) = ParserNode { ctx ->
    val startToken = ctx.tokens[ctx.index]
    val result = this@map.parse(ctx)
    val span = NodeSpan(startToken, ctx.tokens[ctx.index - 1])
    span.transform(result)
}

inline infix fun <TokenType : Any, AstType> ParserNode<TokenType, Iterable<AstType>>.reduce(
    crossinline reduction: (acc: AstType, AstType) -> AstType
) = map { it.reduce(reduction) }

fun <TokenType : Any> skip() = ParserNode<TokenType, Unit> { ctx -> ctx.index++ }

fun <TokenType : Any> skip(node: ParserNode<TokenType, *>) = ParserNode<_, Unit> { ctx -> node.parse(ctx) }

operator fun <TokenType : Any> ParserNode<TokenType, *>.unaryMinus() = skip(this)

fun <TokenType : Any, AstType> just(value: AstType) = ParserNode<TokenType, _> { value }

infix fun <TokenType : Any, AstType> ParserNode<TokenType, AstType>.or(
    other: ParserNode<TokenType, AstType>
) = ParserNode { ctx ->
    val initialIndex = ctx.index
    try {
        this@or.parse(ctx)
    } catch (_: ParseAbort) {
        ctx.index = initialIndex
        other.parse(ctx)
    }
}

fun <TokenType : Any, AstType> optional(
    node: ParserNode<TokenType, AstType>
) = ParserNode { ctx ->
    val initialIndex = ctx.index
    try {
        node.parse(ctx)
    } catch (_: ParseAbort) {
        ctx.index = initialIndex
        null
    }
}

infix fun <TokenType : Any, AstType> ParserNode<TokenType, AstType?>.orElse(
    other: ParserNode<TokenType, AstType>
) = ParserNode { ctx -> this@orElse.parse(ctx) ?: other.parse(ctx) }

fun <TokenType : Any, AstType> zeroOrMore(
    node: ParserNode<TokenType, AstType>
) = ParserNode<TokenType, List<AstType>> { ctx ->
    val results = mutableListOf<AstType>()
    while (true) {
        val initialIndex = ctx.index
        try {
            results.add(node.parse(ctx))
        } catch (_: ParseAbort) {
            ctx.index = initialIndex
            break
        }
    }
    results
}

fun <TokenType : Any, AstType> oneOrMore(
    node: ParserNode<TokenType, AstType>
) = ParserNode<TokenType, List<AstType>> { ctx ->
    val results = mutableListOf(node.parse(ctx))
    while (true) {
        val initialIndex = ctx.index
        try {
            results.add(node.parse(ctx))
        } catch (_: ParseAbort) {
            ctx.index = initialIndex
            break
        }
    }
    results
}

infix fun <TokenType : Any, AstType, SeparatorType> ParserNode<TokenType, AstType>.separatedBy(
    separator: ParserNode<TokenType, SeparatorType>
) = ParserNode<TokenType, List<AstType>> { ctx ->
    val results = mutableListOf<AstType>()
    val initialIndex = ctx.index
    try {
        results.add(this@separatedBy.parse(ctx))
    } catch (_: ParseAbort) {
        ctx.index = initialIndex
        return@ParserNode results
    }
    while (true) {
        val initialIndex = ctx.index
        try {
            separator.parse(ctx)
            results.add(this@separatedBy.parse(ctx))
        } catch (_: ParseAbort) {
            ctx.index = initialIndex
            break
        }
    }
    results
}

inline fun <TokenType : Any, AstType, OpType> leftRecursive(
    expr: ParserNode<TokenType, AstType>,
    op: ParserNode<TokenType, OpType>,
    crossinline reduction: (acc: AstType, OpType, AstType) -> AstType
) = ParserNode { ctx ->
    var result = expr.parse(ctx)
    while (true) {
        val initialIndex = ctx.index
        val opResult = try {
            op.parse(ctx)
        } catch (_: ParseAbort) {
            ctx.index = initialIndex
            break
        }
        result = reduction(result, opResult, expr.parse(ctx))
    }
    result
}