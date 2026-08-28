package net.justinmo.wordsmith

import org.tukaani.xz.LZMA2Options
import org.tukaani.xz.XZOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object XzUtils {

    /**
     * Compresses [source] into [target] with LZMA2 preset 9.
     *
     * The output is written to a sibling `*.tmp` file first and only renamed into
     * place once the stream is fully written and closed, so an interrupted or failed
     * run can never leave a truncated xz at [target].
     */
    fun compressToXz(source: File, target: File) {
        val options = LZMA2Options().apply { setPreset(9) }
        val tmp = File(target.parentFile, "${target.name}.tmp")
        try {
            FileOutputStream(tmp).use { fileOut ->
                XZOutputStream(fileOut, options).use { xzOut ->
                    FileInputStream(source).use { input ->
                        input.copyTo(xzOut)
                    }
                }
            }
            Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } finally {
            tmp.delete()
        }
    }
}
