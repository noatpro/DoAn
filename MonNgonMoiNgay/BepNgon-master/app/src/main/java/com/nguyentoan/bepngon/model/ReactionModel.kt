package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReactionModel(
    val reactionId: String,
    val accountId: String,
    val postId: String,
    var create_time : String
) : Parcelable {

    constructor() : this("", "", "", "") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "reactionId" to reactionId,
            "accountId" to accountId,
            "postId" to postId,
            "create_time" to create_time
        )
    }

    companion object {
        fun toPostModel(jsonData: String): ReactionModel? {
            return Gson().fromJson(jsonData, ReactionModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}