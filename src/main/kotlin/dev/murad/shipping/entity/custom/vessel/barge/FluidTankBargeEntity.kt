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
    protected var tank: FluidTank = object : FluidTank(CAPACITY) {
        override fun onContentsChanged() {
            sendInfoToClient()
        }
    }
    private var clientCurrFluid: Fluid = Fluids.EMPTY
    private var clientCurrAmount = 0


    constructor(type: EntityType<out AbstractBargeEntity?>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.FLUID_TANK_BARGE.get(),
        worldIn,
        x,
        y,
        z
    )

    public override fun getDropItem(): Item? {
        return ModItems.FLUID_BARGE.get()
    }

    protected override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        super.defineSynchedData(pBuilder)
        entityData.set<String?>(FluidTankBargeEntity.Companion.FLUID_TYPE, "minecraft:empty")
        entityData.set<Int?>(FluidTankBargeEntity.Companion.VOLUME, 0)
    }

    override fun doInteract(player: Player?) {
        FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank)
        player?.displayClientMessage(getFluidDisplay(tank), false)
    }

    fun getFluidStack(): FluidStack {
        return tank.getFluid()
    }

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
        entityData.set<Int?>(FluidTankBargeEntity.Companion.VOLUME, tank.getFluidAmount())
        entityData.set<String?>(
            FluidTankBargeEntity.Companion.FLUID_TYPE,
            BuiltInRegistries.FLUID.getKey(tank.getFluid().getFluid()).toString()
        )
    }

    public override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (level().isClientSide) {
            if (FluidTankBargeEntity.Companion.VOLUME == key) {
                clientCurrAmount = entityData.get<Int?>(FluidTankBargeEntity.Companion.VOLUME)
                tank.setFluid(FluidStack(clientCurrFluid, clientCurrAmount))
            } else if (FluidTankBargeEntity.Companion.FLUID_TYPE == key) {
                val fluidName =
                    ResourceLocation.parse(entityData.get<String?>(FluidTankBargeEntity.Companion.FLUID_TYPE))
                clientCurrFluid = BuiltInRegistries.FLUID.get(fluidName)
                tank.setFluid(FluidStack(clientCurrFluid, clientCurrAmount))
            }
        }
    }

    companion object {
        var CAPACITY: Int = FluidType.BUCKET_VOLUME * 10
        private val VOLUME: EntityDataAccessor<Int?> =
            SynchedEntityData.defineId<Int?>(AbstractTugEntity::class.java, EntityDataSerializers.INT)
        private val FLUID_TYPE: EntityDataAccessor<String?> =
            SynchedEntityData.defineId<String?>(AbstractTugEntity::class.java, EntityDataSerializers.STRING)
    }
}
