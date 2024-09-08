package dev.murad.shipping.util

import net.minecraft.network.chat.Component
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

object FluidDisplayUtil {
    @JvmStatic
    fun getFluidDisplay(tank: FluidTank): Component {
        val fluid = tank.fluid.fluid
        return if (fluid == Fluids.EMPTY) Component.translatable(
            "block.littlelogistics.fluid_hopper.capacity_empty",
            tank.capacity
        ) else Component.translatable(
            "block.littlelogistics.fluid_hopper.capacity", tank.fluid.hoverName.string,
            tank.fluidAmount, tank.capacity
        )
    }
}
