package ru.hse.spb

import org.antlr.v4.runtime.tree.TerminalNode

class MyScope(private val outerScope: MyScope? = null) {

    private val functions: HashMap<String, MyFunction> = hashMapOf()
    private val variables: HashMap<String, Int> = hashMapOf()

    fun addVariable(identifier: TerminalNode) {
        when (variables[identifier.text]) {
            null -> variables[identifier.text] = DEFAULT_STATEMENT_VALUE
            else -> throw InterpretationException(identifier.symbol.line, "Multiple variable definition")
        }
    }

    fun setVariable(identifier: TerminalNode, value: Int?) {
        when (variables[identifier.text]) {
            null -> outerScope?.setVariable(identifier, value)
                ?: throw InterpretationException(identifier.symbol.line, "No such variable in current scope")
            else -> variables[identifier.text] = value
                ?: throw InterpretationException(identifier.symbol.line, "Incorrect assignment value")
        }
    }

    fun getVariable(identifier: TerminalNode): Int? {
        return variables[identifier.text]
            ?: outerScope?.getVariable(identifier)
            ?: throw InterpretationException(identifier.symbol.line, "No such variable in corrent scope")
    }

    fun addFunction(identifier: TerminalNode, function: MyFunction) {
        when (functions[identifier.text]) {
            null -> functions[identifier.text] = function
            else -> throw InterpretationException(identifier.symbol.line, "Multiple function definition")
        }
    }

    fun callFunction(identifier: TerminalNode, arguments: List<Int?>): Int? {
        return functions[identifier.text]?.apply(identifier, arguments)
            ?: outerScope?.callFunction(identifier, arguments)
            ?: throw InterpretationException(identifier.symbol.line, "No such function in current scope")
    }

    fun getOuterScope(): MyScope {
        return outerScope ?: MyScope()
    }
}