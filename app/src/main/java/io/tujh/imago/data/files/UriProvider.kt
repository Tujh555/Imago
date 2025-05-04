package io.tujh.imago.data.files

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import io.tujh.imago.domain.image.WriteableUriProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class UriProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : WriteableUriProvider {
    private val dir by lazy { context.filesDir.resolve("images").apply(File::mkdirs) }

    override suspend fun temporaryImage(): Uri = withContext(Dispatchers.IO) {
        val file = dir.resolve("${UUID.randomUUID()}.jpg").apply {
            createNewFile()
            deleteOnExit()
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}${FILE_PROVIDER_AUTHORITY}",
            file
        )
    }
}