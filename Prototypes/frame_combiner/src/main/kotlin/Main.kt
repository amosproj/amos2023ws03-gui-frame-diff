fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")



    //init
    val gifCreator = GifCreator()
    gifCreator.init("output.gif")


    // add images
    gifCreator.write("C:\\Users\\a_misc\\Desktop\\data\\TU_BERLIN\\WiSe_23_24\\AMOS\\prototypes\\frame_combiner\\src\\main\\resources\\Screenshot_50.png")
    gifCreator.write("C:\\Users\\a_misc\\Desktop\\data\\TU_BERLIN\\WiSe_23_24\\AMOS\\prototypes\\frame_combiner\\src\\main\\resources\\Screenshot_51.png")
    gifCreator.write("C:\\Users\\a_misc\\Desktop\\data\\TU_BERLIN\\WiSe_23_24\\AMOS\\prototypes\\frame_combiner\\src\\main\\resources\\Screenshot_52.png")


    // close
    gifCreator.close()

}