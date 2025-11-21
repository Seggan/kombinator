package io.github.seggan.kombinator.lexer

object TestLexer : Lexer<TestTokenType>() {

    val WHITESPACE by regex("""\s+""", TestTokenType.WHITESPACE)

    val COMMENT by regex("""//.*""", TestTokenType.COMMENT)
    val BLOCK_COMMENT by regex("""/\*.*?\*/""", TestTokenType.COMMENT, RegexOption.DOT_MATCHES_ALL)

    val IDENTIFIER by regex("""[a-zA-Z_][a-zA-Z0-9_]*""", TestTokenType.IDENTIFIER)
    val NUMBER by regex("""\d+(\.\d+)?""", TestTokenType.NUMBER)

    val OPEN_PARENTHESIS by literal("(", TestTokenType.OPEN_PARENTHESIS)
    val CLOSE_PARENTHESIS by literal(")", TestTokenType.CLOSE_PARENTHESIS)
    val OPEN_BRACE by literal("{", TestTokenType.OPEN_BRACE)
    val CLOSE_BRACE by literal("}", TestTokenType.CLOSE_BRACE)
    val OPEN_BRACKET by literal("[", TestTokenType.OPEN_BRACKET)
    val CLOSE_BRACKET by literal("]", TestTokenType.CLOSE_BRACKET)

    val COMMA by literal(",", TestTokenType.COMMA)
    val SEMICOLON by literal(";", TestTokenType.SEMICOLON)
    val COLON by literal(":", TestTokenType.COLON)
    val SINGLE_EQUALS by literal("=", TestTokenType.SINGLE_EQUALS)
    val DOUBLE_EQUALS by literal("==", TestTokenType.DOUBLE_EQUALS)
    val NOT_EQUALS by literal("!=", TestTokenType.NOT_EQUALS)
    val LESS_THAN by literal("<", TestTokenType.LESS_THAN)
    val GREATER_THAN by literal(">", TestTokenType.GREATER_THAN)
    val LESS_THAN_OR_EQUAL by literal("<=", TestTokenType.LESS_THAN_OR_EQUAL)
    val GREATER_THAN_OR_EQUAL by literal(">=", TestTokenType.GREATER_THAN_OR_EQUAL)
    val PLUS by literal("+", TestTokenType.PLUS)
    val MINUS by literal("-", TestTokenType.MINUS)
    val STAR by literal("*", TestTokenType.STAR)
    val SLASH by literal("/", TestTokenType.SLASH)
    val PERCENT by literal("%", TestTokenType.PERCENT)
    val EXCLAMATION by literal("!", TestTokenType.EXCLAMATION)
    val AMPERSAND by literal("&", TestTokenType.AMPERSAND)
    val DOUBLE_AMPERSAND by literal("&&", TestTokenType.DOUBLE_AMPERSAND)
    val PIPE by literal("|", TestTokenType.PIPE)
    val DOUBLE_PIPE by literal("||", TestTokenType.DOUBLE_PIPE)
    val CARET by literal("^", TestTokenType.CARET)
    val TILDE by literal("~", TestTokenType.TILDE)

    val FUN by literal("fun", TestTokenType.FUN)
    val RETURN by literal("return", TestTokenType.RETURN)
    val IF by literal("if", TestTokenType.IF)
    val ELSE by literal("else", TestTokenType.ELSE)
    val WHILE by literal("while", TestTokenType.WHILE)
    val FOR by literal("for", TestTokenType.FOR)
    val BREAK by literal("break", TestTokenType.BREAK)
    val CONTINUE by literal("continue", TestTokenType.CONTINUE)
    val TRUE by literal("true", TestTokenType.TRUE)
    val FALSE by literal("false", TestTokenType.FALSE)
    val NULL by literal("null", TestTokenType.NULL)
    val LET by literal("let", TestTokenType.LET)
    val MUT by literal("mut", TestTokenType.MUT)

    override val eofToken = TestTokenType.EOF
}