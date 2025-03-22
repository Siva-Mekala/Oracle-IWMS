package com.plcoding.oraclewms

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException
import java.net.URISyntaxException

object FilePathUtil {
    private val LOG_TAG = FilePathUtil::class.java.simpleName

    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri): String? {
        var uri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (DocumentsContract.isDocumentUri(context.applicationContext, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                var id = DocumentsContract.getDocumentId(uri)
                id.contains(":").let {
                    if (it)
                        id = id.split(":")[1]
                } //Ex:content://com.android.providers.downloads.documents/document/msf:37835
                uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("image".equals(type, true)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video".equals(type, true)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio".equals(type, true)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.lastPathSegment
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
            } finally {
                cursor?.close()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getMimeType(url: String?): String? {
        if (url == null) return null
        var type: String? = null
//TDB Android sdk issue https://code.google.com/p/android/issues/detail?id=5510
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    /**
     * Determines the MIME type for a given file extension.
     *
     * @param ext The file to determine the MIME type of.
     * @return The MIME type of the file, or a wildcard if none could be
     * determined.
     */
    fun getType(ext: String?): String {
        if (ext.equals("mp3", ignoreCase = true)) return "audio/mpeg"
        if (ext.equals("aac", ignoreCase = true)) return "audio/aac"
        if (ext.equals("wav", ignoreCase = true)) return "audio/wav"
        if (ext.equals("ogg", ignoreCase = true)) return "audio/ogg"
        if (ext.equals("mid", ignoreCase = true)) return "audio/midi"
        if (ext.equals("midi", ignoreCase = true)) return "audio/midi"
        if (ext.equals("wma", ignoreCase = true)) return "audio/x-ms-wma"
        if (ext.equals("mp4", ignoreCase = true)) return "video/mp4"
        if (ext.equals("avi", ignoreCase = true)) return "video/x-msvideo"
        if (ext.equals("wmv", ignoreCase = true)) return "video/x-ms-wmv"
        if (ext.equals("png", ignoreCase = true)) return "image/png"
        if (ext.equals("jpg", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("jpe", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("jpeg", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("gif", ignoreCase = true)) return "image/gif"
        if (ext.equals("xml", ignoreCase = true)) return "text/xml"
        if (ext.equals("txt", ignoreCase = true)) return "text/plain"
        if (ext.equals("cfg", ignoreCase = true)) return "text/plain"
        if (ext.equals("csv", ignoreCase = true)) return "text/plain"
        if (ext.equals("conf", ignoreCase = true)) return "text/plain"
        if (ext.equals("rc", ignoreCase = true)) return "text/plain"
        if (ext.equals("htm", ignoreCase = true)) return "text/html"
        if (ext.equals("html", ignoreCase = true)) return "text/html"
        if (ext.equals("pdf", ignoreCase = true)) return "application/pdf"
        if (ext.equals("apk", ignoreCase = true)) return "application/vnd.android.package-archive"
        if (ext.equals("xls", ignoreCase = true)) return "application/vnd.ms-excel"
        if (ext.equals(
                "xlsx",
                ignoreCase = true
            )
        ) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        if (ext.equals("doc", ignoreCase = true)) return "application/msword"
        return if (ext.equals(
                "docx",
                ignoreCase = true
            )
        ) "application/vnd.openxmlformats-officedocument.wordprocessingml.document" else "*/*"
    }

    fun getTypeCodeOfFileFromMimeType(mimeType: String?): FileMimeType {
        if (mimeType == null) return FileMimeType.FILE_TYPE_IMAGE
        return if (mimeType.contains("image")) {
            FileMimeType.FILE_TYPE_IMAGE
        } else if (mimeType.contains("video")) {
            FileMimeType.FILE_TYPE_VIDEO
        } else if (mimeType.contains("audio")) {
            FileMimeType.FILE_TYPE_AUDIO
        } else if (mimeType.contains("text")) {
            FileMimeType.FILE_TYPE_TXT
        } else if (mimeType.contains("pdf")) {
            FileMimeType.FILE_TYPE_PDF
        } else if (mimeType.contains("vnd.ms-excel")) {
            FileMimeType.FILE_TYPE_XLS
        } else if (mimeType.contains("vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            FileMimeType.FILE_TYPE_XLSX
        } else if (mimeType.contains("msword")) {
            FileMimeType.FILE_TYPE_DOC
        } else if (mimeType.contains("vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            FileMimeType.FILE_TYPE_DOCX
        } else {
            FileMimeType.FILE_TYPE_OTHER
        }
    }

    fun getBufferSize(contentType: String?): Int {
        var bufferSize = 4096 * 16
        if (getTypeCodeOfFileFromMimeType(contentType) == FileMimeType.FILE_TYPE_VIDEO) {
            bufferSize = 1024 * 1024
        }
        return bufferSize
    }

    @Throws(IOException::class)
    fun mkdir(path: String?) {
        try {
            val file = File(path)
            if (!file.exists()) file.mkdirs()
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setFileDownloadPaths(
        imagePath: String?,
        audioPath: String?,
        videoPath: String?,
        documentPath: String?
    ) {
        try {
            mkdir(imagePath)
            mkdir(audioPath)
            mkdir(videoPath)
            mkdir(documentPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getExtensionFromMimeType(contentType: String?): String {
        return "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}
