package ru.hse.spb

import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class MyExpVisitor(private var scope: MyScope = MyScope()) : ExpBaseVisitor<Int?>() {

    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        scope.addFunction(
            ctx.IDENTIFIER(),
            MyFunction(
                MyScope(scope),
                ctx.parameterNames().IDENTIFIER(),
                ctx.blockWithBraces()
            )
        )
        return null
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
        return null
    }

    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        scope.setVariable(ctx.IDENTIFIER(), visit(ctx.expression()))
        return null
    }

    override fun visitReturnStatement(ctx: ExpParser.ReturnStatementContext): Int? {
        scope.returnValue = visit(ctx.expression())
        return null
    }

    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        for (statement in ctx.statement()) {
            visit(statement)
            if (scope.returnValue != null) {
                return scope.returnValue
            }
        }
        return null
    }

    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        scope = MyScope(scope)
        visit(ctx.block())
        val result = scope.returnValue
        scope = scope.getOuterScope()
        scope.returnValue = result
        return result
    }

    override fun visitIfBlock(ctx: ExpParser.IfBlockContext): Int? {
        if (visit(ctx.expression()) == 1) {
            return visit(ctx.blockWithBraces(0))
        }
        if (ctx.blockWithBraces().size > 1) {
            return visit(ctx.blockWithBraces(1))
        }
        return null
    }

    override fun visitWhileBlock(ctx: ExpParser.WhileBlockContext): Int? {
        while (visit(ctx.expression()) == 1 && scope.returnValue == null) {
            visit(ctx.blockWithBraces())
        }
        return null
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

    override fun visitExpressionWithBraces(ctx: ExpParser.ExpressionWithBracesContext): Int? {
        return visit(ctx.expression())
    }

    private fun Boolean.toInt() = if (this) 1 else 0
    private fun Int.toBoolean() = this != 0
}