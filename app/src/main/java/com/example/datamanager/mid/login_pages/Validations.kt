package com.example.datamanager.mid.login_pages

import android.util.Patterns


fun isEmailValid(email: String): Boolean {
    return (Patterns.EMAIL_ADDRESS.matcher(email).matches())
}

fun passwordsMatches(password: String, password2: String): Boolean {
    return password == password2
}
