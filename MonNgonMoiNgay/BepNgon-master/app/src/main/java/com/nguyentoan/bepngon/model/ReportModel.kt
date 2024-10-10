package com.nguyentoan.bepngon.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportModel(
    val report_id: String,
    val account_id: String,
    val post_Id: String,
    val img: String,
    val name: String,
    val content: String,
    val create_time: String
) : Parcelable, Comparable<ReportModel> {

    constructor() : this("", "", "","","","","") {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "report_id" to report_id,
            "account_id" to account_id,
            "post_Id" to post_Id,
            "img" to img,
            "name" to name,
            "content" to content,
            "create_time" to create_time,
        )
    }

    companion object {
        fun toReportModel(jsonData: String): ReportModel? {
            return Gson().fromJson(jsonData, ReportModel::class.java)
        }
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    override fun compareTo(other: ReportModel): Int {
        return if (create_time > other.create_time) -1
        else 1
    }
}