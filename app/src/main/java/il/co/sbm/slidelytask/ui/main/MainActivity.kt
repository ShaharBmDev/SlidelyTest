package il.co.sbm.slidelytask.ui.main

import adil.dev.lib.materialnumberpicker.dialog.NumberPickerDialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.yqritc.scalablevideoview.ScalableType
import com.yqritc.scalablevideoview.ScalableVideoView
import il.co.sbm.slidelytask.R
import il.co.sbm.slidelytask.model.MediaObject
import il.co.sbm.slidelytask.model.SlidelyApplication
import il.co.sbm.slidelytask.model.data.GlobalValues.MEDIA_DISPLAY_TIME_MAX_IN_SECONDS
import il.co.sbm.slidelytask.model.data.GlobalValues.MEDIA_DISPLAY_TIME_MIN_IN_SECONDS
import il.co.sbm.slidelytask.utils.DateTileUtils
import timber.log.Timber
import java.io.File

class MainActivity : AppCompatActivity(), MainContract.View {

    @BindView(R.id.iv_maim_image1)
    lateinit var mImageView1: ImageView
    @BindView(R.id.svv_main_video1)
    lateinit var mVideoView1: ScalableVideoView
    @BindView(R.id.iv_maim_image2)
    lateinit var mImageView2: ImageView
    @BindView(R.id.svv_main_video2)
    lateinit var mVideoView2: ScalableVideoView
    @BindView(R.id.tv_main_name)
    lateinit var mTvMediaName: TextView
    @BindView(R.id.tv_main_date)
    lateinit var mTvMediaDate: TextView
    @BindView(R.id.tv_main_message)
    lateinit var mTvMessage: TextView

    /**
     * Main activity presenter
     */
    private val mMainPresenter: MainPresenter = MainPresenter()

    /**
     * Last media view
     */
    private var mLastUsedView: View? = null

    /**
     * A settings dialog, to display and change the media display time
     */
    private val mSettingsDialog: NumberPickerDialog by lazy {
        NumberPickerDialog(
            this,
            MEDIA_DISPLAY_TIME_MIN_IN_SECONDS,
            MEDIA_DISPLAY_TIME_MAX_IN_SECONDS,
            NumberPickerDialog.NumberPickerCallBack { iMediaDisplayTime: Int ->
                mMainPresenter.updateSelectedMediaDisplayTime(iMediaDisplayTime)
            })
    }

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        // attaches the view to the presenter
        mMainPresenter.attachView(this)
        // notifies the presenter that the activity created
        mMainPresenter.onCreate(this)
    }

    /**
     * A method that states that the permissions are given to read the data from the storage.
     */
    override fun readyToLoadMedia() {

        mMainPresenter.loadMediaObjects()
    }

    /**
     * Shows the systems permission request for the given parameter permission
     * @param iPermission the requested permission
     * @param iToken the permission token
     */
    override fun showPermissionRationale(iPermission: PermissionRequest?, iToken: PermissionToken?) {
        iToken?.continuePermissionRequest()
    }

    /**
     * Shows an alert dialog stating [Unable to access storage.][R.string.storage_read_permission_denied]
     */
    override fun showPermissionDenied(iResponse: PermissionDeniedResponse?) {
        AlertDialog.Builder(this).setMessage(getString(R.string.storage_read_permission_denied))
            .setNegativeButton(getString(R.string.close), null).create().show()
    }

    /**
     * Shows a message stating [Loading, please wait…][R.string.loading_please_wait] that covers the layout
     */
    override fun showLoadingMedia() {
        setMessage(R.string.loading_please_wait)
    }

    /**
     * Shows a message stating [No media found][R.string.no_media_found] that covers the layout
     */
    override fun showNoMediaError() {
        setMessage(R.string.no_media_found)
    }

    /**
     * Hides the message layout that covers the layout
     * The equivalent to [setMessage(null)][setMessage]
     */
    private fun hideMessage() {
        setMessage(null)
    }

    /**
     * Shows a message that covers the layout
     * @param iMessageResourceId the message string resource id to use
     */
    private fun setMessage(@StringRes iMessageResourceId: Int) {
        setMessage(getString(iMessageResourceId))
    }

    /**
     * Shows a message that covers the layout
     *
     * If the value is [null][] then the layout becomes hidden. the equivalent to [hideMessage()][hideMessage]
     * @param iMessage the message string  to use
     */
    private fun setMessage(iMessage: String?) {
        if (iMessage == null) {
            mTvMessage.visibility = View.GONE
        } else {
            mTvMessage.visibility = View.VISIBLE
            mTvMessage.text = iMessage
        }
    }

    /**
     * Shows the next media on the display, fading and zooming it in while fading and zooming out the last viewed media.
     * Furthermore, displays the media name and date of creation.
     * @param iMediaObject the next media data to display.
     */
    override fun showMedia(iMediaObject: MediaObject) {

        // determines the next view to use, according to the next media type and the last media that was shown
        val fadeZoomInView = determineViewToUse(iMediaObject.mediaType)

        // sets the media to the next media view by its type
        setMediaContentByType(fadeZoomInView, iMediaObject.data)

        // stops the last media from playing if it is of type MEDIA_TYPE_VIDEO
        stopFadeZoomOutVideoIfNeeded()

        // animates the two appropriate views to show a crossfade and zoom in / zoom out between them
        animateMedia(fadeZoomInView)

        // hides the message layout that covers the layout
        hideMessage()

        // sets the title and the date of the media.
        setTitleAndDate(iMediaObject)

        // notifies the presenter that the next media show procedure is finished
        mMainPresenter.finishedShowingNextMediaObject()
    }

    /**
     * Sets the media to display to the next media view by its type ([MEDIA_TYPE_IMAGE][MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE] or [MEDIA_TYPE_VIDEO][MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO])
     */
    private fun setMediaContentByType(fadeZoomInView: View?, data: String?) {

        when (fadeZoomInView) {
            // if the media is an image
            is ImageView -> {
                data?.let {
                    if (data.startsWith("http://", true) ||
                        data.startsWith("https://", true)
                    ) {
                        SlidelyApplication.getInstance().getPicasso().load(Uri.parse(data))/*.fit().centerCrop()*/.into(
                            fadeZoomInView
                        )
                    } else {
                        SlidelyApplication.getInstance().getPicasso().load(File(data)).fit().centerCrop()
                            .into(fadeZoomInView)
                    }
                }
            }
            // if the media is a video
            is ScalableVideoView -> {
                data?.let {
                    fadeZoomInView.setDataSource(data)
                    fadeZoomInView.setScalableType(ScalableType.CENTER_CROP)
                    fadeZoomInView.prepareAsync(MediaPlayer.OnPreparedListener {
                        fadeZoomInView.isLooping = true
                        fadeZoomInView.start()
                    })
                }
            }
        }
    }

    /**
     * Stops the last media from playing if it is of type [MEDIA_TYPE_VIDEO][MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO]
     */
    private fun stopFadeZoomOutVideoIfNeeded() {
        if (mLastUsedView is ScalableVideoView) {
            try {
                (mLastUsedView as ScalableVideoView).stop()
            } catch (e: Exception) {
                Timber.i(e.stackTrace.toString())
            }
        }
    }

    /**
     * Animates the two appropriate views to show a crossfade and zoom in / zoom out between them.
     * @param fadeZoomInView the view that displays the next media. it will zoom and fade in.
     * [lastViewUsed][mLastUsedView] is used here as it is the view that displays the last media. it will zoom and fade out.
     */
    private fun animateMedia(fadeZoomInView: View?) {

        //loads animation and sets it to the next media view.
        val fadeZoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_zoom_in)
        fadeZoomInView?.visibility = View.VISIBLE
        fadeZoomInView?.animation = fadeZoomInAnimation

        //loads animation and sets it to the last media view.
        val fadeZoomOutView = mLastUsedView
        val fadeZoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_zoom_out)
        fadeZoomOutAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                fadeZoomOutView?.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        fadeZoomOutView?.animation = fadeZoomOutAnimation

        // the next view becomes the last view
        mLastUsedView = fadeZoomInView
    }

    /**
     * Sets the title and the date of the media.
     * @param data the object of whom the data is retrieved.
     */
    private fun setTitleAndDate(data: MediaObject?) {

        var addedDate = ""

        if (data != null && data.dateAdded >= 0) {
            addedDate = DateTileUtils.convertMillisecondsToDateTimeFormat(data.dateAdded)
        }

        mTvMediaName.text = data?.title ?: ""
        mTvMediaDate.text = addedDate
    }

    /**
     * Determines the next view to use, according to the next media type and the last media that was shown.
     * @param mediaType The media type, could be either [MEDIA_TYPE_IMAGE][MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE] or
     * [MEDIA_TYPE_VIDEO][MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO]
     * @return The correct view type to display the media, or [null][] if the mediaType is not supported.
     */
    private fun determineViewToUse(mediaType: Int?): View? {

        var result: View? = null

        when (mediaType) {
            // if next media type is image
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                // checks last media used to use choose next media view
                result = when (mLastUsedView) {
                    // last media used was a video, so it is safe to use the first image view
                    mVideoView1, mVideoView2 -> {
                        mImageView1
                    }
                    // last media used was an image with the first image view, so it is safe to use the second image view
                    mImageView1 -> {
                        mImageView2
                    }
                    // last media used was an image with the second image view, so it is safe to use the first image view
                    mImageView2 -> {
                        mImageView1
                    }
                    // there was no last media used, so it is safe to use the first image view
                    else -> {
                        mImageView1
                    }
                }
            }
            // if next media type is video
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                // checks last media used to use choose next media view
                result = when (mLastUsedView) {
                    // last media used was an image, so it is safe to use the first video view
                    mImageView1, mImageView2 -> {
                        mVideoView1
                    }
                    // last media used was a video with the first video view, so it is safe to use the second video view
                    mVideoView1 -> {
                        mVideoView2
                    }
                    // last media used was a video with the second video view, so it is safe to use the first video view
                    mVideoView2 -> {
                        mVideoView1
                    }
                    // there was no last media used, so it is safe to use the first video view
                    else -> {
                        mVideoView1
                    }
                }
            }
        }

        return result
    }

    /**
     * Sets option menu for the activity.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Consumes option menu item clicks.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.mb_main_settings -> mMainPresenter.onSettingsButtonClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Shows a settings dialog, displaying the current media display time in seconds and allows to change it.
     */
    override fun showSettingsDialog() {

        if (!mSettingsDialog.isShowing) {

            mSettingsDialog.show()

            val selectedMediaDisplayTime: Int = (mMainPresenter.getSelectedMediaDisplayTime() / 1000).toInt()
            if (selectedMediaDisplayTime in MEDIA_DISPLAY_TIME_MIN_IN_SECONDS..MEDIA_DISPLAY_TIME_MAX_IN_SECONDS) {
                mSettingsDialog.onItemClicked(selectedMediaDisplayTime, selectedMediaDisplayTime)
            }
        }
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()

        mMainPresenter.onResume()
    }

    /**
     * onPause
     */
    override fun onPause() {
        super.onPause()

        mMainPresenter.onPause()
    }

    /**
     * onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()

        mMainPresenter.onDestroy()
    }

    /**
     * Resumes video play if it is possible
     */
    override fun resumePlayIfNeeded() {
        if (mLastUsedView is ScalableVideoView) {
            (mLastUsedView as ScalableVideoView).start()
        }
    }

    /**
     * Pauses video play if possible
     */
    override fun pausePlayIfNeeded() {
        if (mLastUsedView is ScalableVideoView) {
            (mLastUsedView as ScalableVideoView).pause()
        }
    }

    /**
     * Stops and releases video play if possible
     */
    override fun stopAndReleasePlayIfNeeded() {
        if (mLastUsedView is ScalableVideoView) {
            (mLastUsedView as ScalableVideoView).stop()
        }
    }
}