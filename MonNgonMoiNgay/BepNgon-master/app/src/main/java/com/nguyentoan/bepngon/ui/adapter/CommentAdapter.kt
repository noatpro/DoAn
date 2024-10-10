package com.nguyentoan.bepngon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.CommentModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.sever.ProfileFBListener
import com.nguyentoan.bepngon.sever.ProfileFBUtil
import com.nguyentoan.bepngon.ui.personalpage.PersonalPageActivity
import com.nguyentoan.bepngon.ui.personalpage.WithoutPageActivity
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.show
import com.squareup.picasso.Picasso

class CommentAdapter(
    var context: Context,
    private var listData: ArrayList<CommentModel>,
    val listener: EventClickCommentListener
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var imAvatar: ImageView = view.findViewById(R.id.imAvatar)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtContent: TextView = view.findViewById(R.id.txtContent)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
        var imgMore: ImageView = view.findViewById(R.id.imgMore)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_comment, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val comment = listData[position]

        viewHolder.txtContent.text = comment.content

//        viewHolder.imgMore.setOnClickListener {
//            listener.click(comment)
//        }

        viewHolder.txtTime.text = DataUtil.showTime(comment.create_time)

//        if (comment.accountId == SharePreferenceUtils.getAccountID()) {
//            viewHolder.imgMore.show()
//        } else {
//            viewHolder.imgMore.gone()
//        }

        viewHolder.imAvatar.setOnClickListener {
            if (comment.accountId == SharePreferenceUtils.getAccountID()) {
                context.openActivity(
                    PersonalPageActivity::class.java
                )
            } else {
                context.openActivity(
                    WithoutPageActivity::class.java,
                    bundleOf("idUser" to comment.accountId)
                )
            }
        }


        showInforComment(viewHolder.imAvatar, viewHolder.txtName, comment.accountId)
    }

    private fun showInforComment(imAvatar: ImageView, txtName: TextView, accountId: String) {
        ProfileFBUtil.getProfile(accountId, object : ProfileFBListener {
            override fun actionSuccess(profileModel: ProfileModel) {
                Picasso.get().load(profileModel.avt).into(imAvatar)
                txtName.text = profileModel.name
            }
            override fun actionFail() {}
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr : ArrayList<CommentModel>) {
        listData.clear()
        listData.addAll(arr)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}

interface EventClickCommentListener {
    fun click(commentModel : CommentModel)
}