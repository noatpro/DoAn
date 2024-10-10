package com.nguyentoan.bepngon.model.chat

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomChatModel(
    val room_id: String,
    val accountId_1: String,
    val accountId_2: String,
    val status : Boolean
) : Parcelable {

    constructor() : this("", "", "", false) {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "room_id" to room_id,
            "accountId_1" to accountId_1,
            "accountId_2" to accountId_2,
            "status" to status,
        )
    }
}