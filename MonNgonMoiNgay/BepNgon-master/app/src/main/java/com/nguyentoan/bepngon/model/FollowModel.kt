package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
class FollowModel(
    var followId : String,
    var accountId : String,
    var account_follow_id : String,
    var create_time : String
) : Parcelable {
    constructor() : this("", "", "", "") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "followId" to followId,
            "accountId" to accountId,
            "account_follow_id" to account_follow_id,
            "create_time" to create_time
        )
    }

    companion object {
        fun toPostModel(jsonData: String): FollowModel? {
            return Gson().fromJson(jsonData, FollowModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}