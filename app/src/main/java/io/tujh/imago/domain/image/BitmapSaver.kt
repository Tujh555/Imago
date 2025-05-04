package io.tujh.imago.domain.image

import android.graphics.Bitmap
import android.net.Uri

interface BitmapSaver : suspend (Bitmap, Uri) -> Unit