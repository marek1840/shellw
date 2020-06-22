package shellw

import shellw.Parameter.Type

sealed class Parameter {
    data class Exact(val value: String) : Parameter()
    data class Variable(val name: String, val type: Type, val pattern: String? = null) : Parameter()

    enum class Type {
        TEXT;

        companion object {
            fun from(name: String): Type = when (name.toLowerCase()) {
                "string" -> throw IllegalStateException("String type cannot be explicitly named")
                else -> throw IllegalStateException("Unsupported type $name")
            }
        }
    }
}

fun parseParameters(line: String): List<Parameter> {
    val input = Input(line.codePoints().iterator())
    val parameters = mutableListOf<Parameter>()

    fun dropWhitespaces() {
        input.readWhile(Character::isWhitespace) {}
    }

    while (input.hasNext()) {
        dropWhitespaces()
        if (input.read('"')) {
            val value = readQuotedString(input)
            parameters.add(Parameter.Exact(value))
        } else if (input.read('(')) {
            dropWhitespaces()
            val name = readIdentifier(input)

            dropWhitespaces()
            if (input.read(':').not()) throw Exception("Missing type or pattern")

            dropWhitespaces()
            if (input.read(')')) throw Exception("Expected string or identifier, got ')'")
            else if (input.read('"')) {
                val pattern = readQuotedString(input)

                dropWhitespaces()
                if (!input.read(')')) throw Exception("Expected ')', got " + input.current())
                parameters.add(Parameter.Variable(name, Type.TEXT, pattern))
            } else {
                val type = Type.from(readIdentifier(input))

                dropWhitespaces()
                if (input.read(')')) {
                    parameters.add(Parameter.Variable(name, type, null))
                } else if (input.read('(')) {
                    dropWhitespaces()
                    if (!input.read('"')) throw Exception("Expected '\"', got " + input.current())
                    val pattern = readQuotedString(input)

                    dropWhitespaces()
                    if (!input.read(')')) throw Exception("Expected ')', got " + input.current())

                    dropWhitespaces()
                    if (!input.read(')')) throw Exception("Expected ')', got " + input.current())
                    parameters.add(Parameter.Variable(name, type, pattern))
                } else throw Exception("Expected ')' or '(', got " + input.current())
            }
        } else if (input.hasNext()) throw Exception("Expected '\"' or '(', got, " + input.current())
    }

    return parameters
}

private fun readIdentifier(iterator: Input): String {
    val identifier = StringBuilder()
    iterator.readWhile(Character::isAlphabetic) { identifier.append(it) }
    return identifier.toString()
}

private fun readQuotedString(iterator: Input): String {
    val string = StringBuilder()
    iterator.readWhile({ it.toChar() != '"' }) { string.append(it) }
    if (!iterator.read('"')) throw Exception("Expected '\"', got" + iterator.current())
    return string.toString()
}