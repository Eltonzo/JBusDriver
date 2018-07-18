package me.jbusdriver.mvp

import me.jbusdriver.base.mvp.BaseView
import me.jbusdriver.base.mvp.bean.ILink
import me.jbusdriver.base.mvp.bean.PageInfo
import me.jbusdriver.base.mvp.presenter.BasePresenter
import me.jbusdriver.base.mvp.bean.ActressInfo
import me.jbusdriver.base.mvp.bean.Genre
import me.jbusdriver.base.mvp.bean.Movie
import me.jbusdriver.mvp.presenter.BaseCollectPresenter
import me.jbusdriver.base.data.enums.DataSourceType

/**
 * Created by Administrator on 2017/4/9.
 */
interface MainContract {
    interface MainView : BaseView
    interface MainPresenter : BasePresenter<MainView>
}

interface LinkListContract {
    interface LinkListView : BaseView.BaseListWithRefreshView {
        val type: DataSourceType
        val pageMode: Int
        fun insertData(pos: Int, data: List<*>)
        fun moveTo(pos: Int)
    }

    interface LinkListPresenter : BasePresenter.BaseRefreshLoadMorePresenter<LinkListView>, BasePresenter.LazyLoaderPresenter {
        fun setAll(iaAll: Boolean)
        fun jumpToPage(page: Int)
        fun isPrevPageLoaded(currentPage: Int): Boolean
        val currentPageInfo: PageInfo
    }
}



interface MovieParseContract {
    interface MovieParseView : BaseView
    interface MovieParsePresenter : BasePresenter<MovieParseView>

}

interface MineCollectContract {
    interface MineCollectView : BaseView
    interface MineCollectPresenter : BasePresenter<MineCollectView>, BasePresenter.LazyLoaderPresenter
}

interface MovieCollectContract {
    interface MovieCollectView : BaseView.BaseListWithRefreshView
    interface MovieCollectPresenter : BasePresenter.BaseRefreshLoadMorePresenter<MovieCollectView>, BaseCollectPresenter<Movie>, BasePresenter.LazyLoaderPresenter
}
interface ActressCollectContract {
    interface ActressCollectView : BaseView.BaseListWithRefreshView
    interface ActressCollectPresenter : BasePresenter.BaseRefreshLoadMorePresenter<ActressCollectView>,BaseCollectPresenter<ActressInfo>, BasePresenter.LazyLoaderPresenter
}

interface LinkCollectContract {
    interface LinkCollectView : BaseView.BaseListWithRefreshView
    interface LinkCollectPresenter : BasePresenter.BaseRefreshLoadMorePresenter<LinkCollectView>, BaseCollectPresenter<ILink>, BasePresenter.LazyLoaderPresenter
}

interface GenrePageContract {
    interface GenrePageView : BaseView {
        val titleValues: MutableList<String>
        val fragmentValues: MutableList<List<Genre>>
    }

    interface GenrePagePresenter : BasePresenter<GenrePageView>, BasePresenter.LazyLoaderPresenter
}

interface GenreListContract {
    interface GenreListView : BaseView.BaseListWithRefreshView {
        val data: List<Genre>
    }

    interface GenreListPresenter : BasePresenter.BaseRefreshLoadMorePresenter<GenreListView>, BasePresenter.LazyLoaderPresenter
}


interface HistoryContract {
    interface HistoryView : BaseView.BaseListWithRefreshView

    interface HistoryPresenter : BasePresenter.BaseRefreshLoadMorePresenter<HistoryView>, BasePresenter.LazyLoaderPresenter {
        fun clearHistory()
    }
}


