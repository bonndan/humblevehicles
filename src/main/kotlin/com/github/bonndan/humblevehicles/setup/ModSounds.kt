package com.github.bonndan.humblevehicles.setup

import com.github.bonndan.humblevehicles.HumVeeMod
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import java.util.function.Supplier

object ModSounds {
    
    val STEAM_TUG_WHISTLE: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("steam_tug_whistle",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    HumVeeMod.MOD_ID,
                    "steam_tug_whistle"
                ), 64f
            )
        })

    
    val TUG_DOCKING: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("tug_docking",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    HumVeeMod.MOD_ID,
                    "tug_docking"
                ), 64f
            )
        })

    
    val TUG_UNDOCKING: Supplier<SoundEvent> = Registration.SOUND_EVENTS.register("tug_undocking",
        Supplier {
            SoundEvent.createFixedRangeEvent(
                ResourceLocation.fromNamespaceAndPath(
                    HumVeeMod.MOD_ID,
                    "tug_undocking"
                ), 64f
            )
        })

    fun register() {}
}
