package Minecraft_On_Mindustry

import mindustry.content.UnitTypes
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.storage.CoreBlock

/**
 * 核心：科技树的起点与仓库。
 * 初级核心 / 中级核心 / 最终核心。
 */
object Cores {
    lateinit var chujihexing: CoreBlock
    lateinit var zhongdaihexing: CoreBlock
    lateinit var zhongjihexing: CoreBlock

    fun load() {
        // 允许玩家直接用手采集树木墙（wallDrop = 原木），
        // 这样即使没有伐木场也能获得原木并加工成木板。
        UnitTypes.alpha.mineWalls = true
        UnitTypes.beta.mineWalls = true
        UnitTypes.gamma.mineWalls = true
        UnitTypes.alpha.mineRange = 100f
        UnitTypes.beta.mineRange = 100f
        UnitTypes.gamma.mineRange = 100f

        chujihexing = CoreBlock("chujihexing").apply {
            requirements(Category.effect, ItemStack.with(
                Items.yuanshi, 800, Items.muban, 400
            ))
            size = 3
            health = 4000
            itemCapacity = 4000
            unitCapModifier = 8
            unitType = UnitTypes.alpha
            isFirstTier = true
            alwaysUnlocked = true
            thrusterLength = 24f / 4f
        }
        zhongdaihexing = CoreBlock("zhongdaihexing").apply {
            requirements(Category.effect, ItemStack.with(
                Items.tong, 2000, Items.tie, 2000, Items.zuanshi, 1000
            ))
            size = 4
            health = 6000
            itemCapacity = 9000
            unitCapModifier = 16
            unitType = UnitTypes.beta
            thrusterLength = 34f / 4f
            researchCostMultiplier = 0.07f
        }
        zhongjihexing = CoreBlock("zhongjihexing").apply {
            requirements(Category.effect, ItemStack.with(
                Items.tie, 6000, Items.jin, 4000, Items.zuanshi, 5000, Items.xiajiehejinding, 3000
            ))
            size = 5
            health = 8000
            itemCapacity = 15000
            unitCapModifier = 24
            unitType = UnitTypes.gamma
            thrusterLength = 40f / 4f
            researchCostMultiplier = 0.11f
        }
    }
}
