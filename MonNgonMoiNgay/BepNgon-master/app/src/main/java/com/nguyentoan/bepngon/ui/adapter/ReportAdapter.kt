package com.nguyentoan.bepngon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.NotificationModel
import com.nguyentoan.bepngon.model.PostModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.model.ReportModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.ui.baidang.DetailBaiDangActivity
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.openActivity
import com.squareup.picasso.Picasso

class ReportAdapter(
    var context: Context,
    private var listData: ArrayList<ReportModel>
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtContent: TextView = view.findViewById(R.id.txtContent)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
        var llView: LinearLayout = view.findViewById(R.id.llView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_report, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val noti = listData[position]

        Picasso.get().load(noti.img).into(viewHolder.imgAvatar)

        viewHolder.txtName.text = noti.name
        viewHolder.txtContent.text = "Đã báo cáo 1 bài viết: "+noti.content
        viewHolder.txtTime.text = DataUtil.showTime(noti.create_time)

        viewHolder.llView.setOnClickListener {
            clickNoti(noti.post_Id, noti)
        }
    }

    private fun clickNoti(postId : String, reportModel: ReportModel) {

        FirebaseDatabase.getInstance().getReference(FBConstant.ROOT).child(FBConstant.POST_F).child(postId).get().addOnCompleteListener{ task->
            if (task.isSuccessful) {
                val result = task.result
                val post = result.getValue<PostModel>()
                if (post != null) {
                    context.openActivity(
                        DetailBaiDangActivity::class.java,
                        bundleOf("post_data" to post.toJson(),
                            "report_data" to reportModel.toJson())
                    )
                } else {
                    context.showToast("Bài viết không còn!")
                }
            } else {
                context.showToast("Bài viết không còn!")
            }

        }.addOnFailureListener {
            context.showToast("Lỗi kết nối!")
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr : ArrayList<ReportModel>) {
        listData.clear()
        listData.addAll(arr)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}