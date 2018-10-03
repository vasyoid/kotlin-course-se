package ru.hse.spb

import org.antlr.v4.runtime.tree.TerminalNode
import ru.hse.spb.parser.ExpParser

class MyFunction(
    private val scope: MyScope,
    private val arguments: List<TerminalNode>,
    private val body: ExpParser.BlockWithBracesContext
) {
    fun apply(identifier: TerminalNode, argumentValues: List<Int?>): Int? {
        if (argumentValues.size != arguments.size) {
            throw InterpretationException(identifier.symbol.line, "Incorrect argument count in function call")
        }
        val executionScope = MyScope(scope)
        argumentValues.forEachIndexed { index, value ->
            when (value) {
                null -> throw InterpretationException(identifier.symbol.line, "Incorrect function argument value")
                else -> {
                    executionScope.addVariable(arguments[index])
                    executionScope.setVariable(arguments[index], value)
                }
            }
        }
        return MyExpVisitor(executionScope).visit(body) ?: DEFAULT_STATEMENT_VALUE
    }
}