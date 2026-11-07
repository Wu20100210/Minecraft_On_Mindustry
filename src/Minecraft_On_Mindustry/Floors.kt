package Minecraft_On_Mindustry

import mindustry.world.blocks.environment.OreBlock
import mindustry.world.meta.Attribute
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.StaticWall

/**
 * 背景方块（矿脉）定义（原名：beijingfangkuai）。
 * 这里定义的每种矿脉决定对应物品能否在星球地表被开采。
 */
object Floors {
    lateinit var tongkuan : OreBlock
    lateinit var tiekuan : OreBlock
    lateinit var zuanshikuan : OreBlock
    lateinit var jingkuan : OreBlock
    lateinit var xiajiehejinshuiping : OreBlock
    lateinit var huangjingqiang : StaticWall
    lateinit var mei : OreBlock
    lateinit var caodi : Floor
    lateinit var shazi : Floor
    lateinit var shitou : Floor
    fun load(){
        tongkuan = OreBlock(Items.tong).apply {
            oreDefault = true
            oreThreshold = 0.82f
            oreScale = 23.47619f
        }
        tiekuan = OreBlock(Items.tie).apply {
            oreDefault = true
            oreThreshold = 0.828f
            oreScale = 23.952381f
        }
        zuanshikuan = OreBlock(Items.zuanshi).apply {
            oreDefault = true
            oreThreshold = 0.882f
            oreScale = 25.380953f
        }
        jingkuan = OreBlock(Items.jing).apply {
            oreDefault = true
            oreThreshold = 0.864f
            oreScale = 24.904762f
        }
        shazi = Floor("shazi").apply {
            itemDrop = Items.shazi
            playerUnmineable = true
            attributes.set(Attribute.oil, 0.7f)
            variants = 0
        }
        xiajiehejinshuiping = OreBlock(Items.xiajiehejingshuipiang).apply {
            oreDefault = true
            oreThreshold = 0.889f
            oreScale = 25.054483f
        }
        mei = OreBlock(Items.mei).apply {
            oreDefault = true
            oreThreshold = 0.846f
            oreScale = 24.428572f
        }
        caodi = Floor("caodi").apply {
            speedMultiplier = 1f
            autotile = false
            // 贴图只有一张 caodi.png。Floor/Block 默认 variants = 3，load() 会依次查找
            // caodi1/caodi2/caodi3，找不到的贴图会被替换成 "error"（红黑缺失贴图）。
            variants = 0
        }
        huangjingqiang = StaticWall("huangjingqiang").apply {
            autotile = false
            // StaticWall 默认 variants = 2，load() 会查找 huangjingqiang1/huangjingqiang2。
            // 本模组只有一张 huangjingqiang.png，必须设为 0 才会使用 region 本身。
            variants = 0
        }
        shitou = Floor("shitou").apply {
            variants = 0
        }

        // Floor.wall 默认为 Blocks.air，也就是"地表上没有环境墙"。
        // 星球生成器用 `tile.block = tile.floor.asFloor().wall` 决定哪块地表是墙，
        // 所以这里把三种地板都绑到本模组的环境墙上，否则整张地图会全是空气。
        caodi.wall = huangjingqiang
        shazi.wall = huangjingqiang
        shitou.wall = huangjingqiang
    }
}
