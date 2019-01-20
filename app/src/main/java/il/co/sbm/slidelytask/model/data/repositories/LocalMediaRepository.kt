package il.co.sbm.slidelytask.model.data.repositories

import android.database.Cursor
import android.provider.MediaStore
import il.co.sbm.slidelytask.model.MediaObject
import il.co.sbm.slidelytask.model.SlidelyApplication
import il.co.sbm.slidelytask.utils.DateTileUtils
import io.reactivex.Observable

object LocalMediaRepository {

    private const val EXTERNAL: String = "external"
    private const val NOT_INITIATED: Int = -1

    private lateinit var mLocalMediaObjects: ArrayList<MediaObject>
    private var mObjectIndex: Int = NOT_INITIATED

    /**
     * Gets the next media object to show.
     * @return The next media object to show or null if there isn't any.
     */
    fun getNextLocalMediaObjects(): Observable<MediaObject> {

        var result: MediaObject? = null

        // loads local media if it not already loaded
        if (mObjectIndex == NOT_INITIATED) {
            getAllVideosAndImagesOnDeviceOrderedByDateTakenDesc()
        }

        // checks if there is media after media load
        if (mObjectIndex != NOT_INITIATED) {

            // if reached to the last media element reset to first and continue
            if (mObjectIndex == mLocalMediaObjects.size) {
                mObjectIndex = 0
            }

            // gets the next media object
            result = mLocalMediaObjects[mObjectIndex]
            // increments to the next media position
            mObjectIndex++
        }

        return Observable.just(result)
    }

    /**
     * Gets all the videos and images on the device, ordered by date taken descending.
     * Saved to heap.
     */
    private fun getAllVideosAndImagesOnDeviceOrderedByDateTakenDesc() {

        val mediaObjects: ArrayList<MediaObject> = ArrayList()

        // creates the uri for the query
        val queryUri = MediaStore.Files.getContentUri(EXTERNAL)

        // creates the projection for the query
        val projection: Array<String> = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DISPLAY_NAME
        )

        // creates selection for the query
        val selection: String =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        // creates sort for the query
        val sort: String = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

        val cursor: Cursor? =
            SlidelyApplication.getInstance().contentResolver.query(queryUri, projection, selection, null, sort)

        // creates media objects from the results received
        try {
            if (cursor != null && !cursor.isAfterLast) {
                cursor.moveToFirst()
                do {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                    // we multiply with SECOND_IN_MILLISECONDS because the result is in seconds
                    val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)) * DateTileUtils.SECOND_IN_MILLISECONDS
                    val mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))
                    val mimeType= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                    mediaObjects.add(
                        MediaObject(
                            id,
                            data,
                            dateAdded,
                            mediaType,
                            mimeType,
                            title,
                            displayName
                        )
                    )
                } while (cursor.moveToNext())

                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // sets the results to the member of the repository
        mLocalMediaObjects = mediaObjects
        // resets the position index of the last media viewed or NOT_INITIATED if there is no media on the device.
        mObjectIndex = if (mLocalMediaObjects.isEmpty()) NOT_INITIATED else 0
    }
}