package aps.front

data class TestUserData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String
)

object TestData {
    val bobul = TestUserData(
        email = "bobul@test.shit.ua",
        firstName = "Иво",
        lastName = "Бобул",
        password = "ohuevajushij-secret")

    // _run aps.back.GenerateTestFileSHA256sKt
    val sha256 = mapOf(
        "crappy release 1.rtf" to "1b8ed82d7a6cb0c9c2ba249eb526c1d2148e55cb76f30f9f8e7c8cde336aa7cf",
        "crappy release 2.rtf" to "3828c0c61561698622bfd5e8c62950be2492a7e6e034f79a05905666b58908bc",
        "crappy release 3.rtf" to "5075d2ff1fd3fe1e8aefd6b4779c0b6e725ce3ba7ebbfe51ad666f332df4524b",
        "crazy monster boobs.rtf" to "ebaf99041969a120e7ecc92ffad9ededd104225d6555b2874c9b735d73ca7538",
        "crime and punishment.rtf" to "33a24abc20d3cf1d038ec5784adc86e526d995156038ef00dc8a2c3824fc8497",
        "demons.rtf" to "9266dbb90f469e6a8e25099842611b4885fe87a8e47d8f3afe5b360ac05f737c",
        "fuck you.rtf" to "7de000882ef3fd5af7f693ccfe321d876908ceb82510ef2b3b9bf3171aa9d0d7",
        "idiot.rtf" to "45877c0d7e46f81fda6f970608aaf33c75a72dfc81749f8388d4c3685c3674bb",
        "little pussy.rtf" to "dcc6359c5dde362c66a8230f443eef138fd799f3d11e36195c1c547134de9991",
        "lousy writing 1.rtf" to "cdedcf97f89bf005f37c280b24261978016261b38b8b52c282f9d02a22ef41e1",
        "lousy writing 10.rtf" to "c74390b92a2fb014c990bec9d7e55edafba388540250fbb0c161d5b6355b204e",
        "lousy writing 11.rtf" to "8421f585635b270e10e3a7d0c8e19d2bfcd724e1fc57fe7bab4f7c10b3984b2d",
        "lousy writing 12.rtf" to "23270073ff486c5c51dfea7aade0e53d1ad76e7e786b7674291cb420bd651ece",
        "lousy writing 13.rtf" to "226937d6a06dfef5098e744cee572d6b3406629db945d0f7a9c5409a0c39006a",
        "lousy writing 2.rtf" to "d8e27b9b521e8b35876d222167d8758548017be5a396a14a4c31347c78eba587",
        "lousy writing 3.rtf" to "fcb0f8bf6480e2cdbc48b90ee33a2f764abe1923242e1dc551ffa9ae3a9bf8b0",
        "lousy writing 4.rtf" to "d5d7ca2ededef3c43da1bcf807a675334fb9f84aef4e23c12edf204456fc9c6b",
        "lousy writing 5.rtf" to "62afff64e64f1ea4742de5ab1ed0a4942c3970bdc972e2afe7c073529737a9a8",
        "lousy writing 6.rtf" to "6a7378c02744f80d57f72ced0ba22b7586ea7fca29859d141b7ccb5d0496e18c",
        "lousy writing 7.rtf" to "597b59c6f5b840eddcdf16bc7f019ada1054cc8d5d57ae4650d440a627bb4a63",
        "lousy writing 8.rtf" to "3ece73625d9b6be4ff6a47474ad86d92c9085da4a48f1db2e23dfa81f2bee252",
        "lousy writing 9.rtf" to "826afa203e4eebb2ba8642d9da4ec5d806da0b115ec091e709efa0b42e004851",
        "monster pussy.rtf" to "59b5e2dd311ec7caf54804cc28def740f0f56c1becd82e10a145cbd8fcd6532d",
        "piece of trial 1.rtf" to "f52a1ec3fca03b2fa110619b45fa6dc10c09482497ec27a08f12f068b91ef9cb",
        "piece of trial 2.rtf" to "32aacef827f6e2cec79b89756c9c8e5577f13f2bc00d54c106e5edb17dc57b93",
        "piece of trial 3.rtf" to "0f862699eada8b4f992be9a1c56adb6c3c78adf9e7cccc5ad1fc63b47ee15d0a",
        "piece of trial 4.rtf" to "ea6ab136dac9d1a2202330cd23dda7ded0a60bb4123f8979cc792c4842015bee",
        "pussy story.rtf" to "4a5a0c3166637945a25e54663bc4add1389306c2bd503a2cc26e81b178314dd2",
        "shitty document 1.rtf" to "06a795a429a38ef2f29fbad910aff5d666044de60b47ab8b3ec4f246320321fa",
        "shitty document 10.rtf" to "7c9855a5bfd29d30ec25bc58e2fc45435dd393ce0d344fbf9050825a7476c85d",
        "shitty document 11.rtf" to "49ff9f7489599ca6710715c30054ebadbfa0e1d3df5912972a9f382de06ab85d",
        "shitty document 12.rtf" to "71dc7e8798de2cbc13344d65ead91b22d4590d2786947042f67686fe0005e860",
        "shitty document 13.rtf" to "94391491d0171e8b7552eaf6d39a21db061a996495ae206e7022148f0c9f01a5",
        "shitty document 14.rtf" to "0a1a2c01aa1882da65286ab480de00102defe906f71e23e0b859d78b7e0c6337",
        "shitty document 15.rtf" to "a0935892715277c5144fa0ac37097da8dd061bae6fabb7666751c0a02becd8f8",
        "shitty document 16.rtf" to "fd5ff7f90ef662514b7a42e0eb930d38b503de38a164173fe4c5a9619d9df1fb",
        "shitty document 17.rtf" to "f650bc419ef6f4bc648e2cecdc70d6c1c9db6c2a990ac15fe00f840198739923",
        "shitty document 18.rtf" to "1fcee700e5b47fc390b680f93a0e561bd76aa220f9ecc6ce426a952788673fcd",
        "shitty document 19.rtf" to "632f7be63856b764ca8dbee37a24ceb8639aca74f5ff254cbe925546f6105a1b",
        "shitty document 2.rtf" to "df16f8bde268b877b1cc5f7bee8c9abaff258f8a227b283f27974fd00bad7786",
        "shitty document 20.rtf" to "f249df123d166c8510133a3b171cdd60b08a9b8221f8fe9ed03861b39aff0ec1",
        "shitty document 3.rtf" to "5033ad35c3633e67aaeb27d77c8812cf33ec3dd96c25c1b4f85459e4ffbddd3a",
        "shitty document 4.rtf" to "c67906d6b1053388f667ee814ad9032a8b1673babbf83d48444d5f9578734da1",
        "shitty document 5.rtf" to "f1ae8e0418e46b0a072f32feb23fcbe42a9b88d2cbed3f2baf6cb8cb764ce5ea",
        "shitty document 6.rtf" to "ff12e4c827749f66c8172ed8fee5b464fd0f2a297d68e281f79170f68864003d",
        "shitty document 7.rtf" to "4c66d5ef49d57dad4017ba253a6e7f81781b4b3f1e21a2b378ca5f1ac503d0c4",
        "shitty document 8.rtf" to "00a2903a4fbf7e8dc7144f5d8c4958b50176e62188c21fba5a01e4fe81b2b4d2",
        "shitty document 9.rtf" to "ef1ce1b2b537fd8fd686a0da6d610e9f1ea57b76bb232c6a3fb5f9d931a0892a",
        "shitty document.rtf" to "afa1610278c75e781ce2dd72192292ebd5330ac7c34465c66c5efe252fa899c4",
        "the trial.doc" to "f08504f3d7697dad7272117455cf97eadcb6f999dd8a89e0d41ab27fbd44c75c",
        "tiny pussy.rtf" to "cdf66bf6983f1bd9d2b79e6786a5a305cb85a4b58ac90c476c9e76814c9adfdf"
    )

    fun generateShit(len: Int): String {
        val bunchOfShit = buildString {
            while (length < len) {
                append("iamlongshit")
            }
        }

        return bunchOfShit.substring(0, len)
    }
}

