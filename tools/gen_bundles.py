#!/usr/bin/env python3
"""Generate English and Simplified-Chinese bundles for the mod.

Content names are prefixed by Mindustry with the mod's internal name
("mc-on-mindustry-"), so bundle keys use that prefix even though the
Kotlin source uses short names.
"""
from __future__ import annotations

import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "assets", "bundles")
PREFIX = "mc-on-mindustry-"

ITEMS = {
    "yuanmu": ("原木", "Log"),
    "muban": ("木板", "Planks"),
    "yuanshi": ("圆石", "Cobblestone"),
    "shikuai": ("石头", "Stone"),
    "shazi": ("沙子", "Sand"),
    "mei": ("煤炭", "Coal"),
    "tongkuang": ("铜矿石", "Copper Ore"),
    "tiekuang": ("铁矿石", "Iron Ore"),
    "jinkuang": ("金矿石", "Gold Ore"),
    "zuanshi": ("钻石", "Diamond"),
    "hongshi": ("红石", "Redstone"),
    "xiajiecanhai": ("下界残骸", "Ancient Debris"),
    "xiajiehejinpian": ("下界合金碎片", "Netherite Scrap"),
    "tong": ("铜锭", "Copper Ingot"),
    "tie": ("铁锭", "Iron Ingot"),
    "jin": ("金锭", "Gold Ingot"),
    "xiajiehejinding": ("下界合金锭", "Netherite Ingot"),
    "boli": ("玻璃", "Glass"),
    "yangmao": ("羊毛", "Wool"),
    "pige": ("皮革", "Leather"),
    "jidan": ("鸡蛋", "Egg"),
    "zhurou": ("猪肉", "Porkchop"),
}

TIERS = [("mu", "木", "Wooden"), ("shi", "石", "Stone"), ("tie", "铁", "Iron"),
         ("jin", "金", "Golden"), ("zuanshi", "钻石", "Diamond"),
         ("xiajiehejin", "下界合金", "Netherite")]
TOOLS = [("fu", "斧", "Axe"), ("gao", "镐", "Pickaxe"), ("chu", "锄", "Hoe"), ("jian", "剑", "Sword")]

BLOCKS = {
    "caodi": ("草地", "Grass"),
    "nitu": ("泥土", "Dirt"),
    "shitou": ("石头地面", "Stone Floor"),
    "shazi": ("沙子地面", "Sand Floor"),
    "huangjingqiang": ("石墙", "Stone Wall"),
    "xiangshu": ("橡树", "Oak Tree"),
    "yunshan": ("云杉", "Spruce Tree"),
    "yuanshikuangshi": ("圆石矿", "Cobblestone Ore"),
    "meikuangshi": ("煤矿", "Coal Ore"),
    "tongkuangshi": ("铜矿", "Copper Ore Block"),
    "tiekuangshi": ("铁矿", "Iron Ore Block"),
    "jinkuangshi": ("金矿", "Gold Ore Block"),
    "zuanshikuangshi": ("钻石矿", "Diamond Ore Block"),
    "hongshikuangshi": ("红石矿", "Redstone Ore Block"),
    "xiajiekuangshi": ("下界残骸矿", "Ancient Debris Block"),
    "kuangjing": ("矿井", "Mine"),
    "shencengkuangjing": ("深层矿井", "Deep Mine"),
    "zuanshikuangjing": ("钻石矿井", "Diamond Mine"),
    "xiajiekuangjing": ("下界矿井", "Nether Mine"),
    "famuchang": ("伐木场", "Lumberyard"),
    "ronglu_tong": ("熔炉·铜", "Copper Furnace"),
    "ronglu_tie": ("熔炉·铁", "Iron Furnace"),
    "ronglu_jin": ("熔炉·金", "Gold Furnace"),
    "ronglu_boli": ("熔炉·玻璃", "Glass Furnace"),
    "ronglu_shitou": ("熔炉·石头", "Stone Furnace"),
    "gaolu": ("高炉", "Blast Furnace"),
    "duanzaolu": ("锻造炉", "Forge"),
    "gongzuotai": ("工作台", "Crafting Table"),
    "gongjiangtai_mu": ("木工台", "Wooden Workbench"),
    "gongjiangtai_shi": ("石匠台", "Stone Workbench"),
    "gongjiangtai_tie": ("铁匠台", "Iron Workbench"),
    "gongjiangtai_jin": ("金匠台", "Golden Workbench"),
    "gongjiangtai_zuanshi": ("钻石匠台", "Diamond Workbench"),
    "gongjiangtai_xiajiehejin": ("合金匠台", "Netherite Workbench"),
    "hongshizhongjiqi": ("红石中继器·电力中枢", "Redstone Repeater Node"),
    "hongshikuai": ("红石块·电池", "Redstone Block Battery"),
    "hongshihuoba": ("红石火把·发电机", "Redstone Torch Generator"),
    "jishe": ("鸡舍", "Chicken Coop"),
    "niushe": ("牛舍", "Cow Barn"),
    "yangjuan": ("羊圈", "Sheep Pen"),
    "zhujuan": ("猪圈", "Pigpen"),
    "cangku": ("仓库", "Warehouse"),
    "chuansongdai": ("传送带", "Conveyor"),
    "loudou": ("漏斗", "Hopper"),
    "luyouqi": ("路由器", "Router"),
    "fenleiqi": ("分类器", "Sorter"),
    "jiaochaqi": ("交叉器", "Junction"),
    "wuliujiedian": ("物流节点", "Logistics Node"),
    "binggongchang": ("兵工厂", "Arsenal"),
    "huaduo": ("花", "Flower"),
    "caocong": ("草丛", "Grass Tuft"),
    "chujihexing": ("初级核心", "Basic Core"),
    "zhongdaihexing": ("中级核心", "Advanced Core"),
    "zhongjihexing": ("最终核心", "Final Core"),
    "mujianpaotai": ("木剑炮台", "Wooden Sword Turret"),
    "shijianpaotai": ("石剑炮台", "Stone Sword Turret"),
    "tiejianpaotai": ("铁剑炮台", "Iron Sword Turret"),
    "jinjianpaotai": ("金剑炮台", "Golden Sword Turret"),
    "zuanshijianpaotai": ("钻石剑炮台", "Diamond Sword Turret"),
    "xiajiehejinjianpaotai": ("下界合金剑炮台", "Netherite Sword Turret"),
    "gongpaotai": ("弓炮台", "Bow Turret"),
    "nupaotai": ("弩炮台", "Crossbow Turret"),
    "sanchajipaotai": ("三叉戟炮台", "Trident Turret"),
}

UNITS = {
    "jiangshi": ("僵尸", "Zombie"),
    "kulou": ("骷髅", "Skeleton"),
    "kulipa": ("苦力怕", "Creeper"),
    "zhizhu": ("蜘蛛", "Spider"),
    "moyingren": ("末影人", "Enderman"),
    "huanyi": ("幻翼", "Phantom"),
    "egang": ("恶魂", "Ghast"),
    "xueguilei": ("雪傀儡", "Snow Golem"),
    "tieguilei": ("铁傀儡", "Iron Golem"),
}

SECTORS = {
    "chudi": ("初级区域", "Starting Area", "方块世界的起点。", "Starting area of the block world."),
    "senlin": ("森林", "Forest", "茂密的橡树与云杉森林。", "A dense forest full of oak and spruce."),
    "xiajie": ("下界", "Nether", "遍布下界残骸的灼热之地。", "A scorched land rich in ancient debris."),
}

DESCRIPTIONS = {
    "kuangjing": ("开采圆石、煤炭、铜矿与红石。", "Mines cobblestone, coal, copper and redstone."),
    "shencengkuangjing": ("开采铁矿与金矿。", "Mines iron and gold ore."),
    "zuanshikuangjing": ("开采钻石矿。", "Mines diamond ore."),
    "xiajiekuangjing": ("开采下界残骸。", "Mines ancient debris."),
    "famuchang": ("对着树木放置，自动采集原木。", "Place facing trees to harvest logs."),
    "gongzuotai": ("把原木加工成木板。", "Turns logs into planks."),
    "hongshizhongjiqi": ("大范围连接电力网络。", "Connects the power grid over a long range."),
    "hongshikuai": ("储存红石电能。", "Stores redstone power."),
    "hongshihuoba": ("燃烧红石发电。", "Burns redstone to generate power."),
    "cangku": ("大容量物品仓库；贴着核心时会并入核心容量。", "A large warehouse. Merges into the adjacent core's storage."),
    "chuansongdai": ("木制传送带，把物品运向下一格。", "A wooden conveyor that moves items forward."),
    "loudou": ("从相邻容器/核心中取出物品并送入前方。", "Pulls items from adjacent containers or cores."),
    "luyouqi": ("把物品平均分配到周围三格。", "Distributes items evenly to three adjacent blocks."),
    "fenleiqi": ("只放行指定物品，其余物品从其他方向输出。", "Only allows a configured item through."),
    "jiaochaqi": ("让两条物品线路交叉通过。", "Lets two item lines cross."),
    "wuliujiedian": ("连接另一个物流节点，远距离运输物品。", "Links to another logistics node to transport items over distance."),
    "binggongchang": ("消耗电力与资源制造雪傀儡和铁傀儡。", "Builds snow golems and iron golems from power and resources."),
    "chujihexing": ("方块世界的起点。", "The starting core of the block world."),
}


def write_bundle(path: str, lang: int):
    lines = [
        "# Generated by tools/gen_bundles.py",
        "mod.mc-on-mindustry.name=MC On Mindustry",
    ]
    for name, pair in ITEMS.items():
        lines.append(f"item.{PREFIX}{name}.name={pair[lang]}")
    for tier, zh, en in TIERS:
        for kind, tzh, ten in TOOLS:
            lines.append(f"item.{PREFIX}{tier}{kind}.name={(zh + tzh) if lang == 0 else (en + ' ' + ten)}")
    for name, pair in BLOCKS.items():
        lines.append(f"block.{PREFIX}{name}.name={pair[lang]}")
        if name in DESCRIPTIONS:
            lines.append(f"block.{PREFIX}{name}.description={DESCRIPTIONS[name][lang]}")
    for name, pair in UNITS.items():
        lines.append(f"unit.{PREFIX}{name}.name={pair[lang]}")
    lines.append(f"planet.{PREFIX}fangkuai.name={'方块世界' if lang == 0 else 'Block World'}")
    for name, pair in SECTORS.items():
        lines.append(f"sector.{PREFIX}{name}.name={pair[lang]}")
        lines.append(f"sector.{PREFIX}{name}.description={pair[lang + 2]}")
    lines.append(f"techtree.mc-on-mindustry-root={'方块世界' if lang == 0 else 'Block World'}")
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines) + "\n")


def main():
    os.makedirs(OUT, exist_ok=True)
    write_bundle(os.path.join(OUT, "bundle.properties"), 1)
    write_bundle(os.path.join(OUT, "bundle_zh_CN.properties"), 0)
    print("bundles written")


if __name__ == "__main__":
    main()
