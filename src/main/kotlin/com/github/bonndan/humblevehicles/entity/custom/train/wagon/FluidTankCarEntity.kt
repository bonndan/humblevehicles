package com.github.bonndan.humblevehicles.entity.custom.train.wagon

import com.github.bonndan.humblevehicles.entity.custom.train.AbstractTrainCarEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.AbstractTugEntity
import com.github.bonndan.humblevehicles.setup.ModEntityTypes
import com.github.bonndan.humblevehicles.setup.ModItems
import com.github.bonndan.humblevehicles.util.FluidDisplayUtil
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

class FluidTankCarEntity : AbstractWagonEntity {

    companion object {
        var CAPACITY: Int = FluidType.BUCKET_VOLUME * 10
        private val VOLUME: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            AbstractTrainCarEntity::class.java, EntityDataSerializers.INT
        )
        private val FLUID_TYPE: EntityDataAccessor<String> = SynchedEntityData.defineId(
            AbstractTrainCarEntity::class.java, EntityDataSerializers.STRING
        )
    }

    protected var tank: FluidTank = object : FluidTank(CAPACITY) {
        override fun onContentsChanged() {
            sendInfoToClient()
        }
    }
    private var clientCurrFluid: Fluid = Fluids.EMPTY
    private var clientCurrAmount = 0

    constructor(p_38087_: EntityType<*>, p_38088_: Level) : super(p_38087_, p_38088_)

    constructor(
        level: Level,
        aDouble: Double,
        aDouble1: Double,
        aDouble2: Double
    ) : super(ModEntityTypes.FLUID_CAR.get(), level, aDouble, aDouble1, aDouble2)

    override fun getPickResult(): ItemStack {
        return ItemStack(ModItems.FLUID_CAR.get())
    }

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        pBuilder.define(FLUID_TYPE, "minecraft:empty")
        pBuilder.define(VOLUME, 0)
    }

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val ret = super.interact(player, hand)
        if (ret.consumesAction()) return ret

        if (!level().isClientSide) {
            FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank)
            player.displayClientMessage(FluidDisplayUtil.getFluidDisplay(tank), false)
        }
        return InteractionResult.CONSUME
    }

    val fluidStack: FluidStack
        get() = tank.fluid

    public override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        tank.readFromNBT(registryAccess(), tag)
        sendInfoToClient()
    }

    public override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tank.writeToNBT(registryAccess(), tag)
    }

    private fun sendInfoToClient() {
        entityData.set(VOLUME, tank.fluidAmount)
        entityData.set(FLUID_TYPE, BuiltInRegistries.FLUID.getKey(tank.fluid.fluid).toString())
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide) {
            if (VOLUME == key) {
                clientCurrAmount = entityData.get(VOLUME)
                tank.fluid = FluidStack(clientCurrFluid, clientCurrAmount)
            } else if (FLUID_TYPE == key) {
                val fluidName = ResourceLocation.parse(entityData.get(FLUID_TYPE))
                clientCurrFluid = BuiltInRegistries.FLUID[fluidName]
                tank.fluid = FluidStack(clientCurrFluid, clientCurrAmount)
            }
        }
    }


}
