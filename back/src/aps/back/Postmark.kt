package aps.back

import aps.*
import into.kommon.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object Postmark {
    val mediaType = MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()

    val apiToken = System.getenv("APS_POSTMARK_TOKEN") ?: wtf("I want APS_POSTMARK_TOKEN env variable")
    val from = System.getenv("APS_POSTMARK_FROM") ?: wtf("I want APS_POSTMARK_FROM env variable")
    val url = "https://api.postmarkapp.com/email"

    fun send(email: Email) {
        val request = PostmarkRequest()-{o->
            o.From = "APS <$from>"
            o.To = email.to
            o.Subject = email.subject
            o.HtmlBody = email.html
        }
        val requestJSON = objectMapper.writeValueAsString(request)
        val body = RequestBody.create(mediaType, requestJSON)
        val httpRequest = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("X-Postmark-Server-Token", apiToken)
            .post(body)
            .build()
        val response = client.newCall(httpRequest).execute()
        val responseJSON = response.body().string()
        println(responseJSON)
    }

}

class PostmarkRequest {
    lateinit var From: String
    lateinit var To: String
    lateinit var Subject: String
    lateinit var HtmlBody: String
}

class PostmarkResponse {

}


