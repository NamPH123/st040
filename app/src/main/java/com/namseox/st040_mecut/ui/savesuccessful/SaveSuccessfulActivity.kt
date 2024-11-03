package com.namseox.st040_mecut.ui.savesuccessful

import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivitySaveSuccessfulBinding
import com.namseox.st040_mecut.ui.main.MainActivity
import com.namseox.st040_mecut.ui.main.MainViewModel
import com.namseox.st040_mecut.ui.preview.PreviewActivity
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.utils.shareFile
import com.namseox.st040_mecut.utils.shareVideoOnFacebook
import com.namseox.st040_mecut.utils.shareVideoOnInstagram
import com.namseox.st040_mecut.utils.shareVideoOnTikTok
import com.namseox.st040_mecut.utils.shareVideoOnWhatsApp
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
@AndroidEntryPoint
class SaveSuccessfulActivity : AbsBaseActivity<ActivitySaveSuccessfulBinding>(false) {
    val viewModel : SaveSuccessViewModel by viewModels()
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_save_successful

    override fun init() {
        var path = intent.getStringExtra("data")
        viewModel.addVideo(VideoModel(path!!))
        Glide.with(applicationContext).load(path).into(binding.imv)
        binding.llHome.onSingleClick {
            startActivity(newIntent(applicationContext, MainActivity::class.java))
        }
        binding.llIntagram.onSingleClick {
            shareVideoOnInstagram(this, File(path))
        }
        binding.llTiktok.onSingleClick {
            shareVideoOnTikTok(this, File(path))
        }
        binding.llWhatsApp.onSingleClick {
            shareVideoOnWhatsApp(this, File(path))
        }
        binding.llFacebook.onSingleClick {
            shareVideoOnFacebook(this, File(path))
        }
        binding.llMore.onSingleClick {
            shareFile(this, File(path))
        }
        binding.imv.setOnClickListener {
            startActivity(newIntent(applicationContext,PreviewActivity::class.java).putExtra("data",path))
        }
        binding.imvBack.setOnClickListener { finish() }
    }
}