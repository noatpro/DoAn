package com.nguyentoan.bepngon.ui.user.main.noti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.databinding.FragmentNotificationBinding
import com.nguyentoan.bepngon.model.NotificationModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast

class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding

    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)

        val  listDemo = ArrayList<NotificationModel>()

        adapter = NotificationAdapter(requireContext(), listDemo)
        binding.toolBar.txtTitle.text = "Thông báo"

        binding.rcyNotification.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotification()
    }

    private val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
    private fun getNotification() {

        mDatabase.child(FBConstant.NOTI_F).child(SharePreferenceUtils.getAccountID()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listData = ArrayList<NotificationModel>()
                for (postSnapshot in dataSnapshot.children) {
                    postSnapshot.getValue<NotificationModel>()?.let {
                        listData.add(
                            it
                        )
                    }
                }
                listData.sort()
                adapter.setListData(listData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                requireContext().showToast("Lỗi kết nối!")
            }
        })
    }


    
}