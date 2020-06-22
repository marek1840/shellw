package shellw

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.*
import java.util.stream.Collectors

private val home = Paths.get(System.getProperty("user.home"))

private val config = home
    .resolve(".config/shellw")
    .apply { Files.createDirectories(this) }

private val registry = config.resolve("registry")
    .let { if (Files.exists(it)) it else Files.createFile(it) }

private val bashCompletions = home.resolve(".config/bash/completions/shellw")
    .let {
        if (Files.exists(it)) it else {
            val content = """
                |_complete_shellw(){
                |   export COMP_CWORD
                |   export COMP_LINE

                |   COMPREPLY=(${'$'}(shellw-tools "complete" bash "${'$'}1"))
                |}
                |
            """.trimMargin()

            Files.write(it, content.toByteArray(), CREATE)
        }
    }

private val registered by lazy { Files.lines(registry).map { Paths.get(it) }.collect(Collectors.toSet()) }

fun main(args: Array<String>) {
    when (args.size) {
        3 -> {
            when (args[0]) {
                "complete" -> {
                    val shell = args[1]
                    if (shell != "bash") throw Exception("Completions are unsupported for shell: $shell")
                    val source = Paths.get(args[2])
                    complete(source).forEach { println(it) }
                }
                "generate" -> {
                    val source = Paths.get(args[1])
                    val target = Paths.get(args[2])
                    val script = Script.from(source)
                    write(target, script.shScript())

                    if (registered.add(source)) {
                        val name = source.fileName.toString()
                        Files.write(registry, name.toByteArray(), APPEND)

                        val bashCompletion = "complete -F _complete_shellw $name\n"
                        Files.write(bashCompletions, bashCompletion.toByteArray(), APPEND)
                    }
                }
            }
        }
    }
}

fun write(path: Path, content: String) {
    Files.write(path, content.toByteArray(), CREATE, TRUNCATE_EXISTING)
}
