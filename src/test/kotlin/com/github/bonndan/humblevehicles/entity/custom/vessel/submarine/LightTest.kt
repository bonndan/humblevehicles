package com.github.bonndan.humblevehicles.entity.custom.vessel.submarine

import net.minecraft.DetectedVersion.BUILT_IN
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.server.Bootstrap
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import org.mockito.kotlin.*

class LightTest {

    private val level: Level = mock<Level>()
    private lateinit var lightBlock: LightBlock
    private lateinit var light: Light

    @BeforeEach
    fun setUp() {
        SharedConstants.setVersion(BUILT_IN)
        Bootstrap.bootStrap()

        lightBlock = mock<LightBlock>()
        whenever(lightBlock.withPropertiesOf(any())).thenReturn(Blocks.WATER.defaultBlockState())
        light = Light(level)
    }

    @Test
    fun `does not do anything if engine is not lit`() {

        val targetPosition = BlockPos(0, 0, 0)

        //when
        light.update(targetPosition, false)

        verify(level, never()).setBlockAndUpdate(any(), any())
        verify(level, never()).setBlockEntity(any())
    }

    @Test
    fun `does not do anything if engine is lit but target is not water`() {

        val targetPosition = BlockPos(0, 0, 0)
        val state: BlockState = Blocks.SAND.defaultBlockState()
        whenever(level.getBlockState(any())).thenReturn(state)

        //when
        light.update(targetPosition, true)

        verify(level, never()).setBlockAndUpdate(any(), any())
        verify(level, never()).setBlockEntity(any())
    }

    @Disabled("runs into 'Trying to access unbound value' for mod blocks and entity")
    @Test
    fun `creates a light if engine is lit`() {

        //given
        val targetPosition = BlockPos(0, 0, 0)
        whenever(level.getBlockState(any())).thenReturn(Blocks.WATER.defaultBlockState())

        //when
        light.update(targetPosition, true)

        verify(level).setBlockEntity(any())
        verify(level, never()).setBlockAndUpdate(any(), any())
    }

    @Test
    fun turnOff() {
    }
}