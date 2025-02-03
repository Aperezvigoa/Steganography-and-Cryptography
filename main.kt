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

    // Stage 2
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

    // Stage 3

    private fun addBitInBlue(image: BufferedImage, message: List<Int>): BufferedImage {
        var mIterator: Int = 0

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                if (mIterator == message.size)
                    break

                val color = Color(image.getRGB(x, y))
                val red = color.red
                val green = color.green
                var blue = color.blue

                if (message[mIterator] == 0)
                    blue = color.blue and 254
                else
                    blue = color.blue or 1

                mIterator++
                image.setRGB(x, y, Color(red, green, blue).rgb)
            }
            if (mIterator == message.size)
                break
        }
        return image
    }

    private fun encodeMessage(message: String) = message.encodeToByteArray()

    private fun addingFinalKey(message: ByteArray): ByteArray {
        val finalMessage = message.copyOf().plus(0).plus(0).plus(3)
        return finalMessage
    }

    private fun checkSize(image: BufferedImage, message: ByteArray): Boolean {
        val messageBitLength = message.size * 8
        val availableSize = image.height * image.width
        return messageBitLength <= availableSize
    }

    private fun byteToBinaryString(message: ByteArray): String {
        val binaryString: String = message.joinToString("") { it.toString(2).padStart(8, '0') }
        return binaryString
    }

    private fun generateBinaryList(message: ByteArray): List<Int> {
        val binaryString = byteToBinaryString(message)
        val binaryList: MutableList<Int> = mutableListOf()

        for (b in binaryString) {
            binaryList.add(b.toString().toInt())
        }
        return binaryList
    }

    // Temporal and for testing reasons
    fun exportPixelsRGB(image:BufferedImage, exportPath: File) {
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x, y))

                val red = color.red
                val green = color.green
                val blue = color.blue

                exportPath.appendText("RED: ${red.toString(2)} || GREEN: ${green.toString(2)} || BLUE: ${blue.toString(2)}\n")
                //exportPath.appendText("Blue ${blue.toString(2)}\n")
            }
        }
    }

    fun hide() {
        // Request input image path
        println("input image file:")
        val inputLine = readln()
        val inputImage = loadImage(inputLine)

        // Request output image path
        println("Output image file:")
        val outputLine = readln()

        // Checking input image path
        if (inputImage == null) {
            println("Can't read input file!")
            return
        } //Hyperskill steganography program.

        // Request message to encrypt
        println("Message to hide:")
        val message = encodeMessage(readln())
        val codedMessage = addingFinalKey(message)

        // The message is bigger than the image?
        if (!checkSize(inputImage, codedMessage)) {
            println("The input image is not large enough to hold this message.")
            return
        }

        // Write the message in the image
        val binaryMessage = generateBinaryList(codedMessage)
        val encryptedImg = addBitInBlue(inputImage, binaryMessage)

        saveImage(encryptedImg, File(outputLine))
        println("Message saved in $outputLine image.")
    }

    private fun readImageBlueBits(image: BufferedImage): String {
        val readedBits = StringBuilder()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x, y))
                val blue = color.blue and 1
                readedBits.append(blue)
            }
        }
        return readedBits.toString()
    }

    private fun splitBinCode(readedBits: String, chunkSize: Int): MutableList<String> {
        val chunkedBits: MutableList<String> = mutableListOf()
        for (i in 0 until readedBits.length step chunkSize) {
            chunkedBits.add(readedBits.substring(i, (i + chunkSize).coerceAtMost(readedBits.length)))
        }
        return chunkedBits
    }

    private fun binToByte(chunkedBits: MutableList<String>): MutableList<UByte> {
        val byteMessage: MutableList<UByte> = mutableListOf()
        for (i in 0 until chunkedBits.size) {
            byteMessage.add(chunkedBits[i].toInt(2).toUByte())
        }
        return byteMessage
    }

    private fun findMessage(byteMessage: MutableList<UByte>): MutableList<UByte> {
        val sequenceToFind: List<UByte> = mutableListOf(0U, 0U, 3U)
        val cutPoint = byteMessage.windowed(sequenceToFind.size).indexOf(sequenceToFind)
        val message: MutableList<UByte> = mutableListOf()
        for (i in 0 until cutPoint) {
            message.add(byteMessage[i])
        }
        return message
    }

    private fun printMessage(byteMessage: MutableList<UByte>) {
        val decodedMessage = String(byteMessage.toUByteArray().toByteArray(), Charsets.UTF_8)
        println(decodedMessage)
    }

    fun show() {
        println("input image file:")
        val inputLine = readln()
        val inputImage = loadImage(inputLine)
        val imageBlueBits = readImageBlueBits(inputImage!!)
        val cleanedImgBlueBits = splitBinCode(imageBlueBits, 8)
        val byteImgBlueBits = binToByte(cleanedImgBlueBits)
        val message = findMessage(byteImgBlueBits)
        println("Message:")
        printMessage(message)
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
            "show" -> processor.show()
            else -> println("Wrong task: [$userChoice]")
        }
    } while (true)
}
