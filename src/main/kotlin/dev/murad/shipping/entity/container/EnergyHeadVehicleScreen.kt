package dev.murad.shipping.entity.container

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.HeadVehicle
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
        val k = (remainingPercent / 100 * METER_HEIGHT)
        graphics.blit(GUI, guiLeft + 56, guiTop + 67 - k, 176, METER_HEIGHT - k, 12, k)
    }

    companion object {
        private val GUI =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/energy_locomotive.png")
    }
}
