package net.justinmo.wordsmith

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class FileLinesParser(val filePath: String) {

  fun parse(readLine: (line: String, lineNumber: Int) -> Unit) {
    val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(filePath), StandardCharsets.UTF_8))
    var line: String? = bufferedReader.readLine()?.trim()?.lowercase()
    var lineNumber = 0
    while (line != null) {
      if (line.isEmpty()) {
        println("line %d is empty".format(lineNumber))
      } else {
        readLine(line, lineNumber)
      }
      line = bufferedReader.readLine()?.trim()?.lowercase()
      lineNumber++
    }
    bufferedReader.close()
  }
}
