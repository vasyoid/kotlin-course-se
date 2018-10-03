package ru.hse.spb

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.parser.ExpParser
import ru.hse.spb.parser.ExpLexer

const val DEFAULT_STATEMENT_VALUE = 0

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Expected arguments: file_to_execute")
        System.exit(1)
    }
    val lexer = ExpLexer(CharStreams.fromFileName(args[0]))
    val parser = ExpParser(BufferedTokenStream(lexer))
    MyExpVisitor(MyScope()).visit(parser.file())
}