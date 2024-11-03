package com.namseox.st040_mecut.utils

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.dialog.DialogRate
import com.namseox.st040_mecut.utils.Const.REQUEST_STORAGE_PERMISSION
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


var RATE = "rate"

fun Activity.shareApp() {
    ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + (this).getPackageName())
        .startChooser()
}

fun Activity.policy() {
    val url =
        "https://sites.google.com/view/lvt-policy-fake-video-call/"
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}
fun newIntent(context: Context, cls: Class<*>): Intent {
    return Intent(context, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
var unItem: (() -> Unit)? = null
fun Activity.rateUs(i: Int, view: View?) {
    if (view != null) {
        unItem?.invoke()
    }
    var dialog = DialogRate(this)
    dialog.init(object : DialogRate.OnPress {
        override fun send(rate: Float) {
        }

        override fun rating() {
            val manager = ReviewManagerFactory.create(this@rateUs!!)
            var request: Task<ReviewInfo> = manager.requestReviewFlow();
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var reviewInfo = task.result;
                    val flow: Task<Void> = manager.launchReviewFlow((this@rateUs)!!, reviewInfo)
                    dialog.dismiss()
                    flow.addOnCompleteListener { task2 ->
                        if (i == 1) {
                            finishAffinity()
                        }
                    }
                } else {
                    dialog.dismiss()
                    if (i == 1) {
                        finishAffinity()
                    }
                }
            }
        }

        override fun cancel() {
            if (i == 1) {
                finishAffinity()
            }
        }

        override fun later() {

        }
    })
    if (!SharedPreferenceUtils.getInstance(this).getBooleanValue("rate")) {
        dialog.show()
    }
}

fun Activity.showSystemUI(white: Boolean) {
    if (white) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//        WindowCompat.setDecorFitsSystemWindows(window, false);
    } else {
//        getWindow().setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                )
    }
}

fun Activity.backPress(providerSharedPreference: SharedPreferenceUtils) {
    var a = providerSharedPreference.getNumber("rate2")
    a += 1
    providerSharedPreference.putNumber("rate2", a)
    if (a % 2 == 1) {
        if (!providerSharedPreference.getBooleanValue("rate")
        ) {
            rateUs(1, null)
        } else {
            finishAffinity()
        }
    } else {
        finishAffinity()
    }
}

fun showToast(context: Context, id: Int) {
    Toast.makeText(context, context.resources.getText(id), Toast.LENGTH_SHORT).show()
}

fun changeColor(
    context: Context,
    text: String,
    color: Int,
    fontfamily: Int,
    textSize: Float
): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        0,
        text.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val font = ResourcesCompat.getFont(context, fontfamily)
    val typefaceSpan = CustomTypefaceSpan("", font)
    spannableString.setSpan(
        typefaceSpan,
        0,
        text.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannableString.setSpan(
        RelativeSizeSpan(textSize),
        0,
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

class CustomTypefaceSpan(private val family: String, private val typeface: Typeface?) :
    TypefaceSpan(family) {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, typeface)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, typeface)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
        if (tf != null) {
            paint.typeface = tf
        } else {
            paint.typeface = Typeface.DEFAULT
        }
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services = activityManager.getRunningServices(Integer.MAX_VALUE)
    for (service in services) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun dpToPx(dp: Float, context: Context): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
}

fun dpToSp(sp: Float, context: Context): Float {
    val floatSize =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    return floatSize
}

fun Activity.setupWindow() {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
//    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
}

fun AppCompatActivity.changeFragment(fragment: Fragment, tag: String?, id: Int) {
    var fragmentManager: FragmentManager? = null
    var transaction: FragmentTransaction? = null
    fragmentManager = supportFragmentManager
    transaction = fragmentManager!!.beginTransaction()
    val existingFragment: Fragment? = fragmentManager!!.findFragmentByTag(tag)
    if (existingFragment != null) {
        transaction!!.replace(id, existingFragment)
    } else {
        transaction!!.replace(id, fragment, tag)
        transaction!!.addToBackStack(tag)
    }
    transaction!!.commit()
}

fun shareFile(context: Context, file: File) {
    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Share Video"))
    }
}

fun getAllUriInFileAsset(context: Context, filePaths: ArrayList<String>): ArrayList<Uri> {
    val assetUris = ArrayList<Uri>()
    for (filePath in filePaths) {

        var uri = Uri.parse(
            "content://com.keyboard.fonts.emojikeyboard.theme.Provider/" + filePath.replace(
                "file:///android_asset/",
                ""
            )
        )
        assetUris.add(uri)
    }
    return assetUris
}
fun shareVideoOnFacebook(context: Context, file: File) {
    // Tạo URI cho file video
    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Tạo intent gửi file với kiểu video
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Đặt gói ứng dụng là Facebook để chia sẻ trực tiếp lên Facebook
    intent.setPackage("com.facebook.katana")

    // Kiểm tra nếu Facebook đã được cài đặt trên thiết bị
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Share Video on Facebook"))
    } else {
        // Hiển thị thông báo nếu Facebook chưa được cài đặt
        Toast.makeText(context, "Facebook app is not installed", Toast.LENGTH_SHORT).show()
    }
}
fun shareVideoOnTikTok(context: Context, file: File) {
    // Tạo URI cho file video
    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Tạo intent gửi file với kiểu video
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Đặt gói ứng dụng là TikTok để chia sẻ trực tiếp lên TikTok
    intent.setPackage("com.zhiliaoapp.musically")

    // Kiểm tra nếu TikTok đã được cài đặt trên thiết bị
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Share Video on TikTok"))
    } else {
        // Hiển thị thông báo nếu TikTok chưa được cài đặt
        Toast.makeText(context, "TikTok app is not installed", Toast.LENGTH_SHORT).show()
    }
}
fun shareVideoOnInstagram(context: Context, file: File) {
    // Tạo URI cho file video
    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Tạo intent gửi file với kiểu video
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Chỉ định gói ứng dụng Instagram
    intent.setPackage("com.instagram.android")

    // Kiểm tra nếu Instagram đã được cài đặt trên thiết bị
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, "Share Video on Instagram"))
    } else {
        // Hiển thị thông báo nếu Instagram chưa được cài đặt
        Toast.makeText(context, "Instagram app is not installed", Toast.LENGTH_SHORT).show()
    }
}
fun shareVideoOnWhatsApp(context: Context, file: File) {
    // Tạo URI cho file video
    val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    // Tạo intent gửi file với kiểu video
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "video/*"
    intent.putExtra(Intent.EXTRA_STREAM, fileUri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    // Kiểm tra nếu WhatsApp đã được cài đặt trên thiết bị
    val whatsappIntent = Intent(intent).apply {
        setPackage("com.whatsapp")
    }

    if (whatsappIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(whatsappIntent, "Share Video on WhatsApp"))
    } else {
        // Hiển thị thông báo nếu WhatsApp chưa được cài đặt
        Toast.makeText(context, "WhatsApp app is not installed", Toast.LENGTH_SHORT).show()
    }
}

fun shareAllFile(context: Context, imageUris: ArrayList<Uri>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "video/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Video"))
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager!!.toggleSoftInputFromWindow(
        view.getApplicationWindowToken(),
        InputMethodManager.SHOW_FORCED, 0
    )
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getAllUriInFIleAsset(context: Context, path: String): ArrayList<String> {
    val pathList = arrayListOf<String>()
    try {
        val files = context.assets.list(path) ?: arrayOf()
        for (file in files) {
            val fullPath = "file:///android_asset/$path/$file"
            pathList.add(fullPath)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    Log.d(TAG, "getAllPathInFileAsset: ${pathList.size}")
    return pathList
}

fun getAllFile(folder: File): ArrayList<String> {
    var arr = arrayListOf<String>()
    if (folder.exists()) {
        val files = folder.listFiles()
        files.forEach {
            arr.add(it.path)
        }
    }
    return arr
}

fun pickImage(fragment: Fragment?, activity: Activity?) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.setType("image/*")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
    if (fragment == null) {
        activity!!.startActivityForResult(intent, 1102)
    } else {
        fragment!!.startActivityForResult(intent, 1103)
    }
}

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
fun fileToDrawable(context: Context, filePath: String?): Drawable? {
    val file = File(filePath)
    if (!file.exists()) {
        return null
    }
    val bitmap = BitmapFactory.decodeFile(filePath)
    return BitmapDrawable(context.resources, bitmap)
}

fun Intent.putParcelableExtra(key: String, value: Parcelable): Intent {
    return this.putExtra(key, value)
}

fun setWidthHeight(view: View, width: Int, height: Int) {
    val params = view.layoutParams
    if (width != 0) {
        params.width = width
    }
    if (height != 0) {
        params.height = height
    }
    view.layoutParams = params
}

fun setLayoutParamParent(view: View, top: Float, right: Float, bottom: Float, left: Float) {
    val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, // width
        LinearLayout.LayoutParams.WRAP_CONTENT // height
    )
    val marginTopInPixels = dpToPx(top, view.context).toInt()
    params.setMargins(0, marginTopInPixels, 0, 0)
    view.layoutParams = params
}

fun setLayoutParam(view: View, top: Float, right: Float, bottom: Float, left: Float) {
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    view.layoutParams = layoutParams
}

fun changeText(context: Context, text: String, color: Int, fontfamily: Int): SpannableString {
    val spannableString = SpannableString(text)
    spannableString.setSpan(
        ForegroundColorSpan(color),
        0,
        text.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val font = ResourcesCompat.getFont(context, fontfamily)
    val typefaceSpan = CustomTypefaceSpan("", font)
    spannableString.setSpan(
        typefaceSpan,
        0,
        text.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

var lastClickTime = 0L
fun View.onSingleClick(action: () -> Unit) {
    this.setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime >= 500) {
            action()
            lastClickTime = System.currentTimeMillis()
        }
    }
}

fun onClick(action: () -> Unit) {
    if (System.currentTimeMillis() - lastClickTime >= 1000) {
        action()
        lastClickTime = System.currentTimeMillis()
    }
}

fun View.onClick(action: () -> Unit) {
    this.setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime >= 3000) {
            action()
            lastClickTime = System.currentTimeMillis()
        }
    }
}

lateinit var documentFile: DocumentFile


fun getFilePathFromURI(context: Context, uri: Uri): String? {
    var filePath: String? = null
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // DocumentProvider
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val type = split[0]

        var contentUri: Uri? = null
        if ("image" == type) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if ("video" == type) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if ("audio" == type) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val selection = "_id=?"
        val selectionArgs = arrayOf(
            split[1]
        )

        filePath = getDataColumn(context, contentUri, selection, selectionArgs)
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        // MediaStore (and general)
        filePath = getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        filePath = uri.path
    }

    return filePath
}

fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )

    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}


fun Activity.pathOfFileCreateFromUri(uri: Uri, endFile: String): String? {

    var filePath: String? = null
    var inputStream: InputStream? = null
    try {
        documentFile = DocumentFile.fromSingleUri(applicationContext, uri)!!
        val contentResolver = contentResolver
        inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val file = createTemporalFileFrom(inputStream, documentFile.name!!, endFile)
            filePath = file?.path
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return filePath
}

val buffer = ByteArray(8 * 1024)
var outputDirectory: File? = null
var outputDir: File? = null
var uploadSuccess = MutableLiveData<Int>()

@Throws(IOException::class)
fun Activity.createTemporalFileFrom(
    inputStream: InputStream,
    name: String,
    endFile: String
): File? {
    outputDirectory = File(applicationContext.filesDir, "Ringtone")
    if (!outputDirectory!!.exists()) {
        outputDirectory!!.mkdirs()
    }
    var outputDir: File? = null
    outputDir = File(outputDirectory, "$name.$endFile")
    if (outputDir!!.exists()) {

    } else {
        try {
            inputStream.use { input ->
                FileOutputStream(outputDir).use { output ->
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        Log.d(TAG, "createTemporalFileFrom: ${bytesRead}")
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()

                }


            }
        } catch (e: Exception) {
            Log.d(TAG, "createTemporalFileFrom: ${e}")
            e.printStackTrace()
            outputDir = null
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                Log.d(TAG, "createTemporalFileFrom: Close stream error")
                e.printStackTrace()
            }
        }
    }

    uploadSuccess.postValue(1)
    return outputDir
}

fun Activity.setNotificationSound(uri: Uri) {
    RingtoneManager.setActualDefaultRingtoneUri(
        this,
        RingtoneManager.TYPE_NOTIFICATION,
        uri
    )
}

fun Activity.setAlarmSound(uri: Uri) {
    RingtoneManager.setActualDefaultRingtoneUri(
        this,
        RingtoneManager.TYPE_ALARM,
        uri
    )
}


fun Activity.requesPermission(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
): Int {
    when (requestCode) {
        REQUEST_STORAGE_PERMISSION->{
            if(Build.VERSION.SDK_INT >= 33){
                if(checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED){
                    return Const.REQUEST_STORAGE_PERMISSION
                }else{
                    showDialogNotifiListener(R.string.reques_storage)
                }
            }else{
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                    return Const.REQUEST_STORAGE_PERMISSION
                }else{
                    showDialogNotifiListener(R.string.reques_storage)
                }
            }
        }
//        Const.REQUEST_RECORD_AUDIO_PERMISSION -> {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                return Const.REQUEST_RECORD_AUDIO_PERMISSION
//            } else {
//                showDialogNotifiListener(R.string.reques_record_audio)
//            }
//        }
//
//        Const.REQUEST_CODE_CAMERA -> {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.CAMERA
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                return Const.REQUEST_CODE_CAMERA
//            } else {
//                showDialogNotifiListener(R.string.reques_camera)
//            }
//        }
//
        Const.REQUEST_NOTIFICATION_PERMISSION -> {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return Const.REQUEST_NOTIFICATION_PERMISSION
            } else {
                showDialogNotifiListener(R.string.content_dialog_record)
            }

        }
    }
    return 0
}


fun Activity.showDialogNotifiListener(i: Int) {
    val builder = android.app.AlertDialog.Builder(this)
    builder.setTitle(R.string.permission)
        .setMessage(i)
        .setPositiveButton(R.string.yes) { dialog, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
            dialog.dismiss()
//            AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
//            AppOpenManager.getInstance().disableAppResumeWithActivity(ChooseIdolActivity::class.java)
        }
        .setNegativeButton(R.string.tv_cancel) { dialog, _ ->
            dialog.dismiss()
            showSystemUI(true)
        }
        .setCancelable(false)
    val alertDialog = builder.create()
    alertDialog.show()
    val negativeButton =
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
    val negativeButton2 =
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
    negativeButton2?.setTextColor(resources.getColor(R.color.color_app))
    negativeButton?.setTextColor(resources.getColor(R.color.color_app))
}

//fun Activity.showDialogPer(i : Int){
//    val builder = android.app.AlertDialog.Builder(this)
//    builder.setTitle(R.string.permission)
//        .setMessage(i)
//        .setPositiveButton(R.string.setting) { dialog, _ ->
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            val uri = Uri.fromParts("package", packageName, null)
//            intent.data = uri
//            startActivity(intent)
//            dialog.dismiss()
//        }
//        .setNegativeButton(R.string.tv_cancel) { dialog, _ ->
//            dialog.dismiss()
//            showSystemUI(true)
//        }
//        .setCancelable(false)
//    val alertDialog = builder.create()
//    alertDialog.show()
//    val negativeButton =
//        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//    val negativeButton2 =
//        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//    negativeButton2?.setTextColor(resources.getColor(R.color.color_app))
//    negativeButton?.setTextColor(resources.getColor(R.color.color_app))
//}
//fun Activity.showDialogRequestService(){
//
//    val builder = android.app.AlertDialog.Builder(this)
//    builder.setTitle(R.string.opern_service)
//        .setMessage(R.string.reques_service)
//        .setPositiveButton(R.string.yes) { dialog, _ ->
//            startForegroundService(Intent(this, StartService::class.java))
//            showSystemUI(true)
//        }
//        .setNegativeButton(R.string.tv_cancel) { dialog, _ ->
//            dialog.dismiss()
//            showSystemUI(true)
//        }
//        .setCancelable(false)
//    val alertDialog = builder.create()
//    alertDialog.show()
//    val negativeButton =
//        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//    val negativeButton2 =
//        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//    negativeButton2?.setTextColor(resources.getColor(R.color.color_app))
//    negativeButton?.setTextColor(resources.getColor(R.color.color_app))
//    alertDialog.window!!.setBackgroundDrawableResource(R.drawable.bg_card_border)
//}
//fun Activity.isServiceRunning( serviceClass: Class<out Service>): Boolean {
//    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//    val services = activityManager.getRunningServices(Int.MAX_VALUE)
//    for (service in services) {
//        if (serviceClass.name == service.service.className) {
//            return true
//        }
//    }
//    return false
//}
fun Activity.checkPermissionCamera(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}


fun startNewActivity(context: Context, cls: Class<*>): Intent {
    return Intent(
        context,
        cls
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
}


//CameraCharacteristics.LENS_FACING_BACK    camera sau
//CameraCharacteristics.LENS_FACING_FRONT    camera trước
fun Activity.isCameraAvailable(lensFacing: Int): Boolean {
    val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    try {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing != null && facing == lensFacing) {
                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun changeGradientText(textView: AppCompatTextView) {
    textView.viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (textView.width > 0 && textView.height > 0) {
                val textShader: Shader = LinearGradient(
                    0f, 0f, textView.width.toFloat(), textView.textSize.toFloat(),
                    intArrayOf(
                        Color.parseColor("#FFB951"),
                        Color.parseColor("#CE50E0")
                    ), floatArrayOf(0.25f, 1f), Shader.TileMode.CLAMP
                )
                textView.paint.setShader(textShader)
            }
            textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}
fun changeGradientText2(textView: AppCompatTextView, color : String) {
    textView.viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (textView.width > 0 && textView.height > 0) {
                val textShader: Shader = LinearGradient(
                    0f, 0f, textView.width.toFloat(), textView.textSize.toFloat(),
                    intArrayOf(
                        Color.parseColor(color),
                        Color.parseColor(color)
                    ), floatArrayOf(0.25f, 1f), Shader.TileMode.CLAMP
                )
                textView.paint.setShader(textShader)
            }
            textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}
fun checkPermision(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return requestPer33(context)
    } else {
        return requestPer26(context)
    }
}
fun requestPer33(context: Context): Boolean {
    return (ActivityCompat.checkSelfPermission(
        context, Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context, Manifest.permission.READ_MEDIA_AUDIO
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context, Manifest.permission.READ_MEDIA_VIDEO
    ) == PackageManager.PERMISSION_GRANTED)
}
fun requestPer26(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}
fun checkUsePermision(): Array<String> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return storge_permissions_33
    } else {
        return storge_permissions_30
    }
}
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
var storge_permissions_33 = arrayOf(
    Manifest.permission.READ_MEDIA_IMAGES,
    Manifest.permission.READ_MEDIA_AUDIO,
    Manifest.permission.READ_MEDIA_VIDEO
)

var storge_permissions_30 = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
fun formatDate(date: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}
fun formatDateTime(date: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy | hh:mm a", Locale.ENGLISH)
    return dateFormat.format(date)
}
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
 fun getVideoDuration(filePath: String): String {
    try {
        val retriever = MediaMetadataRetriever()
        Log.d(TAG, "getVideoDuration: $filePath")
        retriever.setDataSource(filePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
        retriever.release()
        return String.format("%02d:%02d", minutes, seconds)
    }catch (e : Exception){
       return ""
    }

}
fun getAllVideoFolders(context: Context): List<String> {
    val mediaFolders = mutableListOf<String>()

    // Query ảnh
    val imageProjection = arrayOf(
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.DATA
    )
    val imageUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val imageCursor: Cursor? = context.contentResolver.query(
        imageUri, imageProjection, null, null, null
    )

    imageCursor?.use {
        val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        while (it.moveToNext()) {
            val imagePath = it.getString(dataColumnIndex)
            val folderPath = File(imagePath).parent

            if (folderPath != null && !mediaFolders.contains(folderPath)) {
                mediaFolders.add(folderPath)
            }
        }
    }

    // Query video
    val videoProjection = arrayOf(
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.DATA
    )
    val videoUri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val videoCursor: Cursor? = context.contentResolver.query(
        videoUri, videoProjection, null, null, null
    )

    videoCursor?.use {
        val dataColumnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        while (it.moveToNext()) {
            val videoPath = it.getString(dataColumnIndex)
            val folderPath = File(videoPath).parent

            if (folderPath != null && !mediaFolders.contains(folderPath)) {
                mediaFolders.add(folderPath)
            }
        }
    }

    return mediaFolders
}

fun isImageFile(filePath: String): Boolean {
    val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    val fileExtension = File(filePath).extension.lowercase()
    return imageExtensions.contains(fileExtension)
}
fun isVideoFile(filePath: String): Boolean {
    val videoExtensions = listOf("mp4", "mkv", "avi", "3gp", "mov", "flv", "wmv")
    val fileExtension = File(filePath).extension.lowercase()
    return videoExtensions.contains(fileExtension)
}
fun getAllAudioFiles(context: Context): List<String> {
    val audioFiles = mutableListOf<String>()

    val projection = arrayOf(
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION
    )
    val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        null
    )?.use { cursor ->
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

        while (cursor.moveToNext()) {
            val name = cursor.getString(nameColumn)
            val data = cursor.getString(dataColumn)
            audioFiles.add("$data")
        }
    }
    return audioFiles
}
fun fileToBytes(file: File): ByteArray {
    val size = file.length().toInt()
    val bytes = ByteArray(size)
    try {
        val buf = BufferedInputStream(FileInputStream(file))
        buf.read(bytes, 0, bytes.size)
        buf.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return bytes
}





fun requestDeleteFile(activity: Activity, path: ArrayList<String>, deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
    var uriList: ArrayList<Uri> = arrayListOf()
    path.forEach {
        val mediaID: Long = getFilePathToMediaID(it, activity)
        val uri = ContentUris.withAppendedId(
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            mediaID
        )
        uriList.add(uri)
    }

    requestDeletePermission(activity, deleteResultLauncher, uriList,path)
}
fun requestDeletePermission(
    activity: Activity,
    deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
    uriList: List<Uri>,path: List<String>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val pi = MediaStore.createDeleteRequest(activity.contentResolver, uriList)
        try {
            deleteResultLauncher.launch(IntentSenderRequest.Builder(pi.intentSender).build())
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }else{
        path.forEach { File(it).delete() }

    }
}
fun getFilePathToMediaID(path: String, context: Context): Long {
    var id: Long = 0
    val cr = context.contentResolver
    val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
    val selection = MediaStore.Video.Media.DATA
    val selectionArgs = arrayOf(path)
    val projection = arrayOf(MediaStore.Video.Media._ID)
    val cursor = cr.query(
        uri, projection,
        "$selection=?", selectionArgs, null
    )
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            id = cursor.getString(idIndex).toLong()
        }
    }
    return id
}
internal fun tryBlock(func: () -> Unit): Exception? {
    return try {
        func.invoke()
        null
    } catch (e: Exception) {
        e
    }
}
