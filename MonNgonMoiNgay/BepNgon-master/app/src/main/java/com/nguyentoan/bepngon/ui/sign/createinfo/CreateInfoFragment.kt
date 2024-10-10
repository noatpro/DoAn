package com.nguyentoan.bepngon.ui.sign.createinfo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.FragmentCreateInfoBinding
import com.nguyentoan.bepngon.databinding.LayoutBottomSheetMoreBinding
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.sever.AccountFBUtil
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.ui.baidang.DangBaiActivity
import com.nguyentoan.bepngon.ui.user.main.MainActivity
import com.nguyentoan.bepngon.util.Constant
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.hide
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.setOnSafeClick
import com.nguyentoan.bepngon.view.show
import java.io.IOException
import java.util.*

class CreateInfoFragment : Fragment() {

    lateinit var binding: FragmentCreateInfoBinding

    var storageReference: StorageReference? = null

    var storage: FirebaseStorage? = null
    lateinit var profile: ProfileModel

    companion object {
        const val PICK_IMAGE_REQUEST = 12345
    }

    var ngayThangNam = ""
    var namSinh = 2024

    private var filePath: Uri? = null

    val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference

        profile = ProfileModel(
            SharePreferenceUtils.getAccountID(),
            SharePreferenceUtils.getAccountID(),
            SharePreferenceUtils.getUserName(),
            Constant.URL_AVATAR_DEFAUT,
            "",
            true,
            "",
            "",
            ""
        )

        binding = FragmentCreateInfoBinding.inflate(inflater, container, false)

        binding.toolBar.imgChat.gone()
        binding.toolBar.txtTitle.text = "Thông tin người dùng"


        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbNam -> {
                    profile.gender = true
                }
                R.id.rbNu -> {
                    profile.gender = false
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtNext.setOnClickListener {
            if (binding.edtName.text.toString().trim().isEmpty()) {
                requireContext().showToast("Tên không được trống!")
            } else {
                uploadImage()
            }

        }

        binding.txtSkip.setOnClickListener {
            binding.prgLoad.show()
            addNewProfile(
                ProfileModel(
                    SharePreferenceUtils.getAccountID(),
                    SharePreferenceUtils.getAccountID(),
                    SharePreferenceUtils.getUserName(),
                    Constant.URL_AVATAR_DEFAUT,
                    "",
                    true,
                    "",
                    "",
                    ""
                )
            )
        }

        binding.edtBirthDay.setOnSafeClick {
            showBottomSheet()
        }

        binding.imgAvt.setOnSafeClick {
            chooseImage()
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
                        requireContext().contentResolver,
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
                        profile.avt = imageLink
                        profile.name = binding.edtName.text.toString()
                        if (isChooseDate) {
                            profile.birthDay = binding.edtBirthDay.text.toString()
                        } else {
                            profile.birthDay = ""
                        }
                        addNewProfile(profile)
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Có lỗi!", Toast.LENGTH_SHORT)
                            .show()
                        binding.prgLoad.hide()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Có lỗi!", Toast.LENGTH_SHORT)
                        .show()
                    binding.prgLoad.hide()
                }
                .addOnProgressListener { taskSnapshot ->

                }
        }?: kotlin.run {
            profile.name = binding.edtName.text.toString()
            profile.birthDay = binding.edtBirthDay.text.toString()
            addNewProfile(profile)
        }

    }

    private fun addNewProfile(profileModel: ProfileModel) {
        AccountFBUtil.mDatabase.child(FBConstant.PROFILE).child(SharePreferenceUtils.getAccountID())
            .setValue(profileModel).addOnSuccessListener {
                requireContext().openActivity(MainActivity::class.java, true)
                binding.prgLoad.hide()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Có lỗi!", Toast.LENGTH_SHORT)
                    .show()
                binding.prgLoad.hide()
            }

    }

    private var isChooseDate = false
    private val myCalendar = Calendar.getInstance()
    private fun showBottomSheet() {
        val bottomSheetBinding = LayoutBottomSheetMoreBinding.inflate(layoutInflater)
        val moreBottomSheet =
            BottomSheetDialog(requireContext())
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
                    requireContext(),
                    R.color.black
                )
            )
            isChooseDate = true
        }

        moreBottomSheet.show()
    }


}