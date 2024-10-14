package com.github.bonndan.humblevehicles.entity.container

import com.mojang.blaze3d.systems.RenderSystem
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.entity.custom.HeadVehicle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory

abstract class AbstractHeadVehicleScreen<U, T : AbstractHeadVehicleContainer<U>>(
    menu: T,
    inventory: Inventory,
    component: Component
) :
    AbstractVehicleScreen<T>(menu, inventory, component) where U : Entity, U : HeadVehicle {

    private lateinit var on: Button
    private lateinit var off: Button
    private lateinit var register: Button

    private fun tooltipOf(translatableString: String): Tooltip {
        return Tooltip.create(Component.translatable(translatableString))
    }

    override fun init() {
        super.init()
        on = Button.Builder(Component.literal("->")) { menu.setEngineState(true) }
            .pos(this.guiLeft + 130, this.guiTop + 25)
            .size(20, 20)
            .tooltip(tooltipOf("screen.humblevehicles.locomotive.on"))
            .build()

        off = Button.Builder(Component.literal("x")) { menu.setEngineState(false) }
            .pos(this.guiLeft + 96, this.guiTop + 25)
            .size(20, 20)
            .tooltip(tooltipOf("screen.humblevehicles.locomotive.off"))
            .build()

        register = Button.Builder(Component.translatable("screen.humblevehicles.locomotive.register")) { menu.enroll() }
            .pos(this.guiLeft + 181, this.guiTop + 20)
            .size(77, 20)
            .tooltip(tooltipOf("screen.humblevehicles.locomotive.register"))
            .build()

        this.addRenderableWidget(off)
        this.addRenderableWidget(on)
        this.addRenderableWidget(register)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)

        graphics.drawString(
            font, Component.translatable("screen.humblevehicles.locomotive.route"),
            this.guiLeft + 120,
            this.guiTop + 55, 4210752, false
        )
        graphics.drawString(
            font, Component.translatable("screen.humblevehicles.locomotive.registration"),
            this.guiLeft + 180,
            this.guiTop + 5, 16777215
        )

        val text = font.split(Component.translatable("screen.humblevehicles.locomotive.register_info"), 90)
        for (i in text.indices) {
            graphics.drawString(
                font,
                text[i], this.guiLeft + 180, this.guiTop + 48 + i * 10, 16777215
            )
        }

        if (!menu.canMove()) {
            val frozen = font.split(Component.translatable("screen.humblevehicles.locomotive.frozen"), 90)
            for (i in frozen.indices) {
                graphics.drawString(
                    font,
                    frozen[i], this.guiLeft + 180, this.guiTop + 98 + i * 10, 16777215
                )
            }
        }

        graphics.drawString(font, menu.routeText, this.guiLeft + 120, this.guiTop + 65, 4210752, false)
    }

    override fun renderBg(graphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
        RenderSystem.setShaderTexture(0, REGISTRATION)
        val i = this.guiLeft + 175
        val j = this.guiTop
        graphics.blit(REGISTRATION, i, j, 0, 0, this.xSize, this.ySize)
        off.active = menu.isOn
        on.active = !menu.isOn
        register.active = menu.owner == ""
        if (!register.active) {
            register.message = Component.literal(menu.owner)
        }
    }

    companion object {
        private val REGISTRATION =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/vehicle_registration.png")
    }
}
