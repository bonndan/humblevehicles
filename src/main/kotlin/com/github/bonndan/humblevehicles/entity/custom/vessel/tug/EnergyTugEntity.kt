package com.github.bonndan.humblevehicles.entity.custom.vessel.tug

import com.github.bonndan.humblevehicles.entity.container.EnergyHeadVehicleContainer
import com.github.bonndan.humblevehicles.entity.custom.EnergyEngine
import com.github.bonndan.humblevehicles.entity.custom.VehicleControl
import com.github.bonndan.humblevehicles.entity.custom.vessel.TugControl
import com.github.bonndan.humblevehicles.entity.models.PositionAdjustedEntity
import com.github.bonndan.humblevehicles.entity.models.vessel.EnergyTugModel.Companion.MODEL_Y_OFFSET
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.animal.WaterAnimal
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class EnergyTugEntity : AbstractTugEntity, PositionAdjustedEntity {

    init {
        setEngine(EnergyEngine(saveStateCallback))
        setControl(TugControl)
        boundingBox = AABB(0.0, 0.0, 0.0, 1.0, 2.0, 2.0)
    }

    constructor(type: EntityType<out WaterAnimal>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double)
            : super(ModEntityTypes.ENERGY_TUG.get(), worldIn, x, y, z)


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

    override fun getDropItem(): Item? {
        return ModItems.ENERGY_TUG.get()
    }

    /**
     * shift passenger nearer to steering wheel
     */
    override fun getPassengerRidingPosition(pEntity: Entity): Vec3 {
        val vec3 = super.getPassengerRidingPosition(pEntity)
        return vec3.add(transformPoint(Vec3(0.0, -0.1, 0.2), yRot))
    }

    override fun getModelYOffset(): Double {
        return MODEL_Y_OFFSET
    }

    companion object {

        fun setCustomAttributes(): AttributeSupplier.Builder {
            return AbstractTugEntity.setCustomAttributes()
        }
    }

}
