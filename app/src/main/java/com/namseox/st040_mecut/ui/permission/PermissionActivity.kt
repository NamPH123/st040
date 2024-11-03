package com.namseox.st040_mecut.ui.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.namseox.st040_mecut.ui.main.MainActivity
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.databinding.ActivityPermisionBinding
import com.namseox.st040_mecut.utils.Const
import com.namseox.st040_mecut.utils.Const.REQUEST_NOTIFICATION_PERMISSION
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import com.namseox.st040_mecut.utils.changeGradientText
import com.namseox.st040_mecut.utils.changeText
import com.namseox.st040_mecut.utils.checkPermision
import com.namseox.st040_mecut.utils.checkUsePermision
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.utils.requesPermission
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class PermissionActivity: AbsBaseActivity<ActivityPermisionBinding>(false) {
    var check = false
    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_permision

    override fun init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.rl4.visibility = View.VISIBLE
        } else {
            binding.rl4.visibility = View.GONE
        }
        val space = " "
        binding.tvTitle.text = TextUtils.concat(
            changeText(
                this,
                getString(R.string.allow),
                ContextCompat.getColor(this,R.color.white),
                R.font.roboto_regular
            ),space,
            changeText(
                this,
                getString(R.string.app_name),
                ContextCompat.getColor(this,R.color.color_app),
                R.font.roboto_bold
            ),
            space,
            changeText(
                this,
                getString(R.string.request_permission_to_use_notifications_to_notify_you),
                ContextCompat.getColor(this,R.color.white),
                R.font.roboto_regular
            ),
        )
        changeGradientText(binding.btnContinue)
        onClick()
    }
    fun onClick(){
        binding.btnContinue.onSingleClick {
            if(check){
                sharedPreferenceUtils.putBooleanValue(Const.PERMISON, true)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                finish()
            }
        }
        binding.swiVibrate2.onSingleClick {
            if (!checkPermision(this)) {
                ActivityCompat.requestPermissions(
                    this,
                    checkUsePermision(),
                    Const.REQUEST_STORAGE_PERMISSION
                )
            }
        }
        binding.swiVibrate4.onSingleClick {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requesPermission(requestCode, permissions, grantResults)) {
            Const.REQUEST_STORAGE_PERMISSION -> {
                check = true
                binding.swiVibrate2.setImageResource(R.drawable.ic_swith_true_per)
            }


            Const.REQUEST_NOTIFICATION_PERMISSION -> {
                binding.swiVibrate4.setImageResource(R.drawable.ic_swith_true_per)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if (checkPermision(this)
        ) {
            check = true
            binding.swiVibrate2.setImageResource(R.drawable.ic_swith_true_per)
        } else {
            check = false
            binding.swiVibrate2.setImageResource(R.drawable.ic_swith_false_per)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.swiVibrate4.setImageResource(R.drawable.ic_swith_true_per)
            } else {
                binding.swiVibrate4.setImageResource(R.drawable.ic_swith_false_per)
            }
        }
    }
}