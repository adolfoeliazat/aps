fun test1() {
    class Test {
        init {
            add(10)
            add(20)
            addAll(listOf(30, 40, 50))
        }

        fun add(x: Int) {
            println("Adding $x")
        }

        fun addAll(xs: Iterable<Int>) {
            xs.forEach(this::add)
        }
    }

    Test()
}






