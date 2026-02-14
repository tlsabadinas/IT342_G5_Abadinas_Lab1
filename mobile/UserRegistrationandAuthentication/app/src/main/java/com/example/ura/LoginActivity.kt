package com.example.ura

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.username)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignUp = findViewById<TextView>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                performLogin(username, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin(user: String, pass: String) {
        // Run network operation in a background thread
        Thread {
            try {
                // IMPORTANT: Use 10.0.2.2 for emulator to access localhost
                val url = URL("http://10.0.2.2:8080/api/auth/login")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true

                // Create JSON Body
                val jsonInputString = JSONObject()
                jsonInputString.put("username", user)
                jsonInputString.put("password", pass)

                // Write Body
                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonInputString.toString())
                writer.flush()

                // Check Response Code
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    // Read Response
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Handle Success on UI Thread
                    // Inside LoginActivity.kt -> performLogin() -> runOnUiThread (Success block)

                    runOnUiThread {
                        Toast.makeText(this, "Login Success!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this, ProfileActivity::class.java)
                        // PASS THE USERNAME HERE
                        intent.putExtra("USERNAME", user)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    // Handle Failure
                    runOnUiThread {
                        Toast.makeText(this, "Login Failed: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }
                conn.disconnect()

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}