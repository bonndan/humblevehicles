package dev.murad.shipping.entity.custom.vessel.submarine

import dev.murad.shipping.entity.custom.EnergyEngine
import dev.murad.shipping.entity.custom.Engine
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.Boat
import net.minecraft.world.level.material.Fluids.WATER
import net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class SubmarineMovementBehaviourTest {

    private val engine: Engine = EnergyEngine(mock())
    private lateinit var behaviour: SubmarineMovementBehaviour

    @BeforeEach
    fun setUp() {
        behaviour = SubmarineMovementBehaviour(engine)
    }

    @Test
    fun `when engine is off then buoyancy is low`() {

        //given
        engine.setEngineOn(false)

        //when
        val buoyancy = behaviour.calculateBuoyancy(Boat.Status.UNDER_WATER, 0.5, 1.0, 1.0)

        //then
        assertThat(buoyancy).isGreaterThan(0.0)
    }

    @Test
    fun `when engine is on then buoyancy is zero`() {

        //given
        engine.setEngineOn(true)

        //when
        val buoyancy = behaviour.calculateBuoyancy(Boat.Status.UNDER_WATER, 0.5, 1.0, 1.0)

        //then
        assertThat(buoyancy).isEqualTo(0.0)
    }

    @Test
    fun `when submarine is not underwater then downforce is lower`() {

        //given

        //when
        val inWater = behaviour.calculateDownForce(false, Boat.Status.IN_WATER)
        val underwater = behaviour.calculateDownForce(false, Boat.Status.UNDER_WATER)

        //then
        assertThat(inWater).isLessThan(underwater)
    }

    @Test
    fun `when engine is off and in water then undrown force is off`() {

        //given
        engine.setEngineOn(false)

        //when
        val force = behaviour.calculateUndrownForce(mock(), Boat.Status.IN_WATER, BlockPos(0, 0, 0))

        //then
        assertThat(force).isEqualTo(0.0)
    }

    @Test
    fun `when engine is off and underwater then undrown force is high`() {

        //given
        engine.setEngineOn(false)

        //when
        val force = behaviour.calculateUndrownForce(mock(), Boat.Status.UNDER_WATER, BlockPos(0, 0, 0))

        //then
        assertThat(force).isEqualTo(ENGINE_OFF_AUTO_RAISE_SPEED)
    }

    @Test
    fun `when engine is on and underwater then undrown force is off`() {

        //given
        engine.setEngineOn(true)

        //when
        val force = behaviour.calculateUndrownForce(mock(), Boat.Status.UNDER_WATER, BlockPos(0, 0, 0))

        //then
        assertThat(force).isEqualTo(0.0)
    }

    @Test
    fun `is not falling in water`() {

        //given
        engine.setEngineOn(true)

        //when
        val isFalling = behaviour.isFallingIn(WATER)

        //then
        assertThat(isFalling).isFalse()
    }
}