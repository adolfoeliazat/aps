fun main(args: Array<String>) {
    fun f(g: () -> Int) {
        println("g() = " + g())
    }

    f({when {}}) // WTF?

    listOf(123).indexOfFirst {false}
}


