package il.co.sbm.slidelytask.model.data.repositories

import il.co.sbm.slidelytask.model.MediaObject
import il.co.sbm.slidelytask.model.SlidelyApplication
import il.co.sbm.slidelytask.model.network.PixabayPhotoObject
import il.co.sbm.slidelytask.model.network.PixabayPhotoResponse
import io.reactivex.Observable
import java.util.*

object NetworkMediaRepository {

    private const val NOT_INITIATED: Int = -1
    private const val WAITING_RESPONSE: Int = -2
    private const val COULD_NOT_CONNECT: Int = -3

    private lateinit var mNetworkMediaObjects: ArrayList<PixabayPhotoObject>
    private var mObjectIndex: Int = NOT_INITIATED

    fun getNextNetworkMediaObject(): Observable<MediaObject> {

        val result: Observable<MediaObject>

        if (mObjectIndex == NOT_INITIATED) {
            mObjectIndex = WAITING_RESPONSE
            result = getPixabayTopPhotos()
                .switchMap {
                    getNextPixabayPhotoObject()
                }
        } else {
            result = getNextPixabayPhotoObject()
        }

        return result
    }

    private fun getNextPixabayPhotoObject(): Observable<MediaObject> {
        val result: Observable<MediaObject>

        if (mObjectIndex >= 0) {

            if (mObjectIndex == mNetworkMediaObjects.size) {
                mObjectIndex = 0
            }

            result = Observable.just(MediaObject(mNetworkMediaObjects[mObjectIndex]))
            mObjectIndex++

        } else {
            result = Observable.error(RuntimeException("No valid data"))
        }

        return result
    }

    private fun getPixabayTopPhotos(): Observable<PixabayPhotoResponse> {
        return SlidelyApplication.getInstance().getPixabayService().getTopPhotos()
            .doOnNext { iResponse -> savePhotoObjects(iResponse) }
            .doOnError {
                mObjectIndex = COULD_NOT_CONNECT
                throw it
            }
    }

    private fun savePhotoObjects(iResponse: PixabayPhotoResponse) {

        if (iResponse.pixabayPhotoObjects.isNotEmpty()) {
            mNetworkMediaObjects = iResponse.pixabayPhotoObjects as ArrayList<PixabayPhotoObject>
            mObjectIndex = 0
        }
    }
}