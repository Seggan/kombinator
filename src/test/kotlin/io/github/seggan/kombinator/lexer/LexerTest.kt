package io.github.seggan.kombinator.lexer

import io.github.seggan.kombinator.CodeSource
import kotlin.test.Test
import kotlin.test.assertEquals

object LexerTest {

    private val CODE = """
        fun main(args: String[]): Int {
            // This is a comment
            /*
             This is a block comment
            */
            let mut x = 10;
            let y = x + 5;
            if (x >= 10) {
                return x + 1;
            } else {
                return x - 1;
            }
        }
    """.trimIndent()

    @Test
    fun testLexing() {
        val tokens = TestLexer.lex(CodeSource.constant("Test.tst", CODE))
            .map { it.type }
            .filterNot { it == TestTokenType.WHITESPACE }
        assertEquals(
            tokens,
            // @formatter:off
            listOf(
                TestTokenType.FUN, TestTokenType.IDENTIFIER, TestTokenType.OPEN_PARENTHESIS, TestTokenType.IDENTIFIER, TestTokenType.COLON, TestTokenType.IDENTIFIER, TestTokenType.OPEN_BRACKET, TestTokenType.CLOSE_BRACKET, TestTokenType.CLOSE_PARENTHESIS, TestTokenType.COLON, TestTokenType.IDENTIFIER, TestTokenType.OPEN_BRACE,
                TestTokenType.COMMENT,
                TestTokenType.COMMENT,
                TestTokenType.LET, TestTokenType.MUT, TestTokenType.IDENTIFIER, TestTokenType.SINGLE_EQUALS, TestTokenType.NUMBER, TestTokenType.SEMICOLON,
                TestTokenType.LET, TestTokenType.IDENTIFIER, TestTokenType.SINGLE_EQUALS, TestTokenType.IDENTIFIER, TestTokenType.PLUS, TestTokenType.NUMBER, TestTokenType.SEMICOLON,
                TestTokenType.IF, TestTokenType.OPEN_PARENTHESIS, TestTokenType.IDENTIFIER, TestTokenType.GREATER_THAN_OR_EQUAL, TestTokenType.NUMBER, TestTokenType.CLOSE_PARENTHESIS, TestTokenType.OPEN_BRACE,
                TestTokenType.RETURN, TestTokenType.IDENTIFIER, TestTokenType.PLUS, TestTokenType.NUMBER, TestTokenType.SEMICOLON,
                TestTokenType.CLOSE_BRACE, TestTokenType.ELSE, TestTokenType.OPEN_BRACE,
                TestTokenType.RETURN, TestTokenType.IDENTIFIER, TestTokenType.MINUS, TestTokenType.NUMBER, TestTokenType.SEMICOLON,
                TestTokenType.CLOSE_BRACE,
                TestTokenType.CLOSE_BRACE
            )
            // @formatter:on
        )
    }
}