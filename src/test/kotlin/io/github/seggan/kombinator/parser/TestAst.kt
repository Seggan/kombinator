package io.github.seggan.kombinator.parser

sealed interface TestAst {
    data class File(val functions: List<Function>) : TestAst
    data class Function(val name: String, val parameters: List<Parameter>, val returnType: String, val body: List<Statement>) : TestAst
    data class Parameter(val name: String, val type: String) : TestAst

    sealed interface Statement : TestAst
    data class Block(val statements: List<Statement>) : Statement
    data class VariableDeclaration(val mutable: Boolean, val name: String, val type: String?, val value: Expression) : Statement
    data class ReturnStatement(val value: Expression) : Statement
    data class IfStatement(
        val condition: Expression,
        val thenBranch: Statement,
        val elseBranch: Statement?
    ) : Statement

    sealed interface Expression : Statement
    data class NumberLiteral(val value: Double) : Expression
    data class VariableReference(val name: String) : Expression
    data class BinaryOperation(val left: Expression, val operator: String, val right: Expression) : Expression
}