package com.nguyentoan.bepngon.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.databinding.ActivityRoomChatBinding
import com.nguyentoan.bepngon.model.chat.RoomChatModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.ui.SearchUserActivity
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.view.openActivity

class RoomChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomChatBinding
    private lateinit var adapter: RoomChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RoomChatAdapter(this, ArrayList(), object : RoomChatAdapterListener{
            override fun click(roomChatModel: RoomChatModel) {
                val idUser = if (roomChatModel.accountId_1 != SharePreferenceUtils.getAccountID()){
                    roomChatModel.accountId_1
                } else {
                    roomChatModel.accountId_2
                }
                openActivity(ChatActivity::class.java,
                    bundleOf(
                        "idUser" to SharePreferenceUtils.getAccountID(),
                        "idYour" to idUser,
                    )
                )
            }
        })

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.imgSearchChat.setOnClickListener {
            openActivity(
                SearchUserActivity::class.java,
                bundleOf("action" to "chat")
            )
        }

        binding.msgShow.adapter = adapter

        getAllRoom()
    }

    private fun getAllRoom() {
        val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.CHAT_F)
        mDatabase.child(FBConstant.ROOM_F).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listData = ArrayList<RoomChatModel>()
                for (postSnapshot in dataSnapshot.children) {
                    postSnapshot.getValue<RoomChatModel>()?.let {
                        if ( (it.accountId_1 == SharePreferenceUtils.getAccountID()
                            || it.accountId_2 == SharePreferenceUtils.getAccountID()
                        ) && it.status) {
                            listData.add(
                                it
                            )
                        }
                    }
                }
                adapter.setListData(listData)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                
            }
        })
    }
}