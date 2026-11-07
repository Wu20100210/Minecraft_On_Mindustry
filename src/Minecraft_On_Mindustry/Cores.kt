package Minecraft_On_Mindustry

import mindustry.content.UnitTypes
import mindustry.type.Category
import mindustry.type.ItemStack.with
import mindustry.world.blocks.storage.CoreBlock

/**
 * 核心定义（原名：hexing）。
 * 本模组星球的初始核心，同时作为科技树的根节点内容。
 */
object Cores{
    lateinit var chujihexing : CoreBlock
    lateinit var zhongdaihexing : CoreBlock
    lateinit var zhongjihexing : CoreBlock
    fun load(){
        chujihexing = CoreBlock("chujihexing").apply{
            requirements(Category.effect, with(Items.tong, 1000, Items.tie, 800))
            size = 3              // 3x3 大小
            health = 4000         // 生命值
            itemCapacity = 4000   // 物品容量
            unitCapModifier = 8  // 增加的单位上限
            unitType = UnitTypes.alpha
            // 核心专属属性
            isFirstTier = true    // 标记为第一级核心
            alwaysUnlocked = true // 确保在科技树中默认解锁（作为起点）
            //shownPlanets.add(Xingqiu.Xingqiu)
        }
        zhongdaihexing = CoreBlock("zhongdaihexing").apply{
            requirements(Category.effect, with(Items.tong, 3000, Items.tie, 3000, Items.jing, 2000))

            unitType = UnitTypes.beta
            health = 3500
            itemCapacity = 9000
            size = 4
            thrusterLength = 34/4f

            unitCapModifier = 16
            researchCostMultiplier = 0.07f
        }
        zhongjihexing = CoreBlock("zhongjihexing").apply{
            requirements(Category.effect, with(Items.tong, 8000, Items.tie, 8000, Items.jing, 5000, Items.zuanshi, 4000))

            unitType = UnitTypes.gamma
            health = 6000
            itemCapacity = 13000
            size = 5
            thrusterLength = 40/4f

            unitCapModifier = 24
            researchCostMultiplier = 0.11f
        }
    }
}
