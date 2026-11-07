package Minecraft_On_Mindustry

import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.environment.OverlayFloor
import mindustry.world.blocks.environment.StaticWall
import mindustry.world.meta.Attribute

/**
 * 环境方块：草地/泥土/石头/沙子、矿石覆盖层、树木墙、湖面与花草装饰。
 *
 * 树木使用实心墙体（StaticWall，阻挡地面单位），并带有自定义的 `wood` 属性，
 * 供“伐木场”（WallCrafter）识别产出原木。
 */
object Environment {
    /** 自定义属性：树木 */
    val wood: Attribute = Attribute.getOrNull("wood") ?: Attribute.add("wood")

    lateinit var caodi: Floor
    lateinit var nitu: Floor
    lateinit var shitou: Floor
    lateinit var shazi: Floor
    lateinit var shui: Floor
    lateinit var huangjingqiang: StaticWall

    lateinit var xiangshu: StaticWall
    lateinit var yunshan: StaticWall

    lateinit var huaduo: OverlayFloor
    lateinit var caocong: OverlayFloor

    lateinit var yuanshikuangshi: OreBlock
    lateinit var meikuangshi: OreBlock
    lateinit var tongkuangshi: OreBlock
    lateinit var tiekuangshi: OreBlock
    lateinit var jinkuangshi: OreBlock
    lateinit var zuanshikuangshi: OreBlock
    lateinit var hongshikuangshi: OreBlock
    lateinit var xiajiekuangshi: OreBlock

    lateinit var allOres: Array<OreBlock>

    fun load() {
        // 直接复用原版浅水，保证边缘混合、寻路与减速行为正确
        shui = Blocks.water.asFloor()

        huangjingqiang = StaticWall("huangjingqiang").apply {
            autotile = false
            variants = 0
            attributes.set(Attribute.sand, 0.4f)
        }

        caodi = Floor("caodi").apply {
            variants = 0
            speedMultiplier = 1f
            wall = huangjingqiang
            attributes.set(Attribute.spores, 0.4f)
        }
        nitu = Floor("nitu").apply {
            variants = 0
            wall = huangjingqiang
        }
        shitou = Floor("shitou").apply {
            variants = 0
            wall = huangjingqiang
        }
        shazi = Floor("shazi").apply {
            variants = 0
            itemDrop = Items.shazi
            playerUnmineable = true
            attributes.set(Attribute.sand, 1f)
            wall = huangjingqiang
        }

        xiangshu = StaticWall("xiangshu").apply {
            variants = 0
            autotile = false
            solid = true
            itemDrop = Items.yuanmu
            health = 140
            attributes.set(wood, 1f)
            destroyEffect = Fx.breakProp
            breakEffect = Fx.breakProp
        }
        yunshan = StaticWall("yunshan").apply {
            variants = 0
            autotile = false
            solid = true
            itemDrop = Items.yuanmu
            health = 160
            attributes.set(wood, 1f)
            destroyEffect = Fx.breakProp
            breakEffect = Fx.breakProp
        }

        huaduo = OverlayFloor("huaduo").apply { variants = 0 }
        caocong = OverlayFloor("caocong").apply { variants = 0 }

        yuanshikuangshi = ore("yuanshikuangshi", Items.yuanshi, 22f, 0.80f)
        meikuangshi = ore("meikuangshi", Items.mei, 24f, 0.82f)
        tongkuangshi = ore("tongkuangshi", Items.tongkuang, 23f, 0.83f)
        tiekuangshi = ore("tiekuangshi", Items.tiekuang, 25f, 0.84f)
        jinkuangshi = ore("jinkuangshi", Items.jinkuang, 26f, 0.85f)
        zuanshikuangshi = ore("zuanshikuangshi", Items.zuanshi, 27f, 0.86f)
        hongshikuangshi = ore("hongshikuangshi", Items.hongshi, 24f, 0.84f)
        xiajiekuangshi = ore("xiajiekuangshi", Items.xiajiecanhai, 28f, 0.88f)

        allOres = arrayOf(
            yuanshikuangshi, meikuangshi, tongkuangshi, tiekuangshi,
            jinkuangshi, zuanshikuangshi, hongshikuangshi, xiajiekuangshi
        )
    }

    private fun ore(name: String, item: mindustry.type.Item, scale: Float, threshold: Float): OreBlock =
        OreBlock(name, item).apply {
            oreDefault = false
            oreScale = scale
            oreThreshold = threshold
        }
}
