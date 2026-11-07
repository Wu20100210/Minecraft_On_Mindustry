#!/usr/bin/env python3
"""Generate Minecraft-styled sprites for the MC On Mindustry mod.

All output sprites are flat files in assets/sprites/ named after the content's
internal name (Mindustry looks atlas regions up by file name).

Item icons are 32x32. 1x1 blocks are 32x32, 2x2 blocks 64x64, 3x3 cores 96x96.
"""
from __future__ import annotations

import colorsys
import math
import os
import random
from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, "素材")
OUT = os.path.join(ROOT, "assets", "sprites")
os.makedirs(OUT, exist_ok=True)

random.seed(20261107)

# ----------------------------------------------------------------------------
# helpers
# ----------------------------------------------------------------------------

def load(name: str, size=None) -> Image.Image:
    if not name.lower().endswith(".png"):
        name += ".png"
    path = os.path.join(SRC, name)
    if not os.path.exists(path):
        path = os.path.join(OUT, name)
    im = Image.open(path).convert("RGBA")
    if size is not None and im.size != (size, size):
        im = im.resize((size, size), Image.NEAREST)
    return im


def save(im: Image.Image, name: str):
    path = os.path.join(OUT, name + ".png")
    im.save(path)
    return path


def upscale(im: Image.Image, factor: int) -> Image.Image:
    return im.resize((im.width * factor, im.height * factor), Image.NEAREST)


TIERS = {
    "mu":      ("#9c6b3f", "#c08f5a", "#6b4626", "#8b5a2b"),   # wood
    "shi":     ("#8a8a8a", "#b6b6b6", "#5c5c5c", "#8b5a2b"),   # stone
    "tie":     ("#d4d4d4", "#f4f4f4", "#909090", "#8b5a2b"),   # iron
    "jin":     ("#f2cf3a", "#fff3a0", "#a87a12", "#8b5a2b"),   # gold
    "zuanshi": ("#3fd8c8", "#a6fff2", "#1f9a8f", "#8b5a2b"),   # diamond
    "xiajiehejin": ("#4a4048", "#6f6470", "#28232a", "#4a3d33"),  # netherite
}

HANDLE = ("#8b5a2b", "#b07a45", "#5f3d1e")


def hexc(s: str):
    s = s.lstrip("#")
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), 255)


def shade(c, f):
    r, g, b, a = c
    return (max(0, min(255, int(r * f))), max(0, min(255, int(g * f))),
            max(0, min(255, int(b * f))), a)


def new_canvas(n=16) -> Image.Image:
    return Image.new("RGBA", (n, n), (0, 0, 0, 0))


def line(px, x0, y0, x1, y1, color):
    """Bresenham line into a pixel array."""
    dx = abs(x1 - x0); sx = 1 if x0 < x1 else -1
    dy = -abs(y1 - y0); sy = 1 if y0 < y1 else -1
    err = dx + dy
    while True:
        if 0 <= x0 < len(px) and 0 <= y0 < len(px[0]):
            px[x0][y0] = color
        if x0 == x1 and y0 == y1:
            break
        e2 = 2 * err
        if e2 >= dy:
            err += dy; x0 += sx
        if e2 <= dx:
            err += dx; y0 += sy


def thick(px, x0, y0, x1, y1, color, width=2):
    for off in range(width):
        line(px, x0 + off, y0, x1 + off, y1, color)


def to_image(px) -> Image.Image:
    n = len(px)
    im = new_canvas(n)
    for x in range(n):
        for y in range(n):
            if px[x][y] is not None:
                im.putpixel((x, y), px[x][y])
    return im


def grid(n=16):
    return [[None] * n for _ in range(n)]


def tool_icon(kind: str, tier: str) -> Image.Image:
    base, light, dark, _ = TIERS[tier]
    base, light, dark = hexc(base), hexc(light), hexc(dark)
    hb, hl, hd = (hexc(c) for c in HANDLE)
    px = grid()

    if kind in ("fu", "gao", "chu"):
        # handle: lower-left to centre
        thick(px, 4, 14, 11, 7, hb, 2)
        thick(px, 4, 13, 10, 7, hl, 1)
        px[4][15] = hd
        px[3][14] = hd

    if kind == "fu":  # axe head on the upper-left of the handle
        for y in range(2, 9):
            for x in range(5, 11):
                px[x][y] = None
        # blade shape
        pts = [(5, 3), (6, 2), (7, 2), (8, 3), (9, 4), (9, 5), (8, 6),
               (7, 7), (6, 7), (5, 6), (4, 5), (4, 4)]
        for x, y in pts:
            px[x][y] = base
        for x, y in [(5, 2), (6, 2), (7, 3), (8, 4)]:
            px[x][y] = light
        for x, y in [(4, 5), (5, 6), (6, 7), (7, 7)]:
            px[x][y] = dark
        line(px, 11, 6, 12, 7, hb)
    elif kind == "gao":  # pickaxe head: curved band across the top
        for x in range(2, 14):
            t = (x - 2) / 11.0
            y = 2 + 3.2 * (2 * t - 1) ** 2
            y = int(round(y))
            px[x][y] = base
            px[x][y + 1] = dark if x < 12 else base
        px[2][5] = light
        for x in range(4, 11):
            px[x][2] = light
    elif kind == "chu":  # hoe: blade to the right of the top
        for x in range(8, 14):
            px[x][3] = light if x < 12 else base
            px[x][4] = base
            px[x][5] = dark
        px[13][3] = base
        px[13][4] = dark
        line(px, 11, 7, 12, 6, hb)

    elif kind in ("jian",):  # sword: diagonal blade, cross guard
        # blade (3 px wide diagonal band)
        line(px, 5, 10, 13, 2, dark)
        line(px, 6, 10, 14, 2, base)
        line(px, 7, 9, 15, 1, light)
        px[14][2] = light
        px[13][3] = base
        # cross guard, perpendicular to the blade
        line(px, 3, 8, 7, 12, light)
        line(px, 2, 8, 6, 12, dark)
        line(px, 4, 9, 8, 13, dark)
        # handle + pommel
        thick(px, 2, 14, 5, 11, hb, 2)
        px[2][15] = hd
        px[3][14] = hl
        px[3][11] = hd

    elif kind == "gong":  # bow
        wood = hb
        for y in range(2, 14):
            for x in range(3, 13):
                pass
        # arc: circle centred at (12, 8), radius 9, left half
        for y in range(16):
            for x in range(16):
                d = math.hypot(x - 12.5, y - 8.0)
                if 8.0 <= d <= 9.6 and 1 <= x <= 8:
                    px[x][y] = wood if d < 9.0 else hl
        # string
        for y in range(3, 13):
            px[8][y] = (235, 235, 235, 255)
        px[8][2] = (200, 200, 200, 255)
        px[8][13] = (200, 200, 200, 255)
        # grip
        for y in range(7, 10):
            px[5][y] = hd

    elif kind == "sanchaji":  # trident
        shaft = hexc("#5b4227")
        for y in range(6, 16):
            px[8][y] = shaft
            px[9][y] = shade(shaft, 0.75)
        for x in range(3, 14):
            px[x][2] = base
            px[x][3] = dark
        for x in (3, 8, 13):
            line(px, x, 4, x, 1, base)
            px[x][1] = light
        px[8][0] = light
        # crossbar
        line(px, 5, 6, 11, 6, light)

    return to_image(px)


def material_item(name: str, texture: str, src_size=32) -> Image.Image:
    im = load(texture, src_size)
    return im.resize((32, 32), Image.NEAREST)


def square_item(color, noise=0.12) -> Image.Image:
    """A simple saturated square icon (for wool/leather/egg-like items)."""
    im = new_canvas(16)
    base = hexc(color)
    for y in range(16):
        for x in range(16):
            f = 1.0 + random.uniform(-noise, noise)
            im.putpixel((x, y), shade(base, f))
    return im.resize((32, 32), Image.NEAREST)


# ----------------------------------------------------------------------------
# items
# ----------------------------------------------------------------------------

def gen_items():
    # material icons straight from the Minecraft textures
    mats = {
        "mei": "coal1.png",
        "tongkuang": "copper_ore1.png",
        "tiekuang": "iron_ore1.png",
        "jinkuang": "gold_ore1.png",
        "zuanshi": "diamond1.png",
        "xiajiecanhai": "ancient_debris_side1.png",
        "xiajiehejinpian": "netherite_scrap1.png",
        "tong": "copper_ingot1.png",
        "tie": "iron_ingot1.png",
        "jin": "gold_ingot1.png",
        "xiajiehejinding": "netherite_ingot1.png",
        "boli": "glass1.png",
        "yuanshi": "stone1.png",
        "shazi": "sand1.png",
    }
    for name, tex in mats.items():
        save(material_item(name, tex), name)

    # smooth stone item (smelted cobblestone)
    sm = load("stone1.png").copy()
    for y in range(32):
        for x in range(32):
            sm.putpixel((x, y), shade(sm.getpixel((x, y)), 1.14))
    save(sm, "shikuai")

    # log: vertical bark with rings at both ends
    log = new_canvas(16)
    bark, barkd, barkl = hexc("#6b4a2a"), hexc("#4e3319"), hexc("#8a6238")
    for y in range(16):
        for x in range(16):
            v = (x * 7 + y * 3) % 11
            log.putpixel((x, y), bark if v else barkd)
    for y in range(16):
        log.putpixel((0, y), barkd); log.putpixel((15, y), barkd)
        log.putpixel((1, y), barkl)
    for (cx, cy) in ((4, 4), (11, 11)):
        for r in range(3):
            for a in range(0, 360, 12):
                x = int(cx + r * math.cos(math.radians(a)))
                y = int(cy + r * math.sin(math.radians(a)))
                if 0 <= x < 16 and 0 <= y < 16:
                    log.putpixel((x, y), hexc("#b98a5a") if r == 2 else barkd)
    save(log.resize((32, 32), Image.NEAREST), "yuanmu")

    # planks
    planks = new_canvas(16)
    p1, p2, p3 = hexc("#b98a5a"), hexc("#a3764b"), hexc("#8a6238")
    for y in range(16):
        for x in range(16):
            planks.putpixel((x, y), p1 if (y % 4) else p2)
    for y in (3, 7, 11, 15):
        for x in range(16):
            planks.putpixel((x, y), p3)
    for x in (5, 12):
        for y in range(16):
            planks.putpixel((x, y), p2)
    save(planks.resize((32, 32), Image.NEAREST), "muban")

    # dirt
    dirt = new_canvas(16)
    d1, d2, d3 = hexc("#79553a"), hexc("#6b4a2f"), hexc("#8a6242")
    for y in range(16):
        for x in range(16):
            dirt.putpixel((x, y), random.choice((d1, d1, d2, d3)))
    save(dirt.resize((32, 32), Image.NEAREST), "nitu")

    # redstone dust
    red = new_canvas(16)
    rc, rl, rd = hexc("#d81f1f"), hexc("#ff5a4a"), hexc("#8f0f0f")
    for _ in range(70):
        x, y = random.randint(2, 13), random.randint(2, 13)
        red.putpixel((x, y), random.choice((rc, rc, rl, rd)))
    save(red.resize((32, 32), Image.NEAREST), "hongshi")

    # wool / leather / egg / meat
    save(square_item("#e9e9e9"), "yangmao")
    leather = new_canvas(16)
    lc, ll, ld = hexc("#a06a3c"), hexc("#c08a58"), hexc("#6d4524")
    for y in range(3, 13):
        for x in range(2, 14):
            leather.putpixel((x, y), lc)
    for x in range(2, 14):
        leather.putpixel((x, 3), ll); leather.putpixel((x, 12), ld)
    for y in range(3, 13):
        leather.putpixel((2, y), ll); leather.putpixel((13, y), ld)
    save(leather.resize((32, 32), Image.NEAREST), "pige")

    egg = new_canvas(16)
    ec, el = hexc("#f2ead2"), hexc("#fffaf0")
    for y in range(3, 15):
        for x in range(4, 12):
            dx = (x + 0.5 - 8) / 4.0
            dy = (y + 0.5 - 9) / 6.0
            if dx * dx + dy * dy <= 1.0:
                egg.putpixel((x, y), ec)
    for x, y in [(6, 5), (7, 4), (6, 6), (7, 5)]:
        egg.putpixel((x, y), el)
    save(egg.resize((32, 32), Image.NEAREST), "jidan")

    meat = new_canvas(16)
    mc, ml, md = hexc("#c2453f"), hexc("#e8736a"), hexc("#8a2a26")
    for y in range(4, 14):
        for x in range(3, 13):
            if (x - 8) ** 2 / 25.0 + (y - 9) ** 2 / 20.0 <= 1.0:
                meat.putpixel((x, y), mc)
    for x, y in [(5, 6), (6, 5), (7, 6), (8, 7)]:
        meat.putpixel((x, y), ml)
    for x in range(4, 11):
        meat.putpixel((x, 13), md)
    for y in range(6, 13):
        meat.putpixel((13, y), md)
    save(meat.resize((32, 32), Image.NEAREST), "zhurou")

    # tools & swords for every tier
    for tier in TIERS:
        for kind in ("fu", "gao", "chu"):
            save(tool_icon(kind, tier).resize((32, 32), Image.NEAREST), tier + kind)
        save(tool_icon("jian", tier).resize((32, 32), Image.NEAREST), tier + "jian")

    # bows (not tiered)
    save(tool_icon("gong", "mu").resize((32, 32), Image.NEAREST), "gong")
    save(tool_icon("sanchaji", "xiajiehejin").resize((32, 32), Image.NEAREST), "sanchaji")


# ----------------------------------------------------------------------------
# block helpers
# ----------------------------------------------------------------------------

def tile_tex(tex: Image.Image, size: int) -> Image.Image:
    """Tile a 32x32 texture into a size x size canvas."""
    im = Image.new("RGBA", (size, size))
    for y in range(0, size, 32):
        for x in range(0, size, 32):
            im.paste(tex, (x, y))
    return im


def frame(im: Image.Image, dark=0.7, light=1.18) -> Image.Image:
    """Add Minecraft-ish beveled border to a block sprite."""
    im = im.copy()
    w, h = im.size
    px = im.load()
    for x in range(w):
        for y in range(h):
            c = px[x, y]
            if x == 0 or y == 0:
                px[x, y] = shade(c, dark)
            elif x == w - 1 or y == h - 1:
                px[x, y] = shade(c, dark)
            elif x == 1 or y == 1:
                px[x, y] = shade(c, light)
    return im


def make_block(name: str, base: str, size: int = 2, decal: str | None = None,
               decal_scale: float = 0.6, decal_dy: int = 0):
    n = size * 32
    bt = load(base, 32)
    im = tile_tex(bt, n)
    if decal:
        d = load(decal, 32)
        ds = int(n * decal_scale)
        d = d.resize((ds, ds), Image.NEAREST)
        im.alpha_composite(d, ((n - ds) // 2, (n - ds) // 2 + decal_dy))
    im = frame(im)
    save(im, name)
    return im


# ----------------------------------------------------------------------------
# environment
# ----------------------------------------------------------------------------

def noise_tex(colors, n=32, seed=None):
    im = Image.new("RGBA", (n, n))
    for y in range(n):
        for x in range(n):
            im.putpixel((x, y), random.choice(colors))
    return im


def gen_environment():
    # grass + variants from the grass sheet
    grass_sheet = load("Grass_hues_Simplified1.png")
    # caodi is the main variant; a few extra variants for visual variety
    for i in range(4):
        tile = grass_sheet.crop((i * 32 % 96, (i * 32 // 96) * 32, i * 32 % 96 + 32,
                                 (i * 32 // 96) * 32 + 32))
        save(tile, "caodi" if i == 0 else "caodi" + str(i))

    save(load("stone1.png"), "shitou")
    save(load("sand1.png"), "shazi")

    dirt = noise_tex([hexc("#7a5537"), hexc("#6b4a2f"), hexc("#8a6242")])
    save(dirt, "nitu")

    # rock wall
    wall = load("stone1.png").copy()
    for x in range(32):
        for y in range(32):
            c = wall.getpixel((x, y))
            v = 0.72 + 0.1 * ((x * 3 + y * 7) % 5) / 4
            wall.putpixel((x, y), shade(c, v))
    save(frame(wall, 0.6, 1.25), "huangjingqiang")

    # trees: leafy canopy (top-down). The wall is solid and harvestable.
    def tree(name, leaf_a, leaf_b, leaf_c, trunk="#5b4227", dark="#2c4a22"):
        n = 32
        im = Image.new("RGBA", (n, n))
        for y in range(n):
            for x in range(n):
                d = math.hypot(x - 15.5, y - 15.5)
                if d < 15.5:
                    im.putpixel((x, y), random.choice((leaf_a, leaf_a, leaf_b, leaf_c)))
        # clumps of leaves
        for _ in range(26):
            cx, cy = random.randint(2, 29), random.randint(2, 29)
            if math.hypot(cx - 15.5, cy - 15.5) > 14:
                continue
            r = random.randint(2, 4)
            for y in range(cy - r, cy + r + 1):
                for x in range(cx - r, cx + r + 1):
                    if 0 <= x < n and 0 <= y < n and math.hypot(x - cx, y - cy) <= r:
                        im.putpixel((x, y), random.choice((leaf_b, leaf_c)))
        # trunk peeking through the middle
        for y in range(12, 20):
            for x in range(14, 18):
                im.putpixel((x, y), hexc(trunk) if (x + y) % 3 else hexc(dark))
        # outline
        for x in range(n):
            for y in range(n):
                if math.hypot(x - 15.5, y - 15.5) >= 15.0:
                    im.putpixel((x, y), (0, 0, 0, 0))
        im = frame(im, 0.82, 1.06)
        save(im, name)
        sh = Image.new("RGBA", (n, n), (0, 0, 0, 0))
        for y in range(n):
            for x in range(n):
                if im.getpixel((x, y))[3] > 0:
                    sh.putpixel((x, y), (0, 0, 0, 90))
        save(sh, name + "-shadow")

    tree("xiangshu", hexc("#3f7a2e"), hexc("#4c8f36"), hexc("#2f5e22"))
    tree("yunshan", hexc("#2c5a35"), hexc("#357044"), hexc("#1e4026"), trunk="#4a3520")

    # ores (opaque block textures, like MC ore blocks)
    ores = {
        "yuanshikuangshi": "stone1.png",
        "meikuangshi": "coal_ore1.png",
        "tongkuangshi": "copper_ore1.png",
        "tiekuangshi": "iron_ore1.png",
        "jinkuangshi": "gold_ore1.png",
        "zuanshikuangshi": "diamond_ore1.png",
        "xiajiekuangshi": "ancient_debris_side1.png",
    }

    # redstone ore: stone with red specks
    red_ore = load("stone1.png").copy()
    for _ in range(22):
        cx, cy = random.randint(2, 29), random.randint(2, 29)
        for dx in range(2):
            for dy in range(2):
                if 0 <= cx + dx < 32 and 0 <= cy + dy < 32:
                    red_ore.putpixel((cx + dx, cy + dy), hexc("#e01f1f") if random.random() < .6 else hexc("#8f0f0f"))

    def write_ore(name, base):
        save(base, name)
        # OreBlock defaults to 3 variants: name1/2/3
        for i in (1, 2, 3):
            v = base.copy()
            factor = 1.0 + (0.06 if i == 1 else (-0.05 if i == 2 else 0.03))
            for y in range(32):
                for x in range(32):
                    c = v.getpixel((x, y))
                    v.putpixel((x, y), shade(c, factor * (0.97 + 0.06 * (((x * 3 + y + i) % 5) / 4.0))))
            save(v, name + str(i))

    for name, tex in ores.items():
        write_ore(name, load(tex))
    write_ore("hongshikuangshi", red_ore)

    # 水：深蓝色水面 + 波纹
    water = Image.new("RGBA", (32, 32))
    for y in range(32):
        for x in range(32):
            v = 0.90 + 0.10 * (((x * 4 + y * 7) % 9) / 8.0)
            water.putpixel((x, y), shade(hexc("#2a6fc0"), v))
    for i in range(3):
        yy = 6 + i * 9
        for x in range(2, 30):
            y = yy + int(1.5 * math.sin(x * 0.4 + i))
            if 0 <= y < 32:
                water.putpixel((x, y), hexc("#6fb8f0"))
    save(water, "shui")

    # 装饰：花与草丛（透明）
    flower = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(8, 15):
        _px(flower, 8, y, "#3f7a2e")
    for (x, y) in ((7, 5), (8, 4), (9, 5), (8, 6), (7, 6), (9, 6)):
        _px(flower, x, y, "#d83a3a")
    _px(flower, 8, 5, "#ffd84a")
    _px(flower, 6, 7, "#3f7a2e")
    _px(flower, 10, 7, "#3f7a2e")
    save(flower.resize((32, 32), Image.NEAREST), "huaduo")

    tuft = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for (x, h) in ((4, 9), (6, 12), (8, 14), (10, 11), (12, 8)):
        for y in range(15 - h, 16):
            _px(tuft, x + (1 if y < 12 else 0), y, "#4a8a34" if y % 2 else "#356a24")
    save(tuft.resize((32, 32), Image.NEAREST), "caocong")


# ----------------------------------------------------------------------------
# machines
# ----------------------------------------------------------------------------

def metal_base(n, base="#43484f", edge="#2b2f34", rivet="#6d757f"):
    im = Image.new("RGBA", (n, n))
    b, e, r = hexc(base), hexc(edge), hexc(rivet)
    for y in range(n):
        for x in range(n):
            v = 0.96 + 0.08 * (((x * 5 + y * 3) % 7) / 6.0)
            im.putpixel((x, y), shade(b, v))
    for i in range(n):
        im.putpixel((i, 0), e); im.putpixel((i, n - 1), e)
        im.putpixel((0, i), e); im.putpixel((n - 1, i), e)
    for (rx, ry) in ((7, 7), (n - 8, 7), (7, n - 8), (n - 8, n - 8)):
        im.putpixel((rx, ry), r); im.putpixel((rx + 1, ry), r)
        im.putpixel((rx, ry + 1), r); im.putpixel((rx + 1, ry + 1), r)
    return im


def drill_sprite(name, bit_color, gem_color):
    n = 64
    im = metal_base(n)
    # corner plates
    for (ox, oy) in ((4, 4), (44, 4), (4, 44), (44, 44)):
        for y in range(oy, oy + 16):
            for x in range(ox, ox + 16):
                im.putpixel((x, y), shade(im.getpixel((x, y)), 1.12))
    # central shaft
    for y in range(24, 40):
        for x in range(24, 40):
            im.putpixel((x, y), hexc("#22262b"))
    im = frame(im, 0.7, 1.15)
    save(im, name)

    rot = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    c = hexc(bit_color)
    d = shade(c, 0.55)
    for y in range(8, 56):
        t = (y - 8) / 47.0
        w = int(3 + t * 0)
        for x in range(12, 52):
            # twist the bit around the centre
            if 12 <= x < 52:
                ang = math.atan2(y - 31.5, x - 31.5)
                r = math.hypot(x - 31.5, y - 31.5)
                if 4 <= r <= 16 and abs(math.sin(ang * 2 + r * 0.28)) > 0.52:
                    rot.putpixel((x, y), c if (x + y) % 3 else d)
    for r in (2, 5, 8, 11, 14):
        for a in range(0, 720, 4):
            x = int(31.5 + r * math.cos(math.radians(a) + r * 0.3))
            y = int(31.5 + r * math.sin(math.radians(a) + r * 0.3))
            if 0 <= x < n and 0 <= y < n:
                rot.putpixel((x, y), c if a % 8 else d)
    save(rot, name + "-rotator")

    top = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    for (x, y) in ((28, 28), (35, 28), (28, 35), (35, 35)):
        for dx in range(3):
            for dy in range(3):
                top.putpixel((x + dx, y + dy), hexc(gem_color))
    save(top, name + "-top")


def furnace_sprite(name, door_color=None):
    n = 64
    im = tile_tex(load("stone1.png"), n)
    im = frame(im, 0.68, 1.16)
    front = load("furnace_front1.png").resize((44, 44), Image.NEAREST)
    im.alpha_composite(front, (10, 12))
    if door_color:
        for y in range(12, 56):
            for x in range(10, 54):
                c = im.getpixel((x, y))
                if c[0] < 80 and c[1] < 80 and c[2] < 80:
                    im.putpixel((x, y), hexc(door_color))
    save(im, name)


def workbench_sprite(name, base_tex, border, decal):
    n = 64
    im = tile_tex(load(base_tex, 32), n)
    im = frame(im, 0.68, 1.15)
    b = hexc(border)
    for i in range(n):
        im.putpixel((i, 2), b); im.putpixel((i, n - 3), shade(b, 0.7))
        im.putpixel((2, i), b); im.putpixel((n - 3, i), shade(b, 0.7))
    d = decal.resize((36, 36), Image.NEAREST)
    im.alpha_composite(d, (14, 14))
    save(im, name)


def gen_machines():
    drill_sprite("kuangjing", "#b9c2cc", "#c07a3e")
    drill_sprite("shencengkuangjing", "#d6b35a", "#8fd7e8")
    drill_sprite("zuanshikuangjing", "#6cf0dd", "#4ce0ff")
    drill_sprite("xiajiekuangjing", "#7a6a78", "#b06cf0")

    # 伐木场 (wall crafter): wooden frame + saw
    n = 64
    im = tile_tex(load("bricks1.png", 32), n)
    im = frame(im, 0.62, 1.2)
    save(im, "famuchang")
    top = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    for x in range(14, 50):
        top.putpixel((x, 30), hexc("#8a6238"))
        top.putpixel((x, 33), hexc("#6b4a2a"))
    save(top, "famuchang-top")
    saw = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for r in range(11, 15):
        for a in range(0, 720, 3):
            x = int(16 + r * math.cos(math.radians(a)))
            y = int(16 + r * math.sin(math.radians(a)))
            if 0 <= x < 32 and 0 <= y < 32:
                saw.putpixel((x, y), hexc("#d8dde4") if a % 9 else hexc("#9aa1aa"))
    for a in range(0, 360, 45):
        x = int(16 + 6 * math.cos(math.radians(a)))
        y = int(16 + 6 * math.sin(math.radians(a)))
        saw.putpixel((x, y), hexc("#6d757f"))
    save(saw, "famuchang-rotator")
    save(Image.new("RGBA", (32, 32), (0, 0, 0, 0)), "famuchang-rotator-bottom")

    furnace_sprite("ronglu_tong", "#c07a3e")
    furnace_sprite("ronglu_tie", "#c8ced6")
    furnace_sprite("ronglu_jin", "#e8c53a")
    furnace_sprite("ronglu_boli", "#a8e6e0")
    furnace_sprite("ronglu_shitou", "#9a9a9a")
    furnace_sprite("gaolu", "#7a5aa8")
    furnace_sprite("duanzaolu", "#b06cf0")

    # crafting tables
    save(frame(load("crafting_table_top1.png"), 0.7, 1.12), "gongzuotai")
    tier_sword = {}
    for tier in TIERS:
        tier_sword[tier] = tool_icon("jian", tier).resize((32, 32), Image.NEAREST)
    workbench_sprite("gongjiangtai_mu", "muban", TIERS["mu"][0], tier_sword["mu"])
    workbench_sprite("gongjiangtai_shi", "muban", TIERS["shi"][0], tier_sword["shi"])
    workbench_sprite("gongjiangtai_tie", "muban", TIERS["tie"][0], tier_sword["tie"])
    workbench_sprite("gongjiangtai_jin", "muban", TIERS["jin"][0], tier_sword["jin"])
    workbench_sprite("gongjiangtai_zuanshi", "muban", TIERS["zuanshi"][0], tier_sword["zuanshi"])
    workbench_sprite("gongjiangtai_xiajiehejin", "muban", TIERS["xiajiehejin"][0], tier_sword["xiajiehejin"])

    # storage chest
    n = 64
    chest = Image.new("RGBA", (n, n))
    for y in range(n):
        for x in range(n):
            v = 0.95 + 0.08 * (((x * 3 + y * 5) % 6) / 5.0)
            chest.putpixel((x, y), shade(hexc("#a3764b"), v))
    for x in range(n):
        chest.putpixel((x, 26), hexc("#6b4a2a")); chest.putpixel((x, 27), hexc("#4a3018"))
    for y in range(n):
        chest.putpixel((28, y), hexc("#5c6570")); chest.putpixel((29, y), hexc("#7c8794"))
        chest.putpixel((34, y), hexc("#5c6570")); chest.putpixel((35, y), hexc("#7c8794"))
    for y in range(28, 38):
        for x in range(28, 36):
            chest.putpixel((x, y), hexc("#e0c040"))
    chest = frame(chest, 0.6, 1.15)
    save(chest, "cangku")

    # redstone blocks
    rep = load("stone1.png").copy()
    rep = frame(rep, 0.7, 1.15)
    for y in range(10, 22):
        for x in range(10, 22):
            rep.putpixel((x, y), hexc("#6b3a1f") if (x + y) % 4 else hexc("#d8b070"))
    for x in (13, 18):
        rep.putpixel((x, 8), hexc("#ffe9b0")); rep.putpixel((x, 9), hexc("#ffcf60"))
    rep.putpixel((15, 16), hexc("#ff3b2f")); rep.putpixel((16, 16), hexc("#ff3b2f"))
    save(rep, "hongshizhongjiqi")

    rb = noise_tex([hexc("#e02020"), hexc("#c01818"), hexc("#ff3a3a"), hexc("#a01010")])
    rb = frame(rb, 0.6, 1.2)
    for i in range(4, 28, 6):
        for j in range(4, 28, 6):
            if (i * j) % 5 == 0:
                rb.putpixel((i, j), hexc("#ff7a5a"))
    save(rb, "hongshikuai")

    # battery top overlay (drawn unguarded by DrawRegion("-top"))
    top_rb = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for i in range(10, 22):
        for j in range(10, 22):
            top_rb.putpixel((i, j), hexc("#3a0d0d") if (i + j) % 3 else hexc("#ff5a3a"))
    for i in (12, 15, 18, 21):
        top_rb.putpixel((i, 9), hexc("#ffd0a0")); top_rb.putpixel((i, 8), hexc("#ff8a5a"))
    save(top_rb, "hongshikuai-top")

    torch = frame(load("stone1.png").copy(), 0.6, 1.15)
    # stick
    for y in range(18, 30):
        torch.putpixel((15, y), hexc("#6b4a2a")); torch.putpixel((16, y), hexc("#8a6238"))
    for y in range(6, 18):
        for x in range(13, 19):
            d = abs(x - 15.5) / 2.6 + (y - 12) / 7.0
            if abs(x - 15.5) < 2.5 and 7 <= y < 18:
                torch.putpixel((x, y), hexc("#ff2b2b") if d > -0.2 else hexc("#ff6a3a"))
    for x in range(13, 19):
        torch.putpixel((x, 6), hexc("#ffd0a0") if x in (15, 16) else hexc("#ff4a2a"))
    save(torch, "hongshihuoba")

    # friendly mob farms
    farm_sprite("jishe", "chicken")
    farm_sprite("niushe", "cow")
    farm_sprite("yangjuan", "sheep")
    farm_sprite("zhujuan", "pig")


def farm_sprite(name, animal):
    n = 64
    im = tile_tex(load("bricks1.png", 32), n)
    im = frame(im, 0.62, 1.18)
    # white fence border
    for i in range(n):
        im.putpixel((i, 3), hexc("#e8e2d0")); im.putpixel((i, n - 4), hexc("#c8c2b0"))
        im.putpixel((3, i), hexc("#e8e2d0")); im.putpixel((n - 4, i), hexc("#c8c2b0"))
    a = animal_sprite(animal).resize((36, 36), Image.NEAREST)
    im.alpha_composite(a, (14, 14))
    save(im, name)


# ----------------------------------------------------------------------------
# logistics + unit factory
# ----------------------------------------------------------------------------

def conveyor_sprite(index: int, frame: int) -> Image.Image:
    n = 32
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    wood, woodd = hexc("#b07a45"), hexc("#7a5230")
    rail, raild = hexc("#6d757f"), hexc("#454b52")

    def hband(x0, x1):
        for y in range(9, 23):
            for x in range(x0, x1):
                im.putpixel((x, y), wood if ((x + frame * 8) // 5) % 2 else woodd)

    def vband(y0, y1):
        for y in range(y0, y1):
            for x in range(9, 23):
                im.putpixel((x, y), wood if ((y + frame * 8) // 5) % 2 else woodd)

    def hrails(x0, x1):
        for x in range(x0, x1):
            im.putpixel((x, 7), rail); im.putpixel((x, 8), rail)
            im.putpixel((x, 23), raild); im.putpixel((x, 24), raild)

    def vrails(y0, y1):
        for y in range(y0, y1):
            im.putpixel((7, y), rail); im.putpixel((8, y), rail)
            im.putpixel((23, y), raild); im.putpixel((24, y), raild)

    hband(0, 32)
    hrails(0, 32)
    if index == 3:
        vband(0, 32); vrails(0, 32)
    elif index == 4:
        vband(0, 9); vrails(0, 9)
    elif index == 1:
        vband(0, 16); vrails(0, 16)
    elif index == 2:
        vband(16, 32); vrails(16, 32)
    return im


def gen_logistics():
    save(conveyor_sprite(0, 0), "chuansongdai")
    for b in range(7):
        for f in range(4):
            save(conveyor_sprite(b if b < 5 else 0, f), f"chuansongdai-{b}-{f}")

    # 漏斗：木箱 + 金属边 + 中央漏斗
    hop = Image.new("RGBA", (32, 32))
    for y in range(32):
        for x in range(32):
            hop.putpixel((x, y), hexc("#b07a45") if (x + y) % 5 else hexc("#8a6238"))
    for i in range(32):
        hop.putpixel((i, 0), hexc("#5c6570")); hop.putpixel((i, 31), hexc("#5c6570"))
        hop.putpixel((0, i), hexc("#5c6570")); hop.putpixel((31, i), hexc("#5c6570"))
    for y in range(10, 22):
        for x in range(10, 22):
            hop.putpixel((x, y), hexc("#2a2018") if (x + y) % 4 else hexc("#4a3524"))
    for y in range(3, 6):
        for x in range(3, 29):
            hop.putpixel((x, y), hexc("#7c8794"))
    for y in range(26, 29):
        for x in range(3, 29):
            hop.putpixel((x, y), hexc("#4a5058"))
    save(frame(hop, 0.7, 1.15), "loudou")

    center = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    # 四个指向中心的箭头
    for (sx, sy, dx, dy) in ((16, 4, 0, 1), (16, 27, 0, -1), (4, 16, 1, 0), (27, 16, -1, 0)):
        for k in range(8):
            _px(center, sx + dx * k, sy + dy * k, "#ffd84a")
    for (x, y) in ((14, 12), (17, 12), (14, 19), (17, 19)):
        for dx in range(2):
            for dy in range(2):
                _px(center, x + dx, y + dy, "#ffe9a0")
    save(center, "loudou-center")

    # 路由器：四向箭头
    router = Image.new("RGBA", (32, 32))
    for y in range(32):
        for x in range(32):
            router.putpixel((x, y), hexc("#9a9a9a") if (x * 3 + y * 5) % 4 else hexc("#7c7c7c"))
    for i in range(32):
        router.putpixel((i, 0), hexc("#5c5c5c")); router.putpixel((i, 31), hexc("#5c5c5c"))
        router.putpixel((0, i), hexc("#5c5c5c")); router.putpixel((31, i), hexc("#5c5c5c"))
    for (x0, y0, x1, y1) in ((6, 16, 12, 16), (26, 16, 20, 16), (16, 6, 16, 12), (16, 26, 16, 20)):
        line(_to_px(router), x0, y0, x1, y1, hexc("#e8c53a"))
    for (x, y) in ((13, 16), (19, 16), (16, 13), (16, 19)):
        router.putpixel((x, y), hexc("#ffe9a0"))
    save(frame(router, 0.7, 1.15), "luyouqi")

    # 分类器
    sorter = router.copy()
    for (x, y) in ((8, 8), (24, 8), (8, 24), (24, 24)):
        for dx in range(3):
            for dy in range(3):
                sorter.putpixel((x + dx, y + dy), hexc("#d8d8d8"))
    save(frame(sorter, 0.7, 1.15), "fenleiqi")
    cross = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for x in range(32):
        cross.putpixel((x, 14), hexc("#e8e8e8")); cross.putpixel((x, 15), hexc("#2a2018"))
        cross.putpixel((x, 16), hexc("#2a2018")); cross.putpixel((x, 17), hexc("#e8e8e8"))
    for y in range(32):
        cross.putpixel((14, y), hexc("#e8e8e8")); cross.putpixel((15, y), hexc("#2a2018"))
        cross.putpixel((16, y), hexc("#2a2018")); cross.putpixel((17, y), hexc("#e8e8e8"))
    save(cross, "fenleiqi-cross")

    # 交叉器：两条传送带交叉
    junc = conveyor_sprite(3, 0).copy()
    for i in range(32):
        junc.putpixel((i, 0), hexc("#5c6570")); junc.putpixel((i, 31), hexc("#5c6570"))
        junc.putpixel((0, i), hexc("#5c6570")); junc.putpixel((31, i), hexc("#5c6570"))
    save(junc, "jiaochaqi")

    # 物流节点
    node = Image.new("RGBA", (32, 32))
    for y in range(32):
        for x in range(32):
            node.putpixel((x, y), hexc("#6b6f76") if (x * 5 + y * 3) % 6 else hexc("#54585e"))
    for (x0, y0, x1, y1) in ((6, 6, 26, 26),):
        for x in range(x0, x1):
            node.putpixel((x, y0), hexc("#8a6238")); node.putpixel((x, y1), hexc("#5b4227"))
        for y in range(y0, y1):
            node.putpixel((x0, y), hexc("#8a6238")); node.putpixel((x1, y), hexc("#5b4227"))
    for y in range(13, 19):
        for x in range(13, 19):
            node.putpixel((x, y), hexc("#e8c53a") if (x + y) % 3 else hexc("#b08a20"))
    save(frame(node, 0.7, 1.15), "wuliujiedian")

    end = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for y in range(8, 24):
        for x in range(8, 24):
            end.putpixel((x, y), hexc("#8a6238") if (x + y) % 4 else hexc("#5b4227"))
    for y in range(12, 20):
        for x in range(12, 20):
            end.putpixel((x, y), hexc("#2a2018"))
    for i in range(12, 20):
        end.putpixel((i, 12), hexc("#e8c53a")); end.putpixel((i, 19), hexc("#e8c53a"))
    save(end, "wuliujiedian-end")

    bridge = Image.new("RGBA", (32, 8))
    for y in range(8):
        for x in range(32):
            bridge.putpixel((x, y), hexc("#a3764b") if y in (2, 3) else hexc("#5b4227"))
    save(bridge, "wuliujiedian-bridge")

    arrow = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    # 向右的箭头（箭头指向 +X）
    for y in range(13, 19):
        for x in range(4, 20):
            arrow.putpixel((x, y), hexc("#ffd84a"))
    for k in range(5):
        for y in range(13 - k, 19 + k):
            if 0 <= y < 32:
                arrow.putpixel((20 + k, y), hexc("#ffd84a"))
    for y in range(14, 18):
        for x in range(8, 18):
            arrow.putpixel((x, y), hexc("#fff3b0"))
    save(arrow, "wuliujiedian-arrow")


def gen_factory():
    n = 96
    im = metal_base(n, "#5c5650", "#37332f", "#8a8078")
    for (ox, oy) in ((8, 8), (n - 32, 8), (8, n - 32), (n - 32, n - 32)):
        for y in range(oy, oy + 24):
            for x in range(ox, ox + 24):
                im.putpixel((x, y), hexc("#8a8078") if (x + y) % 7 else hexc("#6b635c"))
    for y in range(36, 60):
        for x in range(36, 60):
            im.putpixel((x, y), hexc("#c07a3e") if (x + y) % 5 else hexc("#e8c53a"))
    save(frame(im, 0.65, 1.12), "binggongchang")

    top = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    # 屋顶
    for y in range(8, 42):
        for x in range(8, n - 8):
            top.putpixel((x, y), hexc("#a3764b") if (x // 6 + y // 6) % 2 else hexc("#7a5230"))
    for x in range(8, n - 8):
        top.putpixel((x, 8), hexc("#5b4227")); top.putpixel((x, 41), hexc("#5b4227"))
    for y in range(8, 42):
        top.putpixel((8, y), hexc("#5b4227")); top.putpixel((n - 9, y), hexc("#5b4227"))
    # 中央天窗
    for y in range(18, 32):
        for x in range(40, 56):
            top.putpixel((x, y), hexc("#e8c53a") if (x + y) % 3 else hexc("#b08a20"))
    # 吊臂
    for y in range(42, 68):
        for x in range(46, 50):
            top.putpixel((x, y), hexc("#6d757f"))
    for x in range(26, 74):
        for y in range(64, 68):
            top.putpixel((x, y), hexc("#8a8078"))
    for y in range(68, 76):
        top.putpixel((29, y), hexc("#454b52")); top.putpixel((30, y), hexc("#454b52"))
    save(top, "binggongchang-top")

    out = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    # 出厂闸门
    for y in range(n - 34, n - 8):
        for x in range(30, 66):
            out.putpixel((x, y), hexc("#2a2018"))
    for x in range(28, 68):
        out.putpixel((x, n - 36), hexc("#e8c53a")); out.putpixel((x, n - 6), hexc("#e8c53a"))
    for y in range(n - 36, n - 6):
        out.putpixel((28, y), hexc("#e8c53a")); out.putpixel((67, y), hexc("#e8c53a"))
    # 箭头
    for y in range(n - 30, n - 22):
        for x in range(40, 56):
            out.putpixel((x, y), hexc("#ffd84a"))
    for k in range(7):
        for y in range(n - 30 - k, n - 22 + k):
            out.putpixel((56 + k, y), hexc("#ffd84a"))
    save(out, "binggongchang-out")

    inp = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    for y in range(10, 30):
        for x in range(32, 64):
            inp.putpixel((x, y), hexc("#6b4a2a"))
    for x in range(32, 64):
        inp.putpixel((x, 10), hexc("#a3764b")); inp.putpixel((x, 29), hexc("#4a3018"))
    for y in range(10, 30):
        inp.putpixel((32, y), hexc("#a3764b")); inp.putpixel((63, y), hexc("#4a3018"))
    for x in range(42, 54):
        inp.putpixel((x, 18), hexc("#e8c53a")); inp.putpixel((x, 21), hexc("#e8c53a"))
    save(inp, "binggongchang-in")


# ----------------------------------------------------------------------------
# animals and mobs
# ----------------------------------------------------------------------------

def _px(im, x, y, color):
    if 0 <= x < im.width and 0 <= y < im.height:
        im.putpixel((x, y), color if isinstance(color, tuple) else hexc(color))


def animal_sprite(kind: str) -> Image.Image:
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    def rect(x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                _px(im, x, y, c)
    if kind == "chicken":
        rect(4, 7, 11, 12, "#f2f2f2")
        rect(5, 3, 10, 7, "#f7f7f7")
        rect(6, 2, 7, 3, "#d02020"); rect(8, 2, 9, 3, "#d02020")
        rect(7, 5, 8, 6, "#e0a020")
        _px(im, 6, 5, "#202020"); _px(im, 9, 5, "#202020")
        rect(6, 13, 6, 15, "#e0a020"); rect(9, 13, 9, 15, "#e0a020")
        rect(4, 8, 4, 11, "#e8e8e8"); rect(11, 8, 11, 11, "#e8e8e8")
    elif kind == "cow":
        rect(3, 6, 12, 12, "#8a5a3a")
        rect(5, 2, 10, 6, "#8a5a3a")
        rect(4, 7, 6, 9, "#efeae0"); rect(9, 10, 11, 12, "#efeae0")
        rect(4, 1, 4, 3, "#e8e2d0"); rect(11, 1, 11, 3, "#e8e2d0")
        _px(im, 6, 4, "#202020"); _px(im, 9, 4, "#202020")
        rect(5, 13, 5, 15, "#6b4a2a"); rect(10, 13, 10, 15, "#6b4a2a")
        _px(im, 7, 5, "#c06060"); _px(im, 8, 5, "#c06060")
    elif kind == "sheep":
        rect(3, 6, 12, 12, "#efefef")
        for (x, y) in ((4, 7), (6, 9), (9, 7), (11, 10), (5, 11), (10, 8)):
            rect(x, y, x + 1, y + 1, "#d8d8d8")
        rect(5, 2, 10, 6, "#e8e2d0")
        rect(6, 3, 9, 5, "#3a3a3a")
        _px(im, 6, 4, "#efefef"); _px(im, 9, 4, "#efefef")
        rect(5, 13, 5, 15, "#3a3a3a"); rect(10, 13, 10, 15, "#3a3a3a")
    elif kind == "pig":
        rect(3, 6, 12, 12, "#e8a0a8")
        rect(5, 2, 10, 6, "#f0b0b8")
        rect(6, 4, 9, 6, "#d88a94")
        _px(im, 7, 5, "#a05058"); _px(im, 8, 5, "#a05058")
        _px(im, 6, 3, "#202020"); _px(im, 9, 3, "#202020")
        rect(5, 13, 5, 15, "#c8808a"); rect(10, 13, 10, 15, "#c8808a")
    return im


def mob_sprite(kind: str) -> Image.Image:
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    def rect(x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                _px(im, x, y, c)
    if kind == "zombie":
        rect(5, 1, 10, 6, "#4a7a3a")
        rect(6, 3, 6, 3, "#101010"); rect(9, 3, 9, 3, "#101010")
        rect(7, 5, 8, 5, "#2a4a22")
        rect(5, 7, 10, 11, "#2f4a8a")
        rect(3, 7, 4, 10, "#4a7a3a"); rect(11, 7, 12, 10, "#4a7a3a")
        rect(5, 12, 7, 15, "#253a6a"); rect(9, 12, 11, 15, "#253a6a")
    elif kind == "kulou":
        rect(5, 1, 10, 6, "#d8d8d8")
        rect(6, 3, 6, 3, "#101010"); rect(9, 3, 9, 3, "#101010")
        rect(7, 5, 8, 5, "#909090")
        rect(6, 7, 9, 10, "#c8c8c8")
        for y in range(7, 11):
            rect(7, y, 7, y, "#909090"); rect(8, y, 8, y, "#e8e8e8")
        rect(3, 7, 4, 10, "#c8c8c8"); rect(11, 7, 12, 10, "#c8c8c8")
        rect(5, 12, 6, 15, "#c8c8c8"); rect(9, 12, 10, 15, "#c8c8c8")
    elif kind == "kulipa":
        rect(4, 1, 11, 7, "#4f9a3a")
        rect(4, 8, 11, 14, "#5aa844")
        # creeper face
        rect(5, 3, 7, 4, "#101010"); rect(8, 3, 10, 4, "#101010")
        rect(7, 5, 8, 6, "#101010"); rect(6, 6, 6, 6, "#101010"); rect(9, 6, 9, 6, "#101010")
        for (x, y) in ((4, 9), (5, 10), (9, 11), (10, 9), (6, 13), (9, 13)):
            rect(x, y, x + 1, y + 1, "#3f7a2e")
    elif kind == "zhizhu":
        rect(4, 6, 11, 12, "#1a1a1a")
        rect(6, 3, 9, 6, "#262626")
        rect(6, 6, 6, 6, "#e02020"); rect(9, 6, 9, 6, "#e02020")
        for (x0, y0, x1, y1) in ((3, 5, 1, 2), (12, 5, 14, 2), (3, 8, 1, 10), (12, 8, 14, 10),
                                  (4, 12, 2, 14), (11, 12, 13, 14), (5, 4, 4, 1), (10, 4, 11, 1)):
            line(_to_px(im), x0, y0, x1, y1, hexc("#1a1a1a"))
    elif kind == "moyingren":
        rect(6, 0, 9, 3, "#1a1024")
        rect(6, 1, 6, 1, "#c060ff"); rect(9, 1, 9, 1, "#c060ff")
        rect(5, 4, 10, 10, "#1a1024")
        rect(5, 11, 6, 15, "#141018"); rect(9, 11, 10, 15, "#141018")
        rect(3, 4, 4, 9, "#1a1024"); rect(11, 4, 12, 9, "#1a1024")
        for (x, y) in ((7, 6), (8, 8), (6, 9)):
            _px(im, x, y, "#7a3fd0")
    elif kind == "huanyi":
        # phantom: dark blue body with wide wings
        rect(6, 5, 9, 12, "#2a3f6a")
        rect(5, 3, 10, 6, "#3a5590")
        rect(1, 4, 4, 7, "#22345a"); rect(11, 4, 14, 7, "#22345a")
        rect(2, 3, 3, 4, "#2f4a80"); rect(12, 3, 13, 4, "#2f4a80")
        rect(1, 8, 3, 9, "#1c2c4e"); rect(12, 8, 14, 9, "#1c2c4e")
        _px(im, 6, 5, "#7ff0ff"); _px(im, 9, 5, "#7ff0ff")
        rect(6, 13, 9, 14, "#1c2c4e")
    elif kind == "egang":
        # ghast: pale floating body with tentacles and red eyes
        rect(3, 2, 12, 10, "#e8e8e8")
        rect(4, 1, 11, 2, "#f4f4f4")
        rect(5, 4, 6, 5, "#c02020"); rect(9, 4, 10, 5, "#c02020")
        rect(6, 7, 9, 8, "#b0b0b0")
        for x in (4, 6, 8, 10, 12):
            rect(x, 11, x, 15, "#d8d8d8")
    elif kind == "xueguilei":
        # snow golem: white body, pumpkin head, stick arms
        rect(5, 6, 10, 14, "#f2f2f2")
        rect(5, 1, 10, 5, "#e08a2a")
        rect(5, 1, 10, 1, "#c06a1a")
        rect(6, 2, 7, 3, "#202020"); rect(8, 3, 9, 4, "#202020")
        rect(7, 5, 8, 5, "#5b4227")
        line(_to_px(im), 4, 7, 2, 12, hexc("#6b4a2a"))
        line(_to_px(im), 11, 7, 13, 12, hexc("#6b4a2a"))
        rect(6, 14, 7, 15, "#d8d8d8"); rect(8, 14, 9, 15, "#d8d8d8")
    elif kind == "tieguilei":
        # iron golem: heavy grey body with red eyes and vines
        rect(4, 2, 11, 11, "#b0b0b0")
        rect(4, 2, 11, 3, "#c8c8c8")
        rect(5, 4, 5, 5, "#d02020"); rect(10, 4, 10, 5, "#d02020")
        rect(6, 7, 9, 8, "#8a8a8a")
        rect(2, 3, 3, 10, "#9a9a9a"); rect(12, 3, 13, 10, "#9a9a9a")
        rect(5, 11, 6, 15, "#9a9a9a"); rect(9, 11, 10, 15, "#9a9a9a")
        for (x, y) in ((5, 8), (10, 10), (7, 12), (9, 4)):
            _px(im, x, y, "#3f7a2e")
            _px(im, x + 1, y + 1, "#2f5e22")
    return im


def _to_px(im):
    return [[im.getpixel((x, y)) for y in range(im.height)] for x in range(im.width)]


def gen_units():
    for kind, name in (("zombie", "jiangshi"), ("kulou", "kulou"), ("kulipa", "kulipa"),
                       ("zhizhu", "zhizhu"), ("moyingren", "moyingren"),
                       ("huanyi", "huanyi"), ("egang", "egang"),
                       ("xueguilei", "xueguilei"), ("tieguilei", "tieguilei")):
        im = mob_sprite(kind).resize((32, 32), Image.NEAREST)
        im.save(os.path.join(OUT, name + ".png"))
        alpha = im.split()[3].filter(ImageFilter.MaxFilter(5))
        outline = Image.new("RGBA", im.size, (0, 0, 0, 0))
        outline.putalpha(alpha)
        outline.save(os.path.join(OUT, name + "-outline.png"))


# ----------------------------------------------------------------------------
# turrets
# ----------------------------------------------------------------------------

def turret_base(name, accent):
    n = 64
    im = metal_base(n, "#4b4f56", "#2b2f34", "#7c8794")
    # stone ring
    for y in range(n):
        for x in range(n):
            d = math.hypot(x - 31.5, y - 31.5)
            if 18 <= d <= 26:
                im.putpixel((x, y), shade(hexc("#6b6f76"), 1.0 + 0.1 * math.sin(d * 0.6)))
            elif d < 18:
                im.putpixel((x, y), hexc("#33373d"))
    a = hexc(accent)
    for i in range(n):
        im.putpixel((i, 1), a); im.putpixel((i, n - 2), shade(a, 0.7))
        im.putpixel((1, i), a); im.putpixel((n - 2, i), shade(a, 0.7))
    save(frame(im, 0.72, 1.1), name + "-base")


def heat_sprite(name):
    n = 64
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    for y in range(n):
        for x in range(n):
            d = math.hypot(x - 31.5, y - 31.5)
            if d < 30:
                a = int(150 * (1 - d / 30) ** 2)
                im.putpixel((x, y), (255, 220, 180, a))
    save(im, name + "-heat")


def vertical_sword(tier):
    base, light, dark, _ = TIERS[tier]
    base, light, dark = hexc(base), hexc(light), hexc(dark)
    hb, hl, hd = (hexc(c) for c in HANDLE)
    n = 32
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    def rect(x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                _px(im, x, y, c)
    rect(14, 3, 15, 18, light)
    rect(16, 3, 17, 19, base)
    rect(18, 4, 18, 19, dark)
    rect(15, 1, 16, 2, light); rect(17, 2, 17, 2, light)
    rect(10, 20, 12, 21, dark); rect(13, 20, 18, 21, light); rect(19, 20, 21, 21, dark)
    rect(14, 22, 17, 28, hb)
    rect(15, 22, 15, 28, hl); rect(17, 22, 17, 28, hd)
    rect(13, 29, 18, 30, hd)
    return im


def vertical_bow(kind):
    n = 32
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    wood, wood_d, strg = hexc("#8a6238"), hexc("#5b4227"), hexc("#e8e8e8")
    if kind == "gong":
        for a in range(180, 361, 3):
            x = int(16 + 11 * math.cos(math.radians(a)))
            y = int(20 + 11 * math.sin(math.radians(a)))
            _px(im, x, y, wood)
            _px(im, x, y + 1, wood_d)
        line(_to_px(im), 5, 20, 27, 20, strg)
        # arrow
        for y in range(4, 22):
            _px(im, 16, y, hexc("#b0b0b0"))
        line(_to_px(im), 14, 6, 16, 3, hexc("#e8e8e8"))
        line(_to_px(im), 18, 6, 16, 3, hexc("#e8e8e8"))
        for y in range(17, 21):
            _px(im, 15, y, hexc("#e0e0e0")); _px(im, 17, y, hexc("#e0e0e0"))
    elif kind == "nu":
        # crossbow: stock + limbs
        for x in range(4, 28):
            _px(im, x, 22, wood); _px(im, x, 23, wood_d)
        for y in range(12, 22):
            _px(im, 16, y, wood)
        for (x0, y0, x1, y1) in ((4, 16, 14, 22), (27, 16, 17, 22)):
            line(_to_px(im), x0, y0, x1, y1, wood)
        line(_to_px(im), 4, 16, 27, 16, strg)
        for y in range(3, 20):
            _px(im, 16, y, hexc("#c8c8c8"))
        line(_to_px(im), 14, 5, 16, 2, hexc("#f0f0f0"))
        line(_to_px(im), 18, 5, 16, 2, hexc("#f0f0f0"))
    else:  # trident
        metal, metal_l = hexc("#4a4048"), hexc("#8f7a90")
        for y in range(10, 31):
            _px(im, 16, y, metal)
        for x in range(6, 27):
            _px(im, x, 12, metal_l)
        for x in (6, 16, 26):
            for y in range(3, 13):
                _px(im, x, y, metal_l if y < 6 else metal)
        for y in range(2, 5):
            _px(im, 15, y, metal_l); _px(im, 17, y, metal_l)
        _px(im, 16, 1, metal_l)
    return im


def gen_turrets():
    melee = [("mujianpaotai", "mu"), ("shijianpaotai", "shi"), ("tiejianpaotai", "tie"),
             ("jinjianpaotai", "jin"), ("zuanshijianpaotai", "zuanshi"),
             ("xiajiehejinjianpaotai", "xiajiehejin")]
    def turret(name, top_img):
        turret_base(name, "#8a6238")
        save(top_img, name)
        heat_sprite(name)
        # placement preview + shadow silhouette: base with the barrel on top
        prev = Image.open(os.path.join(OUT, name + "-base.png")).convert("RGBA")
        prev.alpha_composite(top_img)
        save(prev, name + "-preview")

    for name, tier in melee:
        top = vertical_sword(tier).resize((64, 64), Image.NEAREST)
        turret(name, top)
    for name, kind in (("gongpaotai", "gong"), ("nupaotai", "nu"), ("sanchajipaotai", "trident")):
        top = vertical_bow(kind).resize((64, 64), Image.NEAREST)
        turret(name, top)


# ----------------------------------------------------------------------------
# planet + cores
# ----------------------------------------------------------------------------

def gen_planet():
    n = 64
    im = Image.new("RGBA", (n, n), (0, 0, 0, 0))
    for y in range(n):
        for x in range(n):
            d = math.hypot(x - 31.5, y - 31.5)
            if d < 26:
                v = 0.75 + 0.25 * (1 - d / 26)
                if (x * 3 + y * 5) % 17 < 6:
                    im.putpixel((x, y), shade(hexc("#3f8f3a"), v))
                else:
                    im.putpixel((x, y), shade(hexc("#2a6fd0"), v))
            elif d < 29:
                im.putpixel((x, y), (120, 200, 255, 90))
    save(im, "fangkuai")


def gen_cores():
    for src, dst in (("初级核心.png", "chujihexing"), ("中级核心.png", "zhongdaihexing"),
                     ("最终核心.png", "zhongjihexing")):
        im = load(src)
        save(im, dst)
        # simple coloured team region so cores still show team colour
        t = Image.new("RGBA", im.size, (0, 0, 0, 0))
        w, h = im.size
        for y in range(h):
            for x in range(w):
                d = math.hypot(x - w / 2 + .5, y - h / 2 + .5)
                if w * 0.18 < d < w * 0.23:
                    t.putpixel((x, y), (255, 255, 255, 255))
        save(t, dst + "-team")


def gen_mod_icon():
    """Mod list icon: the planet sprite upscaled to 256x256."""
    im = load("fangkuai.png").resize((256, 256), Image.NEAREST)
    im.save(os.path.join(ROOT, "assets", "icon.png"))


# ----------------------------------------------------------------------------
# main
# ----------------------------------------------------------------------------

if __name__ == "__main__":
    gen_items()
    gen_environment()
    gen_machines()
    gen_logistics()
    gen_factory()
    gen_turrets()
    gen_units()
    gen_planet()
    gen_cores()
    gen_mod_icon()
    print("generated", len(os.listdir(OUT)), "sprites")
