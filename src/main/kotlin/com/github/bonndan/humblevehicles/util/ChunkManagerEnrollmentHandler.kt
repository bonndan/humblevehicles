package com.github.bonndan.humblevehicles.util

import com.mojang.authlib.GameProfile
import com.github.bonndan.humblevehicles.ShippingConfig
import com.github.bonndan.humblevehicles.global.PlayerTrainChunkManager.Companion.enroll
import com.github.bonndan.humblevehicles.global.PlayerTrainChunkManager.Companion.enrollIfAllowed
import com.github.bonndan.humblevehicles.global.PlayerTrainChunkManager.Companion.get
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import java.util.*

class ChunkManagerEnrollmentHandler(private val entity: Entity) {
    private var uuid: UUID? = null
    private var enrollMe = -1

    fun tick() {
        if (enrollMe >= 0) {
            if (enrollMe == 0 && !enroll(entity, uuid!!)) {
                enrollMe = 100
            } else {
                enrollMe--
            }
        }
    }

    fun hasOwner(): Boolean {
        return uuid != null
    }

    fun mayMove(): Boolean {
        return if (uuid == null) {
            true
        } else if (ShippingConfig.Server.OFFLINE_LOADING.get()) {
            true
        } else {
            get(entity.level() as ServerLevel, uuid!!).isActive && enrollMe < 0
        }
    }

    fun enroll(uuid: UUID) {
        if (enrollIfAllowed(entity, uuid)) {
            this.uuid = uuid
        }
    }

    fun save(tag: CompoundTag) {
        if (uuid != null) {
            tag.putUUID(UUID_TAG, uuid)
        }
    }

    fun load(tag: CompoundTag) {
        if (tag.contains(UUID_TAG)) {
            uuid = tag.getUUID(UUID_TAG)
            enrollMe = 5
        }
    }

    val playerName: Optional<String>
        get() = if (uuid == null) Optional.empty()
        else (entity.level() as ServerLevel).server.profileCache!![uuid]
            .map { obj: GameProfile -> obj.name }

    companion object {
        private const val UUID_TAG = "EnrollmentHandlerOwner"
    }
}
