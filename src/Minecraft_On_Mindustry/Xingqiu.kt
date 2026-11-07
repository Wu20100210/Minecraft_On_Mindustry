package Minecraft_On_Mindustry

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Tmp
import arc.util.noise.Ridged
import arc.util.noise.Simplex
import mindustry.ai.Astar
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.SpawnGroup
import mindustry.game.Team
import mindustry.graphics.g3d.HexMesh
import mindustry.maps.generators.FileMapGenerator
import mindustry.maps.generators.PlanetGenerator
import mindustry.mod.Mods.LoadedMod
import mindustry.type.ItemStack
import mindustry.type.Planet
import mindustry.type.Sector
import mindustry.type.SectorPreset
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.TileGen
import mindustry.world.Tiles
import mindustry.world.WorldParams
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.Env

/**
 * 方块世界：一颗 Minecraft 风格的星球，包含草地、森林、矿石、树木与下界区域。
 */
object Xingqiu {
    lateinit var fangkuai: Planet

    lateinit var chudi: SectorPreset     // 初级区域（出生点）
    lateinit var senlin: SectorPreset    // 森林
    lateinit var xiajie: SectorPreset    // 下界

    fun load() {
        fangkuai = Planet("fangkuai", Planets.sun, 1f, 2).apply {
            generator = FangkuaiGenerator()
            meshLoader = Prov { HexMesh(this, 6) }

            defaultCore = Cores.chujihexing
            defaultEnv = Env.terrestrial or Env.oxygen or Env.groundWater
            alwaysUnlocked = true
            visible = true
            accessible = true
            startSector = 0
            sectorSeed = 2
            updateLighting = true
            hasAtmosphere = true
            atmosphereRadIn = 0.02f
            atmosphereRadOut = 0.3f
            orbitSpacing = 2f
            minZoom = 0.5f
            maxZoom = 2f
            allowLaunchSchematics = true
            allowLaunchLoadout = true
            iconColor = Color.valueOf("3f8f3a")
            atmosphereColor = Color.valueOf("7ec8ff")
            landCloudColor = Color.valueOf("ffffff")
            ruleSetter = Cons { r ->
                r.waveTeam = Team.crux
                r.placeRangeCheck = false
                r.hideSpawns = true
                // 波次表必须非空，否则读档时会被替换成原版单位
                r.spawns = Seq.with(
                    SpawnGroup(Units.jiangshi).apply {
                        begin = 0; end = 100; spacing = 2; max = 4; unitAmount = 1; unitScaling = 1.2f
                    },
                    SpawnGroup(Units.kulou).apply {
                        begin = 2; end = 100; spacing = 3; max = 3; unitAmount = 1; unitScaling = 1.5f
                    },
                    SpawnGroup(Units.huanyi).apply {
                        begin = 4; end = 100; spacing = 4; max = 3; unitAmount = 1; unitScaling = 1.4f
                    },
                    SpawnGroup(Units.zhizhu).apply {
                        begin = 5; end = 100; spacing = 4; max = 3; unitAmount = 1; unitScaling = 1.5f
                    },
                    SpawnGroup(Units.kulipa).apply {
                        begin = 7; end = 100; spacing = 6; max = 2; unitAmount = 1; unitScaling = 1.8f
                    },
                    SpawnGroup(Units.egang).apply {
                        begin = 9; end = 100; spacing = 6; max = 2; unitAmount = 1; unitScaling = 1.8f
                    },
                    SpawnGroup(Units.moyingren).apply {
                        begin = 12; end = 100; spacing = 9; max = 2; unitAmount = 1; unitScaling = 2f
                    }
                )
            }
        }

        chudi = preset("chudi", 0) {
            alwaysUnlocked = true
            captureWave = 15
            difficulty = 1f
            startWaveTimeMultiplier = 3f
            overrideLaunchDefaults = true
            allowLaunchLoadout = true
            allowLaunchSchematics = true
        }
        senlin = preset("senlin", 1) {
            captureWave = 20
            difficulty = 3f
            startWaveTimeMultiplier = 2.5f
        }
        xiajie = preset("xiajie", 2) {
            captureWave = 30
            difficulty = 6f
            startWaveTimeMultiplier = 2f
        }
    }

    private fun preset(name: String, index: Int, cfg: SectorPreset.() -> Unit): SectorPreset {
        val p = SectorPreset(name, null as LoadedMod?)
        p.generator = PlanetBackedGenerator(p)
        p.initialize(fangkuai, index)
        p.cfg()
        return p
    }

    /** 用行星生成器替代地图文件，让每个区块都按程序生成。 */
    class PlanetBackedGenerator(private val sourcePreset: SectorPreset) : FileMapGenerator(sourcePreset) {
        override fun generate(tiles: Tiles, params: WorldParams) {
            val sector = sourcePreset.sector
            sourcePreset.planet.generator.generate(tiles, sector, params)
        }
    }

    /**
     * 参考 Erekir 行星生成器：
     * - 用 `rawHeight` + `rawTemp` 选择地表，默认整张地图都是实心岩壁；
     * - 用 Ridged 噪声在岩壁中“挖”出连续的峡谷/空腔；
     * - `cells` / `distort` / `median` / `inverseFloodFill` 做地形塑形；
     * - 从出生点向敌巢方向用 `pathfind` + `brush` 强行开出一条通道；
     * - 出生点放核心，另一端放刷怪点。
     */
    class FangkuaiGenerator : PlanetGenerator() {

        enum class Biome { PLAINS, FOREST, DESERT, ROCKY, NETHER }

        private val heightScl = 0.9f
        private val octaves = 8.0
        private val persistence = 0.7
        private val airScl = 14f

        private fun rawHeight(p: arc.math.geom.Vec3): Float = Simplex.noise3d(
            seed, octaves, persistence, 1.0 / heightScl,
            10.0 + p.x, 10.0 + p.y, 10.0 + p.z
        )

        private fun rawTemp(p: arc.math.geom.Vec3): Float =
            p.dst(0f, 0f, 1f) * 2.2f -
                Simplex.noise3d(seed, 8.0, 0.54, 1.4, 10.0 + p.x, 10.0 + p.y, 10.0 + p.z) * 2.9f

        private fun biomeOf(sec: Sector?): Biome {
            if (sec == null) return Biome.PLAINS
            if (sec.preset === Xingqiu.xiajie) return Biome.NETHER
            if (sec.preset === Xingqiu.senlin) return Biome.FOREST
            if (sec.preset === Xingqiu.chudi) return Biome.PLAINS
            return when (sec.id % 5) {
                1 -> Biome.FOREST
                2 -> Biome.NETHER
                3 -> Biome.DESERT
                4 -> Biome.ROCKY
                else -> Biome.PLAINS
            }
        }

        private fun floorFor(height: Float, temp: Float, biome: Biome): Floor {
            val index = Mathf.clamp((height * 6f).toInt(), 0, 5)

            if (temp > 0.95f && biome != Biome.NETHER) return Environment.shazi
            if (temp < 0.15f && biome != Biome.NETHER && biome != Biome.DESERT) return Environment.shitou

            return when (biome) {
                Biome.NETHER -> Environment.shitou
                Biome.DESERT -> if (index >= 4) Environment.shitou else Environment.shazi
                Biome.ROCKY -> if (index >= 2) Environment.shitou else Environment.nitu
                Biome.FOREST -> when (index) {
                    0, 1, 2 -> Environment.caodi
                    3 -> Environment.nitu
                    else -> Environment.shitou
                }
                Biome.PLAINS -> when (index) {
                    0, 1 -> Environment.caodi
                    2, 3 -> Environment.nitu
                    else -> Environment.shitou
                }
            }
        }

        private fun airThreshold(biome: Biome): Float = when (biome) {
            Biome.NETHER -> -0.30f
            Biome.ROCKY -> -0.28f
            Biome.DESERT -> -0.26f
            else -> -0.22f
        }

        override fun genTile(position: arc.math.geom.Vec3, tile: TileGen) {
            val biome = biomeOf(sector)
            val height = rawHeight(position)
            val temp = rawTemp(position)
            val floor = floorFor(height, temp, biome)

            // 低洼处形成浅水湖
            val water = (biome == Biome.PLAINS || biome == Biome.FOREST) && height < 0.325f
            tile.floor = if (water) Environment.shui else floor
            tile.block = if (water) Blocks.air else floor.asFloor().wall

            if (Ridged.noise3d(
                    seed + 1,
                    position.x.toDouble(), position.y.toDouble(), position.z.toDouble(),
                    2, airScl
                ) > airThreshold(biome)
            ) {
                tile.block = Blocks.air
            }
        }

        override fun generate() {
            val sec = sector ?: return
            val biome = biomeOf(sec)

            // 与 Erekir 一致：先做细胞化，让岩壁形成连续块状结构
            cells(4)

            // 在较平坦的区域补充少量岩柱/墙，增加层次
            pass { x, y ->
                if (block === Blocks.air && floor !== Environment.shui &&
                    noise(x.toFloat(), y.toFloat(), 3.0, 0.4, 13.0, 1.0) > 0.68f
                ) {
                    block = floor.asFloor().wall
                }
            }

            // 草地/绿洲斑块：让峡谷底部出现 Minecraft 风格的草地
            pass { x, y ->
                if (block === Blocks.air) {
                    val green = noise(x.toFloat(), y.toFloat(), 3.0, 0.5, 55.0, 1.0)
                    if (floor === Environment.nitu) {
                        when {
                            green > 0.46f -> floor = Environment.caodi
                            green < 0.40f -> floor = Environment.shitou
                        }
                    } else if (floor === Environment.shitou && green > 0.68f) {
                        floor = Environment.caodi
                    } else if (floor === Environment.shazi && green > 0.62f) {
                        floor = Environment.caodi
                    } else if (floor === Environment.caodi && green < 0.38f) {
                        floor = Environment.shitou
                    }
                }
            }

            // 低洼处的水潭（用 2D 噪声生成，避免整张图没有水）
            pass { x, y ->
                if (block === Blocks.air && floor !== Environment.shui &&
                    noise(x.toFloat() + 500f, y.toFloat() - 300f, 3.0, 0.6, 70.0, 1.0) > 0.72f
                ) {
                    floor = Environment.shui
                    block = Blocks.air
                    ore = Blocks.air
                }
            }

            // 出生点 → 敌巢的轴线
            val trns = Tmp.v1.trns(rand.random(360f), width / 2.6f)
            val spawnX = (trns.x + width / 2f).toInt().coerceIn(8, width - 9)
            val spawnY = (trns.y + height / 2f).toInt().coerceIn(8, height - 9)
            val endX = (-trns.x + width / 2f).toInt().coerceIn(8, width - 9)
            val endY = (-trns.y + height / 2f).toInt().coerceIn(8, height - 9)
            val maxd = Mathf.dst(width / 2f, height / 2f)

            erase(spawnX, spawnY, 15)
            brush(
                pathfind(
                    spawnX, spawnY, endX, endY,
                    Astar.TileHeuristic { t ->
                        (if (t.solid()) 300f else 0f) + maxd - t.dst(width / 2f, height / 2f) / 10f
                    },
                    Astar.manhattan
                ), 9
            )
            erase(endX, endY, 15)

            distort(10f, 12f)
            distort(5f, 7f)
            median(2)

            inverseFloodFill(tiles.getn(spawnX, spawnY))
            trimDark()

            // 矿石：按照噪声值形成不同矿脉（Erekir 风格）
            pass { x, y ->
                if (block === Blocks.air && floor !== Environment.shui) {
                    val n = noise(x.toFloat(), y.toFloat(), 4.0, 0.8, 55.0, 1.0)
                    val n2 = noise(
                        x.toFloat() + 150f,
                        y.toFloat() - x.toFloat() * 1.6f + 100f,
                        4.0, 0.8, 48.0, 1.0
                    )
                    when {
                        biome == Biome.NETHER && n2 > 0.62f -> ore = Environment.xiajiekuangshi
                        n > 0.84f -> ore = Environment.zuanshikuangshi
                        n > 0.80f -> ore = Environment.jinkuangshi
                        n > 0.75f -> ore = Environment.tiekuangshi
                        n > 0.72f -> ore = Environment.hongshikuangshi
                        n > 0.69f -> ore = Environment.tongkuangshi
                        n > 0.66f -> ore = Environment.meikuangshi
                        n > 0.60f -> ore = Environment.yuanshikuangshi
                    }
                }
            }

            // 花草装饰
            pass { x, y ->
                if (block === Blocks.air && floor === Environment.caodi && ore === Blocks.air && rand.chance(0.07)) {
                    ore = if (rand.chance(0.35)) Environment.huaduo else Environment.caocong
                }
            }

            // 树木
            val treeThreshold = when (biome) {
                Biome.FOREST -> 0.48f
                Biome.PLAINS -> 0.54f
                Biome.ROCKY -> 0.66f
                else -> 2f
            }
            if (treeThreshold < 1f) {
                pass { x, y ->
                    if (block === Blocks.air && floor === Environment.caodi && ore === Blocks.air) {
                        val nx = x.toFloat() + 300f
                        val ny = y.toFloat() - x.toFloat() * 1.2f
                        val tree = noise(nx, ny, 3.0, 0.6, 30.0, 1.0)
                        if (tree > treeThreshold) {
                            block = if (tree > treeThreshold + 0.12f) Environment.yunshan else Environment.xiangshu
                        }
                    }
                }
            }

            // 出生点核心
            erase(spawnX, spawnY, 5)
            val startFloor = when (biome) {
                Biome.NETHER, Biome.ROCKY -> Environment.shitou
                Biome.DESERT -> Environment.shazi
                else -> Environment.caodi
            }
            for (x in spawnX - 4..spawnX + 4) {
                for (y in spawnY - 4..spawnY + 4) {
                    val t = tiles.getn(x, y) ?: continue
                    t.setFloor(startFloor)
                    t.setAir()
                    t.clearOverlay()
                }
            }
            val coreTile = tiles.getn(spawnX, spawnY)
            coreTile.setBlock(Cores.chujihexing, Vars.state.rules.defaultTeam, 0)
            if (sec.id == 0) {
                coreTile.build?.items?.apply {
                    add(Items.yuanmu, 200)
                    add(Items.yuanshi, 500)
                    add(Items.muban, 300)
                    add(Items.tong, 200)
                    add(Items.tie, 100)
                    add(Items.mei, 100)
                    add(Items.hongshi, 100)
                    add(Items.shazi, 100)
                }
            }

            // 出生点附近保底树林（下界除外）
            if (biome != Biome.NETHER) {
                for ((ox, oy) in arrayOf(
                    9 to 9, -9 to 9, 9 to -9, -9 to -9,
                    12 to 0, -12 to 0, 0 to 12, 0 to -12
                )) {
                    val tx = spawnX + ox
                    val ty = spawnY + oy
                    if (tx < 1 || ty < 1 || tx >= width - 1 || ty >= height - 1) continue
                    val t = tiles.getn(tx, ty) ?: continue
                    t.setFloor(Environment.caodi)
                    t.setAir()
                    t.clearOverlay()
                    t.setBlock(Environment.xiangshu, Team.derelict, 0)
                }
            }

            // 敌巢刷怪点
            erase(endX, endY, 6)
            val endTile = tiles.getn(endX, endY)
            endTile.setFloor(Environment.shitou)
            endTile.setAir()
            endTile.setOverlay(Blocks.spawn)
            // 额外的两个刷怪点，避免单方向过于集中
            for ((ox, oy) in arrayOf(-4 to 0, 0 to 4)) {
                val t = tiles.getn(
                    (endX + ox).coerceIn(2, width - 3),
                    (endY + oy).coerceIn(2, height - 3)
                )
                t.setFloor(Environment.shitou)
                t.setAir()
                t.setOverlay(Blocks.spawn)
            }

            // 规则
            val rules = Vars.state.rules
            rules.waves = true
            rules.waveTimer = true
            rules.waveSending = true
            rules.attackMode = false
            rules.waveTeam = Team.crux
            rules.winWave = if (biome == Biome.NETHER) 30 else 15
            rules.waveSpacing = 60f * 90f
            rules.initialWaveSpacing = 60f * 60f * 1.5f
            rules.env = sec.planet.defaultEnv
            rules.loadout = Seq.with(
                ItemStack(Items.yuanmu, 200),
                ItemStack(Items.yuanshi, 500),
                ItemStack(Items.muban, 300),
                ItemStack(Items.tong, 200),
                ItemStack(Items.tie, 100),
                ItemStack(Items.mei, 100),
                ItemStack(Items.hongshi, 100),
                ItemStack(Items.shazi, 100)
            )
        }

        override fun getColor(position: arc.math.geom.Vec3, out: Color) {
            val biome = biomeOf(sector)
            val f = floorFor(rawHeight(position), rawTemp(position), biome)
            out.set(f.mapColor).a(1f - f.albedo)
        }

        override fun getHeight(position: arc.math.geom.Vec3): Float =
            Mathf.pow(Math.max(rawHeight(position), 0f), 3f) * 1.6f

        override fun getSizeScl(): Float = 2400f
    }
}
