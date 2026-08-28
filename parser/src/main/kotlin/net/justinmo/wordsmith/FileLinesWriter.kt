package net.justinmo.wordsmith

import java.io.*
import java.nio.charset.StandardCharsets

class FileLinesWriter(outputFilePath: String) {

  private val file: File = File(outputFilePath)
  private val bufferedWrite: BufferedWriter

  init {
    if (file.exists()) {
      file.delete()
      file.createNewFile()
    }
    bufferedWrite = BufferedWriter(OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8))
  }

  fun write(lines: List<String>) {
    for (line in lines) {
      writeLine(line)
    }
  }

  fun write(string: String): FileLinesWriter {
    bufferedWrite.write(string)
    return this
  }

  fun writeLine(line: String): FileLinesWriter {
    bufferedWrite.write(line)
    return writeLine()
  }

  fun writeLine(): FileLinesWriter {
    bufferedWrite.write("\n")
    return this
  }

  fun close() {
    bufferedWrite.flush()
    bufferedWrite.close()
  }
}
