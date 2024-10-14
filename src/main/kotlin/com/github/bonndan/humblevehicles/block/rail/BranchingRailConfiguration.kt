package com.github.bonndan.humblevehicles.block.rail

import net.minecraft.core.Direction

class BranchingRailConfiguration(
    val rootDirection: Direction,
    val unpoweredDirection: Direction,
    val poweredDirection: Direction
) {
    fun getPossibleDirections(inputSide: Direction, automaticSwitching: Boolean, powered: Boolean): Set<Direction> {
        if (inputSide == rootDirection) {
            return if (automaticSwitching) {
                java.util.Set.of(unpoweredDirection, poweredDirection)
            } else {
                if (powered) mutableSetOf(poweredDirection) else mutableSetOf(
                    unpoweredDirection
                )
            }
        }

        if (inputSide == unpoweredDirection) {
            return if (automaticSwitching) {
                mutableSetOf(rootDirection)
            } else {
                if (powered) NO_POSSIBILITIES else java.util.Set.of(
                    rootDirection
                )
            }
        }

        if (inputSide == poweredDirection) {
            return if (automaticSwitching) {
                java.util.Set.of(rootDirection)
            } else {
                if (powered) java.util.Set.of(rootDirection) else NO_POSSIBILITIES
            }
        }

        return NO_POSSIBILITIES
    }

    companion object {
        val NO_POSSIBILITIES: Set<Direction> = setOf()
    }
}