package dev.murad.shipping.util

import dev.murad.shipping.setup.ModTags
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item

object InteractionUtil {
    fun doConfigure(player: Player, hand: InteractionHand): Boolean {
        return (player.pose == Pose.CROUCHING && player.getItemInHand(hand).isEmpty) ||
                (player.pose != Pose.CROUCHING && player.getItemInHand(hand).tags.anyMatch { obj: TagKey<Item?>? ->
                    ModTags.Items.WRENCHES.equals(
                        obj
                    )
                })
    }
}
