package Minecraft_On_Mindustry

import mindustry.Vars
import mindustry.mod.Mod
import mindustry.type.ItemStack

class Main : Mod() {
    override fun loadContent() {
        Items.load()
        Environment.load()
        Units.load()
        Machines.load()
        Weapons.load()
        Cores.load()
        Xingqiu.load()
        applyResearchCosts()
        Tech.load()
    }

    /**
     * 所有模组方块的研发花费统一为建造花费的 3 倍。
     * 必须在 Tech.load() 之前执行，TechTree 创建节点时会读取 researchRequirements()。
     */
    private fun applyResearchCosts() {
        for (block in Vars.content.blocks()) {
            if (!block.name.startsWith("mc-on-mindustry-")) continue
            val reqs = block.requirements
            if (reqs.isEmpty()) continue
            block.researchCost = Array(reqs.size) { i ->
                ItemStack(reqs[i].item, reqs[i].amount * 3)
            }
        }
    }
}
