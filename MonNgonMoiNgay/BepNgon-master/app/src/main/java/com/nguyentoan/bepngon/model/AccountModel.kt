package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountModel(
    val account_id: String,
    val userName: String,
    val password: String,
    val role : String = "user",
    var status: Boolean
) : Parcelable {

    constructor() : this("", "", "","user",false) {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "account_id" to account_id,
            "userName" to userName,
            "password" to password,
            "role" to role,
            "status" to status
        )
    }

    companion object {
        fun toPostModel(jsonData: String): AccountModel? {
            return Gson().fromJson(jsonData, AccountModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
