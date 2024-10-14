package com.github.bonndan.humblevehicles.entity.container

import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.HeadVehicle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory

private const val METER_HEIGHT = 50

class EnergyHeadVehicleScreen<T>(menu: EnergyHeadVehicleContainer<T>, inventory: Inventory, title: Component) :
    AbstractHeadVehicleScreen<T, EnergyHeadVehicleContainer<T>>(
        menu,
        inventory,
        title
    ) where T : Entity, T : HeadVehicle {

    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, x: Int, y: Int) {
        super.renderBg(graphics, partialTicks, x, y)

        graphics.blit(GUI, guiLeft, guiTop, 0, 0, this.xSize, this.ySize)

        val remainingPercent = menu.getBurnProgress()
        val meterSize: Int = (remainingPercent / 100.0f * METER_HEIGHT).toInt()
        graphics.blit(GUI, guiLeft + 56, guiTop + 67 - meterSize, 176, METER_HEIGHT - meterSize, 12, meterSize)
    }

    companion object {
        private val GUI =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/energy_locomotive.png")
    }
}
