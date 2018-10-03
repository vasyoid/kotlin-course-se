package ru.hse.spb

class InterpretationException(line: Int, message: String) :
    RuntimeException("Interpretator error on line $line: $message" )