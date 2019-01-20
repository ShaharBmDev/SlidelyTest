package il.co.sbm.slidelytask.ui.main

import android.app.Activity
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import il.co.sbm.slidelytask.model.MediaObject
import il.co.sbm.slidelytask.ui.base.BasePresenter
import il.co.sbm.slidelytask.ui.base.MvpView

object MainContract {

    interface View : MvpView {

        fun readyToLoadMedia()
        fun showPermissionRationale(iPermission: PermissionRequest?,iToken: PermissionToken?)
        fun showPermissionDenied(iResponse: PermissionDeniedResponse?)
        fun showLoadingMedia()
        fun showMedia(iMediaObject: MediaObject)
        fun showNoMediaError()
        fun showSettingsDialog()
        fun resumePlayIfNeeded()
        fun pausePlayIfNeeded()
        fun stopAndReleasePlayIfNeeded()
    }

    abstract class Presenter : BasePresenter<View>(), PermissionListener {

        abstract fun onCreate(iActivity: Activity)
        abstract fun onResume()
        abstract fun onPause()
        abstract fun onDestroy()
        abstract fun loadMediaObjects()
        abstract fun showNextMediaObject()
        abstract fun finishedShowingNextMediaObject()
        abstract fun onSettingsButtonClicked()
        abstract fun getSelectedMediaDisplayTime(): Long
        abstract fun updateSelectedMediaDisplayTime(iMediaDisplayTime: Int)
    }
}