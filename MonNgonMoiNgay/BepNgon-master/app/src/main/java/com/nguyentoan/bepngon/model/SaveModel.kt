package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SaveModel(
    val saveId: String,
    val accountId: String,
    val postId: String,
    var create_time : String
) : Parcelable {

    constructor() : this("", "", "", "") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "saveId" to saveId,
            "accountId" to accountId,
            "postId" to postId,
            "create_time" to create_time
        )
    }

    companion object {
        fun toPostModel(jsonData: String): SaveModel? {
            return Gson().fromJson(jsonData, SaveModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}