# Steganography-and-Cryptography

This project implements image steganography, a technique for hiding secret messages within images.  It uses the least significant bit (LSB) method to embed the message within the image's pixel data.  The project is written in Kotlin.

## Features

* **Hiding:** Encodes a text message within an image using a password for encryption.
* **Showing:** Decodes a hidden message from an image, requiring the same password used for encryption.
* **Encryption:** Uses a simple XOR cipher with a user-provided password to encrypt the message before embedding it in the image.  This adds a layer of security, making it more difficult to extract the message without the password.
* **LSB Modification:**  Modifies the least significant bit of the blue color component of each pixel to store the message bits.
* **Stop Key:** Appends a specific sequence of bits (two sets of eight zeros followed by 00000111) to the encoded message. This acts as a marker to easily identify the end of the message when decoding.

## How it works

1. **Encoding (Hide):**
    * Takes the input image path, output image path, the message to hide, and a password as input.
    * Converts the message and password to binary representations.
    * Encrypts the message using an XOR cipher with the password.
    * Embeds the encrypted message's bits into the least significant bits of the blue color values of the image's pixels.
    * Saves the modified image to the specified output path.

2. **Decoding (Show):**
    * Takes the input image path and the password as input.
    * Reads the LSBs of the blue color values from the image's pixels.
    * Locates the "stop key" to identify the end of the embedded message.
    * Decrypts the extracted bits using the XOR cipher with the password.
    * Converts the decrypted binary back into the original text message.

## Getting Started

1. **Prerequisites:**
    * JDK (Java Development Kit) installed.
    * Kotlin compiler installed.  (If you use an IDE like IntelliJ IDEA, this is typically included).

2. **Compilation:**
    * You can compile the Kotlin code using the command-line compiler: `kotlinc *.kt -d output.jar`
    * Or, if you are using an IDE, you can build the project directly within the IDE.

3. **Running:**
    * Run the compiled JAR file: `java -jar output.jar`

## Limitations

* **Capacity:** The size of the message that can be hidden is limited by the image dimensions.  Larger images can hold more data.
* **Security:** The XOR cipher used is a relatively simple form of encryption.  For more robust security, consider using stronger encryption algorithms.
* **Format:** Currently, the code primarily supports PNG image format due to its lossless compression. Other image formats might introduce compression artifacts that could corrupt the hidden data.

## Author

Aperezvigoa
