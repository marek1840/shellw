package shellw

class Command {
    private var map = mutableMapOf<Parameter, Command>()
    private var action = mutableListOf<String>()

    fun subCommands(): Map<Parameter, Command> = map
    fun action(): List<String> = action

    fun add(parameterList: List<Parameter>, body: List<String>) {
        val parameters = parameterList.iterator()
        var command = this
        while (parameters.hasNext()) {
            val parameter = parameters.next()
            command = command.map.computeIfAbsent(parameter) { Command() }
        }

        if (command.action.isEmpty()) command.action.addAll(body)
        else throw Exception("Overriding action of $parameterList")
    }
}