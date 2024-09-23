package dev.murad.shipping.rendering

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderType
import java.util.*

class ModRenderType(
    pName: String,
    pFormat: VertexFormat,
    pMode: VertexFormat.Mode,
    pBufferSize: Int,
    pAffectsCrumbling: Boolean,
    pSortOnUpload: Boolean,
    pSetupState: Runnable,
    pClearState: Runnable
) :
    RenderType(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState) {
    companion object {
        val LINES: RenderType = create(
            "lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
            CompositeState.builder()
                .setShaderState(RENDERTYPE_LINES_SHADER)
                .setLineState(LineStateShard(OptionalDouble.empty()))
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .setDepthTestState(NO_DEPTH_TEST)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setCullState(NO_CULL).createCompositeState(false)
        )
    }
}