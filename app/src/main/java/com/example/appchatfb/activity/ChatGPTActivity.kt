package com.example.appchatfb.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appchatfb.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatGPTActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatgpt)

        val etQuestion=findViewById<EditText>(R.id.etQuestion)
        val btnSubmit=findViewById<Button>(R.id.btnSubmit)
        val txtResponse=findViewById<TextView>(R.id.txtResponse)

        btnSubmit.setOnClickListener {
            val question=etQuestion.text.toString().trim()
            Toast.makeText(this,question, Toast.LENGTH_SHORT).show()
            if(question.isNotEmpty()){
                getResponse(question) { response ->
                    runOnUiThread {
                        txtResponse.text = response
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.optionsmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.chatGPT ->{
                Toast.makeText(
                    getApplicationContext(),
                    "You are in Chat GPT",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            }
            R.id.chat ->{
                val intent = Intent(this@ChatGPTActivity, UsersActivity::class.java)
                startActivity(intent)
            }
            R.id.logout->{
                val intent = Intent(this@ChatGPTActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return (super.onOptionsItemSelected(item))
    }

    fun getResponse(question: String, callback: (String) -> Unit){
        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"

        val requestBody="""
            {
            "prompt": "$question",
            "max_tokens": 500,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error","API failed",e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else{
                    Log.v("data","empty")
                }
                val jsonObject=JSONObject(body)
                val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }

}