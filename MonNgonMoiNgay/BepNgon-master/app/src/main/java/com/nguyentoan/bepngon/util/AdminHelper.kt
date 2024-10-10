package com.nguyentoan.bepngon.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.ReportModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.sever.FirebaseDatabaseUtil

object AdminHelper {

    fun showDialogReport(context: Context, postId : String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_dialog_report_post)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val txtSend = dialog.findViewById<TextView>(R.id.txtSend)
        val txtCancel = dialog.findViewById<ImageView>(R.id.textView1)
        val editText = dialog.findViewById<EditText>(R.id.editText)

        txtCancel.setOnClickListener { dialog.dismiss() }

        txtSend.setOnClickListener {
            if (editText.text.toString().trim().isNotEmpty()) {
                val report = ReportModel(
                    DataUtil.ConvertToMD5(DataUtil.getTime()),
                    DataHelper.profileUser.value?.accountId?: "",
                    postId,
                    DataHelper.profileUser.value?.avt?: "",
                    DataHelper.profileUser.value?.name?: "",
                    editText.text.toString().trim(),
                    DataUtil.getTime()
                )
                FirebaseDatabase.getInstance().getReference(FirebaseDatabaseUtil.ROOT)
                    .child(FBConstant.REPORT_F).child(report.report_id)
                    .setValue(report)
                context.showToast("Gửi báo cáo thành công!")
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}