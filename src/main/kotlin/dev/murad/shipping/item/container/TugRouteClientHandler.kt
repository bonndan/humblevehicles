package dev.murad.shipping.item.container

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.network.SetRouteTagPacket
import dev.murad.shipping.network.TugRoutePacketHandler
import dev.murad.shipping.util.RouteNode
import dev.murad.shipping.util.TugRoute
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.network.chat.Component
import java.util.*

class TugRouteClientHandler(
    private val screen: TugRouteScreen,
    minecraft: Minecraft?,
    private val route: TugRoute,
    private val isOffHand: Boolean
) {
    private var widget: TugList? = null
    private val minecraft = minecraft!!


    fun initializeWidget(width: Int, height: Int, y0: Int, y1: Int, itemHeight: Int): TugList {
        this.widget = TugList(minecraft, width, height, y0, itemHeight)
        for (i in route.indices) {
            widget!!.add(route[i], i)
        }

        return this.widget!!
    }

    fun deleteSelected() {
        val selected = widget!!.selected
        if (selected != null) {
            val index = selected.getIndex()
            route.removeAt(index)
            widget!!.children().removeAt(index)
            widget!!.selected = null
            markDirty()
        }
    }

    fun moveSelectedUp() {
        val selected = widget!!.selected
        if (selected != null) {
            val index = selected.getIndex()
            if (index > 0) {
                val node = route.removeAt(selected.getIndex())
                widget!!.children().removeAt(index)
                route.add(index - 1, node)
                widget!!.children().add(index - 1, selected)
                markDirty()
            }
        }
    }

    fun moveSelectedDown() {
        val selected = widget!!.selected
        if (selected != null) {
            val index = selected.getIndex()
            if (index < route.size - 1) {
                val node = route.removeAt(selected.getIndex())
                widget!!.children().removeAt(index)
                route.add(index + 1, node)
                widget!!.children().add(index + 1, selected)
                markDirty()
            }
        }
    }

    fun renameSelected(name: String?) {
        val selected = widget!!.selected
        if (selected != null) {
            val index = selected.getIndex()
            route[index]!!.name = name
            markDirty()
        }
    }

    val selected: Optional<Pair<Int, RouteNode>>
        get() {
            val selected = widget!!.selected
            if (selected != null) {
                val index = selected.getIndex()
                return Optional.of(Pair(index, route[index]))
            }

            return Optional.empty()
        }

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        widget!!.render(graphics, mouseX, mouseY, partialTicks)
    }

    inner class TugList(minecraft: Minecraft, width: Int, height: Int, y0: Int, itemHeight: Int) :
        ObjectSelectionList<TugList.Entry>(minecraft, width, height, y0, itemHeight) {


        fun add(node: RouteNode, index: Int) {
            this.addEntry(Entry(node, index))
        }

        override fun getRowWidth(): Int {
            return screen.xSize - 40
        }

        override fun getScrollbarPosition(): Int {
            return (this.width + rowWidth) / 2 + 5
        }

        inner class Entry(private val node: RouteNode, private var index: Int) : ObjectSelectionList.Entry<Entry>() {

            override fun render(
                graphics: GuiGraphics,
                ind: Int,
                rowTop: Int,
                rowLeft: Int,
                width: Int,
                height: Int,
                mouseX: Int,
                mouseY: Int,
                hovered: Boolean,
                partialTicks: Float
            ) {
                val s = node.getDisplayName(index) + ": " + node.getDisplayCoords()

                graphics.blit(
                    TugRouteScreen.GUI,
                    rowLeft,
                    rowTop,
                    0,
                    if (hovered) 216 else 236,
                    width - 3,
                    height
                )
                graphics.drawString(screen.getFont(), s, rowLeft + 3, rowTop + 4, 16777215)
            }

            fun setIndex(index: Int) {
                this.index = index
            }

            fun getIndex(): Int {
                return index
            }

            override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                if (button == 0) {
                    this.select()
                    return true
                }
                return false
            }

            private fun select() {
                this@TugList.selected = this
            }

            override fun getNarration(): Component {
                // FIXME: ????
                return Component.literal("")
            }
        }
    }

    private fun markDirty() {
        var i = 0
        for (entry in widget!!.children()) {
            entry!!.setIndex(i++)
        }

        val setRouteTagPacket = SetRouteTagPacket(route.hashCode(), isOffHand, route.toNBT())
        TugRoutePacketHandler.sendToServer(setRouteTagPacket)
    }
}
