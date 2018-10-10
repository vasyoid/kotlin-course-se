package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TexBuilderTest {

    @Test
    fun documentTest() {
        val doc = document { }.print()
        assertEquals("""
            |\begin{document}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun frameTest() {
        val doc = document {
            frame { }
            frame("name") { }
            frame("name", "param1" to "param2") { }
            frame(params = *arrayOf("param1" to "param2")) { }
            frame {
                frame { }
            }
        }.print()
        assertEquals("""
            |\begin{document}
            |  \begin{frame}
            |  \end{frame}
            |  \begin{frame}{name}
            |  \end{frame}
            |  \begin{frame}[param1=param2]{name}
            |  \end{frame}
            |  \begin{frame}[param1=param2]
            |  \end{frame}
            |  \begin{frame}
            |    \begin{frame}
            |    \end{frame}
            |  \end{frame}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun itemizersTest() {
        val doc = document {
            itemize {
                item {
                    + "sdfsgsdf"
                    + "sgfdgfff"
                }
                item("asd") {  }
            }
            enumerate {
                item { }
                item("asd") {
                    + """
                        |ssss
                        | ssss
                        |ssss
                    """
                }
            }
        }.print()
        assertEquals("""
            |\begin{document}
            |  \begin{itemize}
            |    \item
            |    sdfsgsdf
            |    sgfdgfff
            |    \item[asd]
            |  \end{itemize}
            |  \begin{enumerate}
            |    \item
            |    \item[asd]
            |    ssss
            |     ssss
            |    ssss
            |  \end{enumerate}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun mathTest() {
        val doc = document {
            math {
                + "X_{1} = 0"
                + "\\Xi \\eqiv \\theta"
            }
        }.print()
        assertEquals("""
            |\begin{document}
            |  ${'$'}${'$'}
            |    X_{1} = 0
            |    \Xi \eqiv \theta
            |  ${'$'}${'$'}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun alignmentTest() {
        val doc = document {
            flushleft {
                flushright {
                    + "asdasdasd"
                }
            }
            center {
                center {
                    + """
                        |sdfsdf
                        |          sdfsd
                    """
                }
            }
        }.print()
        assertEquals("""
            |\begin{document}
            |  \begin{flushleft}
            |    \begin{flushright}
            |      asdasdasd
            |    \end{flushright}
            |  \end{flushleft}
            |  \begin{center}
            |    \begin{center}
            |      sdfsdf
            |                sdfsd
            |    \end{center}
            |  \end{center}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun customTagTest() {
        val doc = document {
            customTag("asd") { }
            customTag("fds", "param") {
                customTag("ffff", params = *arrayOf("fff" to "ffsss")) { }
            }
        }.print()
        assertEquals("""
            |\begin{document}
            |  \begin{asd}
            |  \end{asd}
            |  \begin{fds}{param}
            |    \begin{ffff}[fff=ffsss]
            |    \end{ffff}
            |  \end{fds}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun documentClassTest() {
        val doc = document {
            documentClass("asd")
        }.print()
        assertEquals("""
            |\documentclass{asd}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun usePackageTest() {
        val doc = document {
            usePackage("asd")
            usePackage("dd", "asd", "fds")
        }.print()
        assertEquals("""
            |\usepackage{asd}
            |\usepackage[asd, fds]{dd}
            |\begin{document}
            |\end{document}
            |
        """.trimMargin(), doc)
    }

    @Test
    fun complicatedDocumentTest() {
        val doc = document {
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
        }.print()
        assertEquals("""
            |\documentclass{beamer}
            |\usepackage[russian]{babel}
            |\begin{document}
            |  \begin{frame}[arg1=arg2]{frametitle}
            |    \begin{itemize}
            |      \item
            |      1) hello
            |      \item
            |      2) hello
            |    \end{itemize}
            |    \begin{enumerate}
            |      \item
            |      3) hello
            |      \item
            |      4) hello
            |    \end{enumerate}
            |  \end{frame}
            |  \begin{pyglist}[language=kotlin]
            |    val a = 1
            |    fdsffd
            |    ${'$'}${'$'}
            |      F_0 = 1,\\ F_1 = 1
            |      F_n = F_{n-1} + F_{n-2}
            |    ${'$'}${'$'}
            |  \end{pyglist}
            |  \begin{flushleft}
            |    left
            |    \begin{flushright}
            |      right
            |    \end{flushright}
            |  \end{flushleft}
            |  \begin{center}
            |    center
            |  \end{center}
            |\end{document}
            |
        """.trimMargin(), doc)
    }
}