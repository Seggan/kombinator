package io.github.seggan.kombinator.lexer

enum class TestTokenType(private val humanName: String) {
    WHITESPACE("whitespace"),
    COMMENT("a comment"),

    IDENTIFIER("an identifier"),
    NUMBER("a number"),

    OPEN_PARENTHESIS("'('"),
    CLOSE_PARENTHESIS("')'"),
    OPEN_BRACE("'{'"),
    CLOSE_BRACE("'}'"),
    OPEN_BRACKET("'['"),
    CLOSE_BRACKET("']'"),

    COMMA("','"),
    SEMICOLON("';'"),
    COLON("':'"),
    SINGLE_EQUALS("'='"),
    DOUBLE_EQUALS("'=='"),
    NOT_EQUALS("'!='"),
    LESS_THAN("'<'"),
    GREATER_THAN("'>'"),
    LESS_THAN_OR_EQUAL("'<='"),
    GREATER_THAN_OR_EQUAL("'>='"),
    PLUS("'+'"),
    MINUS("'-'"),
    STAR("'*'"),
    SLASH("'/'"),
    PERCENT("'%'"),
    EXCLAMATION("'!'"),
    AMPERSAND("'&'"),
    DOUBLE_AMPERSAND("'&&'"),
    PIPE("'|'"),
    DOUBLE_PIPE("'||'"),
    CARET("'^'"),
    TILDE("'~'"),

    FUN("'fun'"),
    RETURN("'return'"),
    IF("'if'"),
    ELSE("'else'"),
    WHILE("'while'"),
    FOR("'for'"),
    BREAK("'break'"),
    CONTINUE("'continue'"),
    TRUE("'true'"),
    FALSE("'false'"),
    NULL("'null'"),
    LET("'let'"),
    MUT("'mut'"),

    EOF("end of file");

    override fun toString(): String = humanName
}