package tw.dcard.bubblemock.model

object MimeType {

    // application
    const val JSON = "application/json"
    const val OCTET_STREAM = "application/octet-stream"
    const val WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"
    const val ANDROID_PACKAGE_ARCHIVE = "application/vnd.android.package-archive"

    // image
    const val IMAGE = "image/*"
    const val PNG = "image/png"
    const val JPEG = "image/jpeg"
    const val GIF = "image/gif"
    const val WEBP = "image/webp"
    const val THUMBNAIL = "image/thumbnail"
    const val MEGAPX_IMAGE = "image/megapx"

    // multipart
    const val MULTIPART_FORUM_DATA = "multipart/form-data"

    // text
    const val HTML = "text/html"
    const val PLAIN_TEXT = "text/plain"

    // video
    const val VIDEO = "video/*"
    const val MP4_VIDEO = "video/mp4"
    const val AD_VIDEO = "video/advertise"
    const val MEGAPX_VIDEO = "video/megapx"
}