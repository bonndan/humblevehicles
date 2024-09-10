package dev.murad.shipping.item.container

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.util.TugRouteNode
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Consumer

class StringInputScreen(node: TugRouteNode, index: Int, private val callback: Consumer<String?>) :
    Screen(Component.translatable("screen.humblevehicles.tug_route.rename", node.getDisplayName(index))) {
    private var text: String?
    private var textFieldWidget: EditBox? = null

    init {
        this.text = if (node.hasCustomName()) node.name else ""
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun init() {
        super.init()

        LOGGER.info("Initializing StringInputScreen")

        val w = 156
        val h = 65
        val left = (this.width - w) / 2
        val top = (this.height - h) / 2

        // x, y, width, height
        this.textFieldWidget = EditBox(this.font, left + 10, top + 10, 135, 20, Component.literal(text))
        textFieldWidget!!.value = text
        textFieldWidget!!.setMaxLength(20)
        textFieldWidget!!.setResponder { s: String? -> text = s }
        this.addRenderableWidget(textFieldWidget)

        // add button
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("screen.humblevehicles.tug_route.confirm")
            ) { b: Button? ->
                LOGGER.info("Setting to {}", text)
                callback.accept(if (text!!.isEmpty()) null else text)
                minecraft!!.popGuiLayer()
            }.pos(left + 105, top + 37).size(40, 20).build()
        )
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(graphics)
        super.render(graphics, mouseX, mouseY, partialTicks)
    }

    fun renderBackground(graphics: GuiGraphics) {
        val w = 156
        val h = 65
        val i = (this.width - w) / 2
        val j = (this.height - h) / 2
        graphics.blit(GUI, i, j, 0, 0, w, h)
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            StringInputScreen::class.java
        )
        val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/tug_route_rename.png")
    }
}
