package com.nguyentoan.bepngon.util

import android.content.Context
import android.content.SharedPreferences

object SharePreferenceUtils {

    val PER_NAME = "data_app_bep_ngon"

    lateinit var pre: SharedPreferences

    fun init(context: Context) {
        pre = context.getSharedPreferences(PER_NAME, Context.MODE_PRIVATE)
    }

    fun getAccountID() : String {
        return pre.getString("account_id", null).toString()
    }

    fun setAccountID(data: String?) {
        pre.edit().putString("account_id", data).apply()
    }

    fun getUserName() : String {
        return pre.getString("account_username", null).toString()
    }

    fun setUserName(data: String?) {
        pre.edit().putString("account_username", data).apply()
    }

    fun getPassword() : String {
        return pre.getString("account_password", null).toString()
    }

    fun setPassword(data: String?) {
        pre.edit().putString("account_password", data).apply()
    }

    fun getRole() : String {
        return pre.getString("account_role", "user").toString()
    }

    fun setRole(data: String?) {
        pre.edit().putString("account_role", data).apply()
    }

    fun isAdmin() : Boolean {
        return getRole() == "admin"
    }

}