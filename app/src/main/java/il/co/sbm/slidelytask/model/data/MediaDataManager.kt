package il.co.sbm.slidelytask.model.data

import il.co.sbm.slidelytask.model.MediaObject
import il.co.sbm.slidelytask.model.data.repositories.LocalMediaRepository
import il.co.sbm.slidelytask.model.data.repositories.NetworkMediaRepository
import io.reactivex.Observable

object MediaDataManager {

    fun getNextMediaObject(): Observable<MediaObject> {

        return getNextNetworkMediaObject()
            .doOnError {
                // do nothing, onErrorResumeNext will take effect.
            }
            .onErrorResumeNext(getNextLocalMediaObject())
    }

    private fun getNextLocalMediaObject(): Observable<MediaObject> {
        return LocalMediaRepository.getNextLocalMediaObjects()
    }

    private fun getNextNetworkMediaObject() : Observable<MediaObject> {
        return NetworkMediaRepository.getNextNetworkMediaObject()
    }
}