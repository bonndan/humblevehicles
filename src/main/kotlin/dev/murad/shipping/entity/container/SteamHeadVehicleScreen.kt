package dev.murad.shipping.entity.container

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.HeadVehicle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory

class SteamHeadVehicleScreen<T>(menu: SteamHeadVehicleContainer<T>, inventory: Inventory, component: Component) :
    AbstractHeadVehicleScreen<T, SteamHeadVehicleContainer<T>>(
        menu,
        inventory,
        component
    ) where T : Entity, T : HeadVehicle {

    override fun renderBg(graphics: GuiGraphics, pPartialTick: Float, x: Int, y: Int) {

        super.renderBg(graphics, pPartialTick, x, y)

        val i = this.guiLeft
        val j = this.guiTop

        graphics.blit(GUI, i, j, 0, 0, this.xSize, this.ySize)
        if (menu.isLit) {
            val k = menu.getBurnProgress()
            graphics.blit(GUI, i + 43, j + 23 + 12 - k, 176, 12 - k, 14, k + 1)
        }
    }


    companion object {
        private val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/steam_locomotive.png")
    }
}
