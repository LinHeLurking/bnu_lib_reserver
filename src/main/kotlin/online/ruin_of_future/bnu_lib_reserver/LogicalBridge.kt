package online.ruin_of_future.bnu_lib_reserver

import com.beust.klaxon.Klaxon
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.image.*
import javafx.stage.Stage
import tornadofx.View
import tornadofx.alert
import tornadofx.getProperty
import java.lang.StringBuilder
import java.net.URLEncoder
import java.util.*
import kotlin.random.Random
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties


object LogicalBridge {
    private val siteLogin = SiteLogin()

    val randGen = Random(System.currentTimeMillis())
    var userNameProperty = SimpleStringProperty()
    var passwordProperty = SimpleStringProperty()

    private val captchaClickPos = mutableListOf<Pair<Int, Int>>()

    private fun getUserName(): String {
        return userNameProperty.valueSafe
    }

    private fun getPassWord(): String {
        return passwordProperty.valueSafe
    }

    fun showCaptchaWindow() {
        CaptchaView().openWindow()?.setOnCloseRequest {
            clearCaptchaClick()
        }
    }

    fun getTestCaptcha(): Image {
        val url: String = this::class.java.getResource("/test/captchaImg.jpg").toString()
        return Image(url, false)
    }

    fun captchaClick(x: Int, y: Int) {
        captchaClickPos.add(x to y)
    }

    fun shouldCheckCaptcha(): Boolean {
        return captchaClickPos.size >= 3
    }

    fun checkCaptcha(): Boolean {
        return siteLogin.checkCaptcha()
    }

    fun getCaptcha(): Image {
        return siteLogin.getCaptchaImage()
    }

    fun getCaptchaChars(): List<String> {
        return siteLogin.getCaptchaChars()
    }

    fun getCaptchaVerifyParameters(token: String): String {
        val click = "[{\"x\":${captchaClickPos[0].first},\"y\":${captchaClickPos[0].second}}," +
                "{\"x\":${captchaClickPos[1].first},\"y\":${captchaClickPos[1].second}}," +
                "{\"x\":${captchaClickPos[2].first},\"y\":${captchaClickPos[2].second}}]"
        println("click $click")
        val clickBase64 = String(Base64.getEncoder().encode(click.toByteArray()))
        println("clickBase64 $clickBase64")
        return "a=${clickBase64}&token=${token}&userId="
    }

    fun clearCaptchaClick() {
        captchaClickPos.clear()
    }

    fun logIn() {
        siteLogin.logIn(getUserName(), getPassWord())
    }

    fun escape(src: String): String {
        return URLEncoder.encode(src, "UTF-8")
    }

    @Suppress("UNCHECKED_CAST")
    fun getFormString(src: Any): String {
        TODO("Not implemented")
    }
}