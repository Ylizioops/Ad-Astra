package com.github.alexnijjar.beyond_earth.mixin.client;

import com.github.alexnijjar.beyond_earth.entities.vehicles.RocketEntity;
import com.github.alexnijjar.beyond_earth.entities.vehicles.VehicleEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> ci) {

        // Make rocket invisible.
        if (entity instanceof RocketEntity rocket) {
            if (rocket.getPhase() == 3) {
                ci.setReturnValue(false);
            }
        }

        if (entity.getVehicle() instanceof VehicleEntity vehicle) {
            if (!vehicle.shouldRenderPlayer()) {
                ci.setReturnValue(false);
            }

            // Make player that is in the planet selection screen invisible.
            if (entity.getVehicle() instanceof RocketEntity rocket) {
                if (rocket.getPhase() == 3) {
                    ci.setReturnValue(false);
                }
            }
        }
    }
}
