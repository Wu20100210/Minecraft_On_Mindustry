package Minecraft_On_Mindustry

import mindustry.mod.*

class Main : Mod(){
    override fun loadContent() {
        Items.load()
        Cores.load()
        Floors.load()
        //Xingqiu.load()
    }
}
