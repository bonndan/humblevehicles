package dev.murad.shipping.item.container

import dev.murad.shipping.HumVeeMod
import dev.murad.shipping.item.TugRouteItem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Button.OnPress
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class TugRouteScreen(menu: TugRouteContainer, inventory: Inventory, title: Component) :
    AbstractContainerScreen<TugRouteContainer?>(menu, inventory, title) {
    private val stack: ItemStack

    init {
        this.imageWidth = 256
        this.imageHeight = 233

        this.stack = this.menu!!.itemStack
    }

    private val right: Int
        get() = this.leftPos + imageWidth

    private val bot: Int
        get() = this.topPos + imageHeight

    // https://github.com/ChAoSUnItY/EkiLib/blob/9b63591608cefafce32113a68bc8fd4b71972ece/src/main/java/com/chaos/eki_lib/gui/screen/StationSelectionScreen.java
    // https://github.com/ChAoSUnItY/EkiLib/blob/9b63591608cefafce32113a68bc8fd4b71972ece/src/main/java/com/chaos/eki_lib/utils/handlers/StationHandler.java#L21
    // https://github.com/ChAoSUnItY/EkiLib/blob/9b63591608cefafce32113a68bc8fd4b71972ece/src/main/java/com/chaos/eki_lib/utils/network/PacketInitStationHandler.java
    // https://github.com/ChAoSUnItY/EkiLib/blob/9b63591608cefafce32113a68bc8fd4b71972ece/src/main/java/com/chaos/eki_lib/utils/handlers/PacketHandler.java
    private fun getTooltip(tooltip: Component): Tooltip {
        return Tooltip.create(tooltip)
    }

    private fun buildButton(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        msg: MutableComponent,
        onPress: OnPress,
        tooltip: Tooltip
    ): Button {
        return Button.builder(msg, onPress)
            .pos(x, y)
            .size(width, height)
            .tooltip(tooltip).build()
    }

    override fun init() {
        super.init()

        LOGGER.info("Initializing TugRouteScreen")

        val route = TugRouteClientHandler(this, this.minecraft, TugRouteItem.getRoute(stack), menu!!.isOffHand)

        this.addRenderableWidget(
            route.initializeWidget(
                this@TugRouteScreen.width, this@TugRouteScreen.height,
                topPos + 40, topPos + this@TugRouteScreen.imageHeight - 45, 20
            )
        )

        this.addRenderableWidget(
            buildButton(
                right - 92, bot - 24, 20, 20,
                Component.literal("..ꕯ").withStyle(ChatFormatting.BOLD),
                { button: Button? ->
                    val selectedOpt = route.selected
                    if (selectedOpt.isPresent) {
                        val selected = selectedOpt.get()
                        minecraft!!.pushGuiLayer(StringInputScreen(
                            selected.second!!, selected.first!!
                        ) { name: String? -> route.renameSelected(name) })
                    }
                },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.rename_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                right - 70, bot - 24, 20, 20,
                Component.literal("▲"),
                { button: Button? -> route.moveSelectedUp() },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.up_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                right - 47, bot - 24, 20, 20,
                Component.literal("▼"),
                { button: Button? -> route.moveSelectedDown() },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.down_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                right - 24, bot - 24, 20, 20,
                Component.literal("✘"),
                { button: Button? -> route.deleteSelected() },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.delete_button"))
            )
        )
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(graphics, mouseX, mouseY, partialTicks)
        super.render(graphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(graphics, mouseX, mouseY)
    }

    /**
     * Renders the tugroute background in 9 parts
     * 1. 4 Corners
     * 2. 4 Sides
     * 3. 1 Middle
     * This assumes the GUI texture is 12x12, with 4x4 chunks representing each of the chunks above.
     */
    override fun renderBg(graphics: GuiGraphics, partialTicks: Float, x: Int, y: Int) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.setShaderTexture(0, GUI);
        val left = this.guiLeft
        val top = this.guiTop
        val right = this.right
        val bot = this.bot

        // topleft
        graphics.blit(
            GUI,
            left, top,
            0, 0,
            4, 4
        )
        // topright
        graphics.blit(
            GUI,
            right - 4, top,
            8, 0,
            4, 4
        )
        // botleft
        graphics.blit(
            GUI,
            left, bot - 4,
            0, 8,
            4, 4
        )
        // botright
        graphics.blit(
            GUI,
            right - 4, bot - 4,
            8, 8,
            4, 4
        )

        // top
        graphics.blit(
            GUI,
            left + 4, top,
            (xSize - 8).toFloat(), 4f,
            4, 0,
            4, 4
        )

        // bottom
        graphics.blit(
            GUI,
            left + 4, bot - 4,
            (xSize - 8).toFloat(), 4f,
            4, 8,
            4, 4
        )

        // left
        graphics.blit(
            GUI,
            left, top + 4,
            4f, (ySize - 8).toFloat(),
            0, 4,
            4, 4
        )

        // right
        graphics.blit(
            GUI,
            right - 4, top + 4,
            4f, (ySize - 8).toFloat(),
            8, 4,
            4, 4
        )

        // middle
        graphics.blit(
            GUI,
            left + 4, top + 4,
            (xSize - 8).toFloat(), (ySize - 8).toFloat(),
            4, 4,
            4, 4
        )
    }

    // remove inventory tag
    override fun renderLabels(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false)
    }

    fun getFont(): Font {
        return font
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            TugRouteScreen::class.java
        )
        val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/tug_route.png")
    }
}
