fun main(args: Array<String>) {
    println("Hello World!")
    DifferenceGenerator(
        "src/main/resources/uncompressedWater.mov",
        "src/main/resources/uncompressedWater.mov",
        "src/main/resources/output.mov",
    )
    println("Program arguments: ${args.joinToString()}")
}
