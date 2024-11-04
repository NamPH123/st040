package com.namseox.st040_mecut.ui.editvideo

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.FuntionModel
import com.namseox.st040_mecut.data.model.ImvModel
import com.namseox.st040_mecut.data.model.TextFontModel
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityEditVideoBinding
import com.namseox.st040_mecut.ui.music.MusicActivity
import com.namseox.st040_mecut.utils.dpToSp
import com.namseox.st040_mecut.utils.formatDuration
import com.namseox.st040_mecut.utils.hideKeyboard
import com.namseox.st040_mecut.utils.showToast
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import com.namseox.st040_mecut.view.customview.OptiTimeLineView
import com.namseox.st040_mecut.view.customview.customimage.DrawableSticker
import com.namseox.st040_mecut.view.customview.customimage.OnStickerListener
import com.namseox.st040_mecut.view.customview.customimage.Sticker
import com.namseox.st040_mecut.view.customview.customimage.StickerView

class EditVideoActivcity : AbsBaseActivity<ActivityEditVideoBinding>(false) {
    var adapterFunction = FuntionAdapter()
    var adapterTextFuntion = TextFuntionAdapter()
    var adapterFillter = FillterAdapter()
    var adapterTextSticker = StikerAdapter()
    var adapterStiker = StikerAdapter()
    var adapterTextFont = TextFontAdapter()
    var arrSticker = arrayListOf<ImvModel>()
    var arrPaint = arrayListOf<Paint>()
    var arrOptiTimeLineViewLayout = arrayListOf<OptiTimeLineView>()
    var arrOptiTimeLineViewHome = arrayListOf<OptiTimeLineView>()
    var arrIntFiller = arrayListOf<Int>(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    var arrStikerView = arrayListOf<StickerView>()
    var positionHome = 0
    var arrImv = arrayListOf<AppCompatImageView>()
    var arr = arrayListOf<FuntionModel>()
    var arrHome = arrayListOf<FuntionModel>()
    var arrText = arrayListOf<FuntionModel>()
    var arrSellect = arrayListOf<VideoModel>()
    var arrFillter = arrayListOf<ImvModel>()
    var arrTextFont = arrayListOf<TextFontModel>()
    var arrTextStiker = arrayListOf<ImvModel>()
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_edit_video

    @SuppressLint("ClickableViewAccessibility")
    override fun init() {
        arrTextFont = arrayListOf(
            TextFontModel(R.font.aclonica_regular),
            TextFontModel(R.font.blackopsone_regular),
            TextFontModel(R.font.bungeeshade_regular),
            TextFontModel(R.font.cherrybombone_regular),
            TextFontModel(R.font.chilanka_regular),
            TextFontModel(R.font.dynalight_regular),
            TextFontModel(R.font.elsieswashcaps_regular),
            TextFontModel(R.font.faster_one_regular),
            TextFontModel(R.font.harlowsl),
            TextFontModel(R.font.miltonian_regular),
            TextFontModel(R.font.pacifico_regular),
            TextFontModel(R.font.sunshiney_regular),
            TextFontModel(R.font.uvn_chuky),
            TextFontModel(R.font.yellowtail_regular),
            TextFontModel(R.font.roboto_regular),
        )
        arrPaint = arrayListOf(
            Paint(),
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            -0.36f, 1.691f, -0.32f, 0f, 0f,
                            0.325f, 0.398f, 0.275f, 0f, 0f,
                            0.79f, 0.796f, -0.76f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            2f, 0f, 0f, 0f, -130f,
                            0f, 2f, 0f, 0f, -130f,
                            0f, 0f, 2f, 0f, -130f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            5f, 0f, 0f, 0f, -254f,
                            0f, 5f, 0f, 0f, -254f,
                            0f, 0f, 5f, 0f, -254f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            0.393f, 0.769f, 0.189f, 0.0f,
                            0.0f, 0.349f, 0.686f, 0.168f,
                            0.0f, 0.0f, 0.272f, 0.534f,
                            0.131f, 0.0f, 0.0f, 0.0f,
                            0.0f, 0.0f, 1.0f, 0.0f,
                            0.0f, 0.0f, 0.0f, 0.0f, 1.0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            -0.36f, 1.691f, -0.32f, 0f, 0f,
                            0.325f, 0.398f, 0.275f, 0f, 0f,
                            0.79f, 0.796f, -0.76f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            0f, 1f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 1f, 0f, 1f, 0f
                        )
                    )
                )
            },
            Paint().apply {
                setColorFilter(
                    ColorMatrixColorFilter(
                        floatArrayOf(
                            0f, 1f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0.6f, 1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            },
        )
        arrFillter = arrayListOf(
            ImvModel(R.drawable.ic_effect_none, getString(R.string.none), true),
            ImvModel(R.drawable.ic_effect_noir, getString(R.string.noir)),
            ImvModel(R.drawable.ic_effect_warm, getString(R.string.warm)),
            ImvModel(R.drawable.ic_effect_sepia, getString(R.string.sepia)),
            ImvModel(R.drawable.ic_effect_cold, getString(R.string.cold)),
            ImvModel(R.drawable.ic_effect_classic, getString(R.string.classic)),
            ImvModel(R.drawable.ic_effect_monochrome, getString(R.string.monochrome)),
            ImvModel(R.drawable.ic_effect_green, getString(R.string.green)),
        )
        arrSticker = arrayListOf(
            ImvModel(R.drawable.sticker_1),
            ImvModel(R.drawable.sticker_2),
            ImvModel(R.drawable.sticker_3),
            ImvModel(R.drawable.sticker_4),
            ImvModel(R.drawable.sticker_5),
            ImvModel(R.drawable.sticker_6),
            ImvModel(R.drawable.sticker_7),
            ImvModel(R.drawable.sticker_8),
            ImvModel(R.drawable.sticker_9),
            ImvModel(R.drawable.sticker_10),
            ImvModel(R.drawable.sticker_11),
            ImvModel(R.drawable.sticker_12),
            ImvModel(R.drawable.sticker_13),
            ImvModel(R.drawable.sticker_14),
            ImvModel(R.drawable.sticker_15),
            ImvModel(R.drawable.sticker_16),
            ImvModel(R.drawable.sticker_17),
            ImvModel(R.drawable.sticker_18),
            ImvModel(R.drawable.sticker_19),
            ImvModel(R.drawable.sticker_20),
            ImvModel(R.drawable.sticker_21),
            ImvModel(R.drawable.sticker_22),
            ImvModel(R.drawable.sticker_23),
            ImvModel(R.drawable.sticker_24),
            ImvModel(R.drawable.sticker_25),
            ImvModel(R.drawable.sticker_26),
            ImvModel(R.drawable.sticker_27),
            ImvModel(R.drawable.sticker_28),
            ImvModel(R.drawable.sticker_29),
            ImvModel(R.drawable.sticker_30),
            ImvModel(R.drawable.sticker_31),
            ImvModel(R.drawable.sticker_32),
            ImvModel(R.drawable.sticker_33),


            )
        arrTextStiker = arrayListOf(
            ImvModel(R.drawable.imv_text_stiker_1),
            ImvModel(R.drawable.imv_text_stiker_2),
            ImvModel(R.drawable.imv_text_stiker_3),
            ImvModel(R.drawable.imv_text_stiker_4),
            ImvModel(R.drawable.imv_text_stiker_5),
            ImvModel(R.drawable.imv_text_stiker_6),
            ImvModel(R.drawable.imv_text_stiker_7),
            ImvModel(R.drawable.imv_text_stiker_8),
            ImvModel(R.drawable.imv_text_stiker_9),
            ImvModel(R.drawable.imv_text_stiker_10),
        )
        arrImv = arrayListOf(
            binding.imv1,
            binding.imv2,
            binding.imv3,
            binding.imv4,
            binding.imv5,
            binding.imv6,
            binding.imv7,
            binding.imv8,
            binding.imv9,
            binding.imv10,
        )
        arrSellect = intent.getSerializableExtra("data") as ArrayList<VideoModel>
        binding.tvDuration.text = formatDuration(2 * arrSellect.size.toLong())
        arrHome = arrayListOf(
            FuntionModel(
                R.drawable.filter_false,
                R.drawable.filter_true,
                getString(R.string.filter)
            ),
//            FuntionModel(R.drawable.speed_false, R.drawable.speed_true, getString(R.string.speed)),
            FuntionModel(
                R.drawable.sticker_false,
                R.drawable.sticker_true,
                getString(R.string.sticker)
            ),
            FuntionModel(R.drawable.text_false, R.drawable.text_true, getString(R.string.text)),
            FuntionModel(R.drawable.music_false, R.drawable.music_false, getString(R.string.music)),
            FuntionModel(R.drawable.frame_false, R.drawable.frame_true, getString(R.string.frame))
        )
        arrText = arrayListOf(
            FuntionModel(
                R.drawable.text_delete,
                R.drawable.text_delete,
                getString(R.string.filter)
            ),
            FuntionModel(
                R.drawable.text_text_false,
                R.drawable.text_text_true,
                getString(R.string.transition)
            ),
            FuntionModel(
                R.drawable.text_font_false,
                R.drawable.text_font_true,
                getString(R.string.replace)
            ),
            FuntionModel(
                R.drawable.text_style_false,
                R.drawable.text_style_true,
                getString(R.string.delete)
            ),
            FuntionModel(
                R.drawable.text_setting_false,
                R.drawable.text_setting_true,
                getString(R.string.delete)
            ),
            FuntionModel(
                R.drawable.text_gradient_false,
                R.drawable.text_gradient_true,
                getString(R.string.delete)
            ),
            FuntionModel(
                R.drawable.text_tick,
                R.drawable.text_tick,
                getString(R.string.delete)
            )
        )
        arr.clear()
        arr.addAll(arrHome)
        adapterFunction = FuntionAdapter()
        binding.rcv.itemAnimator = null
        binding.rcv.layoutManager =
            GridLayoutManager(applicationContext, 5, GridLayoutManager.VERTICAL, false)
        binding.rcv.adapter = adapterFunction
        adapterFunction.submitList(arr)
        onClick()
        try {
            arrOptiTimeLineViewHome = arrayListOf(
                binding.timeLineView1,
                binding.timeLineView2,
                binding.timeLineView3,
                binding.timeLineView4,
                binding.timeLineView5,
                binding.timeLineView6,
                binding.timeLineView7,
                binding.timeLineView8,
                binding.timeLineView9,
                binding.timeLineView10,
            )
            arrSellect.forEachIndexed { index, videoModel ->
                arrOptiTimeLineViewHome[index].setVideo(Uri.parse(videoModel.path))
            }
        } catch (e: Exception) {

        }
        try {
            arrOptiTimeLineViewLayout = arrayListOf(
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView1),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView2),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView3),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView4),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView5),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView6),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView7),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView8),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView9),
                binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView10)


            )
            arrSellect.forEachIndexed { index, videoModel ->
                arrOptiTimeLineViewLayout[index].setVideo(Uri.parse(videoModel.path))
            }
        } catch (e: Exception) {

        }
        arrStikerView = arrayListOf(
            binding.pv1,
            binding.pv2,
            binding.pv3,
            binding.pv4,
            binding.pv5,
            binding.pv6,
            binding.pv7,
            binding.pv8,
            binding.pv9,
            binding.pv10,
        )
        binding.layoutSpeed.findViewById<RecyclerView>(R.id.rcv).adapter = adapterFillter
        binding.layoutSpeed.findViewById<RecyclerView>(R.id.rcv).layoutManager =
            GridLayoutManager(applicationContext, 1, GridLayoutManager.HORIZONTAL, false)
        binding.layoutSpeed.findViewById<RecyclerView>(R.id.rcv).itemAnimator = null
        adapterFillter.submitList(arrFillter)

        binding.layoutStiker.findViewById<RecyclerView>(R.id.rcv).adapter = adapterStiker
        binding.layoutStiker.findViewById<RecyclerView>(R.id.rcv).itemAnimator = null
        binding.layoutStiker.findViewById<RecyclerView>(R.id.rcv).layoutManager =
            GridLayoutManager(applicationContext, 4, GridLayoutManager.VERTICAL, false)
        adapterStiker.submitList(arrSticker)

        binding.layoutText.findViewById<RecyclerView>(R.id.rcv).adapter = adapterTextFuntion
        binding.layoutText.findViewById<RecyclerView>(R.id.rcv).itemAnimator = null
        binding.layoutText.findViewById<RecyclerView>(R.id.rcv).layoutManager =
            GridLayoutManager(applicationContext, 7, GridLayoutManager.VERTICAL, false)
        adapterTextFuntion.submitList(arrText)

        binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).adapter = adapterTextFont
        binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).itemAnimator = null
        binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).layoutManager =
            GridLayoutManager(applicationContext, 1, GridLayoutManager.VERTICAL, false)
        adapterTextFont.submitList(arrTextFont)

        binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).adapter = adapterTextSticker
        binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).itemAnimator = null
        binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).layoutManager = GridLayoutManager(applicationContext, 3, GridLayoutManager.VERTICAL, false)
        adapterTextSticker.submitList(arrTextStiker)

        updateSelect(binding.timeLineView1)
        updateSelect2(binding.layoutSpeed.findViewById(R.id.timeLineView1))
        positionHome = 0
        showStiker(0)
        binding.imvCover.setOnClickListener {  }
        Handler(Looper.myLooper()!!).postDelayed({
            binding.imv1.setImageBitmap(binding.timeLineView1.bitmapDefault)
            binding.imvCover.visibility = View.GONE
        },5000)
        initStickerView()
    }

    private fun initStickerView() {
        arrStikerView.forEach {
            it.apply {
                setConstrained(true)
                setLocked(false)
                setOnStickerListener(object : OnStickerListener {
                    override fun onAdded(sticker: Sticker) {
                    }

                    override fun onClicked(sticker: Sticker) {
                        if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                            Log.d(TAG, "onClicked______: ${arrStikerView[positionHome].getCurrentSticker()!!.character}")
                            binding.tvEdit.setText(arrStikerView[positionHome].getCurrentSticker()!!.character)
                            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbTextSize).progress = arrStikerView[positionHome].getCurrentSticker()!!.textSize.toInt()
                            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).progress = arrStikerView[positionHome].getCurrentSticker()!!.alpha.toInt()
                            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbMargin).progress = arrStikerView[positionHome].getCurrentSticker()!!.margin.toInt()*100

                        }

                    }
                    override fun onDeleted(sticker: Sticker) {
                    }

                    override fun onDragFinished(sticker: Sticker) {}
                    override fun onTouchedDown(sticker: Sticker) {
                    }

                    override fun onZoomFinished(sticker: Sticker) {}
                    override fun onFlipped(sticker: Sticker) {}
                    override fun onDoubleTapped(sticker: Sticker) {}
                    override fun onHideOptionIcon() {}
                    override fun onUndoDelete(stickers: List<Sticker?>) {}
                    override fun onUndoUpdate(stickers: List<Sticker?>) {}
                    override fun onUndoDeleteAll() {}
                    override fun onRedoAll() {}
                    override fun onReplace(sticker: Sticker) {
                    }
                })
            }
        }
    }

    fun updateSelect2(view: OptiTimeLineView) {
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView1)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView2)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView3)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView4)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView5)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView6)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView7)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView8)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView9)
            .updateWhenSelect(false)
        binding.layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView10)
            .updateWhenSelect(false)
        view.updateWhenSelect(true)
    }

    fun updateSelect(view: OptiTimeLineView) {
        binding.timeLineView1.updateWhenSelect(false)
        binding.timeLineView2.updateWhenSelect(false)
        binding.timeLineView3.updateWhenSelect(false)
        binding.timeLineView4.updateWhenSelect(false)
        binding.timeLineView5.updateWhenSelect(false)
        binding.timeLineView6.updateWhenSelect(false)

        binding.timeLineView7.updateWhenSelect(false)
        binding.timeLineView8.updateWhenSelect(false)
        binding.timeLineView9.updateWhenSelect(false)
        binding.timeLineView10.updateWhenSelect(false)
        view.updateWhenSelect(true)
    }

    lateinit var bitmap: Bitmap
    lateinit var canvas: Canvas

    @SuppressLint("CutPasteId", "ClickableViewAccessibility")
    private fun onClick() {
        binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbTextSize).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                addText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                addText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbMargin).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.layoutText.findViewById<AppCompatTextView>(R.id.tvMargin).text = "${progress}"
                addText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
//        binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).progress
//        binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbMargin).progress
        adapterTextSticker.onClick = {pos->
            Glide.with(applicationContext).asDrawable().load(arrTextStiker[pos].imv1)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        val drawableSticker = DrawableSticker(resource, "${111}.png")
                        arrStikerView[positionHome].addSticker(drawableSticker,false)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
        }
        adapterTextFont.onClick = {
            if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                arrTextFont.forEach {
                    it.check = false
                }
                arrTextFont[it].check = true
                adapterTextFont.submitList(arrTextFont)
                arrStikerView[positionHome].getCurrentSticker()!!.font = it
                addText()
            } else {
                showToast(applicationContext, R.string.you_have_not_selected_text)
            }
        }
        adapterTextFuntion.onClick = {
            arrText.forEach {
                it.check = false
            }
            arrText[it].check = true
            adapterTextFuntion.submitList(arrText)
            when (it) {
                0 -> {
                    checkTrue()
                }

                1 -> {
                    if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                        binding.tvEdit.setText(arrStikerView[positionHome].getCurrentSticker()!!.character)
                    } else {
                        binding.tvEdit.setText("")
                    }
                    binding.tvEdit.requestFocus()
                    binding.tvEdit.setSelection(binding.tvEdit.text!!.length)
                    binding.tvEdit.post {
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.tvEdit, InputMethodManager.SHOW_IMPLICIT)
                    }
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).visibility = View.GONE
                    binding.layoutText.findViewById<ScrollView>(R.id.svTextSetting).visibility = View.GONE
                    binding.layoutText.findViewById<ConstraintLayout>(R.id.ctlGradient).visibility = View.GONE
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).visibility = View.GONE
                }

                2 -> {
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).visibility = View.VISIBLE
                    binding.layoutText.findViewById<ScrollView>(R.id.svTextSetting).visibility = View.GONE
                    binding.layoutText.findViewById<ConstraintLayout>(R.id.ctlGradient).visibility = View.GONE
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).visibility = View.GONE

                }

                3 -> {
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).visibility = View.GONE
                    binding.layoutText.findViewById<ScrollView>(R.id.svTextSetting).visibility = View.GONE
                    binding.layoutText.findViewById<ConstraintLayout>(R.id.ctlGradient).visibility = View.GONE
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).visibility = View.VISIBLE
                }
                4 -> {
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvFont).visibility = View.GONE
                    binding.layoutText.findViewById<ScrollView>(R.id.svTextSetting).visibility = View.VISIBLE
                    binding.layoutText.findViewById<ConstraintLayout>(R.id.ctlGradient).visibility = View.GONE
                    binding.layoutText.findViewById<RecyclerView>(R.id.rcvStiker).visibility = View.GONE
                }
                5 -> {}
                6 -> {}
            }
        }

        binding.root.setOnClickListener {
            hideKeyboard(binding.root)
        }
        var previousText  = ""
        binding.tvEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                    previousText = arrStikerView[positionHome].getCurrentSticker()!!.character
                }

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != previousText){
                    addText()
                }
            }
        })
        adapterFillter.onClick = { pos ->
            arrFillter.forEach {
                it.check = false
            }
            arrFillter[pos].check = true
            adapterFillter.submitList(arrFillter)
            try {
                var check = false
                arrOptiTimeLineViewLayout.forEachIndexed { index, optiTimeLineView ->
                    if (optiTimeLineView.isSelected) {
                        optiTimeLineView.setPaint(arrPaint[pos])
                        arrOptiTimeLineViewHome[index].setPaint(arrPaint[pos])
                        arrIntFiller[index] = pos
                        arrImv[index].setImageBitmap(optiTimeLineView.bitmapDefault)
                        check = true
                    }
                }
                if (!check) {
                    arrOptiTimeLineViewLayout.forEachIndexed { index, optiTimeLineView ->
                        optiTimeLineView.setPaint(arrPaint[pos])
                        arrOptiTimeLineViewHome[index].setPaint(arrPaint[pos])
                        arrIntFiller[index] = pos
                        arrImv[index].setImageBitmap(optiTimeLineView.bitmapDefault)

                    }

                }
            } catch (e: Exception) {

            }
        }
        adapterFunction.onCLick = { it, pos ->
            arr.forEachIndexed { index, funtionModel ->
                if (it.imv1 != R.drawable.music_false) {
                    if (index != pos) {
                        funtionModel.check = false
                    } else {
                        funtionModel.check = !funtionModel.check
                    }
                }
            }
            adapterFunction.submitList(arr)
            when (it.imv1) {
                R.drawable.filter_false -> {
                    if (it.check) {
                        checkTrue()
                        binding.layoutSpeed.visibility = View.VISIBLE
                        binding.layoutSpeed.findViewById<RecyclerView>(R.id.rcv).visibility =
                            View.VISIBLE
                        binding.layoutSpeed.findViewById<ConstraintLayout>(R.id.speed).visibility =
                            View.GONE
                        binding.imvPlay.visibility = View.GONE
                    } else {
                        checkTrue()
                        binding.imvPlay.visibility = View.VISIBLE
                    }
                }

                R.drawable.speed_false -> {
                    if (it.check) {
                        checkTrue()
                        binding.layoutSpeed.visibility = View.VISIBLE
                        binding.layoutSpeed.findViewById<ConstraintLayout>(R.id.speed).visibility =
                            View.VISIBLE
                        binding.layoutSpeed.findViewById<RecyclerView>(R.id.rcv).visibility =
                            View.GONE
                        binding.imvPlay.visibility = View.GONE
                    } else {
                        checkTrue()
                        binding.imvPlay.visibility = View.VISIBLE
                    }
                }

                R.drawable.sticker_false -> {
                    if (it.check) {
                        checkTrue()
                        binding.layoutStiker.visibility = View.VISIBLE
                        binding.imvPlay.visibility = View.GONE
                    } else {
                        checkTrue()
                        binding.imvPlay.visibility = View.VISIBLE
                    }
                }

                R.drawable.text_false -> {
                    if (it.check) {
                        checkTrue()
                        if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                            binding.tvEdit.setText(arrStikerView[positionHome].getCurrentSticker()!!.character)
                            arrTextFont.forEach {
                                it.check = false
                            }
                            arrTextFont[arrStikerView[positionHome].getCurrentSticker()!!.font].check = true
                            adapterTextFont.submitList(arrTextFont)
                        } else {
                            binding.tvEdit.setText("")
                        }
                        binding.tvEdit.requestFocus()
                        binding.tvEdit.setSelection(binding.tvEdit.text!!.length)
                        binding.tvEdit.post {
                            val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(binding.tvEdit, InputMethodManager.SHOW_IMPLICIT)
                        }
                        binding.layoutText.visibility = View.VISIBLE
                        binding.imvPlay.visibility = View.GONE
                    } else {
                        checkTrue()
                        binding.imvPlay.visibility = View.VISIBLE
                    }
                }

                R.drawable.music_false -> {
//                    checkTrue()
                    startActivity(Intent(applicationContext, MusicActivity::class.java))
                }

                R.drawable.frame_false -> {}
                R.drawable.transition_false -> {}
                R.drawable.replace_false -> {}
                R.drawable.delete_false -> {}
            }
        }
        binding.imvBack.setOnClickListener {
            finish()
        }
        binding.apply {
            timeLineView1.setOnClickListener {
                updateSelect(timeLineView1)
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView1))
                positionHome = 0
                showStiker(0)
                binding.imv1.setImageBitmap(binding.timeLineView1.bitmapDefault)
            }
            timeLineView2.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView2))
                updateSelect(timeLineView2)
                positionHome = 1
                showStiker(1)
                binding.imv2.setImageBitmap(binding.timeLineView2.bitmapDefault)
            }
            timeLineView3.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView3))
                updateSelect(timeLineView3)
                positionHome = 2
                showStiker(2)
                binding.imv3.setImageBitmap(binding.timeLineView3.bitmapDefault)
            }
            timeLineView4.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView4))
                updateSelect(timeLineView4)
                positionHome = 3
                showStiker(3)
                binding.imv4.setImageBitmap(binding.timeLineView4.bitmapDefault)
            }
            timeLineView5.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView5))
                updateSelect(timeLineView5)
                positionHome = 4
                showStiker(4)
                binding.imv5.setImageBitmap(binding.timeLineView5.bitmapDefault)
            }
            timeLineView6.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView6))
                updateSelect(timeLineView6)
                positionHome = 5
                showStiker(5)
                binding.imv6.setImageBitmap(binding.timeLineView6.bitmapDefault)
            }
            timeLineView7.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView7))
                updateSelect(timeLineView7)
                positionHome = 6
                showStiker(6)
                binding.imv7.setImageBitmap(binding.timeLineView7.bitmapDefault)
            }
            timeLineView8.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView8))
                updateSelect(timeLineView8)
                positionHome = 7
                showStiker(7)
                binding.imv8.setImageBitmap(binding.timeLineView8.bitmapDefault)
            }
            timeLineView9.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView9))
                updateSelect(timeLineView9)
                positionHome = 8
                showStiker(8)
                binding.imv9.setImageBitmap(binding.timeLineView9.bitmapDefault)
            }
            timeLineView10.setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView10))
                updateSelect(timeLineView10)
                positionHome = 9
                showStiker(9)
                binding.imv10.setImageBitmap(binding.timeLineView10.bitmapDefault)
            }


            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView1).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView1))
                positionHome = 0
                updateSelect(timeLineView1)
                binding.imv1.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView1).bitmapDefault)
                showStiker(0)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView2).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView2))
                positionHome = 1
                updateSelect(timeLineView2)
                showStiker(1)
                binding.imv2.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView2).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView3).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView3))
                positionHome = 2
                updateSelect(timeLineView3)
                showStiker(2)
                binding.imv3.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView3).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView4).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView4))
                positionHome = 3
                updateSelect(timeLineView4)
                showStiker(3)
                binding.imv4.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView4).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView5).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView5))
                positionHome = 4
                updateSelect(timeLineView5)
                showStiker(4)
                binding.imv5.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView5).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView6).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView6))
                positionHome = 5
                updateSelect(timeLineView6)
                showStiker(5)
                binding.imv6.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView6).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView7).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView7))
                positionHome = 6
                updateSelect(timeLineView7)
                showStiker(6)
                binding.imv7.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView7).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView8).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView8))
                positionHome = 7
                updateSelect(timeLineView8)
                showStiker(7)
                binding.imv8.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView8).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView9).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView9))
                positionHome = 8
                updateSelect(timeLineView9)
                showStiker(8)
                binding.imv9.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView9).bitmapDefault)
            }
            layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView10).setOnClickListener {
                updateSelect2(layoutSpeed.findViewById(R.id.timeLineView10))
                positionHome = 9
                updateSelect(timeLineView10)
                showStiker(9)
                binding.imv10.setImageBitmap(layoutSpeed.findViewById<OptiTimeLineView>(R.id.timeLineView10).bitmapDefault)
            }


//            ctv.setOnTouchListener { v, event ->
//                when(event.action){
//                    MotionEvent.ACTION_DOWN -> {
//                        timeLineView1.scaleGestureDetector.onTouchEvent(event)
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        timeLineView1.scaleGestureDetector.onTouchEvent(event)}
//                }
//                true
//            }

            constraintLayout.setOnTouchListener { v, event ->
                when (event.pointerCount) {
                    2 -> {
                        ctv.setScrollingEnabled(false)
                        timeLineView1.scaleGestureDetector.onTouchEvent(event)
                        timeLineView2.scaleGestureDetector.onTouchEvent(event)
                        timeLineView3.scaleGestureDetector.onTouchEvent(event)
                        timeLineView4.scaleGestureDetector.onTouchEvent(event)
                        timeLineView5.scaleGestureDetector.onTouchEvent(event)
                        timeLineView6.scaleGestureDetector.onTouchEvent(event)
                        timeLineView7.scaleGestureDetector.onTouchEvent(event)
                        timeLineView8.scaleGestureDetector.onTouchEvent(event)
                        timeLineView9.scaleGestureDetector.onTouchEvent(event)
                        timeLineView10.scaleGestureDetector.onTouchEvent(event)
                    }

                    else -> {
                        ctv.setScrollingEnabled(true)
                    }
                }
                true
            }
        }
        adapterStiker.onClick = { pos ->
            Glide.with(applicationContext).asDrawable().load(arrSticker[pos].imv1)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        val drawableSticker = DrawableSticker(resource, "${111}.png")
                        arrStikerView[positionHome].addSticker(drawableSticker,false)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addText() {
        val textView = TextView(applicationContext).apply {
            this.text = binding.tvEdit.text.toString() + " "
            this.textSize = binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbTextSize).progress.toFloat()
            this.letterSpacing = binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbMargin).progress.toFloat()/100
            this.typeface = binding.tvEdit.typeface
            if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
                this.textSize = dpToSp(
                    arrStikerView[positionHome].getCurrentSticker()!!.textSize,
                    applicationContext
                )
                this.typeface = ResourcesCompat.getFont(
                    applicationContext,
                    arrTextFont[arrStikerView[positionHome].getCurrentSticker()!!.font].font
                )
            }
            this.setShadowLayer(
                10f, // Shadow radius
                binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).progress.toFloat(), // X offset
                binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).progress.toFloat(), // Y offset
                Color.WHITE // Shadow color
            )
            this.setTextColor(Color.parseColor("#ffffff"))
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            layout(0, 0, measuredWidth, measuredHeight)
        }
        bitmap = Bitmap.createBitmap(
            if (textView.measuredWidth == 0) {
                100
            } else {
                textView.measuredWidth
            },
            if (textView.measuredHeight == 0) {
                100
            } else {
                textView.measuredHeight
            },
            Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(bitmap)
        textView.draw(canvas)
        val drawableSticker = DrawableSticker(BitmapDrawable(resources, bitmap), "${111}.png")
        if (arrStikerView[positionHome].getCurrentSticker() != null && arrStikerView[positionHome].getCurrentSticker()!!.checkText) {
            arrStikerView[positionHome].remove(arrStikerView[positionHome].getCurrentSticker())
        }
        arrStikerView[positionHome].addSticker(
            drawableSticker,
            true,
            binding.tvEdit.text.toString(),
            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbTextSize).progress.toFloat(),
            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbApha).progress.toFloat(),
            binding.layoutText.findViewById<AppCompatSeekBar>(R.id.sbMargin).progress.toFloat()/100
        )
    }

    fun checkTrue() {
        binding.layoutStiker.visibility = View.GONE
        binding.layoutSpeed.visibility = View.GONE
        binding.layoutText.visibility = View.GONE

    }

    fun showStiker(pos: Int) {
        arrStikerView.forEach {
            it.visibility = View.GONE
        }
        arrStikerView[pos].visibility = View.VISIBLE

    }
}