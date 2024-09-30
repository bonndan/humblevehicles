package dev.murad.shipping.entity.custom.vessel.barge

import com.mojang.datafixers.util.Pair
import dev.murad.shipping.ShippingConfig
import dev.murad.shipping.entity.custom.StatusDetector.hasWaterOnSides
import dev.murad.shipping.setup.ModEntityTypes
import dev.murad.shipping.setup.ModItems
import dev.murad.shipping.util.InventoryUtils.moveItemStackIntoHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import java.util.*
import kotlin.math.floor
import kotlin.math.min

class FishingBargeEntity : AbstractBargeEntity {

    private var ticksDeployable = 0
    private var fishCooldown = 0
    private val overFishedCoords: MutableSet<Pair<Int, Int>> = HashSet<Pair<Int, Int>>()
    private val overFishedQueue: Queue<Pair<Int, Int>> = LinkedList()

    constructor(type: EntityType<out FishingBargeEntity>, world: Level) : super(type, world)

    constructor(worldIn: Level, x: Double, y: Double, z: Double) : super(
        ModEntityTypes.FISHING_BARGE.get(),
        worldIn,
        x,
        y,
        z
    )


    // Only called on the server side
    override fun doInteract(player: Player?) {
        val size = getConnectedInventories().size

        player?.displayClientMessage(
            when (size) {
                0 -> Component.translatable("global.humblevehicles.no_connected_inventory_barge")
                else -> Component.translatable("global.humblevehicles.connected_inventory", size)
            }, false
        )
    }

    override fun tick() {
        super.tick()

        if (hasWaterOnSides(level(), onPos, direction)) {
            ticksDeployable++
        } else {
            ticksDeployable = 0
        }

        if (!this.level().isClientSide && this.getStatus() == Status.DEPLOYED) {
            if (fishCooldown < 0) {
                tickFish()
                fishCooldown = FISHING_COOLDOWN
            } else {
                fishCooldown--
            }
        }
    }

    private fun computeDepthPenalty(): Double {
        var count = 0
        var pos = this.onPos
        while (this.level().getBlockState(pos).block == Blocks.WATER) {
            count++
            pos = pos.below()
        }
        count = min(count.toDouble(), 20.0).toInt()
        return (count.toDouble()) / 20.0
    }

    // Only called on server side
    private fun tickFish() {

        val overFishPenalty = if (isOverFished()) 0.05 else 1.0
        val shallowPenalty = computeDepthPenalty()
        val chance = 0.25 * overFishPenalty * shallowPenalty
        val treasure_chance = if (shallowPenalty > 0.4) (chance * (shallowPenalty / 2)
                * FISHING_TREASURE_CHANCE) else 0.0
        val r = Math.random()
        if (r < chance) {
            val params = LootParams.Builder(this.level() as ServerLevel)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.TOOL, ItemStack(Items.FISHING_ROD))
                .withParameter(LootContextParams.ATTACKING_ENTITY, this)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.FISHING)

            val loottable = this.level()
                .server!!
                .reloadableRegistries()
                .getLootTable(if (r < treasure_chance) BuiltInLootTables.FISHING_TREASURE else BuiltInLootTables.FISHING_FISH)

            val randomItems = loottable.getRandomItems(params)

            for (stack in randomItems) {
                var leftOver: ItemStack = stack!!
                for (provider in getConnectedInventories()) {
                    if (leftOver.isEmpty) {
                        break
                    }

                    val itemHandler = provider.getTrainInventoryHandler()
                    if (itemHandler.isPresent) {
                        leftOver = moveItemStackIntoHandler(itemHandler.get(), leftOver)
                    }
                }
                // void the stack if we end up not being able to put it in any connected inventory.
            }

            if (!isOverFished()) {
                addOverFish()
            }
        }
    }

    private fun overFishedString(): String {
        return overFishedQueue.stream()
            .map { t -> t!!.first.toString() + ":" + t.second }
            .reduce("") { acc, curr -> "$acc,$curr" }
    }

    private fun populateOverfish(string: String) {
        Arrays.stream(string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .filter { s -> !s.isEmpty() }
            .map { s -> s!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
            .map { arr -> Pair(arr!![0].toInt(), arr[1]!!.toInt()) }
            .forEach { e: Pair<Int, Int> -> overFishedQueue.add(e) }
        overFishedCoords.addAll(overFishedQueue)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("overfish", overFishedString())
        super.addAdditionalSaveData(compound)
    }

    private fun addOverFish() {
        val x = floor(this.x).toInt()
        val z = floor(this.z).toInt()
        overFishedCoords.add(Pair<Int, Int>(x, z))
        overFishedQueue.add(Pair<Int, Int>(x, z))
        if (overFishedQueue.size > 30) {
            overFishedCoords.remove(overFishedQueue.poll())
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        populateOverfish(compound.getString("overfish"))
        super.readAdditionalSaveData(compound)
    }

    private fun isOverFished(): Boolean {
        val x = floor(this.x).toInt()
        val z = floor(this.z).toInt()
        return overFishedCoords.contains(Pair<Int, Int>(x, z))
    }

    override fun getDropItem(): Item? {
        return ModItems.FISHING_BARGE.get()
    }

    fun getStatus(): Status {
        return if (hasWaterOnSides()) getNonStashedStatus() else Status.STASHED
    }

    private fun getNonStashedStatus(): Status {

        if (ticksDeployable < 40) {
            return Status.TRANSITION
        }

        return if (this.applyWithDominant { obj -> obj!!.hasWaterOnSides() }
                .reduce(true) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalAnd(a, b) }
        )
            Status.DEPLOYED
        else
            Status.TRANSITION
    }

    enum class Status {
        STASHED,
        DEPLOYED,
        TRANSITION
    }

    companion object {
        private val FISHING_LOOT_TABLE = ResourceLocation.tryParse(ShippingConfig.Server.FISHING_LOOT_TABLE!!.get())

        private val FISHING_COOLDOWN: Int = ShippingConfig.Server.FISHING_COOLDOWN!!.get()

        private val FISHING_TREASURE_CHANCE: Double = ShippingConfig.Server.FISHING_TREASURE_CHANCE_MODIFIER!!.get()
    }
}
