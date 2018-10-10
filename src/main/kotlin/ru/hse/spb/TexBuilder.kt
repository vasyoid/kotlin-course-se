package ru.hse.spb

import java.io.PrintWriter

@DslMarker
annotation class TexCommandMarker

interface Element {
    fun render(writer: PrintWriter, indent: String)
}

@TexCommandMarker
abstract class Environment : Element {
    protected val children: MutableList<Element> = mutableListOf()

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

abstract class Tag(private val name: String,
                   private val param: String = "",
                   private vararg val params: Pair<String, String>) : Environment() {

    override fun render(writer: PrintWriter, indent: String) {
        writer.print("$indent\\begin{$name}")
        if (params.isNotEmpty()) {
            writer.print(params.joinToString (", ", "[", "]") { "${it.first}=${it.second}" })
        }
        if (param.isNotEmpty()) {
            writer.print("{$param}")
        }
        writer.println()
        for (child in children) {
            child.render(writer, "$indent  ")
        }
        writer.println("$indent\\end{$name}")
    }

    fun frame(title: String = "", vararg params: Pair<String, String>, init: Frame.() -> Unit) =
        initElement(Frame(title, *params), init)

    fun itemize(init: TagWithItems.() -> Unit) = initElement(TagWithItems("itemize"), init)

    fun enumerate(init: TagWithItems.() -> Unit) = initElement(TagWithItems("enumerate"), init)

    fun customTag(name: String, param: String = "", vararg params: Pair<String, String>, init: CustomTag.() -> Unit) =
        initElement(CustomTag(name, param, *params), init)

    fun math(init: Math.() -> Unit) = initElement(Math(), init)

    fun flushleft(init: Align.() -> Unit) = initElement(Align("flushleft"), init)

    fun flushright(init: Align.() -> Unit) = initElement(Align("flushright"), init)

    fun center(init: Align.() -> Unit) = initElement(Align("center"), init)
}

@TexCommandMarker
abstract class Command(
    private val name: String,
    private val param: String = "",
    private vararg val params: String) : Element {

    override fun render(writer: PrintWriter, indent: String) {
        writer.print("$indent\\$name")
        if (params.isNotEmpty()) {
            writer.print(params.joinToString(", ", "[", "]"))
        }
        if (param.isNotEmpty()) {
            writer.print("{$param}")
        }
        writer.println()
    }
}

open class Document : Tag("document") {

    private val headers: MutableList<Command> = mutableListOf()

    private fun <T : Command> initHeader(element: T, init: T.() -> Unit): T {
        element.init()
        headers.add(element)
        return element
    }

    override fun render(writer: PrintWriter, indent: String) {
        for (header in headers) {
            header.render(writer, indent)
        }
        super.render(writer, indent)
    }

    fun documentClass(param: String) = initHeader(DocumentClass(param)) { }

    fun usePackage(param: String, vararg params: String) = initHeader(UsePackage(param, *params)) { }

    fun print(writer: PrintWriter) = render(writer, "")
}

class TextElement(private val text: String) : Element {
    override fun render(writer: PrintWriter, indent: String) {
        writer.println(text.trimMargin().replace(Regex("^|\\n")) { "${it.value}$indent"})
    }
}

class Math : Environment() {

    override fun render(writer: PrintWriter, indent: String) {
        writer.println("$indent$$")
        for (child in children) {
            child.render(writer, "$indent  ")
        }
        writer.println("$indent$$")
    }
}

class Frame(title: String, vararg params: Pair<String, String>) : Tag("frame", title, *params)

class TagWithItems(name: String) : Tag(name) {
    fun item(label: String = "", init: TagWithItems.() -> Unit): Item {
        val item = if (label.isNotEmpty()) {
            initElement(Item(label)) { }
        } else {
            initElement(Item()) { }
        }
        this.init()
        return item
    }
}

class CustomTag(name: String, param: String, vararg params: Pair<String, String>) : Tag(name, param, *params)

class Align(name: String) : Tag(name)

class Item(vararg label: String) : Command("item", params = *label)

class DocumentClass(className: String) : Command("documentclass", className)

class UsePackage(packageName: String, vararg params: String) : Command("usepackage", packageName, *params)

fun document(init: Document.() -> Unit): Document {
    val document = Document()
    document.init()
    return document
}
