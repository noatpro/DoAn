package com.nguyentoan.bepngon.util

import android.annotation.SuppressLint
import java.math.BigInteger
import java.security.MessageDigest
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object DataUtil {
    fun getTime(): String {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getIdByTime(): String {
        return ConvertToMD5(getTime())
    }

    fun ConvertToMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun cutTextLong(m: String, length: Int): String? {
        return if (m.length <= length) {
            m
        } else {
            m.substring(0, length - 13) + "...Xem thêm."
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun showTime(data : String) : String {
        val dateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        val date = dateFormat.parse(data)
        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        return date?.let { sdf.format(it) }?: kotlin.run {
            data
        }
    }

    fun checkSearch(text1 : String, text2 : String) : Boolean {
        val data1 = deAccent(text1.trim())
        val data2 = deAccent(text2.trim())
        return data1.lowercase().contains(data2.lowercase())
    }

    fun deAccent(str: String): String {
        val nfdNormalizedString: String = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }

    fun checkSpam(string: String): Boolean {
        val data  = string.lowercase()
        val list = listOf<String>(
            "vl",
            "vcl",
            "dm",
            "dmm",
            "cmm",
            "clgt",
            "ngu"
        )
        list.forEach {
            if (data.contains(it))
                return true
        }
        return false
    }
}