package me.jbusdriver.component.recommend.mvp.presenter

import android.net.Uri
import android.util.Base64
import com.bumptech.glide.Glide
import me.jbusdriver.component.recommend.http.RecommendService
import me.jbusdriver.base.mvp.bean.RecommendRespBean
import me.jbusdriver.component.recommend.mvp.Contract
import io.reactivex.rxkotlin.addTo
import me.jbusdriver.base.*
import me.jbusdriver.base.common.toGlideUrl
import me.jbusdriver.base.http.JAVBusService
import me.jbusdriver.base.mvp.bean.Magnet
import me.jbusdriver.base.mvp.bean.PageInfo
import me.jbusdriver.base.mvp.bean.ResultPageBean
import me.jbusdriver.base.mvp.model.BaseModel
import me.jbusdriver.base.mvp.presenter.AbstractRefreshLoadMorePresenterImpl
import org.jsoup.nodes.Document
import java.util.concurrent.atomic.AtomicInteger

class HotRecommendPresenterImpl : AbstractRefreshLoadMorePresenterImpl<Contract.HotRecommendContract.HotRecommendView, Magnet>(), Contract.HotRecommendContract.HotRecommendPresenter {

    private val count = AtomicInteger(1)

    override val model: BaseModel<Int, Document>
        get() = error("not call model")

    override fun stringMap(pageInfo: PageInfo, str: Document) = error("not call stringMap")

    override fun onFirstLoad() {
        loadData4Page(count.get())
    }

    override fun loadData4Page(page: Int) {
        KLog.d("loadData4Page :$page")
        RecommendService.INSTANCE.recommends(page)
                .map {
                    val res = it.getAsJsonObject("result")
                    val data = res.getAsJsonArray("data").mapNotNull {
                        it.asString?.let {
                            val bean = GSON.fromJson<RecommendRespBean>(String(Base64.decode(it, Base64.DEFAULT or Base64.URL_SAFE)))
                            if (!Uri.parse(bean.key.url).isAbsolute) {
                                val images = JAVBusService.defaultImageUrlHosts.flatMap { it.value }.map {
                                    mView?.viewContext?.let { c ->
                                        Glide.with(c).load((it + bean.key.img).toGlideUrl).submit()
                                    }
                                    it + bean.key.img
                                }.shuffled()
                                KLog.d("images :$images")
                                bean.copy(key = bean.key.copy(img = images.firstOrNull()
                                        ?: bean.key.img, url = JAVBusService.defaultFastUrl + bean.key.url))
                            } else bean
                        }
                    }
                    KLog.d(data)
                    val max = res.getAsJsonPrimitive("pages").asInt
                    lastPage = max
                    return@map ResultPageBean(PageInfo(page), data)
                }.doOnTerminate { mView?.dismissLoading() }.compose(SchedulersCompat.io()).subscribe(
                        {
                            KLog.d("$it")
                            mView?.showContents(it.data)
                            mView?.loadMoreEnd()
                            mView?.viewContext?.toast("加载成功！")

                        }, {
                    it.printStackTrace()
                    KLog.w(it.message.toString())
                }
                ).addTo(rxManager)

    }

    override fun onLoadMore() {
        if (lastPage < count.incrementAndGet()) {
            count.set(1)
        }
        loadData4Page(count.get())
    }

    override fun hasLoadNext() = false
    override fun onRefresh() {
        if (lastPage < count.get()) {
            count.set(1)
        }
        rxManager.clear()
        loadData4Page(count.get())
    }

}