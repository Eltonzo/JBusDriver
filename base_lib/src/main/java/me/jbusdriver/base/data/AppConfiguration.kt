package me.jbusdriver.base.data

import android.content.Context
import io.reactivex.schedulers.Schedulers
import me.jbusdriver.base.*
import me.jbusdriver.base.mvp.bean.CategoryChangeEvent
import me.jbusdriver.base.mvp.bean.MenuChangeEvent
import me.jbusdriver.base.mvp.bean.PageChangeEvent
import kotlin.properties.Delegates

/**
 * Created by Administrator on 2017/9/9.
 */


object AppConfiguration {

    private val ctx by lazy { JBusManager.context }


     fun getSp(key: String): String? = ctx.getSharedPreferences("config", Context.MODE_PRIVATE).getString(key, null)
     fun saveSp(key: String, value: String) = Schedulers.io().scheduleDirect { ctx.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putString(key, value).apply() }

    //region pageMode value
    object PageMode {
        const val Page = 1
        const val Normal = 0
    }

    private const val PageModeS: String = "PageMode"
    var pageMode: Int by Delegates.vetoable(
            getSp(PageModeS)?.toIntOrNull() ?: let {
                saveSp(PageModeS, "1")
                1
            }) { _, old, new ->
        return@vetoable (new in 0..1 && old != new).also {
            if (it) {
                saveSp(PageModeS, new.toString())
                RxBus.post(PageChangeEvent(new))
            }
        }
    }

    //endregion



    //region menu
    private const val MenuConfigS: String = "MenuConfig"

    val menuConfig: MutableMap<String, Boolean> by lazy {
        GSON.fromJson<MutableMap<String, Boolean>>(
                getSp(MenuConfigS)
                        ?: hashMapOf("最近" to false).toJsonString().apply {
                            saveSp(MenuConfigS, this)
                })
    }

    fun saveSaveMenuConfig(menuOpValue: MutableMap<String, Boolean>) {
        menuConfig.clear()
        menuConfig.putAll(menuOpValue)
        saveSp(MenuConfigS, menuConfig.toJsonString())
        RxBus.post(MenuChangeEvent())
    }
    //endregion

    //region collectCategory 开启收藏分类
    private const val collectCategoryS: String = "collectCategoryS"
    var enableCategory: Boolean by Delegates.observable(java.lang.Boolean.parseBoolean(getSp(collectCategoryS))) { _, old, new ->
        saveSp(collectCategoryS, new.toString())
        RxBus.post(CategoryChangeEvent())
    }

    //endregion


    private const val HistoryS: String = "HistoryS"
    var enableHistory: Boolean = true




}

