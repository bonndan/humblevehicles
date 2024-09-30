package dev.murad.shipping.entity.custom.vessel.tug

import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.entity.custom.FueledEngine
import dev.murad.shipping.entity.custom.SmokeGenerator.makeSmoke
import dev.murad.shipping.entity.custom.vessel.TugControl
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.setup.ModSounds
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

class SteamTugEntity : AbstractTugEntity {

    init {
        engine = FueledEngine(ShippingConfig.Server.STEAM_TUG_FUEL_MULTIPLIER!!.get())
        control = TugControl
    }

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.STEAM_TUG.get(),
        worldIn,
        x,
        y,
        z
    )

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.tug")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return SteamHeadVehicleContainer<SteamTugEntity>(i, level(), getDataAccessor(), playerInventory, player)
            }
        }
    }

    override fun tick() {
        super.tick()
        makeSmoke(level(), independentMotion, onPos, this)
    }

    override fun getDropItem(): Item {
        return ModItems.STEAM_TUG.get()
    }

    override fun onUndock() {
        super.onUndock()
        this.playSound(ModSounds.STEAM_TUG_WHISTLE.get(), 1f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f)
    }

    companion object {
        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }
}
