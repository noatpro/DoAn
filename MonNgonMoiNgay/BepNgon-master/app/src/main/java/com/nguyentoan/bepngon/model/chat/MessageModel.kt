package com.nguyentoan.bepngon.model.chat

import android.os.Message
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageModel(
    val message_id : String,
    val room_id: String,
    val accountId: String,
    val content: String,
    var seen : Boolean,
    val crete_time : String
) : Parcelable {

    constructor() : this("","", "", "",false,"") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "message_id" to message_id,
            "room_id" to room_id,
            "accountId" to accountId,
            "content" to content,
            "seen" to seen,
            "crete_time" to crete_time
        )
    }
}