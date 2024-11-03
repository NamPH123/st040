package com.namseox.st040_mecut.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.namseox.st040_mecut.data.repository.ApiRepository
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.databinding.ActivitySplashBinding
import com.namseox.st040_mecut.ui.language.LanguageActivity
import com.namseox.st040_mecut.ui.tutorial.TutorialActivity
import com.namseox.st040_mecut.utils.Const
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity: AbsBaseActivity<ActivitySplashBinding>(false) {
    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    var handle = Handler(Looper.myLooper()!!)
    var runnable = Runnable {
        if (!sharedPreferenceUtils.getBooleanValue(Const.LANGUAGE)
        ) {
            startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            startActivity(Intent(this, TutorialActivity::class.java))
        }
        finish()
    }
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun init() {

        if (!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
            && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)
        ) {
            finish(); return;
        }
    }
    override fun onStart() {
        super.onStart()
        handle.postDelayed(runnable, 3000)
    }

    override fun onStop() {
        super.onStop()
        handle.removeCallbacks(runnable)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }
}