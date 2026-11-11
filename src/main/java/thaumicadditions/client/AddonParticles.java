package thaumicadditions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import thaumicadditions.ThaumicAdditions;
import thaumicadditions.fx.ColoredFlames;
import thaumicadditions.fx.VisDrips;
import thaumicadditions.registry.ModParticles;

@EventBusSubscriber(modid = ThaumicAdditions.MOD_ID, value = Dist.CLIENT)
public final class AddonParticles {
    private AddonParticles() {
    }

    @SubscribeEvent
    public static void providers(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.COLORED_FLAME.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new Flame(level, x, y, z, sprites));
        event.registerSpriteSet(ModParticles.VIS_DRIP.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new Drip(level, x, y, z, sprites));
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ColoredFlames.setEmitter(flame -> {
                Minecraft client = Minecraft.getInstance();
                if (client.level == flame.level()) {
                    Particle particle = client.particleEngine.createParticle(ModParticles.COLORED_FLAME.get(), flame.x(), flame.y(), flame.z(), 0, 0, 0);
                    if (particle != null) {
                        color(particle, flame.color());
                        particle.setLifetime(flame.lifetime());
                        particle.scale(flame.scale());
                    }
                }
            });
            VisDrips.setEmitter(drip -> {
                Minecraft client = Minecraft.getInstance();
                if (client.level == drip.level()) {
                    Particle particle = client.particleEngine.createParticle(ModParticles.VIS_DRIP.get(), drip.x(), drip.y(), drip.z(), 0, 0, 0);
                    if (particle instanceof Drip visual) {
                        color(visual, drip.color());
                        visual.setGravity(drip.gravity());
                    }
                }
            });
        });
    }

    private static void color(Particle particle, int color) {
        particle.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);
    }

    private static final class Flame extends TextureSheetParticle {
        private final SpriteSet sprites;

        private Flame(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
            super(level, x, y, z);
            this.sprites = sprites;
            lifetime = 40;
            gravity = -0.01F;
            quadSize = 0.1F;
            setSpriteFromAge(sprites);
        }

        @Override
        public void tick() {
            super.tick();
            setSpriteFromAge(sprites);
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }
    }

    private static final class Drip extends TextureSheetParticle {
        private final SpriteSet sprites;
        private int grounded;

        private void setGravity(float gravity) {
            this.gravity = gravity;
        }

        private Drip(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
            super(level, x, y, z);
            this.sprites = sprites;
            lifetime = 200;
            gravity = 0.5F;
            quadSize = 0.05F;
            setSprite(sprites.get(0, 1));
        }

        @Override
        public void tick() {
            super.tick();
            if (onGround || grounded > 0) {
                setSprite(sprites.get(1, 1));
                if (++grounded >= 20) {
                    remove();
                }
            }
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }
    }
}
