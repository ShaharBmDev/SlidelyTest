package il.co.sbm.slidelytask.ui.base

open class BasePresenter<T : MvpView> : Presenter<T> {

    private var _view: T? = null
    val view: T
        get() { return _view ?: throw MvpViewNotAttachedException() }

    /**
     * Attaches the view to the presenter
     */
    override fun attachView(view: T) {
        _view = view
    }

    /**
     * Detaches the view from the presenter
     */
    override fun detachView() {
        _view = null
    }

    /**
     * Checks if view is attached to presenter
     */
    override fun isViewAttached(): Boolean {
        return _view != null
    }

    class MvpViewNotAttachedException : RuntimeException(
        "Please call Presenter.attachView(MvpView) before requesting data to the Presenter")
}