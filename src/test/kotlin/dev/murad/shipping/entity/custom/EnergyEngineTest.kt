package dev.murad.shipping.entity.custom

import dev.murad.shipping.entity.custom.Engine.Companion.BURN
import dev.murad.shipping.entity.custom.Engine.Companion.ENGINE_ON
import dev.murad.shipping.entity.custom.Engine.Companion.FUEL_ITEMS
import dev.murad.shipping.entity.custom.Engine.Companion.TOTAL_BURN_CAPACITY
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EnergyEngineTest {

    private lateinit var energyEngine: EnergyEngine

    var engineStateUpdate = false
    var remainingBurnTimeUpdate = 0

    private val saveStateCallback: SaveStateCallback = object : SaveStateCallback {

        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            engineStateUpdate = engineState
            remainingBurnTimeUpdate = remainingBurnTime
        }
    }

    @BeforeEach
    fun setup() {
        engineStateUpdate = false
        remainingBurnTimeUpdate = 0
        energyEngine = EnergyEngine(saveStateCallback)
    }

    @Test
    fun `is not lit when off`() {

        //when
        energyEngine.setEngineOn(false)

        //then
        assertThat(energyEngine.isLit()).isFalse()
    }

    @Test
    fun `is not lit when empty but on`() {

        //when
        energyEngine.setEngineOn(true)

        //then
        assertThat(energyEngine.isLit()).isFalse()
    }

    @Test
    fun `is lit when empty and on`() {

        //given
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 2))
        energyEngine.setEngineOn(true)

        //when
        energyEngine.tickFuel()

        //then
        assertThat(energyEngine.isLit()).isTrue()
    }

    @Test
    fun `has progress when fuel is consumed`() {

        //given
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 2))
        energyEngine.setEngineOn(true)

        //when
        energyEngine.tickFuel()
        energyEngine.tickFuel()

        //then
        assertThat(energyEngine.getBurnProgressPct()).isEqualTo(99)
    }

    @Test
    fun `redstone is valid fuel`() {

        assertThat(energyEngine.isItemValid(0, ItemStack(Items.REDSTONE, 2))).isTrue()
    }

    @Test
    fun `coal is not valid fuel`() {

        assertThat(energyEngine.isItemValid(0, ItemStack(Items.COAL, 2))).isFalse()
    }

    @Test
    fun `remaining percent is 100 when loaded and engine still off`() {

        //given
        energyEngine.setEngineOn(false)

        //when
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 20))

        //then
        assertThat(energyEngine.getBurnProgressPct()).isEqualTo(100)
    }

    @Test
    fun `fuel is not burned when engine is off`() {

        //given
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 20))
        energyEngine.setEngineOn(false)

        //when
        energyEngine.tickFuel()

        //then
        assertThat(energyEngine.getBurnProgressPct()).isEqualTo(100)

        //when
        energyEngine.tickFuel()

        //then
        assertThat(energyEngine.getBurnProgressPct()).isEqualTo(100)
    }

    @Test
    fun `serializes data`() {

        //given
        val compound = CompoundTag()
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 2))
        energyEngine.setEngineOn(true)
        energyEngine.tickFuel()

        //when
        energyEngine.addAdditionalSaveData(compound, RegistryAccess.EMPTY)

        //then
        assertThat(compound.getInt(BURN)).isNotNull()
        assertThat(compound.getInt(TOTAL_BURN_CAPACITY)).isNotNull()
        assertThat(compound.getBoolean(ENGINE_ON)).isTrue()
        assertThat(compound.getCompound(FUEL_ITEMS)).isNotNull()
    }

    @Test
    fun `writes to save state callback data`() {

        //when
        energyEngine.setStackInSlot(0, ItemStack(Items.REDSTONE, 2))
        val remaining = remainingBurnTimeUpdate
        //then
        assertThat(remainingBurnTimeUpdate).isGreaterThan(0)

        //when
        energyEngine.setEngineOn(true)
        //then
        assertThat(engineStateUpdate).isTrue()


        energyEngine.tickFuel()
        //when
        assertThat(remainingBurnTimeUpdate).isLessThan(remaining)

    }
}