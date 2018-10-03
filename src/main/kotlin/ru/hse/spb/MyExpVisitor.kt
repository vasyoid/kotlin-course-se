package ru.hse.spb

import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class MyExpVisitor(private var scope: MyScope) : ExpBaseVisitor<Int?>() {

    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        scope.addFunction(
            ctx.IDENTIFIER(),
            MyFunction(
                MyScope(scope),
                ctx.parameterNames().IDENTIFIER(),
                ctx.blockWithBraces()
            )
        )
        return DEFAULT_STATEMENT_VALUE
    }

    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): Int? {
        val arguments = ctx.arguments().expression().map { visit(it) }
        return when (ctx.IDENTIFIER().text) {
            "println" -> {
                println(arguments.joinToString(" "))
                DEFAULT_STATEMENT_VALUE
            }
            else -> {
                scope.callFunction(ctx.IDENTIFIER(), arguments)
            }
        }
    }

    override fun visitVariable(ctx: ExpParser.VariableContext): Int? {
        scope.addVariable(ctx.IDENTIFIER())
        ctx.expression()?.let { scope.setVariable(ctx.IDENTIFIER(), visit(it)) }
        return DEFAULT_STATEMENT_VALUE
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        scope.setVariable(ctx.IDENTIFIER(), visit(ctx.expression()))
        return DEFAULT_STATEMENT_VALUE
    }

    override fun visitReturnStatement(ctx: ExpParser.ReturnStatementContext): Int? {
        return visit(ctx.expression())
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        for (statement in ctx.statement()) {
            val result = visit(statement)
            if (statement.returnStatement() != null) {
                return result
            }
        }
        return DEFAULT_STATEMENT_VALUE
    }

    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        scope = MyScope(scope)
        val result = visit(ctx.block())
        scope = scope.getOuterScope()
        return result
    }

    override fun visitIfBlock(ctx: ExpParser.IfBlockContext): Int? {
        if (visit(ctx.expression()) == 1) {
            return visit(ctx.blockWithBraces(0))
        }
        if (ctx.blockWithBraces().size > 1) {
            return visit(ctx.blockWithBraces(1))
        }
        return DEFAULT_STATEMENT_VALUE
    }

    override fun visitWhileBlock(ctx: ExpParser.WhileBlockContext): Int? {
        var result : Int? = DEFAULT_STATEMENT_VALUE
        while (visit(ctx.expression()) == 1) {
            result = visit(ctx.blockWithBraces())
        }
        return result
    }

    override fun visitFile(ctx: ExpParser.FileContext): Int? {
        return visit(ctx.block())
    }

    override fun visitStatement(ctx: ExpParser.StatementContext): Int? {
        return visit(ctx.getChild(0))
    }

    override fun visitExpression(ctx: ExpParser.ExpressionContext): Int? {
        if (ctx.LITERAL() != null) {
            return Integer.parseInt(ctx.LITERAL().text)
        }
        if (ctx.IDENTIFIER() != null) {
            return scope.getVariable(ctx.IDENTIFIER())
        }
        if (ctx.childCount == 1) {
            return visit(ctx.getChild(0))
        }
        val larg = visit(ctx.larg)
        val rarg = visit(ctx.rarg)
        if (larg == null || rarg == null) {
            return null
        }
        return when (ctx.op.text) {
            "*" -> larg * rarg
            "/" -> larg / rarg
            "%" -> larg % rarg
            "+" -> larg + rarg
            "-" -> larg - rarg
            "<" -> (larg < rarg).toInt()
            "<=" -> (larg <= rarg).toInt()
            ">" -> (larg > rarg).toInt()
            ">=" -> (larg >= rarg).toInt()
            "==" -> (larg == rarg).toInt()
            "!=" -> (larg != rarg).toInt()
            "&&" -> (larg.toBoolean() && rarg.toBoolean()).toInt()
            "||" -> (larg.toBoolean() || rarg.toBoolean()).toInt()
            else -> throw InterpretationException(ctx.op.line, "Incorrect binary operation")
        }
    }

    private fun Boolean.toInt() = if (this) 1 else 0
    private fun Int.toBoolean() = this != 0
}