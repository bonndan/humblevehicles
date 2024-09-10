package dev.murad.shipping.entity.custom.vessel.barge

import dev.murad.shipping.entity.custom.vessel.tug.AbstractTugEntity
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.FluidDisplayUtil.getFluidDisplay
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.SynchedEntityData.defineId
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class FluidTankBargeEntity : AbstractBargeEntity {

    companion object {
        var CAPACITY: Int = FluidType.BUCKET_VOLUME * 10
        private val FLUID_TYPE = defineId(AbstractTugEntity::class.java, EntityDataSerializers.STRING)
        private val VOLUME = defineId(AbstractTugEntity::class.java, EntityDataSerializers.INT)
    }

    protected var tank: FluidTank = object : FluidTank(CAPACITY) {
        override fun onContentsChanged() {
            sendInfoToClient()
        }
    }
    private var clientCurrFluid: Fluid = Fluids.EMPTY
    private var clientCurrAmount = 0


    constructor(type: EntityType<out AbstractBargeEntity>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.FLUID_TANK_BARGE.get(),
        worldIn,
        x,
        y,
        z
    )

    override fun getDropItem(): Item? {
        return ModItems.FLUID_BARGE.get()
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(FLUID_TYPE, "minecraft:empty")
        pBuilder.define(VOLUME, 0)
    }

    override fun doInteract(player: Player?) {
        FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank)
        player?.displayClientMessage(getFluidDisplay(tank), false)
    }

    fun getFluidStack(): FluidStack {
        return tank.fluid
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        tank.readFromNBT(registryAccess(), tag)
        sendInfoToClient()
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tank.writeToNBT(registryAccess(), tag)
    }

    private fun sendInfoToClient() {
        entityData.set(VOLUME, tank.fluidAmount)
        entityData.set(
            FLUID_TYPE,
            BuiltInRegistries.FLUID.getKey(tank.fluid.fluid).toString()
        )
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide) {
            if (VOLUME == key) {
                clientCurrAmount = entityData.get<Int?>(VOLUME)
                tank.fluid = FluidStack(clientCurrFluid, clientCurrAmount)
            } else if (FLUID_TYPE == key) {
                val fluidName = ResourceLocation.parse(entityData.get(FLUID_TYPE))
                clientCurrFluid = BuiltInRegistries.FLUID.get(fluidName)
                tank.fluid = FluidStack(clientCurrFluid, clientCurrAmount)
            }
        }
    }


}
