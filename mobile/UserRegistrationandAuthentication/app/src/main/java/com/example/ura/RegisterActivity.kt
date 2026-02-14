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

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Initialize UI Views
        val etUsername = findViewById<EditText>(R.id.username)
        val etEmail = findViewById<EditText>(R.id.email)
        val etPassword = findViewById<EditText>(R.id.password)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.btnSignIn)

        // 2. Set Button Click Listener
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Basic validation
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                performRegister(username, email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Navigation to Login screen
        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close register so back button doesn't return here
        }
    }

    private fun performRegister(username: String, email: String, password: String) {
        Thread {
            try {
                // IMPORTANT: 10.0.2.2 is required for Emulator to see localhost
                val url = URL("http://10.0.2.2:8080/api/auth/register")

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 5000 // 5 seconds timeout
                conn.readTimeout = 5000

                // Create JSON Body
                val jsonInput = JSONObject()
                jsonInput.put("username", username)
                jsonInput.put("email", email)
                jsonInput.put("password", password)

                // Add role if your backend requires it (optional)
                // jsonInput.put("role", "USER")

                // Send Data
                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonInput.toString())
                writer.flush()
                writer.close()

                // Check Response
                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {

                    // Read Success Response
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Update UI
                    runOnUiThread {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show()
                        // Automatically go to Login
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // Read Error Response (if available)
                    val errorStream = conn.errorStream
                    val errorMsg = if (errorStream != null) {
                        BufferedReader(InputStreamReader(errorStream)).use { it.readText() }
                    } else {
                        "Error Code: $responseCode"
                    }

                    runOnUiThread {
                        Toast.makeText(this, "Failed: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }
                conn.disconnect()

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Connection Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}