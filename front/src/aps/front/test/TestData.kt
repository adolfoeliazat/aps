package aps.front

data class TestUserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)

object TestData {
    val bobul = TestUserData(email = "bobul@test.shit.ua", firstName = "Иво", lastName = "Бобул", password = "bobulsecret")

    fun generateShit(len: Int): String {
        val bunchOfShit = buildString {
            while (length < len) {
                append("iamlongshit")
            }
        }

        return bunchOfShit.substring(0, len)
    }
}

