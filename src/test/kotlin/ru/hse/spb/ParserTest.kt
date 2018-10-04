package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams.fromString
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser

class ParserTest {

    private var parser: ExpParser? = null

    private fun initParser(text: String) {
        parser = ExpParser(BufferedTokenStream(ExpLexer(fromString(text))))
    }

    @Test
    fun shouldParseFunctionDeclaration() {
        initParser("""
            fun foo(bar, baz) { }
        """.trimIndent())
        val expected = "(file (block (statement (function fun foo ( (parameterNames bar , baz) ) " +
            "(blockWithBraces { block })))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseVariableInitialization() {
        initParser("""
            var foo = bar
        """.trimIndent())
        val expected = "(file (block (statement (variable var foo = (expression bar)))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseWhileStatement() {
        initParser("""
            while (a < b) {
                return 0
            }
        """.trimIndent())
        val expected = "(file (block (statement (whileBlock while ( (expression (expression a) < (expression b)) ) " +
            "(blockWithBraces { (block (statement (returnStatement return (expression 0)))) })))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseIfElseStatement() {
        initParser("""
            if (a < b) {
                return 0
            } else { }
        """.trimIndent())
        val expected = "(file (block (statement (ifBlock if ( (expression (expression a) < (expression b)) ) " +
            "(blockWithBraces { (block (statement (returnStatement return (expression 0)))) }) else " +
            "(blockWithBraces { block })))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseFunctionCall() {
        initParser("""
            _printF02(1, 2) // {custom printf("%i, %i") function}
        """.trimIndent())
        val expected = "(file (block (statement (expression (functionCall _printF02 " +
            "( (arguments (expression 1) , (expression 2)) ))))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseBinaryComplexExpression() {
        initParser("""
            1 + (2 - 3) * 4 / (5 % 6) < 7 || 8 <= 9 && 10 == 11 || 12 != 13
        """.trimIndent())
        val expected = "(file (block (statement (expression (expression (expression (expression (expression 1) + " +
            "(expression (expression (expression ( (expression (expression 2) - (expression 3)) )) * (expression 4)) / " +
            "(expression ( (expression (expression 5) % (expression 6)) )))) < (expression 7)) || " +
            "(expression (expression (expression 8) <= (expression 9)) && (expression (expression 10) == " +
            "(expression 11)))) || (expression (expression 12) != (expression 13))))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }

    @Test
    fun shouldParseFoldingBlocks() {
        initParser("""
            fun foo() {
                while (x) {
                    if (y) {
                        return z
                    }
                }
            }
        """.trimIndent())
        val expected = "(file (block (statement (function fun foo ( parameterNames ) " +
            "(blockWithBraces { (block (statement (whileBlock while ( (expression x) ) " +
            "(blockWithBraces { (block (statement (ifBlock if ( (expression y) ) " +
            "(blockWithBraces { (block (statement (returnStatement return (expression z)))) })))) })))) })))) <EOF>)"
        assertEquals(expected, parser!!.file().toStringTree(parser))
    }
}