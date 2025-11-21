package io.github.seggan.kombinator.parser

import io.github.seggan.kombinator.lexer.TestLexer
import io.github.seggan.kombinator.lexer.TestTokenType

object TestParser : Parser<TestTokenType, TestAst.File>() {

    private val identifier by TestLexer.IDENTIFIER map { it.span.text }

    private val primaryExpression by (identifier map { TestAst.VariableReference(it) } named "variable reference") or
            (TestLexer.NUMBER map { TestAst.NumberLiteral(it.span.text.toDouble()) } named "number literal") or
            (-TestLexer.OPEN_PARENTHESIS then ref(::expression) then -TestLexer.CLOSE_PARENTHESIS)

    private val addExpression by leftRecursive(
        primaryExpression,
        TestLexer.PLUS or TestLexer.MINUS map { it.span.text }
    ) { a, op, b -> TestAst.BinaryOperation(a, op, b) }

    private val cmpEqExpression by leftRecursive(
        addExpression,
        TestLexer.DOUBLE_EQUALS or TestLexer.NOT_EQUALS or
                TestLexer.GREATER_THAN or TestLexer.GREATER_THAN_OR_EQUAL or
                TestLexer.LESS_THAN or TestLexer.LESS_THAN_OR_EQUAL map { it.span.text }
    ) { a, op, b -> TestAst.BinaryOperation(a, op, b) }

    private val booleanExpression by leftRecursive(
        cmpEqExpression,
        TestLexer.DOUBLE_AMPERSAND or TestLexer.DOUBLE_PIPE map { it.span.text }
    ) { a, op, b -> TestAst.BinaryOperation(a, op, b) }

    private val expression: ParserNode<TestTokenType, TestAst.Expression> by booleanExpression

    private val variableDeclaration by -TestLexer.LET then
            (optional(TestLexer.MUT) map { it != null }) then
            (identifier named "variable name") then
            optional(-TestLexer.COLON then identifier named "variable type") then
            -TestLexer.SINGLE_EQUALS then
            (expression named "variable expression") then
            -TestLexer.SEMICOLON map
            { (mutable, name, type, value) -> TestAst.VariableDeclaration(mutable, name, type, value) }

    private val block by -TestLexer.OPEN_BRACE then
            zeroOrMore(ref(::statement)) then
            -TestLexer.CLOSE_BRACE map { TestAst.Block(it) }

    private val ifStatement by -TestLexer.IF then
            -TestLexer.OPEN_PARENTHESIS then
            (expression named "if test") then
            -TestLexer.CLOSE_PARENTHESIS then
            (ref(::statement) named "if block") then
            optional(-TestLexer.ELSE then ref(::statement) named "else block") map
            { (condition, thenBranch, elseBranch) ->
                TestAst.IfStatement(condition, thenBranch, elseBranch)
            }

    private val returnStatement by -TestLexer.RETURN then expression then -TestLexer.SEMICOLON map
            { TestAst.ReturnStatement(it) }

    private val statement: ParserNode<TestTokenType, TestAst.Statement> by block or
            variableDeclaration or
            ifStatement or
            returnStatement or
            (expression then -TestLexer.SEMICOLON)

    private val parameter by identifier then -TestLexer.COLON then identifier map
            { (name, type) -> TestAst.Parameter(name, type) }

    private val function by -TestLexer.FUN then identifier then
            -TestLexer.OPEN_PARENTHESIS then (parameter separatedBy TestLexer.COMMA) then -TestLexer.CLOSE_PARENTHESIS then
            (optional(-TestLexer.COLON then identifier) orElse just("Unit")) then
            -TestLexer.OPEN_BRACE then zeroOrMore(statement) then -TestLexer.CLOSE_BRACE map
            { (name, parameters, type, body) ->
                TestAst.Function(name, parameters, type, body)
            }

    override val rootParser by zeroOrMore(function) map { TestAst.File(it) }
    override val eofToken = TestTokenType.EOF
}