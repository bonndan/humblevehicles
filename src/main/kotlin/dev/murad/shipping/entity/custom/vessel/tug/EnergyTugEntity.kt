package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer
import dev.murad.shipping.entity.custom.EnergyEngine
import dev.murad.shipping.entity.custom.vessel.TugControl
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class EnergyTugEntity : AbstractTugEntity {

    init {
        engine = EnergyEngine()
        control = TugControl
    }

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.ENERGY_TUG.get(),
        worldIn,
        x,
        y,
        z
    )

    override fun getDropItem(): Item? {
        return ModItems.ENERGY_TUG.get()
    }

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.energy_tug")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return EnergyHeadVehicleContainer<EnergyTugEntity>(
                    i,
                    level(),
                    getDataAccessor(),
                    playerInventory,
                    player
                )
            }
        }
    }

    // Energy tug can be loaded at all times since there is no concern
    // with mix-ups like with fluids and items
    override fun allowDockInterface(): Boolean {
        return true
    }

    companion object {

        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }

}
