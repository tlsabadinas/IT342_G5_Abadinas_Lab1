package com.example.ura

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ProfileActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Get the username passed from LoginActivity
        val username = intent.getStringExtra("USERNAME") ?: ""

        val tvUsername = findViewById<TextView>(R.id.username)
        val tvEmail = findViewById<TextView>(R.id.email)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // 2. Fetch Profile Data
        if (username.isNotEmpty()) {
            fetchProfileData(username, tvUsername, tvEmail)
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }

        // 3. Handle Logout
        btnLogout.setOnClickListener {
            // Clear any saved data (SharedPreferences) if you have any
            val intent = Intent(this, LoginActivity::class.java)
            // Clear back stack so user can't press "back" to return to profile
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun fetchProfileData(user: String, tvUser: TextView, tvEmail: TextView) {
        Thread {
            try {
                // Using 10.0.2.2 for Emulator
                // Ideally, your backend has an endpoint like /api/auth/profile/{username}
                // OR you send the token in the header.
                // Here is a generic GET request example:
                val url = URL("http://10.0.2.2:8080/api/auth/profile?username=$user")

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")

                // If your backend needs a JWT token, add it here:
                // conn.setRequestProperty("Authorization", "Bearer $yourToken")

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    // Parse JSON
                    val jsonResponse = JSONObject(response.toString())
                    // Assuming JSON is like: { "username": "admin", "email": "admin@test.com" }
                    val returnedUsername = jsonResponse.optString("username", "N/A")
                    val returnedEmail = jsonResponse.optString("email", "N/A")

                    runOnUiThread {
                        tvUser.text = "Username: $returnedUsername"
                        tvEmail.text = "Email: $returnedEmail"
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
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