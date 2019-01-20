package il.co.sbm.slidelytask.ui.main

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import il.co.sbm.slidelytask.R
import il.co.sbm.slidelytask.model.SlidelyApplication
import il.co.sbm.slidelytask.model.data.MediaDataManager
import il.co.sbm.slidelytask.utils.PausableCountDownTimer
import il.co.sbm.slidelytask.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainPresenter : MainContract.Presenter() {

    private var mPausableCountDownTimer: PausableCountDownTimer? = null
    private var mNextMediaDisposable: Disposable? = null

    /**
     * onCreate
     */
    override fun onCreate(iActivity: Activity) {
        checkStorageReadPermissions(iActivity)
    }

    /**
     * Checks storage read permissions and requests the user if needed.
     */
    private fun checkStorageReadPermissions(iActivity: Activity) {
        Dexter.withActivity(iActivity).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(this)
            .check()
    }

    /**
     * onResume
     */
    override fun onResume() {
        if (mPausableCountDownTimer != null) {
            mPausableCountDownTimer!!.start()
        }

        if (isViewAttached()) {
            view.resumePlayIfNeeded()
        }
    }

    /**
     * onPause
     */
    override fun onPause() {
        if (mPausableCountDownTimer != null) {
            mPausableCountDownTimer!!.pause()
        }

        if (isViewAttached()) {
            view.pausePlayIfNeeded()
        }
    }

    /**
     * onDestroy
     */
    override fun onDestroy() {
        if (mPausableCountDownTimer != null) {
            mPausableCountDownTimer!!.cancel()
        }

        if (isViewAttached()) {
            view.stopAndReleasePlayIfNeeded()
        }

        if (mNextMediaDisposable != null) {
            mNextMediaDisposable!!.dispose()
        }
    }

    /**
     * notifies the view that reading permissions are granted.
     */
    override fun onPermissionGranted(iResponse: PermissionGrantedResponse?) {
        val requestPermission: String = iResponse?.requestedPermission?.name!!

        if (isViewAttached() && requestPermission == Manifest.permission.READ_EXTERNAL_STORAGE) {
            view.readyToLoadMedia()
        }
    }

    /**
     * Shows the systems permission request for the given parameter permission
     * @param iPermission the requested permission
     * @param iToken the permission token
     */
    override fun onPermissionRationaleShouldBeShown(iPermission: PermissionRequest?, iToken: PermissionToken?) {
        if (isViewAttached()) {
            view.showPermissionRationale(iPermission, iToken)
        }
    }

    /**
     * Shows an alert dialog stating [Unable to access storage.][R.string.storage_read_permission_denied]
     */
    override fun onPermissionDenied(iResponse: PermissionDeniedResponse?) {
        if (isViewAttached()) {
            view.showPermissionDenied(iResponse)
        }
    }

    /**
     * Shows loading message and initiate data loading.
     */
    override fun loadMediaObjects() {

        if (isViewAttached()) {
            view.showLoadingMedia()
        }

        showNextMediaObject()
    }

    /**
     * Requests and shows the next media onject from the media data manager
     */
    override fun showNextMediaObject() {

        if (mNextMediaDisposable != null) {
            mNextMediaDisposable!!.dispose()
        }

        mNextMediaDisposable = MediaDataManager.getNextMediaObject()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ iMediaObject ->
                view.showMedia(iMediaObject)
            },
                {
                    view.showNoMediaError()
                })
    }

    /**
     * An event triggered when last media was shown.
     * Starts timer to show media with time intervals.
     */
    override fun finishedShowingNextMediaObject() {

        mPausableCountDownTimer = object : PausableCountDownTimer(getSelectedMediaDisplayTime()) {
            override fun onTick(millisUntilFinished: Long) {
                //nothing
            }

            override fun onFinish() {
                showNextMediaObject()
            }
        }.start()
    }

    /**
     * Shows settings dialog after settings option clicked.
     */
    override fun onSettingsButtonClicked() {
        if (isViewAttached()) {
            view.showSettingsDialog()
        }
    }

    /**
     * Retrieves the media display time
     */
    override fun getSelectedMediaDisplayTime(): Long {
        return PreferenceUtils.getSharedPreferencesLongOrDefault(
            SlidelyApplication.getInstance(),
            PreferenceUtils.MEDIA_DISPLAY_TIME,
            SlidelyApplication.getInstance().resources.getInteger(R.integer.media_duration).toLong()
        )
    }

    /**
     * Updates the media display time
     */
    override fun updateSelectedMediaDisplayTime(iMediaDisplayTime: Int) {
        PreferenceUtils.setSharedPreference(
            SlidelyApplication.getInstance(), PreferenceUtils.MEDIA_DISPLAY_TIME,
            (iMediaDisplayTime * 1000).toLong()
        )
    }
}