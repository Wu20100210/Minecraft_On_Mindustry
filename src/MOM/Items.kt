package MOM

import arc.graphics.Color
import mindustry.type.Item

object Items{
    lateinit var xiangshuyuanmu : Item

    fun load() {
        xiangshuyuanmu = Item("xiangshuyuanmu", Color.valueOf("ffffff"))
    }
}