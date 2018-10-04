package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import org.junit.rules.ExpectedException


class InterpretatorTest {

    @get:Rule
    var thrown = ExpectedException.none()

    private var parser: ExpParser? = null

    private fun initParser(text: String) {
        parser = ExpParser(BufferedTokenStream(ExpLexer(CharStreams.fromString(text))))
    }

    @Test
    fun shouldReturnMaxValue() {
        initParser("""
            var a = 10
            var b = 20
            if (a > b) {
                return a
            } else {
                return b
            }
        """.trimIndent())
        assertEquals(20, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldReturnFirstFiveFibonacciProduct() {
        initParser("""
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }
            var i = 1
            var j = 1
            while (i <= 5) {
                j = j * fib(i)
                i = i + 1
            }
            return j
        """.trimIndent())
        assertEquals(240, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldReturn42() {
        initParser("""
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
                return bar(1)
            }
            return foo(41) // returns 42
        """.trimIndent())
        assertEquals(42, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldEvaluateComplexExpression() {
        initParser("""
            return 10000 % (2 * (5 - 1) + 193 / 5)
        """.trimIndent())
        assertEquals(18, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldEvaluateComplexBooleanExpression() {
        initParser("""
            return 1 && (0 || 1 && 0 || 0 && 1 || 2) && (136 == 682 / 5)
        """.trimIndent())
        assertEquals(1, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldReturnFirstReturnStatement() {
        initParser("""
            fun foo() {
                return 1
                return 2
            }
            return foo()
        """.trimIndent())
        assertEquals(1, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldReturnDefaultValue() {
        initParser("""
            fun foo() { }
            return foo()
        """.trimIndent())
        assertEquals(0, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldInitializeVariableWithDefaultValue() {
        initParser("""
            var x
            return x
        """.trimIndent())
        assertEquals(0, MyExpVisitor().visit(parser!!.file()))
    }

    @Test
    fun shouldThrowMultipleVariableDefinition() {
        initParser("""
            var a = 1
            var a = 2
        """.trimIndent())
        thrown.expectMessage("Interpretator error on line 2: Multiple variable definition")
        MyExpVisitor().visit(parser!!.file())
    }

    @Test
    fun shouldThrowNoSuchVariable() {
        initParser("""
            println(x)
        """.trimIndent())
        thrown.expectMessage("Interpretator error on line 1: No such variable in current scope")
        MyExpVisitor().visit(parser!!.file())
    }

    @Test
    fun shouldThrowMultipleFunctionDefinition() {
        initParser("""
            fun foo() { return 1 }
            fun foo() { return 2 }
        """.trimIndent())
        thrown.expectMessage("Interpretator error on line 2: Multiple function definition")
        MyExpVisitor().visit(parser!!.file())
    }

    @Test
    fun shouldThrowNoSuchFunction() {
        initParser("""
            printf()
        """.trimIndent())
        thrown.expectMessage("Interpretator error on line 1: No such function in current scope")
        MyExpVisitor().visit(parser!!.file())
    }
}
