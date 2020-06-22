package shellw

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path

class Script(val env: Map<String, String>, val api: Command) {
    companion object {
        fun from(path: Path) = parseScript(BufferedReader(InputStreamReader(Files.newInputStream(path))))
    }
}

internal fun parseScript(input: BufferedReader): Script {
    val variables = linkedMapOf<String, String>()
    val api = Command()

    val lines: Iterator<String> = input.lines().map(String::trim).iterator()

    while (lines.hasNext()) {
        val line = lines.next().trim()
        if (line.isEmpty()) continue
        if (line.startsWith("#!")) continue

        if (line.startsWith("def ") && line.endsWith("{")) {
            val params = parseParameters(line.drop(4).dropLast(1))
            val body = parseBody(lines)
            api.add(params, body)
        } else {
            val separator = line.indexOf("=")
            if (separator > 0) {
                val name = line.substring(0, separator)
                val value = line.substring(separator + 1)
                variables[name] = value
            } else {
                throw Exception("Unrecognised line: $line")
            }
        }
    }
    return Script(variables, api)
}

private fun parseBody(lines: Iterator<String>): List<String> {
    val body = mutableListOf<String>()
    while (lines.hasNext()) {
        val line = lines.next()
        if (line.trim() == "}") return body
        else body += line
    }

    throw Exception("""Expected "}", found EOF """)
}
