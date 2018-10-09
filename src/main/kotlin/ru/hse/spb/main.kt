package ru.hse.spb

fun main(args: Array<String>) {
    document {
        documentClass("beaner")
        usePackage("babel", "russian", "cmath")
        document {
            usePackage("babel", "russian", "cmath")
        }
    }.print(System.out)
}