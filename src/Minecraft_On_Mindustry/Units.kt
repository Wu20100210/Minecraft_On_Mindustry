package Minecraft_On_Mindustry

import arc.graphics.g2d.Draw
import mindustry.ai.types.SuicideAI
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.gen.Unit
import mindustry.type.UnitType
import mindustry.type.Weapon

/**
 * 敌对生物单位。它们会作为波次敌人出现在方块世界。
 *
 * 贴图是正视图，这里重写 draw() 直接绘制本体（不绘制机械腿），
 * 让它们看起来像从 MC 走进 Mindustry 的生物。
 */
object Units {
    lateinit var jiangshi: UnitType   // 僵尸：近战、血厚
    lateinit var kulou: UnitType      // 骷髅：远程射箭
    lateinit var kulipa: UnitType     // 苦力怕：死亡爆炸
    lateinit var zhizhu: UnitType     // 蜘蛛：高速近战
    lateinit var moyingren: UnitType  // 末影人：高伤高血
    lateinit var huanyi: UnitType     // 幻翼：飞行近战
    lateinit var egang: UnitType      // 恶魂：飞行远程
    lateinit var xueguilei: UnitType  // 雪傀儡：玩家单位，远程
    lateinit var tieguilei: UnitType  // 铁傀儡：玩家单位，重型近战

    fun load() {
        jiangshi = MobType("jiangshi").apply {
            health = 180f
            speed = 0.42f
            hitSize = 9f
            armor = 1f
            rotateSpeed = 3f
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(meleeWeapon(16f, 32f, 45f))
        }

        kulou = MobType("kulou").apply {
            health = 140f
            speed = 0.38f
            hitSize = 9f
            armor = 0f
            rotateSpeed = 3f
            targetAir = true
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(arrowWeapon(20f, 120f))
        }

        kulipa = MobType("kulipa").apply {
            health = 90f
            speed = 0.62f
            hitSize = 8f
            armor = 0f
            rotateSpeed = 4f
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            aiController = arc.func.Prov { SuicideAI() }
            weapons.add(Weapon().apply {
                shootOnDeath = true
                targetUnderBlocks = false
                reload = 24f
                shootCone = 180f
                mirror = false
                x = 0f
                y = 0f
                shootY = 0f
                ejectEffect = Fx.none
                bullet = BulletType(0f, 0f).apply {
                    collides = false
                    collidesTiles = false
                    instantDisappear = true
                    killShooter = true
                    hittable = false
                    splashDamage = 120f
                    splashDamageRadius = 40f
                    buildingDamageMultiplier = 0.7f
                    hitEffect = Fx.pulverize
                    despawnEffect = Fx.none
                }
            })
        }

        zhizhu = MobType("zhizhu").apply {
            health = 120f
            speed = 1.05f
            hitSize = 10f
            armor = 0f
            rotateSpeed = 5f
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(meleeWeapon(14f, 26f, 30f))
        }

        moyingren = MobType("moyingren").apply {
            health = 480f
            speed = 0.72f
            hitSize = 12f
            armor = 4f
            rotateSpeed = 3f
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(meleeWeapon(55f, 40f, 60f))
        }

        huanyi = MobType("huanyi", false).apply {
            health = 150f
            speed = 1.1f
            hitSize = 9f
            armor = 0f
            rotateSpeed = 6f
            flying = true
            lowAltitude = true
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(meleeWeapon(18f, 30f, 40f))
        }

        egang = MobType("egang", false).apply {
            health = 340f
            speed = 0.4f
            hitSize = 14f
            armor = 2f
            rotateSpeed = 2f
            flying = true
            targetAir = false
            targetGround = true
            isEnemy = true
            createWreck = false
            createScorch = false
            drawCell = false
            weapons.add(fireballWeapon(42f, 15f))
        }

        // ------------------------------------------------------------ 玩家单位（兵工厂产物）
        xueguilei = MobType("xueguilei").apply {
            health = 220f
            speed = 0.5f
            hitSize = 9f
            armor = 1f
            rotateSpeed = 4f
            targetAir = true
            targetGround = true
            drawCell = false
            alwaysUnlocked = true
            weapons.add(snowballWeapon(12f, 8f))
        }

        tieguilei = MobType("tieguilei").apply {
            health = 950f
            speed = 0.35f
            hitSize = 14f
            armor = 6f
            rotateSpeed = 2.5f
            targetAir = false
            targetGround = true
            drawCell = false
            alwaysUnlocked = true
            weapons.add(meleeWeapon(65f, 34f, 70f))
        }
    }

    /** 一只正视图生物：本体直接绘制，无机械腿。地面单位使用 MechUnit 以获得正常寻路。 */
    private open class MobType(name: String, ground: Boolean = true) : UnitType(name) {
        init {
            if (ground) {
                `constructor` = arc.func.Prov { mindustry.gen.MechUnit.create() }
            }
        }

        override fun draw(unit: Unit) {
            Draw.color(0f, 0f, 0f, 0.22f)
            Draw.rect(region, unit.x, unit.y + 1.5f)
            Draw.color()
            Draw.rect(region, unit.x, unit.y)
        }
    }

    private fun meleeWeapon(damage: Float, range: Float, reloadTicks: Float): Weapon = Weapon().apply {
        reload = reloadTicks
        x = 0f
        y = 0f
        shootY = 4f
        shootCone = 45f
        mirror = false
        ejectEffect = Fx.none
        shootSound = Sounds.none
        bullet = BasicBulletType(4f, damage).apply {
            lifetime = range / 4f
            width = 10f
            height = 6f
            collidesAir = false
            collidesGround = true
            collidesTiles = true
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.none
            shootEffect = Fx.none
            smokeEffect = Fx.none
        }
    }

    private fun fireballWeapon(damage: Float, rangeTiles: Float): Weapon = Weapon().apply {
        reload = 90f
        x = 0f
        y = 0f
        shootY = 6f
        shootCone = 25f
        mirror = false
        ejectEffect = Fx.none
        shootSound = Sounds.none
        bullet = BasicBulletType(3.2f, damage).apply {
            lifetime = rangeTiles * 8f / 3.2f
            width = 12f
            height = 12f
            collidesAir = false
            collidesGround = true
            collidesTiles = true
            splashDamageRadius = 20f
            splashDamage = 12f
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.none
            shootEffect = Fx.shootBig
            smokeEffect = Fx.shootBigSmoke
        }
    }

    private fun snowballWeapon(damage: Float, rangeTiles: Float): Weapon = Weapon().apply {
        reload = 50f
        x = 0f
        y = 0f
        shootY = 6f
        shootCone = 20f
        mirror = false
        ejectEffect = Fx.none
        shootSound = Sounds.none
        bullet = BasicBulletType(6f, damage).apply {
            lifetime = rangeTiles * 8f / 6f
            width = 8f
            height = 8f
            collidesAir = true
            collidesGround = true
            collidesTiles = true
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.none
            shootEffect = Fx.shootSmall
            smokeEffect = Fx.none
        }
    }

    private fun arrowWeapon(damage: Float, range: Float): Weapon = Weapon().apply {        reload = 65f
        x = 0f
        y = 0f
        shootY = 6f
        shootCone = 18f
        mirror = false
        ejectEffect = Fx.none
        shootSound = Sounds.none
        bullet = BasicBulletType(7f, damage).apply {
            lifetime = range / 7f
            width = 4f
            height = 12f
            collidesAir = true
            collidesGround = true
            collidesTiles = true
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.none
            shootEffect = Fx.shootSmall
            smokeEffect = Fx.shootSmallSmoke
        }
    }
}
