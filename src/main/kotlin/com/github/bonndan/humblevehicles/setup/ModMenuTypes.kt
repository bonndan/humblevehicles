package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.entity.accessor.HeadVehicleDataAccessor
import com.github.bonndan.humblevehicles.entity.accessor.RouteScreenDataAccessor
import com.github.bonndan.humblevehicles.entity.container.EnergyHeadVehicleContainer
import com.github.bonndan.humblevehicles.entity.container.FishingBargeContainer
import com.github.bonndan.humblevehicles.entity.container.SteamHeadVehicleContainer
import com.github.bonndan.humblevehicles.entity.custom.vessel.submarine.SubmarineEntity
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.EnergyLocomotiveEntity
import com.github.bonndan.humblevehicles.entity.custom.train.locomotive.SteamLocomotiveEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.EnergyTugEntity
import com.github.bonndan.humblevehicles.entity.custom.vessel.tug.SteamTugEntity
import com.github.bonndan.humblevehicles.item.container.RouteContainer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import java.util.function.Supplier

object ModMenuTypes {

    val TUG_CONTAINER: Supplier<MenuType<SteamHeadVehicleContainer<SteamTugEntity>>> =
        Registration.CONTAINERS.register("tug_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    SteamHeadVehicleContainer(
                        windowId, inv.player.level(), HeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player
                    )
                }
            })

    val ENERGY_TUG_CONTAINER: Supplier<MenuType<EnergyHeadVehicleContainer<EnergyTugEntity>>> =
        Registration.CONTAINERS.register("energy_tug_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    EnergyHeadVehicleContainer(
                        windowId, inv.player.level(), HeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player
                    )
                }
            })

    val SUBMARINE_CONTAINER: Supplier<MenuType<EnergyHeadVehicleContainer<SubmarineEntity>>> =
        Registration.CONTAINERS.register("submarine_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    EnergyHeadVehicleContainer(
                        windowId, inv.player.level(), HeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player
                    )
                }
            })

    val STEAM_LOCOMOTIVE_CONTAINER: Supplier<MenuType<SteamHeadVehicleContainer<SteamLocomotiveEntity>>> =
        Registration.CONTAINERS.register("steam_locomotive_container",
            Supplier{IMenuTypeExtension.create(::steamHeadVehicleContainer)}
        )


    private fun steamHeadVehicleContainer(windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf):
            SteamHeadVehicleContainer<SteamLocomotiveEntity> {
        return SteamHeadVehicleContainer(
            windowId,
            inv.player.level(),
            HeadVehicleDataAccessor(makeIntArray(data)),
            inv,
            inv.player
        )
    }

    val ENERGY_HEAD_CONTAINER: Supplier<MenuType<EnergyHeadVehicleContainer<EnergyLocomotiveEntity>>> =
        Registration.CONTAINERS.register("energy_locomotive_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    EnergyHeadVehicleContainer(
                        windowId, inv.player.level(), HeadVehicleDataAccessor(makeIntArray(data)), inv, inv.player
                    )
                }
            })

    val FISHING_BARGE_CONTAINER: Supplier<MenuType<FishingBargeContainer>> =
        Registration.CONTAINERS.register("fishing_barge_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    FishingBargeContainer(
                        windowId,
                        inv.player.level(),
                        data.readInt(),
                        inv,
                        inv.player
                    )
                }
            })


    val TUG_ROUTE_CONTAINER: Supplier<MenuType<RouteContainer>> =
        Registration.CONTAINERS.register("tug_route_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    RouteContainer(
                        windowId, RouteScreenDataAccessor(
                            makeIntArray(data)
                        ), inv.player
                    )
                }
            })


    fun register() {}

    //on a NPE check if somewhere player.openMenu(...) is called without 2nd param  getDataAccessor(...)::write
    private fun makeIntArray(buffer: FriendlyByteBuf): SimpleContainerData {

        val size = (buffer.readableBytes() + 1) / 4
        val arr = SimpleContainerData(size)
        for (i in 0 until size) {
            arr[i] = buffer.readInt()
        }
        return arr
    }
}
