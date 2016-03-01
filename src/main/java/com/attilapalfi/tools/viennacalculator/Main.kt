package com.attilapalfi.tools.viennacalculator

import com.attilapalfi.tools.viennacalculator.controller.Controller
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        //val fxml = javaClass.classLoader.getResource("fxml/ViennaCalculator.fxml")
        val loader: FXMLLoader = FXMLLoader(javaClass.classLoader.getResource("fxml/ViennaCalculator.fxml"))
        val root = loader.load<Parent>()
        val controller: Controller = loader.getController()
        controller.stage = primaryStage
        primaryStage.title = "Vienna Calculator"
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }

    companion object {
        fun main(vararg args: String) {
            Application.launch(*args)
        }
    }
}
