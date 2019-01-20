package il.co.sbm.slidelytask.model

import android.provider.MediaStore
import il.co.sbm.slidelytask.model.network.PixabayPhotoObject

data class MediaObject(
    val id: Long,
    val data: String,
    val dateAdded: Long,
    val mediaType: Int,
    val mimeType: String,
    val title: String,
    val displayName: String
) {
    constructor(iPixabayPhotoObject: PixabayPhotoObject) : this(iPixabayPhotoObject.id.toLong(), iPixabayPhotoObject.largeImageURL, -1, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, "image/jpeg", iPixabayPhotoObject.tags, iPixabayPhotoObject.tags)
}
