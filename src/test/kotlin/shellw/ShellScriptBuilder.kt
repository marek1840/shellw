package shellw

import org.junit.Assert.assertEquals
import org.junit.Test
import shellw.Parameter.Exact
import shellw.Parameter.Type.TEXT
import shellw.Parameter.Variable
import shellw.Shell.SH

private const val tab = '\t'
private const val echo = "echo foo"

class ShellScriptBuilderTest {
    @Test
    fun bindsVariables() {
        val builder = ShellScriptBuilder()

        builder.bind("foo", "bar")

        val expected =
            """|${SH.shebang}
               |foo=bar
               |""".trimMargin()

        assertEquals(expected, builder.toString())
    }

    @Test
    fun foo() {
        val builder = ShellScriptBuilder()
        val command = Command()
        command.add(emptyList(), listOf(echo))

        builder.matchNextArg(command)

        val expected =
            """|${SH.shebang}
               |$echo
               |""".trimMargin()

        assertEquals(expected, builder.toString())
    }

    @Test
    fun matchesExactParameter() {
        val builder = ShellScriptBuilder()
        val command = Command()
        command.add(listOf(Exact("foo")), listOf(echo))

        builder.matchNextArg(command)

        val expected =
            """|${SH.shebang}
               |case $1 in
               |foo)
               |$tab$echo
               |$tab;;
               |$tab
               |esac
               |""".trimMargin()

        assertEquals(expected, builder.toString())
    }

    @Test
    fun matchesVariableWithoutPattern() {
        val builder = ShellScriptBuilder()
        val command = Command()
        command.add(listOf(Variable("foo", TEXT)), listOf(echo))

        builder.matchNextArg(command)

        val binding = """foo="$1""""

        val expected =
            """|${SH.shebang}
               |case $1 in
               |*)
               |$tab$binding
               |$tab$echo
               |$tab;;
               |$tab
               |esac
               |""".trimMargin()

        assertEquals(expected, builder.toString())
    }

    @Test
    fun matchesVariableWithPattern() {
        val builder = ShellScriptBuilder()
        val command = Command()
        command.add(listOf(Variable("foo", TEXT, "bar")), listOf(echo))

        builder.matchNextArg(command)

        val binding = """foo="$1""""

        val expected =
            """|${SH.shebang}
               |case $1 in
               |bar)
               |$tab$binding
               |$tab$echo
               |$tab;;
               |$tab
               |esac
               |""".trimMargin()

        assertEquals(expected, builder.toString())
    }
}