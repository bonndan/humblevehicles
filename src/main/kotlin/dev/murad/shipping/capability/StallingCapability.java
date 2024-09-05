package dev.murad.shipping.capability;


import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.jetbrains.annotations.Nullable;

public interface StallingCapability {

    EntityCapability<StallingCapability, @Nullable Void> STALLING_CAPABILITY = EntityCapability.createVoid(
            ResourceLocation.parse("item_handler"), //"create"
            StallingCapability.class
    );

    boolean isDocked();
    void dock(double x, double y, double z);
    void undock();

    boolean isStalled();
    void stall();
    void unstall();

    boolean isFrozen();
    void freeze();
    void unfreeze();
}
