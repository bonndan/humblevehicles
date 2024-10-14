package com.github.bonndan.humblevehicles.util

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.core.Direction
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.FluidStack
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import kotlin.math.abs

/*
   Adapted from https://github.com/tigres810/TestMod-1.16.4

   Copyright (c) 2020 tigres810

   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:

   The above copyright notice and this permission notice shall be included in all
   copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
*/

object FluidRenderUtil {
    private fun addQuadVertex(
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        renderBuffer: VertexConsumer,
        pos: Vector3f,
        texUV: Vector2f,
        normalVector: Vector3f,
        color: Int,
        lightmapValue: Int
    ) {
        val a = 1.0f
        val r = (color shr 16 and 0xFF) / 255.0f
        val g = (color shr 8 and 0xFF) / 255.0f
        val b = (color and 0xFF) / 255.0f
        renderBuffer.addVertex(matrixPos, pos.x(), pos.y(), pos.z()) // position coordinate
            .setColor(r, g, b, a) // color
            .setUv(texUV.x, texUV.y) // texel coordinate
            .setOverlay(OverlayTexture.NO_OVERLAY) // only relevant for rendering Entities (Living)
            .setUv2(0, 240) // lightmap with full brightness
            .setNormal(normalVector.x(), normalVector.y(), normalVector.z())
        //.endVertex()
    }

    private fun addQuad(
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        renderBuffer: VertexConsumer,
        blpos: Vector3f,
        brpos: Vector3f,
        trpos: Vector3f,
        tlpos: Vector3f,
        blUVpos: Vector2f,
        brUVpos: Vector2f,
        trUVpos: Vector2f,
        tlUVpos: Vector2f,
        normalVector: Vector3f,
        color: Int,
        lightmapValue: Int
    ) {
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, blpos, blUVpos, normalVector, color, lightmapValue)
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, brpos, brUVpos, normalVector, color, lightmapValue)
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, trpos, trUVpos, normalVector, color, lightmapValue)
        addQuadVertex(matrixPos, matrixNormal, renderBuffer, tlpos, tlUVpos, normalVector, color, lightmapValue)
    }

    private fun addFace(
        whichFace: Direction,
        matrixPos: Matrix4f,
        matrixNormal: Matrix3f,
        renderBuffer: VertexConsumer,
        color: Int,
        centrePos: Vector3f,
        width: Float,
        height: Float,
        bottomLeftUV: Vector2f,
        texUwidth: Float,
        texVheight: Float,
        lightmapValue: Int
    ) {
        // the Direction class has a bunch of methods which can help you rotate quads
        //  I've written the calculations out long hand, and based them on a centre position, to make it clearer what
        //   is going on.
        // Beware that the Direction class is based on which direction the face is pointing, which is opposite to
        //   the direction that the viewer is facing when looking at the face.
        // Eg when drawing the NORTH face, the face points north, but when we're looking at the face, we are facing south,
        //   so that the bottom left corner is the eastern-most, not the western-most!


        // calculate the bottom left, bottom right, top right, top left vertices from the VIEWER's point of view (not the
        //  face's point of view)


        val leftToRightDirection: Vector3f
        val bottomToTopDirection: Vector3f

        when (whichFace) {
            Direction.NORTH -> {
                // bottom left is east
                leftToRightDirection = Vector3f(-1f, 0f, 0f) // or alternatively Vector3f.XN
                bottomToTopDirection = Vector3f(0f, 1f, 0f) // or alternatively Vector3f.YP
            }

            Direction.SOUTH -> {
                // bottom left is west
                leftToRightDirection = Vector3f(1f, 0f, 0f)
                bottomToTopDirection = Vector3f(0f, 1f, 0f)
            }

            Direction.EAST -> {
                // bottom left is south
                leftToRightDirection = Vector3f(0f, 0f, -1f)
                bottomToTopDirection = Vector3f(0f, 1f, 0f)
            }

            Direction.WEST -> {
                // bottom left is north
                leftToRightDirection = Vector3f(0f, 0f, 1f)
                bottomToTopDirection = Vector3f(0f, 1f, 0f)
            }

            Direction.UP -> {
                // bottom left is southwest by minecraft block convention
                leftToRightDirection = Vector3f(-1f, 0f, 0f)
                bottomToTopDirection = Vector3f(0f, 0f, 1f)
            }

            Direction.DOWN -> {
                // bottom left is northwest by minecraft block convention
                leftToRightDirection = Vector3f(1f, 0f, 0f)
                bottomToTopDirection = Vector3f(0f, 0f, 1f)
            }

            else -> {
                // should never get here, but just in case;
                leftToRightDirection = Vector3f(0f, 0f, 1f)
                bottomToTopDirection = Vector3f(0f, 1f, 0f)
            }
        }
        leftToRightDirection.mul(0.5f * width) // convert to half width
        bottomToTopDirection.mul(0.5f * height) // convert to half height

        // calculate the four vertices based on the centre of the face
        val bottomLeftPos = Vector3f(centrePos)
        bottomLeftPos.sub(leftToRightDirection)
        bottomLeftPos.sub(bottomToTopDirection)

        val bottomRightPos = Vector3f(centrePos)
        bottomRightPos.add(leftToRightDirection)
        bottomRightPos.sub(bottomToTopDirection)

        val topRightPos = Vector3f(centrePos)
        topRightPos.add(leftToRightDirection)
        topRightPos.add(bottomToTopDirection)

        val topLeftPos = Vector3f(centrePos)
        topLeftPos.sub(leftToRightDirection)
        topLeftPos.add(bottomToTopDirection)

        // texture coordinates are "upside down" relative to the face
        // eg bottom left = [U min, V max]
        val bottomLeftUVpos = Vector2f(bottomLeftUV.x, bottomLeftUV.y)
        val bottomRightUVpos = Vector2f(bottomLeftUV.x + texUwidth, bottomLeftUV.y)
        val topLeftUVpos = Vector2f(bottomLeftUV.x + texUwidth, bottomLeftUV.y + texVheight)
        val topRightUVpos = Vector2f(bottomLeftUV.x, bottomLeftUV.y + texVheight)

        val normalVector = whichFace.step() // gives us the normal to the face

        addQuad(
            matrixPos, matrixNormal, renderBuffer,
            bottomLeftPos, bottomRightPos, topRightPos, topLeftPos,
            bottomLeftUVpos, bottomRightUVpos, topLeftUVpos, topRightUVpos,
            normalVector, color, lightmapValue
        )
    }

    @JvmStatic
    fun renderCubeUsingQuads(
        capacity: Int,
        fluid: FluidStack,
        partialTicks: Float,
        PoseStack: PoseStack,
        renderBuffers: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        // draw the object as a cube, using quad
        // When render method is called, the origin [0,0,0] is at the current [x,y,z] of the block.

        // The cube-drawing method draws the cube in the region from [0,0,0] to [1,1,1] but we want it
        //   to be in the block one above this, i.e. from [0,1,0] to [1,2,1],
        //   so we need to translate up by one block, i.e. by [0,1,0]

        PoseStack.pushPose() // push the current transformation matrix + normals matrix

        drawCubeQuads(PoseStack, renderBuffers, combinedLight, fluid, capacity)
        PoseStack.popPose() // restore the original transformation matrix + normals matrix
    }

    /**
     * Draw a cube from [0,0,0] to [1,1,1], same texture on all sides, using a supplied texture
     */
    private fun drawCubeQuads(
        PoseStack: PoseStack,
        renderBuffer: MultiBufferSource,
        combinedLight: Int,
        fluid: FluidStack,
        capacity: Int
    ) {
        // other typical RenderTypes used by TER are:
        // getEntityCutout, getBeaconBeam (which has translucent),
        val attributes =
            IClientFluidTypeExtensions.of(fluid.fluid) ?: return
        val fluidStill = attributes.getStillTexture(fluid) ?: return

        //        VertexConsumer vertexBuilderBlockQuads = renderBuffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation("minecraft:textures/block/lava_still.png")));
        val vertexBuilderBlockQuads = renderBuffer.getBuffer(RenderType.translucent())

        val color = attributes.tintColor
        val matrixPos = PoseStack.last().pose() // retrieves the current transformation matrix
        val matrixNormal =
            PoseStack.last().normal() // retrieves the current transformation matrix for the normal vector
        val sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidStill)

        // we use the whole texture
        val bottomLeftUV = Vector2f(sprite.u0, sprite.v0)
        val UVwidth = sprite.u1 - sprite.u0
        val UVheight = sprite.v1 - sprite.v0

        // all faces have the same height and width
        val WIDTH = 1.0f
        val HEIGHT = 1.0f

        val scale = (1.0f - WIDTH / 2 - WIDTH) * fluid.amount / capacity

        if (scale <= 0) {
            PoseStack.scale(.5f, (abs(scale.toDouble()) + .21f).toFloat(), .5f)
        }

        val EAST_FACE_MIDPOINT = Vector3f(1.0f, 0.5f, 0.5f)
        val WEST_FACE_MIDPOINT = Vector3f(0.0f, 0.5f, 0.5f)
        val NORTH_FACE_MIDPOINT = Vector3f(0.5f, 0.5f, 0.0f)
        val SOUTH_FACE_MIDPOINT = Vector3f(0.5f, 0.5f, 1.0f)
        val UP_FACE_MIDPOINT = Vector3f(0.5f, 1.0f, 0.5f)
        val DOWN_FACE_MIDPOINT = Vector3f(0.5f, 0.0f, 0.5f)

        addFace(
            Direction.EAST, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, EAST_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
        addFace(
            Direction.WEST, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, WEST_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
        addFace(
            Direction.NORTH, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, NORTH_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
        addFace(
            Direction.SOUTH, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, SOUTH_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
        addFace(
            Direction.UP, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, UP_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
        addFace(
            Direction.DOWN, matrixPos, matrixNormal, vertexBuilderBlockQuads,
            color, DOWN_FACE_MIDPOINT, WIDTH, HEIGHT, bottomLeftUV, UVwidth, UVheight, combinedLight
        )
    }
}
