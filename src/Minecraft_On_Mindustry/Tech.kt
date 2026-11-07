package Minecraft_On_Mindustry

import mindustry.content.TechTree

/**
 * 按 drawio 科技树文件的结构重排。
 *
 * 风格：
 * - 核心在顶部，向下是“工作台”总枢纽；
 * - 工作台直接分出多条平行支线：工匠台链、剑/弓炮台链、矿井链、熔炉链、物流链、伐木场；
 * - 每条支线内部是按建筑等级排列的线性升级 链；
 * - 核心升级与区域节点独立成链。
 */
object Tech {
    fun load() {
        val root = TechTree.nodeRoot("mc-on-mindustry-root", Cores.chujihexing, false) {
            TechTree.context()!!.planet = Xingqiu.fangkuai

            // 核心升级：初级 → 中级 → 最终
            TechTree.node(Cores.zhongdaihexing) {
                TechTree.node(Cores.zhongjihexing) {}
            }

            // 区域：初级区域 → 森林 → 下界
            TechTree.node(Xingqiu.chudi) {
                TechTree.node(Xingqiu.senlin) {
                    TechTree.node(Xingqiu.xiajie) {}
                }
            }

            // 工作台：整个科技树的总枢纽
            TechTree.node(Machines.gongzuotai) {

                // 工匠台链：木 → 石 → 铁 → 金 → 钻石 → 合金
                TechTree.node(Machines.gongjiangtai_mu) {
                    TechTree.node(Machines.gongjiangtai_shi) {
                        TechTree.node(Machines.gongjiangtai_tie) {
                            TechTree.node(Machines.gongjiangtai_jin) {
                                TechTree.node(Machines.gongjiangtai_zuanshi) {
                                    TechTree.node(Machines.gongjiangtai_xiajiehejin) {}
                                }
                            }
                        }
                    }
                }

                // 伐木场
                TechTree.node(Machines.famuchang) {}

                // 红石电力：工作台的直接子分支
                TechTree.node(Machines.hongshizhongjiqi) {
                    TechTree.node(Machines.hongshihuoba) {}
                    TechTree.node(Machines.hongshikuai) {}
                }

                // 四种生物工厂：工作台的直接子分支
                TechTree.node(Machines.jishe) {
                    TechTree.node(Machines.niushe) {
                        TechTree.node(Machines.yangjuan) {
                            TechTree.node(Machines.zhujuan) {}
                        }
                    }
                }

                // 兵工厂：工作台的直接子分支
                TechTree.node(Machines.binggongchang) {}

                // 近战炮台链：木 → 石 → 铁 → 金 → 钻石 → 合金
                TechTree.node(Weapons.mujianpaotai) {
                    TechTree.node(Weapons.shijianpaotai) {
                        TechTree.node(Weapons.tiejianpaotai) {
                            TechTree.node(Weapons.jinjianpaotai) {
                                TechTree.node(Weapons.zuanshijianpaotai) {
                                    TechTree.node(Weapons.xiajiehejinjianpaotai) {}
                                }
                            }
                        }
                    }
                    // 远程炮台链：弓 → 弩 → 三叉戟
                    TechTree.node(Weapons.gongpaotai) {
                        TechTree.node(Weapons.nupaotai) {
                            TechTree.node(Weapons.sanchajipaotai) {}
                        }
                    }
                }

                // 矿井链：矿井 → 深层矿井 → 钻石矿井 → 下界矿井
                TechTree.node(Machines.kuangjing) {
                    TechTree.node(Machines.shencengkuangjing) {
                        TechTree.node(Machines.zuanshikuangjing) {
                            TechTree.node(Machines.xiajiekuangjing) {}
                        }
                    }
                }

                // 熔炉链：石头 → 铜 → 铁 → 金 → 高炉 → 锻造炉；玻璃旁支
                TechTree.node(Machines.ronglu_shitou) {
                    TechTree.node(Machines.ronglu_boli) {}
                    TechTree.node(Machines.ronglu_tong) {
                        TechTree.node(Machines.ronglu_tie) {
                            TechTree.node(Machines.ronglu_jin) {
                                TechTree.node(Machines.gaolu) {
                                    TechTree.node(Machines.duanzaolu) {}
                                }
                            }
                        }
                    }
                }

                // 物流链：传送带 → 物流节点 / 路由器 → 交叉器 → 分类器 / 仓库
                TechTree.node(Machines.chuansongdai) {
                    TechTree.node(Machines.wuliujiedian) {
                        TechTree.node(Machines.loudou) {}
                    }
                    TechTree.node(Machines.luyouqi) {
                        TechTree.node(Machines.jiaochaqi) {
                            TechTree.node(Machines.fenleiqi) {}
                            TechTree.node(Machines.cangku) {}
                        }
                    }
                }
            }
        }

        Xingqiu.fangkuai.techTree = root
    }
}
