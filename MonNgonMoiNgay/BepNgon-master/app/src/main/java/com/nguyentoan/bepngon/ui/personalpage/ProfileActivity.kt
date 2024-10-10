package com.nguyentoan.bepngon.ui.personalpage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityProfileBinding
import com.nguyentoan.bepngon.databinding.LayoutBottomSheetMoreBinding
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.sever.AccountFBUtil
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.ui.baidang.DangBaiActivity
import com.nguyentoan.bepngon.util.Constant
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.hide
import com.nguyentoan.bepngon.view.setOnSafeClick
import com.nguyentoan.bepngon.view.show
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    var storageReference: StorageReference? = null

    var storage: FirebaseStorage? = null

    var userProfile = ProfileModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        getData()

        initListener()
    }

    private fun initListener() {
        binding.edtBirthDay.setOnSafeClick {
            showBottomSheet()
        }

        binding.imgAvt.setOnSafeClick {
            chooseImage()
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbNam -> {
                    userProfile.gender = true
                }
                R.id.rbNu -> {
                    userProfile.gender = false
                }
            }
        }

        binding.txtSave.setOnClickListener {
            if (binding.edtName.text.toString().trim().isEmpty()) {
                showToast("Tên không được trống!")
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        binding.prgLoad.show()
        val ref = storageReference!!.child("images/" + UUID.randomUUID().toString())
        filePath?.let {
            ref.putFile(it)
                .addOnSuccessListener {
                    val downloadUri: Task<Uri> = it.storage.downloadUrl
                    downloadUri.addOnSuccessListener { link ->
                        val imageLink = link.toString()
                        Log.d(Constant.TAG, "uploadImage: $imageLink")
                        userProfile.avt = imageLink
                        userProfile.name = binding.edtName.text.toString()
                        userProfile.birthDay = binding.edtBirthDay.text.toString()
                        userProfile.address = binding.edtAddress.text.toString()
                        userProfile.phoneNumber = binding.edtPhone.text.toString()
                        userProfile.gmail = binding.edtGmail.text.toString()
                        addNewProfile(userProfile)
                    }.addOnFailureListener {
                        Toast.makeText(this, "Có lỗi!", Toast.LENGTH_SHORT)
                            .show()
                        binding.prgLoad.hide()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Có lỗi!", Toast.LENGTH_SHORT)
                        .show()
                    binding.prgLoad.hide()
                }
                .addOnProgressListener { taskSnapshot ->

                }
        }?: kotlin.run {
            userProfile.name = binding.edtName.text.toString()
            userProfile.birthDay = binding.edtBirthDay.text.toString()
            userProfile.address = binding.edtAddress.text.toString()
            userProfile.phoneNumber = binding.edtPhone.text.toString()
            userProfile.gmail = binding.edtGmail.text.toString()
            addNewProfile(userProfile)
        }

    }

    private fun addNewProfile(profileModel: ProfileModel) {
        AccountFBUtil.mDatabase.child(FBConstant.PROFILE).child(SharePreferenceUtils.getAccountID())
            .setValue(profileModel).addOnSuccessListener {
                onBackPressed()
                getDataProfileUser()
                binding.prgLoad.hide()
            }.addOnFailureListener {
                Toast.makeText(this, "Có lỗi!", Toast.LENGTH_SHORT)
                    .show()
                binding.prgLoad.hide()
            }
    }

    private val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
    private fun getDataProfileUser() {
        val id = SharePreferenceUtils.getAccountID()
        mDatabase.child(FBConstant.PROFILE).child(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val profileModel = result.getValue<ProfileModel>()
                if (profileModel != null) {
                    if (profileModel != DataHelper.profileUser.value )
                        DataHelper.profileUser.postValue(profileModel)
                } else {
                    showToast("Có lỗi kết nối!")
                }
            }
        }.addOnFailureListener {
            showToast("Có lỗi kết nối!")
        }
    }

    private var filePath: Uri? = null

    private fun getData() {
        DataHelper.profileUser.observe(this){
            binding.edtName.setText(it.name)
            binding.edtGmail.setText(it.gmail)
            binding.edtAddress.setText(it.address)
            binding.edtPhone.setText(it.phoneNumber)
            binding.edtBirthDay.text = it.birthDay
            Picasso.get().load(it.avt).into(binding.imgAvt)
            if (it.gender){
                binding.rbNam.isChecked = true
                binding.rbNu.isChecked = false
            } else {
                binding.rbNam.isChecked = false
                binding.rbNu.isChecked = true
            }
            userProfile = it
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DangBaiActivity.PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                binding.imgAvt.setImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        filePath
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), DangBaiActivity.PICK_IMAGE_REQUEST)
    }

    var ngayThangNam = ""
    var namSinh = 2024
    private val myCalendar = Calendar.getInstance()
    private fun showBottomSheet() {
        val bottomSheetBinding = LayoutBottomSheetMoreBinding.inflate(layoutInflater)
        val moreBottomSheet =
            BottomSheetDialog(this)
        moreBottomSheet.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.namSinh.maxValue = 2024
        bottomSheetBinding.namSinh.minValue = 1950
        bottomSheetBinding.namSinh.value = myCalendar[Calendar.YEAR]
        bottomSheetBinding.namSinh.wrapSelectorWheel = false

        bottomSheetBinding.ngaySinh.maxValue = 31
        bottomSheetBinding.ngaySinh.minValue = 1
        bottomSheetBinding.ngaySinh.value = myCalendar[Calendar.DAY_OF_MONTH]
        bottomSheetBinding.ngaySinh.wrapSelectorWheel = false

        bottomSheetBinding.thangSinh.maxValue = 12
        bottomSheetBinding.thangSinh.minValue = 1
        bottomSheetBinding.thangSinh.value = myCalendar[Calendar.MONTH] + 1
        bottomSheetBinding.thangSinh.wrapSelectorWheel = false

        var isNhuan = false

        bottomSheetBinding.namSinh.setOnValueChangedListener { picker, oldVal, newVal ->
            val values = bottomSheetBinding.namSinh.value
            isNhuan = if (values % 100 == 0) {
                values % 400 == 0
            } else {
                values % 4 == 0
            }
            if (isNhuan) {
                if (bottomSheetBinding.thangSinh.value == 2) {
                    bottomSheetBinding.ngaySinh.maxValue = 29
                }
            } else {
                if (bottomSheetBinding.thangSinh.value == 2) {
                    bottomSheetBinding.ngaySinh.maxValue = 28
                }
            }
        }

        bottomSheetBinding.thangSinh.setOnValueChangedListener { picker, oldVal, newVal ->
            when (bottomSheetBinding.thangSinh.value) {
                1, 3, 5, 7, 8, 10, 12 -> {
                    bottomSheetBinding.ngaySinh.maxValue = 31
                }
                4, 6, 9, 11 -> {
                    bottomSheetBinding.ngaySinh.maxValue = 30
                }
                else -> {
                    if (isNhuan) {
                        bottomSheetBinding.ngaySinh.maxValue = 29
                    } else {
                        bottomSheetBinding.ngaySinh.maxValue = 28
                    }
                }
            }
        }

        moreBottomSheet.setOnDismissListener {
            ngayThangNam =
                "${bottomSheetBinding.ngaySinh.value}/${bottomSheetBinding.thangSinh.value}/${bottomSheetBinding.namSinh.value}"
            namSinh = bottomSheetBinding.namSinh.value
            binding.edtBirthDay.text = ngayThangNam
            binding.edtBirthDay.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
        }

        moreBottomSheet.show()
    }
}