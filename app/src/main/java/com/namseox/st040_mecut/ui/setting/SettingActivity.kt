package com.namseox.st040_mecut.ui.setting

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.SettingModel
import com.namseox.st040_mecut.databinding.ActivitySettingBinding
import com.namseox.st040_mecut.ui.language.LanguageActivity
import com.namseox.st040_mecut.utils.RATE
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.policy
import com.namseox.st040_mecut.utils.rateUs
import com.namseox.st040_mecut.utils.shareApp
import com.namseox.st040_mecut.utils.unItem
import com.namseox.st040_mecut.view.base.AbsBaseActivity

class SettingActivity : AbsBaseActivity<ActivitySettingBinding>(false) {
    lateinit var adapter: SettingAdapter
    var arr = arrayListOf<SettingModel>()
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun init() {

        arr = arrayListOf(
            SettingModel(R.drawable.ic_language, getString(R.string.language), 0),
            SettingModel(R.drawable.ic_rate_us, getString(R.string.rate_us), 0),
            SettingModel(R.drawable.ic_share, getString(R.string.share), 0),
            SettingModel(R.drawable.ic_policy, getString(R.string.policy), 0),
        )
        unItem = {
            arr.removeAt(1)
            adapter.submitList(arr)
        }
        if (SharedPreferenceUtils.getInstance(application).getBooleanValue(RATE)) {
            arr.removeAt(1)
        }
        adapter = SettingAdapter()
        binding.rcv.adapter = adapter
        binding.rcv.itemAnimator = null
        binding.rcv.layoutManager = GridLayoutManager(this, 1)
        adapter.submitList(arr)
        adapter.onClick = {
            when (it) {
                R.drawable.ic_language -> {
                    startActivity(newIntent(applicationContext, LanguageActivity::class.java))
                }
                R.drawable.ic_rate_us -> {
                    rateUs(0, binding.rcv)
                }
                R.drawable.ic_share -> {shareApp()}
                R.drawable.ic_policy -> {policy()}
            }
        }
        binding.icBack.setOnClickListener {
            finish()
        }
    }
}