package online.ruin_of_future.bnu_lib_reserver

import javafx.geometry.Pos
import javafx.scene.control.Alert
import tornadofx.*
import kotlin.math.roundToInt

class CaptchaView : View() {
    override val root = vbox {
        spacing = 20.0
        val img = LogicalBridge.getCaptcha()
        val imgView = imageview(img) {
            alignment = Pos.CENTER
            setOnMouseClicked {
                val posX = it.x.roundToInt()
                val posY = it.y.roundToInt()
                LogicalBridge.captchaClick(posX, posY)
                if (LogicalBridge.shouldCheckCaptcha()) {
                    if (LogicalBridge.checkCaptcha()) {
                        alert(
                            Alert.AlertType.INFORMATION,
                            "Pass",
                            "Captcha passed"
                        )
                    } else {
                        alert(
                            Alert.AlertType.ERROR,
                            "Error",
                            "Captcha not passed, close captcha window and try again"
                        )
                    }
                    this@CaptchaView.currentStage?.close()
                }
            }
        }
        hbox {
            spacing = 5.0
            vbox {
                textfield {
                    val toClick = LogicalBridge.getCaptchaChars()
                    text = "依次点击 ${toClick[0]} ${toClick[1]} ${toClick[2]}"
                    isEditable = false
                }
            }
            vbox {
                textfield {
                    isEditable = false

                }
            }
        }
        println("Image size: height=${img.height}, width=${img.width}")
        prefWidth = img.width
        prefHeight = img.height
    }
}