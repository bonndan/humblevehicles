package com.github.bonndan.humblevehicles.entity.container

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.HeadVehicle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.guiTextured
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

        graphics.blit(RenderType::guiTextured, GUI, i, j, 0f, 0f, this.xSize, this.ySize, 256, 256)
        if (menu.isLit) {
            val k = menu.getBurnProgress()
            graphics.blit(RenderType::guiTextured, GUI, i + 43, j + 23 + 12 - k, 176f, 12f - k, 14, k + 1, 256,256)
        }
    }


    companion object {
        private val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/steam_locomotive.png")
    }
}
