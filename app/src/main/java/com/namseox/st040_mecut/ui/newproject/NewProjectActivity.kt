package com.namseox.st040_mecut.ui.newproject

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityNewProjectBinding
import com.namseox.st040_mecut.ui.cutvideo.CutVideoActivity
import com.namseox.st040_mecut.ui.editvideo.EditVideoActivcity
import com.namseox.st040_mecut.ui.mergevideo.MergeVideoActivity
import com.namseox.st040_mecut.utils.getAllFile
import com.namseox.st040_mecut.utils.getAllVideoFolders
import com.namseox.st040_mecut.utils.getVideoDuration
import com.namseox.st040_mecut.utils.isImageFile
import com.namseox.st040_mecut.utils.isVideoFile
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.putParcelableExtra
import com.namseox.st040_mecut.utils.showToast
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
@AndroidEntryPoint
class NewProjectActivity : AbsBaseActivity<ActivityNewProjectBinding>(false) {
    //    var arrayFolderVideo = arrayListOf<ArrayList<VideoModel>>()
//    var arrayFolderImage = arrayListOf<ArrayList<VideoModel>>()
    var arrayAllFolder = arrayListOf<ArrayList<VideoModel>>()
    var arrVideo = arrayListOf<VideoModel>()
    var arrImage = arrayListOf<VideoModel>()
    var arrSellect = arrayListOf<VideoModel>()
    lateinit var file: File
// check true - video   false - image
    var checkVideo = true
    lateinit var adapterVideo: NewProjectAdapter
    lateinit var adapterImage: NewProjectAdapter
    lateinit var adapterSelected: SelectedAdapter
    lateinit var adapterSelectedFolder: SelectedFolderAdapter
    var dataIntent = ""

    //merge - cut - edit
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_new_project

    override fun init() {
        dataIntent = intent.getStringExtra("data").toString()
        when (dataIntent) {
            "merge" -> {
                binding.ll1.visibility = View.GONE
                binding.ll2.visibility = View.GONE
            }

            "cut" -> {
                binding.ll1.visibility = View.GONE
                binding.ll2.visibility = View.GONE
                binding.btnApply.visibility = View.GONE
                binding.ctl.visibility = View.GONE
            }

            "edit" -> {
                checkVideo = false
//                binding.ll1.visibility = View.VISIBLE
//                binding.ll2.visibility = View.VISIBLE
                binding.ll1.visibility = View.GONE
                binding.ll2.visibility = View.GONE
            }
        }
        binding.tvSelect.isSelected = true
        adapterSelectedFolder = SelectedFolderAdapter()
        binding.rcvSelectFolder.itemAnimator = null
        binding.rcvSelectFolder.layoutManager =
            GridLayoutManager(application, 1, RecyclerView.VERTICAL, false)
        binding.rcvSelectFolder.adapter = adapterSelectedFolder

        adapterVideo = NewProjectAdapter()
        binding.rcvVideo.itemAnimator = null
        binding.rcvVideo.layoutManager =
            GridLayoutManager(application, 3, RecyclerView.VERTICAL, false)
        binding.rcvVideo.adapter = adapterVideo

        adapterImage = NewProjectAdapter()
        binding.rcvImage.itemAnimator = null
        binding.rcvImage.layoutManager =
            GridLayoutManager(application, 3, RecyclerView.VERTICAL, false)
        binding.rcvImage.adapter = adapterImage

        adapterSelected = SelectedAdapter()
        binding.rcvSelect.itemAnimator = null
        binding.rcvSelect.layoutManager =
            GridLayoutManager(application, 1, RecyclerView.HORIZONTAL, false)
        binding.rcvSelect.adapter = adapterSelected

        getAllVideoFolders(application).forEachIndexed { i, data ->
            file = File(data)
            arrayAllFolder.add(arrayListOf())
            getAllFile(file).forEach {
                if (isImageFile(it)) {
                        var a = VideoModel(it, false)
                        arrayAllFolder[i].add(a)
                        arrImage.add(a)
                } else {
                    if (isVideoFile(it)) {
                        var a = VideoModel(
                            it, true, checkCut = if (dataIntent == "cut") {
                                true
                            } else {
                                false
                            }
                        )
                        arrayAllFolder[i].add(a)
                        arrVideo.add(a)
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            arrVideo.forEach {
                it.duration = getVideoDuration(it.path)
            }
            withContext(Dispatchers.Main){
                adapterVideo.submitList(arrVideo)
            }
        }
        arrayAllFolder.add(0, (arrImage + arrVideo) as ArrayList<VideoModel>)
        adapterSelectedFolder.submitList(arrayAllFolder)
        adapterVideo.submitList(arrVideo)
        adapterImage.submitList(arrImage)
        if (checkVideo) {
            binding.rcvVideo.visibility = View.VISIBLE
            binding.rcvImage.visibility = View.GONE
        } else {
            binding.rcvVideo.visibility = View.GONE
            binding.rcvImage.visibility = View.VISIBLE
        }

        onCLíck()
    }

    var check = false

    @SuppressLint("SetTextI18n")
    fun onCLíck() {
        binding.llChooseData.setOnClickListener {
            if (check) {
                binding.llSelectFolder.visibility = View.GONE
                binding.imvArrow.setImageResource(R.drawable.ic_arrow_down)
            } else {
                binding.llSelectFolder.visibility = View.VISIBLE
                binding.imvArrow.setImageResource(R.drawable.ic_arrow_up)
            }
            check = !check
        }
        adapterSelectedFolder.onCLick = { it ->
            if (it == 0) {
                binding.tvTitle.text = getString(R.string.recent)
            } else {
                binding.tvTitle.text = File(arrayAllFolder[it][0].path).parentFile.name
            }
            arrImage.clear()
            arrVideo.clear()
            arrayAllFolder[it].forEach {
                if (it.isVideo) {
                    arrVideo.add(it)
                } else {
                    arrImage.add(it)
                }
            }
            adapterVideo.submitList(arrVideo)
            adapterImage.submitList(arrImage)
            check = false
            binding.llSelectFolder.visibility = View.GONE
            binding.imvArrow.setImageResource(R.drawable.ic_arrow_down)
        }
        binding.btnApply.setOnClickListener {
            if (arrSellect.size == 0) {
                showToast(applicationContext, R.string.you_have_no_choice_yet)
            } else {
                when (dataIntent) {
                    "merge" -> {
                        startActivity(
                            newIntent(
                                applicationContext,
                                MergeVideoActivity::class.java
                            ).putExtra("data", arrSellect)
                        )
                    }

//                    "cut" -> {
//                        startActivity(
//                            newIntent(applicationContext, EditVideoActivcity::class.java).putExtra("data", arrSellect)
//                        )
//                    }

                    "edit" -> {
                        startActivity(
                            newIntent(
                                applicationContext,
                                EditVideoActivcity::class.java
                            ).putExtra("data", arrSellect)
                        )
                    }
                }
            }
        }
        binding.tvVideos.setOnClickListener {
            binding.imvTrue.setBackgroundResource(R.drawable.bg_card_border8)
            binding.imvFalse.setBackgroundResource(R.drawable.bg_card_border9)
            checkVideo = true
            binding.rcvVideo.visibility = View.VISIBLE
            binding.rcvImage.visibility = View.GONE

        }
        binding.tvPhotos.setOnClickListener {
            checkVideo = false
            binding.imvFalse.setBackgroundResource(R.drawable.bg_card_border8)
            binding.imvTrue.setBackgroundResource(R.drawable.bg_card_border9)
            binding.rcvVideo.visibility = View.GONE
            binding.rcvImage.visibility = View.VISIBLE
        }
        adapterVideo.onCLick = {
            if (dataIntent != "cut") {
                if (arrSellect.size > 9 && !arrVideo[it].check) {
                    showToast(applicationContext, R.string.you_cannot_select_more_than_10_items)
                } else {
                    arrVideo[it].check = !arrVideo[it].check
                    adapterVideo.submitList(arrVideo)
                    if (arrVideo[it].check) {
                        arrSellect.add(arrVideo[it])
                    } else {
                        arrSellect.remove(arrVideo[it])
                    }
                    if (arrSellect.size == 0) {
                        binding.btnApply.alpha = 0.4f
                        binding.tvSelect.text = getString(R.string.select_at_least_1_item)
                        binding.tvNoVideo.visibility = View.VISIBLE
                        binding.btnClearAll.visibility = View.GONE
                    } else {
                        binding.btnApply.alpha = 1f
                        binding.tvSelect.text =
                            "${arrSellect.size} " + getString(R.string.item_selected)
                        binding.tvNoVideo.visibility = View.GONE
                        binding.btnClearAll.visibility = View.VISIBLE
                    }
                    adapterSelected.submitList(arrSellect)
                }
            } else {
                startActivity(newIntent(applicationContext, CutVideoActivity::class.java).putParcelableExtra("data", arrVideo[it])
                )
            }

        }
        adapterImage.onCLick = {
            if (arrSellect.size > 9 && !arrVideo[it].check) {
                showToast(applicationContext, R.string.you_cannot_select_more_than_10_items)
            } else {
                arrImage[it].check = !arrImage[it].check
                adapterImage.submitList(arrImage)
                if (arrImage[it].check) {
                    arrSellect.add(arrImage[it])
                } else {
                    arrSellect.remove(arrImage[it])
                }
                if (arrSellect.size == 0) {
                    binding.btnApply.alpha = 0.4f
                    binding.tvSelect.text = getString(R.string.select_at_least_1_item)
                    binding.tvNoVideo.visibility = View.VISIBLE
                    binding.btnClearAll.visibility = View.GONE
                } else {
                    binding.btnApply.alpha = 1f
                    binding.tvSelect.text =
                        "${arrSellect.size} " + getString(R.string.item_selected)
                    binding.tvNoVideo.visibility = View.GONE
                    binding.btnClearAll.visibility = View.VISIBLE
                }
                adapterSelected.submitList(arrSellect)
            }
        }
        adapterSelected.onCLick = {
            arrSellect[it].check = false
            arrSellect.removeAt(it)
            if (arrSellect.size == 0) {
                binding.btnApply.alpha = 0.4f
                binding.tvSelect.text = getString(R.string.select_at_least_1_item)
                binding.tvNoVideo.visibility = View.VISIBLE
                binding.btnClearAll.visibility = View.GONE
            } else {
                binding.btnApply.alpha = 1f
                binding.tvSelect.text = "${arrSellect.size} " + getString(R.string.item_selected)
            }
            adapterSelected.submitList(arrSellect)
            adapterImage.submitList(arrImage)
            adapterVideo.submitList(arrVideo)
        }
        binding.btnClearAll.setOnClickListener {
            binding.btnApply.alpha = 0.4f
            binding.btnClearAll.visibility = View.GONE
            binding.tvNoVideo.visibility = View.VISIBLE
            arrSellect.forEach {
                it.check = false
            }
            arrSellect.clear()
            adapterSelected.submitList(arrSellect)
            adapterImage.submitList(arrImage)
            adapterVideo.submitList(arrVideo)

        }
        binding.ctl.setOnClickListener { }
        binding.imvBack.setOnClickListener { finish() }
    }
}