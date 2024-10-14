package com.github.bonndan.humblevehicles.entity.custom.vessel.submarine

import com.github.bonndan.humblevehicles.entity.container.EnergyHeadVehicleContainer
import com.github.bonndan.humblevehicles.entity.custom.EnergyEngine
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.AbstractTugEntity
import com.github.bonndan.humblevehicles.entity.models.RIDING_POSITION_Y_OFFSET
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3


class SubmarineEntity : AbstractTugEntity {

    init {
        val engine = EnergyEngine(saveStateCallback)
        setEngine(engine)
        setControl(SubmarineControl())
        movementBehaviour = SubmarineMovementBehaviour(engine)
    }

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.SUBMARINE.get(),
        worldIn,
        x,
        y,
        z
    )

    override fun getDropItem(): Item {
        return ModItems.SUBMARINE.get()
    }

    override fun createContainerProvider(): MenuProvider {
        return object : MenuProvider {
            override fun getDisplayName(): Component {
                return Component.translatable("screen.humblevehicles.submarine")
            }

            override fun createMenu(i: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
                return EnergyHeadVehicleContainer<SubmarineEntity>(
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

    /**
     * Lower the riding position
     */
    override fun getPassengerRidingPosition(pEntity: Entity): Vec3 {
        val vec3 = super.getPassengerRidingPosition(pEntity)
        return vec3.add(0.0, RIDING_POSITION_Y_OFFSET, 0.0)
    }

    /**
     * Original WaterAnimal code plus engine logics
     */
    override fun handleAirSupply(airSupply: Int) {

        if (this.isAlive && !this.isInWaterOrBubble && !getEngine().isLit()) {
            this.airSupply = airSupply - 1
            if (this.airSupply == -20) {
                this.airSupply = 0
                this.hurt(damageSources().drown(), 2.0f)
            }
        } else {
            this.airSupply = 300
        }
    }

}
