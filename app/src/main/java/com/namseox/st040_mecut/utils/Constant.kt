package com.namseox.st040_mecut.utils

import android.Manifest

object Constant {

    const val SINGLE_CLICK_TIME = 500L

    var lastTimeClick = 0L

    const val INTENT_KEY = "intent_key"
    const val INTENT_KEY_DETAIL = "intent_key_detail"
    const val INTENT_KEY_LANGUAGE = "intent_key_language"
    const val INTENT_REQUEST_CODE = 100
    const val FROM_SUCCESSFULLY = "from_successfully"

    const val PER_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
    const val PER_READ_STORAGE_ABOVE_33 = Manifest.permission.READ_MEDIA_IMAGES
    const val PER_POST_NOTIFICATION = Manifest.permission.POST_NOTIFICATIONS
    const val PER_STORAGE_CODE = 1
    const val PER_STORAGE_ABOVE_33_CODE = 2
    const val PER_NOTIFICATION_CODE = 3
    const val GALLERY_REQUEST_CODE = 100

    //region Type
    const val KEYBOARD_TYPE = "Keyboard"
    const val BACKGROUND_TYPE = "background"
    const val STICKERS_TYPE = "Stickers"
    const val BACKGROUND_TEXT_TYPE = "background_text"
    const val CAT_TYPE = "cat"
    const val TEMPLATE_TYPE = "template"
    //endregion

    //region
    const val KEYBOARD_CATEGORY = "Keyboard"
    const val TAG_CATEGORY = "Tag"
    //endregion


    //endregion

    var uri: String = ""

    //region KEY TEMPLATE
    const val CAT_KEY_BUNDLE = "cat_key_bundle"
    const val BACKGROUND_KEY_BUNDLE = "background_key_bundle"
    //endregion

    enum class SharedPreferencesEnum {
        SHAREDPREF_RATE_SUCCESSFULLY, SHAREDPREF_FIRST_TIME, SHAREDPREF_FIRST_TIME_LANGUAGE, SHAREDPREF_FIRST_TIME_INTRO, SHAREDPREF_FIRST_TIME_PERMISSION, SHAREDPREF_FIRST_TIME_INTERACT, SHAREDPREF_IN_APP_COUNT, SHAREDPREF_LANGUAGE_CODE,
    }

}