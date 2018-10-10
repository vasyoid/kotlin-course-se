package ru.hse.spb

fun main(args: Array<String>) {
    document {
        documentClass("beamer")
        usePackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1" to "arg2") {
            itemize {
                for (row in (1..2)) {
                    item { +"$row) hello" }
                }
            }
            enumerate {
                for (row in (3..4)) {
                    item { +"$row) hello" }
                }
            }
        }
        customTag("pyglist", params = *arrayOf("language" to "kotlin")) {
            +"""
               |val a = 1
               |fdsffd
            """
            math {
                +"F_0 = 1,\\\\ F_1 = 1"
                +"F_n = F_{n-1} + F_{n-2}"
            }
        }
        flushleft {
            +"left"
            flushright {
                +"right"
            }
        }
        center {
            +"center"
        }
    }.print(System.out)
}