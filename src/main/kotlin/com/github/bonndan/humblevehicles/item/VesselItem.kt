package com.github.bonndan.humblevehicles.item

import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySelector
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class VesselItem(props: Properties, private val addEntity: AddEntityFunction) : Item(props) {

    fun interface AddEntityFunction {
        fun apply(level: Level, x: Double, y: Double, z: Double): Entity
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {

        val itemstack = player.getItemInHand(hand)
        val hitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.ANY)
        if (hitResult.type == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack)
        }

        val vector3d = player.getViewVector(1.0f)
        val entities = world.getEntities(
            player, player.boundingBox.expandTowards(vector3d.scale(5.0)).inflate(1.0),
            EntitySelector.NO_SPECTATORS.and { obj: Entity -> obj.isPickable })
        if (entities.isNotEmpty()) {
            val vector3d1 = player.getEyePosition(1.0f)

            for (entity in entities) {
                val axisalignedBox = entity.boundingBox.inflate(entity.pickRadius.toDouble())
                if (axisalignedBox.contains(vector3d1)) {
                    return InteractionResultHolder.pass(itemstack)
                }
            }
        }

        if (hitResult.type == HitResult.Type.BLOCK) {
            val entity = getEntity(world, itemstack, hitResult)
            entity.yRot = player.yRot

            return if (!world.noCollision(entity, entity.boundingBox.inflate(-0.1))) {
                InteractionResultHolder.fail(itemstack)
            } else {
                if (!world.isClientSide) {
                    world.addFreshEntity(entity)
                    if (!player.abilities.instabuild) {
                        itemstack.shrink(1)
                    }
                }

                player.awardStat(Stats.ITEM_USED[this])
                InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide())
            }
        }

        return InteractionResultHolder.pass(itemstack)
    }

    private fun getEntity(world: Level, stack: ItemStack, hitResult: BlockHitResult): Entity {

        val entity = addEntity.apply(world, hitResult.location.x, hitResult.location.y, hitResult.location.z)
        if (stack.hoverName.string.isNotBlank()) {
            entity.customName = stack.hoverName
        }
        return entity
    }
}
