package Minecraft_On_Mindustry

import arc.graphics.Color
import mindustry.type.Item

/**
 * 所有物品定义。
 *
 * 内部名保持简短，Mindustry 会自动给模组内容加上 `mc-on-mindustry-` 前缀
 * （贴图、bundle 键也使用同样的前缀）。
 */
object Items {
    // ---- 基础材料 ----
    lateinit var yuanmu: Item        // 原木
    lateinit var muban: Item         // 木板
    lateinit var yuanshi: Item       // 圆石
    lateinit var shikuai: Item       // 石头（熔炼圆石）
    lateinit var shazi: Item         // 沙子
    lateinit var mei: Item           // 煤炭
    lateinit var tongkuang: Item     // 铜矿石
    lateinit var tiekuang: Item      // 铁矿石
    lateinit var jinkuang: Item      // 金矿石
    lateinit var zuanshi: Item       // 钻石
    lateinit var hongshi: Item       // 红石
    lateinit var xiajiecanhai: Item  // 下界残骸
    lateinit var xiajiehejinpian: Item // 下界合金碎片
    lateinit var tong: Item          // 铜锭
    lateinit var tie: Item           // 铁锭
    lateinit var jin: Item           // 金锭
    lateinit var xiajiehejinding: Item // 下界合金锭
    lateinit var boli: Item          // 玻璃

    // ---- 友好生物产物 ----
    lateinit var yangmao: Item       // 羊毛
    lateinit var pige: Item          // 皮革
    lateinit var jidan: Item         // 鸡蛋
    lateinit var zhurou: Item        // 猪肉

    // ---- 工具与武器 ----
    /** 等级顺序：木、石、铁、金、钻石、下界合金 */
    val tierKeys = arrayOf("mu", "shi", "tie", "jin", "zuanshi", "xiajiehejin")
    val tierColors = arrayOf("#9c6b3f", "#8a8a8a", "#d4d4d4", "#f2cf3a", "#3fd8c8", "#4a4048")
    val tierNames = arrayOf("木", "石", "铁", "金", "钻石", "下界合金")

    lateinit var fu: Array<Item>   // 斧头
    lateinit var gao: Array<Item>  // 镐子
    lateinit var chu: Array<Item>  // 锄头
    lateinit var jian: Array<Item> // 剑

    fun load() {
        yuanmu = Item("yuanmu", Color.valueOf("#b98a5a")).apply {
            hardness = 0
            cost = 0.6f
            flammability = 0.6f
        }
        muban = Item("muban", Color.valueOf("#c08f5a")).apply {
            cost = 0.5f
            flammability = 0.5f
        }
        yuanshi = Item("yuanshi", Color.valueOf("#8a8a8a")).apply {
            hardness = 1
            cost = 0.5f
        }
        shikuai = Item("shikuai", Color.valueOf("#9d9d9d")).apply {
            hardness = 1
            cost = 0.5f
        }
        shazi = Item("shazi", Color.valueOf("#d3c091")).apply {
            hardness = 1
            lowPriority = true
            cost = 0.4f
        }
        mei = Item("mei", Color.valueOf("#323232")).apply {
            hardness = 2
            explosiveness = 0.2f
            flammability = 1f
            cost = 0.7f
        }
        tongkuang = Item("tongkuang", Color.valueOf("#e08a6a")).apply {
            hardness = 2
            cost = 0.7f
        }
        tiekuang = Item("tiekuang", Color.valueOf("#b8a8a0")).apply {
            hardness = 3
            cost = 0.8f
        }
        jinkuang = Item("jinkuang", Color.valueOf("#e8c53a")).apply {
            hardness = 3
            cost = 0.9f
        }
        zuanshi = Item("zuanshi", Color.valueOf("#5ef0dd")).apply {
            hardness = 4
            cost = 1.2f
        }
        hongshi = Item("hongshi", Color.valueOf("#e02020")).apply {
            hardness = 2
            charge = 0.6f
            cost = 0.9f
        }
        xiajiecanhai = Item("xiajiecanhai", Color.valueOf("#7a5a52")).apply {
            hardness = 5
            cost = 1.4f
        }
        xiajiehejinpian = Item("xiajiehejinpian", Color.valueOf("#6b5a52")).apply {
            cost = 1.2f
            healthScaling = 0.1f
        }
        tong = Item("tong", Color.valueOf("#f0997a")).apply {
            cost = 0.8f
        }
        tie = Item("tie", Color.valueOf("#d8d8d8")).apply {
            cost = 1.0f
        }
        jin = Item("jin", Color.valueOf("#f7d64a")).apply {
            cost = 1.2f
        }
        xiajiehejinding = Item("xiajiehejinding", Color.valueOf("#4a4048")).apply {
            cost = 1.6f
            charge = 0.4f
            healthScaling = 0.25f
        }
        boli = Item("boli", Color.valueOf("#c8ece8")).apply {
            cost = 1.1f
        }
        yangmao = Item("yangmao", Color.valueOf("#eeeeee")).apply {
            cost = 0.6f
            flammability = 0.3f
        }
        pige = Item("pige", Color.valueOf("#a06a3c")).apply {
            cost = 0.7f
        }
        jidan = Item("jidan", Color.valueOf("#f2ead2")).apply {
            cost = 0.8f
        }
        zhurou = Item("zhurou", Color.valueOf("#d0605a")).apply {
            cost = 0.8f
        }

        // 工具与剑：每个等级一种
        fu = Array(tierKeys.size) { i ->
            Item("${tierKeys[i]}fu", Color.valueOf(tierColors[i])).apply {
                cost = 1.0f + i * 0.4f
                healthScaling = 0.05f * i
            }
        }
        gao = Array(tierKeys.size) { i ->
            Item("${tierKeys[i]}gao", Color.valueOf(tierColors[i])).apply {
                cost = 1.0f + i * 0.4f
                healthScaling = 0.05f * i
            }
        }
        chu = Array(tierKeys.size) { i ->
            Item("${tierKeys[i]}chu", Color.valueOf(tierColors[i])).apply {
                cost = 1.0f + i * 0.4f
                healthScaling = 0.05f * i
            }
        }
        jian = Array(tierKeys.size) { i ->
            Item("${tierKeys[i]}jian", Color.valueOf(tierColors[i])).apply {
                cost = 1.2f + i * 0.5f
                healthScaling = 0.08f * i
            }
        }
    }
}
