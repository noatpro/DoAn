package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentModel(
    val commentId: String,
    val accountId: String,
    val postId: String,
    var content : String,
    var create_time : String
) : Parcelable {

    constructor() : this("", "", "", "", "") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "commentId" to commentId,
            "accountId" to accountId,
            "postId" to postId,
            "content" to content,
            "create_time" to create_time
        )
    }

    companion object {
        fun toPostModel(jsonData: String): CommentModel? {
            return Gson().fromJson(jsonData, CommentModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}