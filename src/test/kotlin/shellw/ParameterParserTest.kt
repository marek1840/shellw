package shellw

import org.junit.Assert.assertEquals
import org.junit.Test
import shellw.Parameter.*

class ParameterParserTest {
    @Test
    fun parsesExactParameter() {
        val expected = listOf(Exact("foo"))
        parse(expected, """ "foo" """)
    }

    @Test
    fun parsesParameterWithJustAType() {
        val expected = listOf(Variable("foo", Type.TEXT, "some pattern"))
        parse(expected, """ (foo: "some pattern") """)
    }

    @Test
    fun parsesIntertwinedParameters() {
        val expected = listOf(Exact("a"), Variable("b", Type.TEXT, "*"), Exact("c"))
        parse(expected, """ "a" (b: "*") "c" """)
    }

    private fun parse(expected: List<Parameter>, text: String) {
        parseParameters(text).apply { assertEquals(expected, this) }
    }
}