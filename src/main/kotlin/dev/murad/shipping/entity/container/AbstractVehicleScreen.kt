package dev.murad.shipping.entity.container

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

abstract class AbstractVehicleScreen<T : AbstractItemHandlerContainer?>(
    menu: T,
    inventory: Inventory,
    p_i51105_3_: Component
) :
    AbstractContainerScreen<T>(menu, inventory, p_i51105_3_) {
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    protected fun inBounds(mouseX: Int, mouseY: Int, x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
        return (mouseX >= x1) && (mouseX < x2) && (mouseY >= y1) && (mouseY < y2)
    }

}