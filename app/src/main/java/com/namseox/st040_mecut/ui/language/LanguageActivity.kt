package com.namseox.st040_mecut.ui.language

import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namseox.st040_mecut.ui.main.MainActivity
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.LanguageModel
import com.namseox.st040_mecut.databinding.ActivityLanguageBinding
import com.namseox.st040_mecut.ui.tutorial.TutorialActivity
import com.namseox.st040_mecut.utils.Const.LANGUAGE
import com.namseox.st040_mecut.utils.Const.listLanguage
import com.namseox.st040_mecut.utils.Const.positionLanguageOld
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import com.namseox.st040_mecut.utils.SystemUtils
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LanguageActivity : AbsBaseActivity<ActivityLanguageBinding>(false){
    lateinit var adapter: LanguageAdapter
    var codeLang: String? = null

    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_language

    override fun init() {
        codeLang = sharedPreferenceUtils.getStringValue("language")
//        if (codeLang.equals("")) {
//            providerSharedPreference.putStringValue("language", "en")
//            codeLang = "en"
//        }
        binding.rclLanguage.itemAnimator = null
        adapter = LanguageAdapter()
        setRecycleView()
        setClick()
    }

    private fun setClick() {
        binding.imvDone.onSingleClick {
                if(codeLang.equals("")){
                    Toast.makeText(this, getString(R.string.you_have_not_selected_anything_yet), Toast.LENGTH_SHORT).show()
                }else{
                    SystemUtils.setPreLanguage(applicationContext, codeLang)
                    sharedPreferenceUtils.putStringValue("language", codeLang)
                    if (sharedPreferenceUtils.getBooleanValue(LANGUAGE)
                    ) {
                        var intent = Intent(
                            applicationContext,
                            MainActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finishAffinity()
                        startActivity(intent)
                    } else {
                        SharedPreferenceUtils.getInstance(applicationContext)
                            .putBooleanValue(LANGUAGE, true)
                        var intent = Intent(applicationContext, TutorialActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }
    }

    private fun setRecycleView() {
        var i = 0
        lateinit var x: LanguageModel
        if (!codeLang.equals("")) {
            listLanguage.forEach {
                listLanguage[i].active = false
                if (codeLang.equals(it.code)) {
                    x = listLanguage[i]
                    x.active = true
                }
                i++
            }

            listLanguage.remove(x)
            listLanguage.add(0, x)
        }
        adapter.getData(listLanguage)
        binding.rclLanguage.adapter = adapter
        val manager = GridLayoutManager(applicationContext, 1, RecyclerView.VERTICAL, false)
        binding.rclLanguage.layoutManager = manager

        adapter.onClick = {
            codeLang = listLanguage[it].code
        }
    }

    override fun onResume() {
        super.onResume()

    }
    override fun onBackPressed() {
        listLanguage[positionLanguageOld].active = false
        positionLanguageOld = 0
        super.onBackPressed()
    }
}