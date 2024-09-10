package dev.murad.shipping.entity.container

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.entity.custom.HeadVehicle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import java.util.*

class EnergyHeadVehicleScreen<T>(menu: EnergyHeadVehicleContainer<T>, inventory: Inventory, title: Component) :
    AbstractHeadVehicleScreen<T, EnergyHeadVehicleContainer<T>>(
        menu,
        inventory,
        title
    ) where T : Entity, T : HeadVehicle {
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        if (inBounds(mouseX - leftPos, mouseY - topPos, 56, 17, 68, 67)) {
            graphics.renderTooltip(
                font,
                listOf<Component>(
                    Component.translatable(
                        "screen.humblevehicles.energy_tug.energy",
                        getMenu().energy,
                        getMenu().capacity
                    )
                ),
                Optional.empty(),
                mouseX, mouseY
            )
        }
    }

    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, x: Int, y: Int) {
        super.renderBg(graphics, partialTicks, x, y)

        val i = this.guiLeft
        val j = this.guiTop

        graphics.blit(GUI, i, j, 0, 0, this.xSize, this.ySize)
        val r = menu.energyCapacityRatio
        val k = (r * 50).toInt()
        graphics.blit(GUI, i + 56, j + 17 + 50 - k, 176, 50 - k, 12, k)
    }

    companion object {
        private val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/energy_locomotive.png")
    }
}
