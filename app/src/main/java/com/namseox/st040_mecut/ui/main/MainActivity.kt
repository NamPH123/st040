package com.namseox.st040_mecut.ui.main

import android.app.Activity
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityMainBinding
import com.namseox.st040_mecut.dialog.DialogDelete
import com.namseox.st040_mecut.ui.newproject.NewProjectActivity
import com.namseox.st040_mecut.ui.preview.PreviewActivity
import com.namseox.st040_mecut.ui.setting.SettingActivity
import com.namseox.st040_mecut.utils.Const
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import com.namseox.st040_mecut.utils.changeGradientText
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.utils.requestDeleteFile
import com.namseox.st040_mecut.utils.shareAllFile
import com.namseox.st040_mecut.utils.showToast
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AbsBaseActivity<ActivityMainBinding>(false) {
    val viewModel: MainViewModel by viewModels()
    var checkSortGrid = false
    var listVideo = arrayListOf<VideoModel>()

    var checkSort = true
    var checkTickAll = false

    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    lateinit var adapter: MainAdapter

    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun init() {

        getData()
        initView()
        setClick()

    }

    private fun setClick() {
        binding.imvSetting.setOnClickListener {
            startActivity(newIntent(this, SettingActivity::class.java))
        }
        binding.ll1.setOnClickListener {
            startActivity(newIntent(this, NewProjectActivity::class.java).putExtra("data", "edit"))
        }
        binding.rlCutVideo.setOnClickListener {
            startActivity(newIntent(this, NewProjectActivity::class.java).putExtra("data", "cut"))
        }
        binding.rlMerVideo.setOnClickListener {
            startActivity(newIntent(this, NewProjectActivity::class.java).putExtra("data", "merge"))
        }
        binding.icSort.setOnClickListener {
            if (checkSort) {
                checkSortGrid = !checkSortGrid
                sharedPreferenceUtils.putBooleanValue(Const.SORT, checkSortGrid)
                if (checkSortGrid) {
                    adapter.setType(1)
                    binding.icSort.setImageResource(R.drawable.ic_sort_grid)
                    binding.recyclerView.layoutManager =
                        GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                } else {
                    adapter.setType(0)
                    binding.icSort.setImageResource(R.drawable.ic_sort_list)
                    binding.recyclerView.layoutManager =
                        GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
                }
                adapter.submitList(listVideo)
            } else {
                checkTickAll = !checkTickAll
                listVideo.forEach {
                    it.checkTick = checkTickAll
                }
                adapter.submitList(listVideo)
            }
        }
        adapter.onClickItem = { i, type ->
            when (type) {
                "item" -> {
                    startActivity(
                        newIntent(
                            applicationContext,
                            PreviewActivity::class.java
                        ).putExtra("data", listVideo[i].path)
                    )
                }

                "tick" -> {
                    listVideo[i].checkTick = !listVideo[i].checkTick
                    adapter.submitList(listVideo)
                }

                "long" -> {
                    binding.ll.visibility = View.VISIBLE
                    binding.icSort.setImageResource(R.drawable.ic_tick_home)
                    checkSort = false
                }
            }
        }
        binding.llShare.onSingleClick {
            var listPath = arrayListOf<Uri>()
            listVideo.forEach {
                if (it.checkTick) {
                    listPath.add(
                        FileProvider.getUriForFile(
                            this,
                            "${packageName}.fileprovider",
                            File(it.path)
                        )
                    )
                }
            }
            if (listPath.size > 0) {
                shareAllFile(this, listPath)
            } else {
                showToast(applicationContext, R.string.you_have_not_selected_any_items_yet)
            }
        }
        binding.llDelete.onSingleClick {
            var arrPath = arrayListOf<String>()
            listVideo.forEach {
                if (it.checkTick) {
                    arrPath.add(it.path)
                }
            }
            if (listVideo.size > 0) {

                var dialogDelete = DialogDelete(this)
                dialogDelete.init(object : DialogDelete.OnPress {
                    override fun cancel() {
                        dialogDelete.dismiss()
                    }
                    override fun delete() {
                        requestDeleteFile(this@MainActivity, arrPath, deleteResultLauncher)
                    }
                })
            } else {
                showToast(applicationContext, R.string.you_have_not_selected_any_items_yet)
            }
        }
        deleteResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.getAllVideo()
                } else {

                }
            }
    }

    lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onStart() {
        super.onStart()
        viewModel.getAllVideo()
    }

    private fun getData() {
        viewModel.listArrVideo.observe(this) { data ->
            listVideo.clear()
            adapter.checkTick = false
            binding.ll.visibility = View.GONE
            checkSort = true
            checkTickAll = false
            if (checkSortGrid) {
                binding.icSort.setImageResource(R.drawable.ic_sort_grid)
            } else {
                binding.icSort.setImageResource(R.drawable.ic_sort_list)
            }
            data.forEach {
                if (File(it.path).exists()) {
                    listVideo.add(it)
                }
            }
            adapter.submitList(listVideo)
            if (listVideo.size == 0) {
                binding.icNull.visibility = View.VISIBLE
                binding.tvNull.visibility = View.VISIBLE
            } else {
                binding.icNull.visibility = View.GONE
                binding.tvNull.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        changeGradientText(binding.tvCut)
        changeGradientText(binding.tvMerge)
        checkSortGrid = sharedPreferenceUtils.getBooleanValue(Const.SORT)

        adapter = MainAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        if (checkSortGrid) {
            adapter.setType(1)
            binding.icSort.setImageResource(R.drawable.ic_sort_grid)
            binding.recyclerView.layoutManager =
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        } else {
            adapter.setType(0)
            binding.icSort.setImageResource(R.drawable.ic_sort_list)
            binding.recyclerView.layoutManager =
                GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        }
    }

}
