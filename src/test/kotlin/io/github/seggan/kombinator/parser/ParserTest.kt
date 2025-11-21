package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.CodeSource
import io.github.seggan.kombinator.lexer.LexerTest
import io.github.seggan.kombinator.lexer.TestLexer
import io.github.seggan.kombinator.lexer.TestTokenType
import kotlin.test.Test
import kotlin.test.assertEquals

object ParserTest {

    @Test
    fun testParsing() {
        TestParser.debugMode = true
        val source = CodeSource.constant("Test.tst", LexerTest.CODE)
        val tokens = TestLexer.lex(source)
            .filterNot { it.type == TestTokenType.WHITESPACE }
            .filterNot { it.type == TestTokenType.COMMENT }
        val ast = try {
            TestParser.parse(tokens, source)
        } catch (e: ParseException) {
            System.err.println(e.report())
            throw e
        }
        val expectedAst = TestAst.File(functions = listOf(
            TestAst.Function(
                name = "main",
                parameters = listOf(
                    TestAst.Parameter(name = "arg", type = "String")
                ),
                returnType = "Int",
                body = listOf(
                    TestAst.VariableDeclaration(
                        mutable = true,
                        name = "x",
                        type = null,
                        value = TestAst.NumberLiteral(10.0)
                    ),
                    TestAst.VariableDeclaration(
                        mutable = false,
                        name = "y",
                        type = null,
                        value = TestAst.BinaryOperation(
                            left = TestAst.VariableReference("x"),
                            operator = "+",
                            right = TestAst.NumberLiteral(5.0)
                        )
                    ),
                    TestAst.IfStatement(
                        condition = TestAst.BinaryOperation(
                            left = TestAst.VariableReference("x"),
                            operator = ">=",
                            right = TestAst.NumberLiteral(10.0)
                        ),
                        thenBranch = TestAst.Block(statements = listOf(
                            TestAst.ReturnStatement(
                                value = TestAst.BinaryOperation(
                                    left = TestAst.VariableReference("x"),
                                    operator = "+",
                                    right = TestAst.NumberLiteral(1.0)
                                )
                            )
                        )),
                        elseBranch = TestAst.Block(statements = listOf(
                            TestAst.ReturnStatement(
                                value = TestAst.BinaryOperation(
                                    left = TestAst.VariableReference("x"),
                                    operator = "-",
                                    right = TestAst.NumberLiteral(1.0)
                                )
                            )
                        ))
                    )
                )
            )
        ))
        assertEquals(expectedAst, ast)
    }
}