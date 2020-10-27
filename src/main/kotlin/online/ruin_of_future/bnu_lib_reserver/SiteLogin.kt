package online.ruin_of_future.bnu_lib_reserver

import com.beust.klaxon.Klaxon
import javafx.scene.image.Image
import org.jsoup.Jsoup
import java.net.CookieHandler
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class SiteLogin {
    private val hostName = "http://seat.lib.bnu.edu.cn/"
    private val site = "${hostName}login?targetUri=%2F"
    private val captchaJsonURL = "${hostName}cap/captcha"
    private val captchaJsonURLRefer = "${hostName}simpleCaptcha/chCaptcha"
    private val captchaImgURL = "${hostName}cap/captchaImg"
    private val captchaCheckURL = "${hostName}cap/checkCaptcha"
    private val logInURL = "${hostName}auth/signIn"
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/85.0.4183.121 Safari/537.36"

    private var jSessionID: String = ""
    private var token: String = ""
    private var charToClick: List<String> = listOf()
    private var synchronizerToken = ""
    private var synchronizerURI = ""

    private val client: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
//        .cookieHandler(CookieHandler.getDefault())
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .build()

    init {
        firstConnection()
    }

    private fun firstConnection() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(site))
            .timeout(Duration.ofMinutes(1))
            .header("User-Agent", userAgent)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.headers().firstValue("Set-Cookie").isPresent) {
            jSessionID = response.headers().firstValue("Set-Cookie").get()
            jSessionID = "JSESSIONID=" + jSessionID.substringAfter("JSESSIONID=")
            val idx = jSessionID.indexOfFirst {
                it == ';'
            }
            jSessionID = jSessionID.removeRange(idx, jSessionID.length)
            println(jSessionID)
        }
        val html = response.body()!!
        val doc = Jsoup.parse(html)!!
        synchronizerToken = doc.select("[name='SYNCHRONIZER_TOKEN']").first().attr("value")!!
        synchronizerURI = doc.select("[name='SYNCHRONIZER_URI']").first().attr("value")!!
        assert(synchronizerToken.isNotEmpty())
        assert(synchronizerURI.isNotEmpty())
        assert(jSessionID.isNotEmpty())
        assert(charToClick.isNotEmpty())
    }

    private fun getCaptchaJson(): String {
//        TODO("Add fetch to simple captcha")
        val request = HttpRequest.newBuilder()
            .uri(URI.create(captchaJsonURL))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "application/json, text/javascript, */*; q=0.01")
            .header("Refer", captchaJsonURLRefer)
            .header("User-Agent", userAgent)
            .header("Cookie", jSessionID)
            .header("X-Requested-With","XMLHttpRequest")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.statusCode())
        println(response.body())
        val parsed = Klaxon().parse<CaptchaApiData>(response.body())!!
        token = parsed.token
        charToClick = parsed.data
        return response.body()
    }

    fun getCaptchaImage(): Image {
        getCaptchaJson()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${captchaImgURL}?token=${token}&r=${LogicalBridge.randGen.nextDouble()}"))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
            .header("Refer", captchaJsonURLRefer)
            .header("User-Agent", userAgent)
            .header("Cookie", jSessionID)
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
        return Image(response.body())
    }

    fun getCaptchaChars(): List<String> {
        return charToClick
    }

    fun checkCaptcha(): Boolean {
        val requestParameter = LogicalBridge.getCaptchaVerifyParameters(token)
        println("requestParameter $requestParameter")
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${captchaCheckURL}?${requestParameter}"))
            .timeout(Duration.ofMinutes(1))
            .header("Accept", "application/json, text/javascript, */*; q=0.01")
            .header("Refer", captchaJsonURLRefer)
            .header("User-Agent", userAgent)
            .header("Cookie", jSessionID)
            .header("X-Requested-With","XMLHttpRequest")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.body())
        val status = Klaxon().parse<Map<String, String>>(response.body())!!["status"]
        LogicalBridge.clearCaptchaClick()
        return status == "OK"
    }

    fun logIn(userName: String, passWord: String) {
        val formData = LogInFormData(synchronizerToken, synchronizerURI, userName, passWord, token)
        val form = LogicalBridge.getFormString(formData)
        println(form)
        val request = HttpRequest.newBuilder()
            .uri(URI.create(logInURL))
            .timeout(Duration.ofMinutes(1))
            .header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
            )
            .header("Origin", hostName)
            .header("DNT","1")
            .header("Accept-Encoding","gzip, deflate")
            .header("Accept-Language", "en-US,en;=0.9,zh-CN;q=0.8,zh;q=0.7")
            .header("Refer", site)
            .header("User-Agent", userAgent)
            .header("Cookie", jSessionID)
            .header("Upgrade-Insecure-Requests", "1")
            .header("Cache-Control", "max-age=0")

            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build()
        println(request.headers())
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        println(response.statusCode())
        println(response.body())
    }
}