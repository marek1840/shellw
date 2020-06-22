package shellw

import shellw.Parameter.Exact
import shellw.Parameter.Variable

fun Script.shScript(): String {
    val builder = ShellScriptBuilder()
    env.forEach { (name, value) -> builder.bind(name, value) }
    builder.matchNextArg(api)
    return builder.toString()
}

internal class ShellScriptBuilder {
    private val builder = StringBuilder().appendln(Shell.SH.shebang)
    private var indent = 0

    private lateinit var arg: String

    fun bind(name: String, value: String) {
        this += "$name=$value"
    }

    fun matchNextArg(command: Command) {
        val subCommands = command.subCommands()
        val body = command.action()

        if (subCommands.isEmpty()) {
            body.forEach { this += it }
            return
        }

        arg = "$${indent + 1}"

        this += "case $arg in"

        subCommands.forEach { (p, c) ->
            this += "${p.pattern()})"
            indent += 1

            p.argBinding()?.let { bind(it, "\"$arg\"") }
            matchNextArg(c)
            this += ";;"
            this += ""
            indent -= 1
        }

        if (body.isNotEmpty()) {
            this += "*)"
            indent += 1
            body.forEach { this += it }
            indent -= 1
        }
        this += "esac"
    }

    private fun Parameter.pattern(): String = when (this) {
        is Exact -> this.value
        is Variable -> this.pattern ?: "*"
    }

    private fun Parameter.argBinding(): String? = when (this) {
        is Exact -> null
        is Variable -> this.name
    }

    private operator fun plusAssign(line: String) {
        repeat(indent) { builder.append("\t") }
        builder.appendln(line)
    }

    override fun toString() = builder.toString()
}
