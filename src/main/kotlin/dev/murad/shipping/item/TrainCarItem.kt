package dev.murad.shipping.item

import com.mojang.datafixers.util.Function4
import dev.murad.shipping.entity.custom.train.AbstractTrainCarEntity
import dev.murad.shipping.entity.custom.train.locomotive.AbstractLocomotiveEntity
import net.minecraft.core.Direction
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseRailBlock
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.level.gameevent.GameEvent
import kotlin.math.floor

// taken from Minecart item
class TrainCarItem(
    val constructor: Function4<Level, Double, Double, Double, AbstractTrainCarEntity>,
    pProperties: Properties
) :
    Item(pProperties) {
    init {
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR)
    }

    /**
     * Called when this item is used when targetting a Block
     */
    override fun useOn(pContext: UseOnContext): InteractionResult {
        val level = pContext.level
        val blockpos = pContext.clickedPos
        val blockstate = level.getBlockState(blockpos)
        if (!blockstate.`is`(BlockTags.RAILS)) {
            return InteractionResult.FAIL
        } else {
            val itemstack = pContext.itemInHand
            if (!level.isClientSide) {
                val railshape =
                    if (blockstate.block is BaseRailBlock) (blockstate.block as BaseRailBlock).getRailDirection(
                        blockstate,
                        level,
                        blockpos,
                        null
                    ) else RailShape.NORTH_SOUTH
                var d0 = 0.0
                if (railshape.isAscending) {
                    d0 = 0.5
                }

                val abstractminecart: AbstractMinecart = constructor.apply(
                    level,
                    blockpos.x.toDouble() + 0.5,
                    blockpos.y.toDouble() + 0.0625 + d0,
                    blockpos.z.toDouble() + 0.5
                )
                if (!itemstack.hoverName.string.isBlank()) {
                    abstractminecart.customName = itemstack.hoverName
                }


                if (pContext.player != null && abstractminecart.direction == pContext.player!!.direction) {
                    if (abstractminecart is AbstractLocomotiveEntity) abstractminecart.flip()
                }

                level.addFreshEntity(abstractminecart)
                level.gameEvent(pContext.player, GameEvent.ENTITY_PLACE, blockpos)
            }

            itemstack.shrink(1)
            return InteractionResult.sidedSuccess(level.isClientSide)
        }
    }

    companion object {
        private val DISPENSE_ITEM_BEHAVIOR: DispenseItemBehavior = object : DefaultDispenseItemBehavior() {
            private val defaultDispenseItemBehavior = DefaultDispenseItemBehavior()

            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            public override fun execute(blockSource: BlockSource, p_42950_: ItemStack): ItemStack {
                val direction = blockSource.state().getValue(DispenserBlock.FACING)
                val level: Level = blockSource.level()
                val d0 = blockSource.pos().x + direction.stepX.toDouble() * 1.125
                val d1 = floor(blockSource.pos().y.toDouble()) + direction.stepY.toDouble()
                val d2 = blockSource.pos().z + direction.stepZ.toDouble() * 1.125
                val blockpos = blockSource.pos().relative(direction)
                val blockstate = level.getBlockState(blockpos)
                val railshape =
                    if (blockstate.block is BaseRailBlock) (blockstate.block as BaseRailBlock).getRailDirection(
                        blockstate,
                        level,
                        blockpos,
                        null
                    ) else RailShape.NORTH_SOUTH
                val d3: Double
                if (blockstate.`is`(BlockTags.RAILS)) {
                    d3 = if (railshape.isAscending) {
                        0.6
                    } else {
                        0.1
                    }
                } else {
                    if (!blockstate.isAir || !level.getBlockState(blockpos.below()).`is`(BlockTags.RAILS)) {
                        return defaultDispenseItemBehavior.dispense(blockSource, p_42950_)
                    }

                    val blockstate1 = level.getBlockState(blockpos.below())
                    val railshape1 =
                        if (blockstate1.block is BaseRailBlock) blockstate1.getValue((blockstate1.block as BaseRailBlock).shapeProperty) else RailShape.NORTH_SOUTH
                    d3 = if (direction != Direction.DOWN && railshape1.isAscending) {
                        -0.4
                    } else {
                        -0.9
                    }
                }

                val abstractminecart: AbstractMinecart =
                    (p_42950_.item as TrainCarItem).constructor.apply(level, d0, d1 + d3, d2)
                if (!p_42950_.hoverName.string.isBlank()) {
                    abstractminecart.customName = p_42950_.hoverName
                }

                level.addFreshEntity(abstractminecart)
                p_42950_.shrink(1)
                return p_42950_
            }

            /**
             * Play the dispense sound from the specified block.
             */
            override fun playSound(p_42947_: BlockSource) {
                p_42947_.level().levelEvent(1000, p_42947_.pos(), 0)
            }
        }
    }
}
