package eu.qiou.sudoku

import javafx.application.Application

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(Sudoku::class.java, *args)
    }
}