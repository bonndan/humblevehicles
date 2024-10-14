package com.github.bonndan.humblevehicles.item.container

import com.mojang.math.Divisor
import com.github.bonndan.humblevehicles.HumVeeMod
import com.github.bonndan.humblevehicles.util.Route
import it.unimi.dsi.fastutil.ints.IntIterator
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
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class RouteScreen(
    menu: RouteContainer,
    inventory: Inventory,
    title: Component
) :
    AbstractContainerScreen<RouteContainer>(menu, inventory, title) {

    private val stack: ItemStack

    init {
        this.imageWidth = 256
        this.imageHeight = 233

        this.stack = this.menu.itemStack
    }

    private fun getRight(): Int {
        return this.leftPos + imageWidth
    }

    private fun getBottom(): Int {
        return this.topPos + imageHeight
    }

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

        val clientHandler = TugRouteClientHandler(
            screen = this,
            minecraft = this.minecraft,
            route = Route.getRoute(stack),
            itemStack = stack,
            isOffHand = menu.isOffHand
        )

        this.addRenderableWidget(
            clientHandler.initializeWidget(
                this@RouteScreen.width, this@RouteScreen.height,
                topPos + 40, topPos + this@RouteScreen.imageHeight - 45, 20
            )
        )

        this.addRenderableWidget(
            buildButton(
                getRight() - 92, getBottom() - 24, 20, 20,
                Component.literal("..ꕯ").withStyle(ChatFormatting.BOLD),
                { button: Button ->
                    val selectedOpt = clientHandler.selected
                    if (selectedOpt.isPresent) {
                        val selected = selectedOpt.get()
                        minecraft!!.pushGuiLayer(
                            StringInputScreen(
                                selected.second!!,
                                selected.first!!
                            ) { name: String? -> clientHandler.renameSelected(name) })
                    }
                },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.rename_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                getRight() - 70, getBottom() - 24, 20, 20,
                Component.literal("▲"),
                { _ -> clientHandler.moveSelectedUp() },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.up_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                getRight() - 47, getBottom() - 24, 20, 20,
                Component.literal("▼"),
                { _ -> clientHandler.moveSelectedDown() },
                getTooltip(Component.translatable("screen.humblevehicles.tug_route.down_button"))
            )
        )

        this.addRenderableWidget(
            buildButton(
                getRight() - 24, getBottom() - 24, 20, 20,
                Component.literal("✘"),
                { _ -> clientHandler.deleteSelected() },
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
            getRight() - 4, top,
            8, 0,
            4, 4
        )
        // botleft
        graphics.blit(
            GUI,
            left, getBottom() - 4,
            0, 8,
            4, 4
        )
        // botright
        graphics.blit(
            GUI,
            getRight() - 4, getBottom() - 4,
            8, 8,
            4, 4
        )

        // top
        blitRepeating(GUI, left + 4, top, (xSize - 8), 4, 4, 0, 4, 4, graphics = graphics)

        // bottom
        blitRepeating(GUI, left + 4, getBottom() - 4, (xSize - 8), 4, 4, 8, 4, 4, graphics = graphics)

        // left
        blitRepeating(GUI, left, top + 4, 4, (ySize - 8), 0, 4, 4, 4, graphics = graphics)

        // right
        blitRepeating(GUI, getRight() - 4, top + 4, 4, (ySize - 8), 8, 4, 4, 4, graphics = graphics)

        // middle
        blitRepeating(GUI, left + 4, top + 4, (xSize - 8), (ySize - 8), 4, 4, 4, 4, graphics = graphics)
    }

    fun blitRepeating(
        resourceLocation: ResourceLocation,
        p_283575_: Int,
        p_283192_: Int,
        p_281790_: Int,
        p_283642_: Int,
        p_282691_: Int,
        p_281912_: Int,
        p_281728_: Int,
        p_282324_: Int,
        textureWidth: Int = 256,
        textureHeight: Int = 256,
        graphics: GuiGraphics
    ) {
        var i = p_283575_

        var j: Int
        val intiterator: IntIterator = slices(p_281790_, p_281728_)
        while (intiterator.hasNext()) {
            j = intiterator.nextInt()
            val k = (p_281728_ - j) / 2
            var l = p_283192_

            var i1: Int
            val intiterator1: IntIterator = slices(p_283642_, p_282324_)
            while (intiterator1.hasNext()) {
                i1 = intiterator1.nextInt()
                val j1 = (p_282324_ - i1) / 2
                graphics.blit(
                    resourceLocation, i, l,
                    (p_282691_ + k).toFloat(),
                    (p_281912_ + j1).toFloat(), j, i1, textureWidth, textureHeight
                )
                l += i1
            }
            i += j
        }
    }

    private fun slices(p_282197_: Int, p_282161_: Int): IntIterator {
        val i = Mth.positiveCeilDiv(p_282197_, p_282161_)
        return Divisor(p_282197_, i)
    }

    // remove inventory tag
    override fun renderLabels(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false)
    }

    fun getFont(): Font {
        return font
    }

    companion object {
        val GUI: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "textures/container/tug_route.png")
    }
}
