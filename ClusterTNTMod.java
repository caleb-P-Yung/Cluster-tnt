package com.example.clustertnt;

// Copyright (c) 2025 Caleb Pierre Yung
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(ClusterTNTMod.MODID)
public class ClusterTNTMod {
    public static final String MODID = "clustertnt";

    public ClusterTNTMod() {
        // No init logic needed, we use the ForgeEvents inner class
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onTntDetonate(ExplosionEvent.Detonate evt) {
            Level level = evt.getLevel();
            if (level.isClientSide) return;

            // Only react to vanilla TNT explosions
            if (!(evt.getExplosion().getExploder() instanceof PrimedTnt)) return;

            Vec3 center = evt.getExplosion().getPosition();
            double spawnY  = center.y + 0.5;   // slight lift so they don't suffocate in ground
            int    fuse    = 60;               // 3s fuse (60 ticks)

            // Cardinal direction velocities (x, y, z)
            Vec3[] directions = new Vec3[] {
                new Vec3( 0.5, 0.3,  0.0),  // east
                new Vec3(-0.5, 0.3,  0.0),  // west
                new Vec3( 0.0, 0.3,  0.5),  // south
                new Vec3( 0.0, 0.3, -0.5)   // north
            };            

            for (Vec3 vel : directions) {
                PrimedTnt t = new PrimedTnt(level,
                    center.x, spawnY, center.z,
                    null  // no specific igniter
                );
                t.setFuse(fuse);
                t.setDeltaMovement(vel);
                level.addFreshEntity(t);
            }
        }
    }
}
