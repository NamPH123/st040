package com.namseox.st040_mecut.view.customview.customimage

interface OnStickerListener {
    fun onAdded(sticker: Sticker)

    fun onClicked(sticker: Sticker)

    fun onDeleted(sticker: Sticker)

    fun onDragFinished(sticker: Sticker)

    fun onTouchedDown(sticker: Sticker)

    fun onZoomFinished(sticker: Sticker)

    fun onFlipped(sticker: Sticker)

    fun onDoubleTapped(sticker: Sticker)

    fun onHideOptionIcon()

    fun onUndoDelete(stickers: List<Sticker?>)

    fun onUndoUpdate(stickers: List<Sticker?>)

    fun onUndoDeleteAll()

    fun onRedoAll()

    fun onReplace(sticker: Sticker)
}