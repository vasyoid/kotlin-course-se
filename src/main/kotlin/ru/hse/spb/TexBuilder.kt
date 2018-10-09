package ru.hse.spb

import java.io.PrintStream

@DslMarker
annotation class TexCommandMarker

interface Element {
    fun render(writer: PrintStream, indent: String)
}

@TexCommandMarker
abstract class Tag(private val name: String) : Element {

    private val children: MutableList<Element> = mutableListOf()

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }


    override fun render(writer: PrintStream, indent: String) {
        writer.println("$indent\\begin{$name}")
            for (child in children) {
                child.render(writer, "$indent ")
            }
        writer.println("$indent\\end{$name}")
    }
}

@TexCommandMarker
abstract class Command(
    private val name: String,
    private val param: String,
    private val optionalParams: Array<out String>?) : Element {

    override fun render(writer: PrintStream, indent: String) {
        writer.println("$indent\\$name${optionalParams?.joinToString(", ", "[", "]") ?: ""}{$param}")
    }
}

class Document : Tag("document") {

    fun documentClass(param: String) = initElement(DocumentClass(param)) { }

    fun usePackage(param: String, vararg optionalParams: String) = initElement(UsePackage(param, optionalParams)) { }

    fun print(writer: PrintStream) {
        render(writer, "")
    }
}

class DocumentClass(param: String) : Command("documentclass", param, null)
class UsePackage(param: String, optionalParams: Array<out String>?) : Command("usepackage", param, optionalParams)


fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}
