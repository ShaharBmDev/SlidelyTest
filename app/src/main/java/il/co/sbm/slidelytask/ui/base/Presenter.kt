package il.co.sbm.slidelytask.ui.base

interface Presenter<V : MvpView> {

    /**
     * Attaches the view to the presenter
     */
    fun attachView(view: V)

    /**
     * Detaches the view from the presenter
     */
    fun detachView()

    /**
     * Checks if view is attached to presenter
     */
    fun isViewAttached(): Boolean
}