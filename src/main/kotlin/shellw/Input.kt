package shellw

import java.util.*
import kotlin.NoSuchElementException

private val None = '\uFFFF'.toInt()

class Input(private val source: PrimitiveIterator.OfInt) {
    private var current = None

    fun hasNext(): Boolean = current != None || source.hasNext()

    fun current(): Char {
        if (current == None) throw NoSuchElementException()
        return current.toChar()
    }

    fun read(expected: Char): Boolean {
        if (buffered() && current.toChar() == expected) {
            current = None
            return true
        }

        return false
    }

    fun read(matches: (Int) -> Boolean): Boolean {
        if (buffered() && matches(current)) {
            current = None
            return true
        }

        return false
    }

    fun read(matches: (Int) -> Boolean, read: (Char) -> Unit): Boolean {
        if (buffered() && matches(current)) {
            read(current.toChar())
            current = None
            return true
        }

        return false
    }

    fun readWhile(matches: (Int) -> Boolean, read: (Char) -> Unit) {
        while (read(matches, read)) continue
    }

    private fun buffered(): Boolean {
        if (current != None) return true
        if (!source.hasNext()) return false

        current = source.nextInt()
        return true
    }
}