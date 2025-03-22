import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class InputStreamRequestBody(
    private val context: Context,
    private val uri: Uri,
    private val thumbnail: Bitmap?
) : RequestBody() {
    private val KEY_PNG = "png"

    override fun contentType(): MediaType? {
        return getMimeType(context, uri)?.toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (thumbnail != null) {
            val stream = ByteArrayOutputStream()
            thumbnail.compress(
                if (contentType()?.subtype.equals(
                        KEY_PNG,
                        true
                    )
                ) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG, 100, stream
            )
            sink.write(stream.toByteArray())
        } else sink.writeAll(context.contentResolver?.openInputStream(uri)!!.source())
    }

    companion object {
        private val TAG = InputStreamRequestBody::class.java.simpleName

        fun getMimeType(context: Context, uri: Uri): String? {
            return when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> {
                    context.contentResolver?.getType(uri)
                }

                ContentResolver.SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString()).lowercase()
                )

                else -> null
            }
        }

        fun getFileName(context: Context, uri: Uri): String? {
            when (uri.scheme) {
                ContentResolver.SCHEME_FILE -> {
                    val filePath = uri.path
                    if (!filePath.isNullOrEmpty()) {
                        return File(filePath).name
                    }
                }

                ContentResolver.SCHEME_CONTENT -> {
                    return getCursorContent(uri, context.contentResolver)
                }
            }

            return null
        }

        private fun getCursorContent(uri: Uri, contentResolver: ContentResolver): String? =
            kotlin.runCatching {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst()) {
                        cursor.getString(nameColumnIndex)
                    } else null
                }
            }.getOrNull()
    }

}