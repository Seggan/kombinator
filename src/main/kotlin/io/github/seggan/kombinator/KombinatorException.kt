package io.github.seggan.kombinator

open class KombinatorException(
    message: String?,
    val backtrace: MutableList<Span> = mutableListOf(),
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    constructor(message: String?, span: Span) : this(message, mutableListOf(span), null)

    open fun report(): String {
        if (backtrace.isEmpty()) return "Error: ${message ?: "Unknown error"}"
        val mainSb = StringBuilder("Error ")
        var first = true
        for (span in backtrace) {
            mainSb.append("in ")
                .append(span.source.name)
                .append(':')
                .append(span.line)
                .append(':')
                .append(span.col)
                .append(": ")
            if (first) {
                mainSb.appendLine(message ?: "Unknown error")
                first = false
            } else {
                mainSb.appendLine()
            }
            mainSb.appendLine()
            mainSb.appendLine(span.fancyToString())
        }
        return mainSb.toString()
    }

    fun addStackFrame(span: Span) {
        backtrace.add(span)
    }
}

class CompositeKombinatorException(
    val exceptions: List<KombinatorException>,
    cause: Throwable? = null
) : KombinatorException(null, mutableListOf(), cause) {

    override fun report(): String {
        val mainSb = StringBuilder()
        if (message != null) {
            mainSb.appendLine("Multiple errors occurred:")
        }
        for (exception in exceptions) {
            mainSb.appendLine(exception.report())
            mainSb.appendLine()
        }
        return mainSb.toString()
    }
}