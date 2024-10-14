package com.github.bonndan.humblevehicles.entity.models

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.CompositeState
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

object SubmarineRenderType {

    fun unsortedTranslucent(textureLocation: ResourceLocation): RenderType {
        val sortingEnabled = false
        val renderState = CompositeState.builder()
            .setShaderState(RenderType.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(TextureStateShard(textureLocation, false, false))
            .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
            .setCullState(RenderType.NO_CULL)
            .setLightmapState(RenderType.LIGHTMAP)
            .setOverlayState(RenderType.OVERLAY)
            .createCompositeState(true)
        return RenderType.create(
            "neoforge_entity_unsorted_translucent_submarine",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, sortingEnabled, renderState
        )
    }

    fun get(textureLocation: ResourceLocation): Function<ResourceLocation, RenderType> {
        return  Util.memoize { obj -> unsortedTranslucent(textureLocation) }
    }
}