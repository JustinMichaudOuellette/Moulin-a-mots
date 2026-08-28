package net.justinmo.wordsmith

import java.awt.BorderLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.random.Random

const val MAX_COUNT = 25

fun main() {
  WordSmithGUI()
}

class WordSmithGUI: JFrame("Inventeur de mots") {

  init {
    val wordSmith = WordSmith(Language.FR)
    defaultCloseOperation = EXIT_ON_CLOSE
    setSize(300, 300)
    val editText = JTextField()
    val list = JList<String>()
    list.setListData(wordSmith.generateNextWords(editText.text, Random(0), MAX_COUNT).toTypedArray())
    editText.document.addDocumentListener(object : DocumentListener {
      override fun insertUpdate(e: DocumentEvent?) {
        genWords()
      }

      override fun removeUpdate(e: DocumentEvent?) {
        genWords()
      }

      override fun changedUpdate(e: DocumentEvent?) {
        genWords()
      }

      fun genWords() {
        list.setListData(arrayOf())
        SwingUtilities.invokeLater {
          list.setListData(wordSmith.generateNextWords(editText.text.lowercase(), Random(0), MAX_COUNT).toTypedArray())
        }
      }
    })
    list.addListSelectionListener {
      if (list.selectedIndex >= 0) {
        SwingUtilities.invokeLater {
          val newText = list.model.getElementAt(list.selectedIndex)
          if (!newText.equals(editText.text)) {
            editText.text = newText
          }
          list.clearSelection()
        }
      }
    }
    contentPane.add(BorderLayout.NORTH, editText)
    contentPane.add(BorderLayout.CENTER, JScrollPane(list))
    isVisible = true
    setLocationRelativeTo(null)
    editText.requestFocus()
  }
}
