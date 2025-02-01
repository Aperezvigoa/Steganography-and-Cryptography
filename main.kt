package cryptography
import java.io.File
import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.ImageIO


class ImageProcessor {
    private fun loadImage(path: String): BufferedImage? {
        val userPath = File(path)
        try {
            val loadedImage: BufferedImage = ImageIO.read(userPath)
            return loadedImage
        } catch (e: Exception) {
            //println("Something goes wrong. ${e.message}")
            return null
        }
    }

    private fun saveImage(image: BufferedImage, path: File) {
        try {
            ImageIO.write(image, "png", path)
            //println("Exported successfully in $path")
        } catch (e: Exception) {
            println("Unexpected error ocurred.")
        }
    }

    private fun addBit(image: BufferedImage): BufferedImage {
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))
                val red = color.red or 1
                val green = color.green or 1
                val blue = color.blue or 1

                image.setRGB(x, y, Color(red, green, blue).rgb)
            }
        }
        return image
    }

    // Temporal and for testing reasons
    private fun exportPixelsRGB(image:BufferedImage, exportPath: File) {
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val color = Color(image.getRGB(x, y))

                val red = color.red
                val green = color.green
                val blue = color.blue

                exportPath.appendText("RED: ${red.toString(2)} || GREEN: ${green.toString(2)} || BLUE: ${blue.toString(2)}\n")
            }
        }
    }

    fun hide() {
        println("input image file:")
        val inputLine = readln()
        val inputImage = loadImage(inputLine)
        println("Output image file:")
        val outputLine = readln()
        if (inputImage == null) {
            println("Can't read input file!")
            return
        }
        println("Input image: $inputLine")
        println("Output image: $outputLine")
        val newImage = addBit(inputImage)
        saveImage(newImage, File(outputLine))
        println("Image ${outputLine} is saved.")
    }
}

fun main() {

    val processor = ImageProcessor()
    do {
        println("Task (hide, show, exit):")
        val userChoice = readln()
        when (userChoice) {
            "exit" -> {
                println("Bye!")
                break
            }
            "hide" -> processor.hide()
            "show" -> println("Obtaining message from image.")
            else -> println("Wrong task: [$userChoice]")
        }
    } while (true)
}