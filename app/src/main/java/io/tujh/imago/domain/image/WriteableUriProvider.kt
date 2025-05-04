package io.tujh.imago.domain.image

import android.net.Uri

interface WriteableUriProvider {
    suspend fun temporaryImage(): Uri
}