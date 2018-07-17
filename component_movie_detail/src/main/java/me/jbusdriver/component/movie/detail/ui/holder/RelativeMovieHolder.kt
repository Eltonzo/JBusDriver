package me.jbusdriver.component.movie.detail.ui.holder

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.billy.cc.core.component.CC
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import io.reactivex.Flowable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.layout_detail_relative_movies.view.*
import me.jbusdriver.base.*
import me.jbusdriver.base.common.C
import me.jbusdriver.base.common.toGlideUrl
import me.jbusdriver.base.data.AppConfiguration
import me.jbusdriver.base.data.contextMenu.LinkMenu
import me.jbusdriver.base.mvp.bean.Movie
import me.jbusdriver.base.mvp.bean.convertDBItem
import me.jbusdriver.base.mvp.model.CollectModel
import me.jbusdriver.base.mvp.ui.adapter.BaseAppAdapter
import me.jbusdriver.base.mvp.ui.holder.BaseHolder
import me.jbusdriver.component.movie.detail.R
import java.util.*


/**
 * Created by Administrator on 2017/5/9 0009.
 */
class RelativeMovieHolder(context: Context) : BaseHolder(context) {

    val view by lazy {
        weakRef.get()?.let {
            it.inflate(R.layout.layout_detail_relative_movies).apply {
                rv_recycle_relative_movies.layoutManager = LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)
                relativeAdapter.bindToRecyclerView(rv_recycle_relative_movies)
                rv_recycle_relative_movies.isNestedScrollingEnabled = true
                relativeAdapter.setOnItemClickListener { _, v, position ->
                    relativeAdapter.data.getOrNull(position)?.let {
                        KLog.d("relative  : $it")
                        CC.obtainBuilder(C.C_MOVIE_DETAIL::class.java.name)
                                .setActionName(C.C_MOVIE_DETAIL.Open_Movie_Detail)
                                .setContext(context)
                                .addParam("movie_bean", it)
                                .setTimeout(3000)
                                .build()
                                .call()

                    }
                }
                relativeAdapter.setOnItemLongClickListener { _, view, position ->
                    relativeAdapter.data.getOrNull(position)?.let { movie ->
                        val action = (if (CollectModel.has(movie.convertDBItem())) LinkMenu.movieActions.minus("收藏")
                        else LinkMenu.movieActions.minus("取消收藏")).toMutableMap()

                        if (AppConfiguration.enableCategory) {
                            val ac = action.remove("收藏")
                            if (ac != null) {
                                action["收藏到分类..."] = ac
                            }
                        }

                        MaterialDialog.Builder(view.context).title(movie.title)
                                .items(action.keys)
                                .itemsCallback { _, _, _, text ->
                                    action[text]?.invoke(movie)
                                }
                                .show()
                    }
                    return@setOnItemLongClickListener true
                }
            }
        } ?: error("context ref is finish")
    }

    private val relativeAdapter: BaseQuickAdapter<Movie, BaseViewHolder> by lazy {
        object : BaseAppAdapter<Movie, BaseViewHolder>(R.layout.layout_detail_relative_movies_item) {
            override fun convert(holder: BaseViewHolder, item: Movie) {
                GlideApp.with(holder.itemView.context).asBitmap().load(item.imageUrl.toGlideUrl).into(object : BitmapImageViewTarget(holder.getView(R.id.iv_relative_movie_image)) {
                    override fun setResource(resource: Bitmap?) {
                        super.setResource(resource)
                        resource?.let {

                            Flowable.just(it).map {
                                Palette.from(it).generate()
                            }.compose(SchedulersCompat.io())
                                    .subscribeWith(object : SimpleSubscriber<Palette>() {
                                        override fun onNext(it: Palette) {
                                            super.onNext(it)
                                            val swatch = listOfNotNull(it.lightMutedSwatch, it.lightVibrantSwatch, it.vibrantSwatch, it.mutedSwatch)
                                            if (!swatch.isEmpty()) {
                                                swatch[randomNum(swatch.size)].let {
                                                    holder.setBackgroundColor(R.id.tv_relative_movie_title, it.rgb)
                                                    holder.setTextColor(R.id.tv_relative_movie_title, it.bodyTextColor)
                                                }
                                            }
                                        }
                                    })
                                    .addTo(rxManager)


                            /*  Palette.from(it).generate {

                              }.let {
                                  paletteReq.add(it)
                              }*/
                        }
                    }
                })
                //加载名字
                holder.setText(R.id.tv_relative_movie_title, item.title)
            }
        }
    }


    private val random = Random()
    private fun randomNum(number: Int) = Math.abs(random.nextInt() % number)

    fun init(relativeMovies: List<Movie>) {
        //actress
        KLog.d("relate moview : $relativeMovies")
        if (relativeMovies.isEmpty()) view.tv_movie_relative_none_tip.visibility = View.VISIBLE
        else {
            //load header
            relativeAdapter.setNewData(relativeMovies)
        }
    }

}