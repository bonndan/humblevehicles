package dev.murad.shipping.entity.custom.vessel.submarine

import net.minecraft.client.player.Input
import net.minecraft.world.entity.vehicle.Boat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubmarineControlTest {

    private lateinit var control: SubmarineControl

    @BeforeEach
    fun setUp() {
        control = SubmarineControl()
    }

    @Test
    fun `when no input is given then result contains no effect`() {

        //given

        //when
        val result = control.calculateResult(Input(), Boat.Status.IN_WATER)

        //then
        assertThat(result.yMovement).isEqualTo(0.0)
        assertThat(result.yRotationModifier).isEqualTo(0)
        assertThat(result.deltaRotationModifier).isEqualTo(0)
    }

    @Test
    fun `when jumping if underwater then result contains up movement effect`() {

        //given
        val input = Input()
        input.jumping = true

        //when
        val result = control.calculateResult(input, Boat.Status.UNDER_WATER)

        //then
        assertThat(result.yMovement).isGreaterThan(0.0)
    }

    @Test
    fun `when down thrust is given then result contains down movement effect`() {

        //given
        control.addDownThrust()

        //when
        val result = control.calculateResult(Input(), Boat.Status.IN_WATER)

        //then
        assertThat(result.yMovement).isEqualTo(DOWN_THRUST)
    }

}