package shellw

import java.nio.file.Path
import java.util.regex.Pattern

fun complete(path: Path): List<String> {
    val current = System.getenv()["COMP_CWORD"]!!.toInt()

    val parameters = System.getenv()["COMP_LINE"]!!
        .split(Pattern.compile("\\W+"))
        .take(current)

    val script = Script.from(path)
    return find(script.api, parameters, 1).sorted()
}

fun find(command: Command, arguments: List<String>, current: Int): List<String> {
    if (current == arguments.size) {
        return command.subCommands().map { completions(it.key) }
    }

    val argument = arguments[current]

    if (current == arguments.size - 1) {
        return command.subCommands().map { completions(it.key, argument) }.filterNotNull()
    }

    return command.subCommands().filter { matches(it.key, argument) }
        .flatMap { find(it.value, arguments, current + 1) }
}

fun matches(key: Parameter, argument: String) = when (key) {
    is Parameter.Exact -> key.value == argument
    is Parameter.Variable -> key.pattern?.let { argument.startsWith(it) } ?: true
    else -> throw Exception("Unsupported parameter: $key")
}

fun completions(key: Parameter): String = when (key) {
    is Parameter.Exact -> key.value
    is Parameter.Variable -> key.pattern ?: ""
    else -> throw Exception("Unsupported parameter: $key")
}

fun completions(key: Parameter, argument: String) = when (key) {
    is Parameter.Exact -> key.value.takeIf { it.startsWith(argument) }
    is Parameter.Variable -> key.pattern?.takeIf { it.startsWith(argument) }
    else -> throw Exception("Unsupported parameter: $key")
}
