package com.namseox.st040_mecut.utils

import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.LanguageModel


object Const {
    val SORT  = "hdgfhe"
    val REQUEST_STORAGE_PERMISSION = 2000
    val REQUEST_NOTIFICATION_PERMISSION = 4000
    val PERMISON: String = "ahghe3gt"
    val LANGUAGE: String = "hgfehgh"
    var positionLanguageOld: Int = 0
    var listLanguage = arrayListOf<LanguageModel>(
        LanguageModel("Spanish", "es", R.drawable.ic_flag_spanish),
        LanguageModel("French", "fr", R.drawable.ic_flag_french),
        LanguageModel("Hindi", "hi", R.drawable.ic_flag_hindi),
        LanguageModel("English", "en", R.drawable.ic_flag_english),
        LanguageModel("Portuguese", "pt", R.drawable.ic_flag_portugeese),
        LanguageModel("German", "de", R.drawable.ic_flag_germani),
        LanguageModel("Indonesian", "in", R.drawable.ic_flag_indo)
    )
}