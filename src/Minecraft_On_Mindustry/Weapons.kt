package Minecraft_On_Mindustry

import arc.graphics.Color
import mindustry.content.Fx
import mindustry.entities.bullet.BasicBulletType
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.ItemTurret

/**
 * 武器炮台。
 *
 * - 近战武器（剑）→ 只对地炮台，射程短、伤害高，消耗红石。
 * - 远程武器（弓/弩/三叉戟）→ 只对空炮台，射程长。
 */
object Weapons {
    lateinit var mujianpaotai: ItemTurret
    lateinit var shijianpaotai: ItemTurret
    lateinit var tiejianpaotai: ItemTurret
    lateinit var jinjianpaotai: ItemTurret
    lateinit var zuanshijianpaotai: ItemTurret
    lateinit var xiajiehejinjianpaotai: ItemTurret

    lateinit var gongpaotai: ItemTurret
    lateinit var nupaotai: ItemTurret
    lateinit var sanchajipaotai: ItemTurret

    fun load() {
        mujianpaotai = sword("mujianpaotai", 0, 5f, 10f, 26f, 350, 40, ItemStack.with(
            Items.jian[0], 1, Items.muban, 40, Items.yuanshi, 30
        ))
        shijianpaotai = sword("shijianpaotai", 1, 6f, 16f, 24f, 550, 50, ItemStack.with(
            Items.jian[1], 1, Items.yuanshi, 80, Items.muban, 40
        ))
        tiejianpaotai = sword("tiejianpaotai", 2, 7f, 24f, 22f, 800, 60, ItemStack.with(
            Items.jian[2], 1, Items.tie, 60, Items.yuanshi, 60
        ))
        jinjianpaotai = sword("jinjianpaotai", 3, 7f, 32f, 15f, 900, 60, ItemStack.with(
            Items.jian[3], 1, Items.jin, 60, Items.tie, 40
        ))
        zuanshijianpaotai = sword("zuanshijianpaotai", 4, 8f, 48f, 18f, 1200, 80, ItemStack.with(
            Items.jian[4], 1, Items.zuanshi, 40, Items.tie, 60
        ))
        xiajiehejinjianpaotai = sword("xiajiehejinjianpaotai", 5, 9f, 70f, 20f, 1600, 100, ItemStack.with(
            Items.jian[5], 1, Items.xiajiehejinding, 40, Items.zuanshi, 40
        ))

        gongpaotai = bow("gongpaotai", 18f, 20f, 60f, 700, 40, ItemStack.with(
            Items.muban, 60, Items.tong, 30, Items.hongshi, 20
        ))
        nupaotai = bow("nupaotai", 26f, 50f, 80f, 1100, 50, ItemStack.with(
            Items.tie, 80, Items.tong, 40, Items.hongshi, 40
        ))
        sanchajipaotai = bow("sanchajipaotai", 20f, 90f, 50f, 1500, 60, ItemStack.with(
            Items.xiajiehejinding, 50, Items.zuanshi, 30, Items.hongshi, 80
        ), pierce = true)
    }

    private fun sword(
        name: String, tier: Int, rangeTiles: Float, damage: Float, reloadTicks: Float,
        health: Int, ammo: Int, reqs: Array<ItemStack>
    ): ItemTurret = ItemTurret(name).apply {
        requirements(Category.turret, reqs)
        size = 2
        this.health = health
        range = rangeTiles * 8f
        reload = reloadTicks
        rotateSpeed = 7f
        shootCone = 35f
        targetAir = false
        targetGround = true
        targetBlocks = true
        maxAmmo = ammo
        itemCapacity = ammo
        ammoPerShot = 1
        shootY = 9f
        recoil = 2.5f
        cooldownTime = 25f
        heatColor = Color.valueOf(Items.tierColors[tier])
        ammo(Items.hongshi, BasicBulletType(6f, damage).apply {
            width = 16f
            height = 5f
            collidesAir = false
            collidesGround = true
            collidesTiles = true
            hitEffect = Fx.scatheSlash
            despawnEffect = Fx.none
            shootEffect = Fx.none
            smokeEffect = Fx.none
            hitSize = 8f
            frontColor = Color.valueOf(Items.tierColors[tier])
            backColor = Color.valueOf(Items.tierColors[tier])
        })
        limitRange()
    }

    private fun bow(
        name: String, rangeTiles: Float, damage: Float, reloadTicks: Float,
        health: Int, ammo: Int, reqs: Array<ItemStack>, pierce: Boolean = false
    ): ItemTurret = ItemTurret(name).apply {
        requirements(Category.turret, reqs)
        size = 2
        this.health = health
        range = rangeTiles * 8f
        reload = reloadTicks
        rotateSpeed = 4f
        shootCone = 10f
        inaccuracy = 3f
        targetAir = true
        targetGround = false
        targetBlocks = false
        maxAmmo = ammo
        itemCapacity = ammo
        shootY = 10f
        recoil = 3f
        ammo(Items.hongshi, BasicBulletType(10f, damage).apply {
            width = 5f
            height = 14f
            collidesAir = true
            collidesGround = false
            collidesTiles = false
            this.pierce = pierce
            pierceCap = 2
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.none
            shootEffect = Fx.shootSmall
            smokeEffect = Fx.shootSmallSmoke
        })
        limitRange()
    }
}
