package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
class PostModel(
    var postId : String,
    var accountId : String,
    var tag : String,
    var content : String,
    var img : String,
    var create_time : String
) : Parcelable {
    constructor() : this("", "", "", "", "", "") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "accountId" to accountId,
            "tag" to tag,
            "content" to content,
            "img" to img,
            "create_time" to create_time
        )
    }

    companion object {
        fun toPostModel(jsonData: String): PostModel? {
            return Gson().fromJson(jsonData, PostModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}