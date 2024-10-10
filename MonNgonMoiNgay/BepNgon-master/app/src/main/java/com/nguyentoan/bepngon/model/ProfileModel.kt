package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import com.nguyentoan.bepngon.util.Constant
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileModel(
    var profileId: String,
    var accountId: String,
    var name: String,
    var avt: String,
    var birthDay: String,
    var gender: Boolean,
    var gmail: String,
    var phoneNumber:  String,
    var address: String,
) : Parcelable {

    constructor() : this("", "", "", Constant.URL_AVATAR_DEFAUT, "",true,"", "","") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "profileId" to profileId,
            "accountId" to accountId,
            "name" to name,
            "avt" to avt,
            "birthDay" to birthDay,
            "gender" to gender,
            "gmail" to gmail,
            "phoneNumber" to phoneNumber,
            "address" to address,
        )
    }

    companion object {
        fun toPostModel(jsonData: String): ProfileModel? {
            return Gson().fromJson(jsonData, ProfileModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }
}
