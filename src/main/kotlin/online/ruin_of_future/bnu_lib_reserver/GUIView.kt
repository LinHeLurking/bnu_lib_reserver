package online.ruin_of_future.bnu_lib_reserver

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.stage.StageStyle
import tornadofx.*

class GUIView : View() {

    override val root = vbox {
        spacing = 10.0
        prefWidth = 300.0
        prefHeight = 150.0
        val rootWidthProperty = this.widthProperty()
        var textWidthProperty: DoubleProperty? = null
        var inputWidthProperty: DoubleProperty? = null
        val textWidthRatio = 0.38
        val inputWidthRatio = 0.58
        hbox {
            vbox {
                text("User Name") {

                }
                this.prefWidthProperty().bind(rootWidthProperty * textWidthRatio)
                textWidthProperty = this.prefWidthProperty()
                alignment = Pos.CENTER
            }
            textfield(LogicalBridge.userNameProperty) {
                this.prefWidthProperty().bind(rootWidthProperty * inputWidthRatio)
                inputWidthProperty = this.prefWidthProperty()
            }
        }
        hbox {
            vbox {
                text("Password") {

                }
                this.prefWidthProperty().bind(textWidthProperty)
                alignment = Pos.CENTER
            }
            textfield(LogicalBridge.passwordProperty) {
                this.prefWidthProperty().bind(inputWidthProperty)
            }
        }
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0
            button("Captcha") {
                setOnAction {
                    LogicalBridge.showCaptchaWindow()
                }
            }
            button("Login") {
                setOnAction {
                    LogicalBridge.logIn()
                }
            }
        }
    }
}