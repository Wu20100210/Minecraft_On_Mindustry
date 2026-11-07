package Minecraft_On_Mindustry

import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.distribution.Conveyor
import mindustry.world.blocks.distribution.ItemBridge
import mindustry.world.blocks.distribution.Junction
import mindustry.world.blocks.distribution.Router
import mindustry.world.blocks.distribution.Sorter
import mindustry.world.blocks.power.Battery
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.blocks.power.PowerNode
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.blocks.production.WallCrafter
import mindustry.world.blocks.storage.StorageBlock
import mindustry.world.blocks.storage.Unloader
import mindustry.world.blocks.units.UnitFactory
import mindustry.world.blocks.units.UnitFactory.UnitPlan

/**
 * 生产、合成、电力、仓储、物流与兵工厂方块。
 */
object Machines {
    // 采矿
    lateinit var kuangjing: Drill            // 矿井
    lateinit var shencengkuangjing: Drill     // 深层矿井
    lateinit var zuanshikuangjing: Drill      // 钻石矿井
    lateinit var xiajiekuangjing: Drill       // 下界矿井
    lateinit var famuchang: WallCrafter       // 伐木场

    // 熔炉
    lateinit var ronglu_tong: GenericCrafter
    lateinit var ronglu_tie: GenericCrafter
    lateinit var ronglu_jin: GenericCrafter
    lateinit var ronglu_boli: GenericCrafter
    lateinit var ronglu_shitou: GenericCrafter
    lateinit var gaolu: GenericCrafter
    lateinit var duanzaolu: GenericCrafter

    // 工作台
    lateinit var gongzuotai: GenericCrafter
    lateinit var gongjiangtai_mu: GenericCrafter
    lateinit var gongjiangtai_shi: GenericCrafter
    lateinit var gongjiangtai_tie: GenericCrafter
    lateinit var gongjiangtai_jin: GenericCrafter
    lateinit var gongjiangtai_zuanshi: GenericCrafter
    lateinit var gongjiangtai_xiajiehejin: GenericCrafter

    // 红石电力
    lateinit var hongshizhongjiqi: PowerNode
    lateinit var hongshikuai: Battery
    lateinit var hongshihuoba: ConsumeGenerator

    // 友好生物工厂
    lateinit var jishe: GenericCrafter
    lateinit var niushe: GenericCrafter
    lateinit var yangjuan: GenericCrafter
    lateinit var zhujuan: GenericCrafter

    // 仓库
    lateinit var cangku: StorageBlock

    // 物流
    lateinit var chuansongdai: Conveyor
    lateinit var loudou: Unloader
    lateinit var luyouqi: Router
    lateinit var fenleiqi: Sorter
    lateinit var jiaochaqi: Junction
    lateinit var wuliujiedian: ItemBridge

    // 兵工厂
    lateinit var binggongchang: UnitFactory

    fun load() {
        // ---------------------------------------------------------------- 采矿
        kuangjing = Drill("kuangjing").apply {
            requirements(Category.production, ItemStack.with(
                Items.yuanshi, 30, Items.muban, 15, Items.gao[0], 1
            ))
            size = 2
            tier = 2
            drillTime = 300f
            itemCapacity = 30
            hasPower = false
            drillEffect = Fx.mine
        }
        shencengkuangjing = Drill("shencengkuangjing").apply {
            requirements(Category.production, ItemStack.with(
                Items.yuanshi, 60, Items.tong, 40, Items.muban, 30, Items.gao[1], 2
            ))
            size = 2
            tier = 3
            drillTime = 340f
            itemCapacity = 40
            hasPower = false
        }
        zuanshikuangjing = Drill("zuanshikuangjing").apply {
            requirements(Category.production, ItemStack.with(
                Items.tie, 60, Items.tong, 60, Items.boli, 40, Items.gao[2], 2
            ))
            size = 2
            tier = 4
            drillTime = 380f
            itemCapacity = 50
            hasPower = true
            consumePower(1.2f)
        }
        xiajiekuangjing = Drill("xiajiekuangjing").apply {
            requirements(Category.production, ItemStack.with(
                Items.zuanshi, 50, Items.tie, 80, Items.hongshi, 40, Items.gao[4], 2
            ))
            size = 2
            tier = 5
            drillTime = 420f
            itemCapacity = 60
            hasPower = true
            consumePower(2f)
        }

        famuchang = WallCrafter("famuchang").apply {
            requirements(Category.production, ItemStack.with(
                Items.yuanshi, 40, Items.muban, 30, Items.fu[0], 1
            ))
            size = 2
            drillTime = 110f
            output = Items.yuanmu
            attribute = Environment.wood
            itemCapacity = 40
        }

        // ---------------------------------------------------------------- 熔炉
        ronglu_tong = smelter("ronglu_tong", Items.tongkuang, Items.tong, ItemStack.with(
            Items.yuanshi, 40, Items.muban, 20
        ))
        ronglu_tie = smelter("ronglu_tie", Items.tiekuang, Items.tie, ItemStack.with(
            Items.yuanshi, 60, Items.tong, 40
        ))
        ronglu_jin = smelter("ronglu_jin", Items.jinkuang, Items.jin, ItemStack.with(
            Items.yuanshi, 60, Items.tie, 40
        ))
        ronglu_boli = smelter("ronglu_boli", Items.shazi, Items.boli, ItemStack.with(
            Items.yuanshi, 40, Items.muban, 30
        ), time = 70f)
        ronglu_shitou = smelter("ronglu_shitou", Items.yuanshi, Items.shikuai, ItemStack.with(
            Items.yuanshi, 40, Items.muban, 20
        ), time = 60f)

        gaolu = GenericCrafter("gaolu").apply {
            requirements(Category.crafting, ItemStack.with(
                Items.tie, 80, Items.boli, 40, Items.hongshi, 20
            ))
            size = 2
            craftTime = 150f
            outputItem = ItemStack(Items.xiajiehejinpian, 1)
            consumeItems(ItemStack(Items.xiajiecanhai, 2), ItemStack(Items.mei, 2))
            itemCapacity = 20
            craftEffect = Fx.smeltsmoke
        }
        duanzaolu = GenericCrafter("duanzaolu").apply {
            requirements(Category.crafting, ItemStack.with(
                Items.zuanshi, 60, Items.jin, 60, Items.hongshi, 40
            ))
            size = 2
            craftTime = 180f
            outputItem = ItemStack(Items.xiajiehejinding, 1)
            consumeItems(
                ItemStack(Items.xiajiehejinpian, 4),
                ItemStack(Items.jin, 4),
                ItemStack(Items.mei, 1)
            )
            itemCapacity = 30
            craftEffect = Fx.smeltsmoke
        }

        // ---------------------------------------------------------------- 工作台
        gongzuotai = GenericCrafter("gongzuotai").apply {
            requirements(Category.crafting, ItemStack.with(
                Items.yuanmu, 40, Items.yuanshi, 20
            ))
            // 研发花费单独下调：原木 15 + 圆石 20（建造花费不变）
            researchCost = ItemStack.with(Items.yuanmu, 15, Items.yuanshi, 20)
            size = 2
            craftTime = 60f
            outputItem = ItemStack(Items.muban, 4)
            consumeItem(Items.yuanmu, 2)
            itemCapacity = 40
        }

        gongjiangtai_mu = toolBench("gongjiangtai_mu", 0, Items.muban, 10, ItemStack.with(
            Items.muban, 50, Items.yuanshi, 30
        ))
        gongjiangtai_shi = toolBench("gongjiangtai_shi", 1, Items.yuanshi, 12, ItemStack.with(
            Items.yuanshi, 80, Items.muban, 40
        ))
        gongjiangtai_tie = toolBench("gongjiangtai_tie", 2, Items.tie, 8, ItemStack.with(
            Items.tie, 80, Items.yuanshi, 60
        ))
        gongjiangtai_jin = toolBench("gongjiangtai_jin", 3, Items.jin, 8, ItemStack.with(
            Items.jin, 80, Items.tie, 40
        ))
        gongjiangtai_zuanshi = toolBench("gongjiangtai_zuanshi", 4, Items.zuanshi, 6, ItemStack.with(
            Items.zuanshi, 60, Items.jin, 40
        ))
        gongjiangtai_xiajiehejin = toolBench("gongjiangtai_xiajiehejin", 5, Items.xiajiehejinding, 6, ItemStack.with(
            Items.xiajiehejinding, 60, Items.zuanshi, 40
        ))

        // ---------------------------------------------------------------- 红石电力
        hongshizhongjiqi = PowerNode("hongshizhongjiqi").apply {
            requirements(Category.power, ItemStack.with(
                Items.hongshi, 15, Items.yuanshi, 20
            ))
            laserRange = 10f
            maxNodes = 6
        }
        hongshikuai = Battery("hongshikuai").apply {
            requirements(Category.power, ItemStack.with(
                Items.hongshi, 40, Items.yuanshi, 30
            ))
            size = 1
            consumePowerBuffered(4000f)
        }
        hongshihuoba = ConsumeGenerator("hongshihuoba").apply {
            requirements(Category.power, ItemStack.with(
                Items.hongshi, 20, Items.yuanshi, 25
            ))
            size = 1
            powerProduction = 1.5f
            itemDuration = 180f
            consumeItem(Items.hongshi)
            generateEffect = Fx.generatespark
            itemCapacity = 10
        }

        // ---------------------------------------------------------------- 友好生物工厂
        jishe = farm("jishe", Items.jidan, ItemStack.with(
            Items.muban, 60, Items.yuanshi, 40, Items.chu[0], 1
        ))
        niushe = farm("niushe", Items.pige, ItemStack.with(
            Items.muban, 80, Items.yuanshi, 60, Items.chu[1], 1
        ))
        yangjuan = farm("yangjuan", Items.yangmao, ItemStack.with(
            Items.muban, 80, Items.tie, 40, Items.chu[2], 1
        ))
        zhujuan = farm("zhujuan", Items.zhurou, ItemStack.with(
            Items.muban, 80, Items.jin, 40, Items.chu[3], 1
        ))

        // ---------------------------------------------------------------- 仓库
        cangku = StorageBlock("cangku").apply {
            requirements(Category.effect, ItemStack.with(
                Items.muban, 80, Items.tong, 20
            ))
            size = 2
            itemCapacity = 1200
            // 贴着核心时把容量并入核心库存
            coreMerge = true
            health = 600
        }

        // ---------------------------------------------------------------- 物流
        chuansongdai = Conveyor("chuansongdai").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.muban, 10, Items.tong, 5
            ))
            health = 90
            speed = 0.045f
            displayedSpeed = 4.5f
        }
        loudou = Unloader("loudou").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.muban, 30, Items.tong, 15
            ))
            health = 160
            speed = 1.2f
            allowCoreUnload = true
            itemCapacity = 10
        }
        luyouqi = Router("luyouqi").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.muban, 20, Items.tong, 10
            ))
            health = 120
            speed = 2f
            itemCapacity = 10
        }
        fenleiqi = Sorter("fenleiqi").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.muban, 25, Items.tong, 10, Items.hongshi, 5
            ))
            health = 140
        }
        jiaochaqi = Junction("jiaochaqi").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.muban, 20, Items.tong, 10
            ))
            health = 120
            speed = 2f
            capacity = 8
        }
        wuliujiedian = ItemBridge("wuliujiedian").apply {
            requirements(Category.distribution, ItemStack.with(
                Items.tong, 20, Items.muban, 15
            ))
            health = 160
            range = 8
            transportTime = 25f
            arrowSpacing = 5f
            bridgeWidth = 6f
            itemCapacity = 10
        }

        // ---------------------------------------------------------------- 兵工厂
        binggongchang = UnitFactory("binggongchang").apply {
            requirements(Category.units, ItemStack.with(
                Items.tie, 150, Items.tong, 100, Items.muban, 80, Items.hongshi, 60
            ))
            size = 3
            health = 1400
            consumePower(3f)
            plans.add(UnitPlan(Units.xueguilei, 60f * 10f, ItemStack.with(
                Items.tie, 30, Items.yuanshi, 40, Items.hongshi, 20
            )))
            plans.add(UnitPlan(Units.tieguilei, 60f * 22f, ItemStack.with(
                Items.tie, 120, Items.jin, 30, Items.hongshi, 60
            )))
        }
    }

    private fun smelter(
        name: String, input: Item, output: Item, reqs: Array<ItemStack>,
        time: Float = 90f, inputAmount: Int = 2, coal: Int = 1, effect: Effect = Fx.smeltsmoke
    ): GenericCrafter = GenericCrafter(name).apply {
        requirements(Category.crafting, reqs)
        size = 2
        craftTime = time
        outputItem = ItemStack(output, 1)
        consumeItems(ItemStack(Items.mei, coal), ItemStack(input, inputAmount))
        itemCapacity = 20
        craftEffect = effect
    }

    private fun toolBench(
        name: String, tierIndex: Int, material: Item, materialAmount: Int, reqs: Array<ItemStack>
    ): GenericCrafter = GenericCrafter(name).apply {
        requirements(Category.crafting, reqs)
        size = 2
        craftTime = 120f
        outputItems = arrayOf(
            ItemStack(Items.fu[tierIndex], 1),
            ItemStack(Items.gao[tierIndex], 1),
            ItemStack(Items.chu[tierIndex], 1),
            ItemStack(Items.jian[tierIndex], 1)
        )
        consumeItems(ItemStack(Items.muban, 6), ItemStack(material, materialAmount))
        itemCapacity = 30
        craftEffect = Fx.smeltsmoke
    }

    private fun farm(name: String, product: Item, reqs: Array<ItemStack>): GenericCrafter =
        GenericCrafter(name).apply {
            requirements(Category.crafting, reqs)
            size = 2
            craftTime = 300f
            outputItem = ItemStack(product, 1)
            consumePower(1.0f)
            itemCapacity = 20
            craftEffect = Fx.none
        }
}
