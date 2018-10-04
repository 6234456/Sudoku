package eu.qiou.sudoku

import javafx.application.Application
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableIntegerArray
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.shape.Line
import javafx.stage.Stage


class Sudoku():Application() {


    val data:Array<Array<SimpleStringProperty>> = Array(9) { _ -> Array(9) {_ -> SimpleStringProperty("") }}
    val btnTxt:SimpleStringProperty = SimpleStringProperty("Überprüfen...")


    override fun start(primaryStage: Stage?) {
        val width = 600.0
        val reg = """^[1-9]{1}$""".toRegex()

        with(primaryStage!!){
            scene = Scene(
                    Group().apply{
                        with(this.children){
                            addAll(

                                GridPane().apply {

                                    0.until(9).forEach {
                                        columnConstraints.add(
                                                ColumnConstraints(width / 9)
                                        )

                                        rowConstraints.add(
                                                RowConstraints(width / 9)
                                        )
                                    }

                                    0.until(9).forEach {
                                        val x = it
                                        0.until(9).forEach {
                                            this.add(TextField().apply {
                                                this.textProperty().bindBidirectional(data[it][x])
                                                prefHeight = width / 9
                                                prefWidth = width / 9
                                                style = "-fx-alignment : center; -fx-font-family: Bahnschrift; -fx-font-size: 24px;"
                                                textProperty().addListener { _, oldValue, newValue ->
                                                    if(newValue.isEmpty()){
                                                        this.text = ""
                                                    }else{
                                                        if (reg.matches(newValue.last().toString())){
                                                            this.text = newValue.last().toString()
                                                            btnTxt.value = validate(it, x).toString()
                                                        }else{
                                                            this.text = oldValue
                                                        }
                                                    }
                                                }
                                            }, x, it)
                                        }
                                    }

                                    rowConstraints.add(
                                            RowConstraints(width / 9)
                                    )

                                    rowConstraints.add(
                                            RowConstraints(width / 9)
                                    )

                                    add(HBox().apply {
                                        GridPane.setColumnSpan(this, 4)
                                        with(this.children){
                                            addAll(
                                                    Button("Dem Nächsten").apply {
                                                        prefHeight = 60.0
                                                        prefWidth = 240.0
                                                        style = "-fx-font-family: 'Bahnschrift'; -fx-font-size: 18px"
                                                        setOnMouseClicked { _ ->
                                                            fill()
                                                        }
                                                    }
                                            )
                                        }
                                    }, 0, 10)

                                    add(HBox().apply {
                                        GridPane.setColumnSpan(this, 4)
                                        with(this.children){
                                            addAll(
                                                    Button().apply {
                                                        prefHeight = 60.0
                                                        prefWidth = 240.0
                                                        style = "-fx-font-family: 'Bahnschrift'; -fx-font-size: 18px"

                                                        textProperty().bindBidirectional(btnTxt)
                                                        setOnMouseClicked { _ ->
                                                            clearData()
                                                        }
                                                    }
                                            )
                                        }
                                    }, 5, 10)

                                },

                                Line().apply {
                                    startX = width / 3
                                    startY = 0.0
                                    endY = width
                                    endX = startX
                                    style = "-fx-stroke-width:3px"
                                },

                                Line().apply {
                                    startX = width / 3 * 2
                                    startY = 0.0
                                    endY = width
                                    endX = startX
                                    style = "-fx-stroke-width:3px"
                                },

                                Line().apply {
                                    startY = width / 3
                                    startX = 0.0
                                    endX = width
                                    endY = startY
                                    style = "-fx-stroke-width:3px"
                                },

                                Line().apply {
                                    startY = width / 3 * 2
                                    startX = 0.0
                                    endX = width
                                    endY = startY
                                    style = "-fx-stroke-width:3px"
                                }
                            )
                        }
                    },
                    600.0, 750.0)

            title = "Sudoku-Qiou"

            show()
        }
    }

    private fun clearData(){
        for (i in 0.until(9)){
            for (j in 0.until(9)){
                data[i][j].value = ""
            }
        }
    }


    private fun fill(arr :Iterable<MutableList<Int>> = convert().map { it.toMutableList() }){

        val init = arr.map { it.map { it } }

        fun occupied(i:Int, j:Int) :Boolean {
            return init.elementAt(i).elementAt(j) != 0
        }

        fun getLast(i:Int, j:Int):Pair<Int, Int>{
            if (i == 0 && j == 0)
                return 0 to 0
            else if (j == 0){
                return i-1 to 8
            }else{
                return i to j - 1
            }
        }

        fun getLast1(i:Int, j:Int):Pair<Int, Int>{
            val (i1, j1) = getLast(i,j)
            if(occupied(i1, j1)){
                return getLast1(i1, j1)
            }

            return getLast(i, j)
        }


        fun getNext(i:Int, j:Int):Pair<Int, Int>{
            if (i == 8 && j == 8)
                return 8 to 8
            else if (j == 8){
                return i+1 to 0
            }else{
                return i to j+1
            }
        }

        fun getNext1(i:Int, j:Int):Pair<Int, Int>{
            val (i1, j1) = getNext(i,j)
            if(occupied(i1, j1)){
                return getNext1(i1, j1)
            }

            return getNext(i, j)
        }

        tailrec fun trail(i:Int, j:Int){
            var k = arr.elementAt(i)[j]
            var flag = false

            while (k < 9){
                arr.elementAt(i)[j] = ++k
                if (validate(i, j, arr)) {
                    flag = true
                    break
                }
            }

            if(flag){
                data[i][j].value = k.toString()
                if(!(i == 8 && j == 8)){
                    val (i1, j1) = getNext1(i, j)
                    trail(i1, j1)
                }
            }
            else{
                data[i][j].value = ""
                arr.elementAt(i)[j] = 0
                val (i1, j1) = getLast1(i, j)
                trail(i1, j1)
            }
        }

        if (occupied(0,0)){
            val (i1, j1) = getNext1(0, 0)
            trail(i1, j1)
        }else{
            trail(0, 0)
        }

    }


    private fun convert() : Iterable<Iterable<Int>> {
        return data.map { it.map { if(it.value.isEmpty()) 0 else it.value.toInt() } }
    }

    private fun validate( arr :Iterable<Iterable<Int>> = convert() ): Boolean {

        return arr.all { it.nonNullNotDuplicated() } &&
                arr.zipMe().all { it.nonNullNotDuplicated() } &&
                arr.take(3).zipMe().let {
                    it.take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(3).take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(6).flatten().nonNullNotDuplicated()
                } &&
                arr.drop(3).take(3).zipMe().let {
                    it.take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(3).take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(6).flatten().nonNullNotDuplicated()
                } &&
                arr.drop(6).zipMe().let {
                    it.take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(3).take(3).flatten().nonNullNotDuplicated() &&
                    it.drop(6).flatten().nonNullNotDuplicated()
                }
    }

    private fun validate(x:Int, y:Int, arr :Iterable<Iterable<Int>> = convert()): Boolean {

        return arr.elementAt(x).nonNullNotDuplicated()  &&
                arr.zipMe().elementAt(y).nonNullNotDuplicated() &&
                when{
                    x < 3           -> arr.take(3).zipMe().let {
                                            when{
                                                y < 3           ->  it.take(3).flatten().nonNullNotDuplicated()
                                                y >=3 && y < 6  ->  it.drop(3).take(3).flatten().nonNullNotDuplicated()
                                                else            ->  it.drop(6).flatten().nonNullNotDuplicated()
                                            }
                                        }
                    x>= 3 && x < 6 -> arr.drop(3).take(3).zipMe().let {
                                            when{
                                                y < 3           ->  it.take(3).flatten().nonNullNotDuplicated()
                                                y >=3 && y < 6  ->  it.drop(3).take(3).flatten().nonNullNotDuplicated()
                                                else            ->  it.drop(6).flatten().nonNullNotDuplicated()
                                            }
                                        }
                    else          ->  arr.drop(6).zipMe().let {
                                            when{
                                                y < 3           ->  it.take(3).flatten().nonNullNotDuplicated()
                                                y >=3 && y < 6  ->  it.drop(3).take(3).flatten().nonNullNotDuplicated()
                                                else            ->  it.drop(6).flatten().nonNullNotDuplicated()
                                            }
                                        }
                }
    }


    fun Iterable<Int>.nonNullNotDuplicated(): Boolean = this.filter { it > 0 }.let { it.count() == it.toSet().count() }


    fun <T> Iterable<Iterable<T>>.zipMe(): List<List<T>>{
        if(this.count() == 2){
            return this.elementAt(0).zip(this.elementAt(1)) {
                a: T, b: T ->  listOf(a, b)
            }
        }else{
            return this.take(this.count() -1).zipMe().zip(this.last()){
                a: List<T>, b: T ->
                    a + b
            }
        }
    }
}