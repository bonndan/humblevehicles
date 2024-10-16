package com.github.bonndan.humblevehicles.data.client

import com.github.bonndan.humblevehicles.HumVeeMod
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper


class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, HumVeeMod.MOD_ID, existingFileHelper) {
    override fun registerModels() {

        val itemGenerated: ModelFile = getExistingFile(mcLoc("item/generated"))

        withExistingParent("tug_dock", modLoc("block/tug_dock"))
        withExistingParent("barge_dock", modLoc("block/barge_dock"))
        withExistingParent("guide_rail_corner", modLoc("block/guide_rail_corner"))
        withExistingParent("guide_rail_tug", modLoc("block/guide_rail_tug"))
        withExistingParent("fluid_hopper", modLoc("block/fluid_hopper"))
        withExistingParent("vessel_detector", modLoc("block/vessel_detector"))

        builder(itemGenerated, "barge")
        builder(itemGenerated, "barrel_barge")
        builder(itemGenerated, "vacuum_barge")
        builder(itemGenerated, "chunk_loader_barge")
        builder(itemGenerated, "fishing_barge")
        builder(itemGenerated, "fluid_barge")
        builder(itemGenerated, "seater_barge")
        builder(itemGenerated, "tug")
        builder(itemGenerated, "energy_tug")
        builder(itemGenerated, "submarine")
        builder(itemGenerated, "steam_locomotive")
        builder(itemGenerated, "energy_locomotive")
        builder(itemGenerated, "chest_car")
        builder(itemGenerated, "barrel_car")
        builder(itemGenerated, "chunk_loader_car")
        builder(itemGenerated, "fluid_car")
        builder(itemGenerated, "seater_car")
        builder(itemGenerated, "book")
        builder(itemGenerated, "tug_route")
            .override()
            .model(builder(itemGenerated, "tug_route_empty"))
            .predicate(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "routestate"), 1f).end()

        builder(itemGenerated, "spring")
            .override()
            .model(builder(itemGenerated, "spring_dominant_selected"))
            .predicate(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "springstate"), 1f).end()

        builder(itemGenerated, "conductors_wrench")
        builder(itemGenerated, "creative_capacitor")
        builder(itemGenerated, "rapid_hopper")
        builder(itemGenerated, "switch_rail")
        builder(itemGenerated, "automatic_switch_rail")
        builder(itemGenerated, "tee_junction_rail")
        builder(itemGenerated, "automatic_tee_junction_rail")
        builder(itemGenerated, "junction_rail")
        builder(itemGenerated, "car_dock_rail")
        builder(itemGenerated, "locomotive_dock_rail")

        builder(itemGenerated, "receiver_component")
        builder(itemGenerated, "transmitter_component")

        builder(itemGenerated, "locomotive_route")
            .override()
            .model(builder(itemGenerated, "locomotive_route_empty"))
            .predicate(ResourceLocation.fromNamespaceAndPath(HumVeeMod.MOD_ID, "locoroutestate"), 1f).end()
    }


    private fun builder(itemGenerated: ModelFile, name: String): ItemModelBuilder {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/$name")
    }
}
