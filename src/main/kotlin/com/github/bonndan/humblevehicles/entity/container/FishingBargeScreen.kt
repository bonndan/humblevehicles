package com.github.bonndan.humblevehicles.entity.container

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class FishingBargeScreen(menu: FishingBargeContainer, inventory: Inventory, title: Component) :
    AbstractContainerScreen<FishingBargeContainer?>(menu, inventory, title) {

    private val containerRows = 3

    init {
        this.imageHeight = 114 + this.containerRows * 18
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun render(graphics: GuiGraphics, x: Int, y: Int, partialTicks: Float) {
        this.renderBackground(graphics, x, y, partialTicks)
        super.render(graphics, x, y, partialTicks)
        this.renderTooltip(graphics, x, y)
    }

    override fun renderBg(graphics: GuiGraphics, p_230450_2_: Float, p_230450_3_: Int, p_230450_4_: Int) {
        val i = (this.width - this.imageWidth) / 2
        val j = (this.height - this.imageHeight) / 2

        graphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17)
        graphics.blit(
            CONTAINER_BACKGROUND, i, j + this.containerRows * 18 + 17, 0, 126,
            this.imageWidth, 96
        )
    }

    companion object {
        private val CONTAINER_BACKGROUND = ResourceLocation.parse("textures/gui/container/generic_54.png")
    }
}
