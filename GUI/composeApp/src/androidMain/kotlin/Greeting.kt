class Greeting {
    private val platform = getPlatform()
    var textFromAnotherClass = "this is text from another class!"

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
