package Minecraft_On_Mindustry

import arc.graphics.Color
import mindustry.type.Item

object Items{
    lateinit var xiangshuyuanmu : Item
    lateinit var shitou : Item
    lateinit var tong : Item
    lateinit var tie : Item
    lateinit var jing : Item
    lateinit var zuanshi : Item
    lateinit var xiajiehejing : Item
    lateinit var mei : Item
    lateinit var shazi : Item
    lateinit var boli : Item
    lateinit var xiajiehejingshuipiang : Item

    fun load() {
        xiangshuyuanmu = Item("xiangshuyuanmu", Color.valueOf("ffffff")).apply {
            hardness = 1
            cost = 0.5f
            alwaysUnlocked = true
        }
        shitou = Item("shitou", Color.valueOf("111111")).apply {
            hardness = 1
            cost = 0.5f
            alwaysUnlocked = true
        }
        tong = Item("tong", Color.valueOf("FC9982")).apply {
            hardness = 1
            cost = 0.5f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        tie = Item("tie", Color.valueOf("#D8D8D8")).apply {
            hardness = 1
            cost = 0.7f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        jing = Item("jing", Color.valueOf("#FDF55F")).apply {
            hardness = 3
            cost = 1f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        zuanshi = Item("zuanshi", Color.valueOf("#8DF8E4")).apply {
            explosiveness = 0.2f
            hardness = 4
            radioactivity = 1f
            cost = 1.1f
            healthScaling = 0.2f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        xiajiehejing = Item("xiajiehejing", Color.valueOf("#4D494D")).apply {
            cost = 1.2f
            charge = 0.75f
            healthScaling = 0.25f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        mei = Item("mei",Color.valueOf("#323232")).apply {
            explosiveness = 0.2f
            flammability = 1f
            hardness = 2
            buildable = false
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        shazi = Item("shazi", Color.valueOf("#D3C091")).apply {
            lowPriority = true
            buildable = false
            //needed to show up as requirement
            alwaysUnlocked = true
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        boli = Item("boli" , Color.valueOf("D0EAE9")).apply {
            cost = 1.5f
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        xiajiehejingshuipiang = Item("xiajiehejingshuipiang", Color.valueOf("#4D494D")).apply {
            explosiveness = 0.2f
            flammability = 1f
            hardness = 4
            radioactivity = 1f
            buildable = false
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
    }
}