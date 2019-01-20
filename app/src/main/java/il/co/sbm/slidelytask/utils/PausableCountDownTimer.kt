package il.co.sbm.slidelytask.utils

import android.os.CountDownTimer


/**
 * @param millisInFuture The number of millis in the future from the call
 *   to [start] until the countdown is done and [onFinish]
 *   is called.
 * @param countDownInterval The interval along the way to receive
 *   {@link #onTick(long)} callbacks.
 */
abstract class PausableCountDownTimer(private var millisInFuture: Long, private var countDownInterval: Long) {

    companion object {
        private const val SECOND: Long = 1000
    }

    /**
     * @param millisInFuture The number of millis in the future from the call to [start]
     * until the countdown is done and [onFinish] is called, ticking at intervals of a [SECOND].
     */
    constructor(millisInFuture: Long) : this(millisInFuture, SECOND)

    private var mMillisecondsRemaining: Long = 0

    private var mCountDownTimer: CountDownTimer? = null

    private var mIsPaused = true

    init {
        this.mMillisecondsRemaining = this.millisInFuture
    }

    /**
     * Creates a count down timer
     */
    private fun createCountDownTimer() {
        mCountDownTimer = object : CountDownTimer(mMillisecondsRemaining, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                mMillisecondsRemaining = millisUntilFinished
                this@PausableCountDownTimer.onTick(millisUntilFinished)

            }

            override fun onFinish() {
                mMillisecondsRemaining = 0
                this@PausableCountDownTimer.onFinish()

            }
        }
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    /**
     * Cancel the countdown.
     */
    fun cancel() {
        if (mCountDownTimer != null) {
            mCountDownTimer!!.cancel()
        }
        this.mMillisecondsRemaining = 0
    }

    /**
     * Start or Resume the countdown.
     * @return PausableCountDownTimer current instance
     */
    @Synchronized
    fun start(): PausableCountDownTimer {
        if (mIsPaused) {
            createCountDownTimer()
            mCountDownTimer!!.start()
            mIsPaused = false
        }
        return this
    }

    /**
     * Pauses the PausableCountDownTimer, so it could be resumed(start) later from the same point where it was paused.
     */
    @Throws(IllegalStateException::class)
    fun pause() {
        if (!mIsPaused) {
            mCountDownTimer!!.cancel()
        }

        mIsPaused = true
    }

    /**
     * Checks if the video is paused
     * @return True if pause, false otherwise
     */
    fun isPaused(): Boolean {
        return mIsPaused
    }
}