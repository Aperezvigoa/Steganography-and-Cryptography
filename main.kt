package cryptography
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.IIOException

class TextProcessor {

    fun setPath(input: String) = File(input)

    fun getByteArr(input: String) = input.encodeToByteArray()

    fun getBinary(input: ByteArray): List<Int> {
        val binCharMessage = input.joinToString("") { it.toString(2).padStart(8, '0') }.toMutableList()
        val binIntMessage: MutableList<Int> = mutableListOf()
        for (b in binCharMessage) {
            binIntMessage.add(b.toString().toInt())
        }
        return binIntMessage.toList()
    }

    fun xorMessage(message: List<Int>, passwordBin: List<Int>): List<Int> {
        var pIterator = 0
        var encodedMessage: MutableList<Int> = mutableListOf()

        for(b in message.indices) {
            if (pIterator == passwordBin.size)
                pIterator = 0
            encodedMessage.add(message[b] xor passwordBin[pIterator])
            pIterator++
        }
        encodedMessage = writeStopKey(encodedMessage)
        return encodedMessage
    }

    private fun writeStopKey(input: MutableList<Int>): MutableList<Int> {
        val zeroValues: List<Int> = listOf(0,0,0,0,0,0,0,0)
        val threeValue: List<Int> = listOf(0,0,0,0,0,1,1,1)
        return input.plus(zeroValues).plus(zeroValues).plus(threeValue).toMutableList()
    }

    fun findMessageBin(imageBin: MutableList<Int>): MutableList<Int> {
        val sequenceToFind: List<Int> = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1)
        val cutPoint = imageBin.windowed(sequenceToFind.size).indexOf(sequenceToFind)
        val message: MutableList<Int> = mutableListOf()
        for (i in 0 until cutPoint) {
            message.add(imageBin[i])
        }
        return message
    }

    fun bintoText(binario: List<Int>): String {
        val message = binario.subList(0, binario.size - 24)
        val bytes = message.chunked(8) { it.joinToString("").toInt(2).toByte() }
        return String(bytes.toByteArray(), Charsets.UTF_8)
    }
}

class ImageProcessor(inputPath: File, passwordBin: List<Int>) {

    val textProcessor = TextProcessor()
    val inputPath = inputPath
    val passwordBin = passwordBin
    var outputPath = File("C:\\Users\\NAME\\Desktop\\")
    var messageBin = listOf(0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0) // Kotlin in binary :P
        set(value) {
            if (value.isEmpty())
                throw IllegalArgumentException("Message cant be blank")
            field = value
        }

    constructor(inputPath: File, outputPath: File, messageBin: List<Int>, passwordBin: List<Int>): this(inputPath, passwordBin) {
        this.outputPath = outputPath
        this.messageBin = messageBin
    }

    fun checkSize(message: List<Int>, image: BufferedImage): Boolean {
        if((message.size * 8) <= (image.height * image.width))
            return true
        return false
    }

    fun readImage(path: File): BufferedImage? {
        try {
            val inputImage: BufferedImage = ImageIO.read(path)
            return inputImage
        } catch (e: IIOException) {
            println("Path not found!")
            return null
        } catch (e: Exception) {
            println("Unexpected error!")
            return null
        }
    }

    fun writeImage(image: BufferedImage, encodedMessage: List<Int>): BufferedImage {
        var bIterator = 0
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                if (bIterator == encodedMessage.size)
                    break

                val color = Color(image.getRGB(x, y))
                var blue = color.blue
                blue = (blue and 0b11111110) or encodedMessage[bIterator]

                bIterator++
                image.setRGB(x, y, Color(color.red, color.green, blue).rgb)
            }
            if (bIterator == encodedMessage.size)
                break
        }
        return image
    }

    fun readImageBin(image: BufferedImage): MutableList<Int> {
        val imageBits: MutableList<Int> = mutableListOf()
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val color = Color(image.getRGB(x,y))
                val blue = color.blue
                imageBits.add(blue.toString(2).last().toString().toInt())
            }
        }
        return imageBits
    }
}

class Options {
    val textProcessor = TextProcessor()

    fun hide() {
        println("Input image file:")
        val inputPath = textProcessor.setPath(readln())
        println("Output image file:")
        val outputPath = textProcessor.setPath(readln())
        println("Message to hide:")
        val messageBytes = textProcessor.getByteArr(readln())
        println("Password:")
        val passwordBytes = textProcessor.getByteArr(readln())

        val messageBin = textProcessor.getBinary(messageBytes)
        val passwordBin = textProcessor.getBinary(passwordBytes)

        val encodedMessage = textProcessor.xorMessage(messageBin, passwordBin)

        val imageProcessor = ImageProcessor(inputPath, outputPath, encodedMessage, passwordBin)
        val inputImage = imageProcessor.readImage(inputPath) ?: return

        if (!imageProcessor.checkSize(encodedMessage, inputImage))
            return

        val encodedImage = imageProcessor.writeImage(inputImage, encodedMessage)
        ImageIO.write(encodedImage, "png", outputPath)
        println("Message saved in $outputPath image.")
    }

    fun show() {
        println("Input image file:")
        val inputPath = textProcessor.setPath(readln())
        println("Password:")
        val password = textProcessor.getByteArr(readln())
        val passwordBin = textProcessor.getBinary(password)

        val imageProcessor = ImageProcessor(inputPath, passwordBin)
        val image = imageProcessor.readImage(inputPath) ?: return
        val imageBits = imageProcessor.readImageBin(image)
        val encodedMessageBin = textProcessor.findMessageBin(imageBits)
        val messageBin = textProcessor.xorMessage(encodedMessageBin, passwordBin)
        println("Message:")
        println(textProcessor.bintoText(messageBin))
    }
}

class Menu {
    val option = Options()
    fun run() {
        do {
            println("Task (hide, show, exit):")
            val userChoice = readln()
            when (userChoice) {
                "hide" -> option.hide()
                "show" -> option.show()
                "exit" -> break
                else -> println("Wrong task: [$userChoice]")
            }
        } while(true)
        println("Bye!")
    }
}

fun main() {
    val menu = Menu()
    menu.run()
}
