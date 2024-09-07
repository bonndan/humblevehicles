package dev.murad.shipping.setup

import dev.murad.shipping.entity.accessor.EnergyHeadVehicleDataAccessor
import dev.murad.shipping.entity.accessor.SteamHeadVehicleDataAccessor
import dev.murad.shipping.entity.accessor.TugRouteScreenDataAccessor
import dev.murad.shipping.entity.container.EnergyHeadVehicleContainer
import dev.murad.shipping.entity.container.FishingBargeContainer
import dev.murad.shipping.entity.container.SteamHeadVehicleContainer
import dev.murad.shipping.entity.custom.train.locomotive.EnergyLocomotiveEntity
import dev.murad.shipping.entity.custom.train.locomotive.SteamLocomotiveEntity
import dev.murad.shipping.entity.custom.vessel.tug.EnergyTugEntity
import dev.murad.shipping.entity.custom.vessel.tug.SteamTugEntity
import dev.murad.shipping.item.container.TugRouteContainer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import java.util.function.Supplier

object ModMenuTypes {
    private fun makeIntArray(buffer: FriendlyByteBuf): SimpleContainerData {
        val size = (buffer.readableBytes() + 1) / 4
        val arr = SimpleContainerData(size)
        for (i in 0 until size) {
            arr[i] = buffer.readInt()
        }
        return arr
    }

    val TUG_CONTAINER: Supplier<MenuType<SteamHeadVehicleContainer<SteamTugEntity>>> =
        Registration.CONTAINERS.register("tug_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    SteamHeadVehicleContainer(
                        windowId, inv.player.level(), SteamHeadVehicleDataAccessor(
                            makeIntArray(data)
                        ), inv, inv.player
                    )
                }
            })

    val ENERGY_TUG_CONTAINER: Supplier<MenuType<EnergyHeadVehicleContainer<EnergyTugEntity>>> =
        Registration.CONTAINERS.register("energy_tug_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    EnergyHeadVehicleContainer(
                        windowId, inv.player.level(), EnergyHeadVehicleDataAccessor(
                            makeIntArray(data)
                        ), inv, inv.player
                    )
                }
            })

    @JvmField
    val STEAM_LOCOMOTIVE_CONTAINER: Supplier<MenuType<SteamHeadVehicleContainer<SteamLocomotiveEntity>>> =
        Registration.CONTAINERS.register("steam_locomotive_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    SteamHeadVehicleContainer(
                        windowId, inv.player.level(), SteamHeadVehicleDataAccessor(
                            makeIntArray(data)
                        ), inv, inv.player
                    )
                }
            })

    @JvmField
    val ENERGY_LOCOMOTIVE_CONTAINER: Supplier<MenuType<EnergyHeadVehicleContainer<EnergyLocomotiveEntity>>> =
        Registration.CONTAINERS.register("energy_locomotive_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    EnergyHeadVehicleContainer(
                        windowId, inv.player.level(), EnergyHeadVehicleDataAccessor(
                            makeIntArray(data)
                        ), inv, inv.player
                    )
                }
            })

    @JvmField
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

    @JvmField
    val TUG_ROUTE_CONTAINER: Supplier<MenuType<TugRouteContainer>> =
        Registration.CONTAINERS.register("tug_route_container",
            Supplier {
                IMenuTypeExtension.create { windowId: Int, inv: Inventory, data: RegistryFriendlyByteBuf ->
                    TugRouteContainer(
                        windowId, inv.player.level(), TugRouteScreenDataAccessor(
                            makeIntArray(data)
                        ), inv, inv.player
                    )
                }
            })


    fun register() {}
}
