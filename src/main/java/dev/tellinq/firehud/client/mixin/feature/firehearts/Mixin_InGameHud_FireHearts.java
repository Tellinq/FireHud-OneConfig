package dev.tellinq.firehud.client.mixin.feature.firehearts;

//#if MC <= 1.21.1
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import dev.tellinq.firehud.client.FireHud;
//#if FABRIC && MC >= 1.20.5
import dev.tellinq.firehud.client.EnchantTags;
import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.registry.entry.RegistryEntry;
//#endif
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.enchantment.Enchantment;
//#if MC <= 1.20.4
//$$ import net.minecraft.enchantment.EnchantmentHelper;
//#endif
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class Mixin_InGameHud_FireHearts {
    @Unique private static final Identifier FIRE_HEART_FULL_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_full");
    @Unique private static final Identifier FIRE_HEART_FULL_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_full_blinking");
    @Unique private static final Identifier FIRE_HEART_HALF_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_half");
    @Unique private static final Identifier FIRE_HEART_HALF_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_half_blinking");
    @Unique private static final Identifier FIRE_HEART_HARDCORE_FULL_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_hardcore_full");
    @Unique private static final Identifier FIRE_HEART_HARDCORE_FULL_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_hardcore_full_blinking");
    @Unique private static final Identifier FIRE_HEART_HARDCORE_HALF_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_hardcore_half");
    @Unique private static final Identifier FIRE_HEART_HARDCORE_HALF_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/fire_hardcore_half_blinking");
    
    @Unique private static final Identifier SOUL_FIRE_HEART_FULL_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_full");
    @Unique private static final Identifier SOUL_FIRE_HEART_FULL_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_full_blinking");
    @Unique private static final Identifier SOUL_FIRE_HEART_HALF_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_half");
    @Unique private static final Identifier SOUL_FIRE_HEART_HALF_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_half_blinking");
    @Unique private static final Identifier SOUL_FIRE_HEART_HARDCORE_FULL_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_hardcore_full");
    @Unique private static final Identifier SOUL_FIRE_HEART_HARDCORE_FULL_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_hardcore_full_blinking");
    @Unique private static final Identifier SOUL_FIRE_HEART_HARDCORE_HALF_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_hardcore_half");
    @Unique private static final Identifier SOUL_FIRE_HEART_HARDCORE_HALF_BLINKING_TEXTURE = FireHud.getFireHudResource("hud/heart/soul_fire_hardcore_half_blinking");

    /**
     * Redirects the heart texture rendering in the in-game HUD to use custom Fire and Soul Fire heart textures
     * based on the player's status and configuration. This method is injected via Mixin and replaces the default
     * heart texture selection logic to provide visual feedback for fire-related effects.
     */
    // TODO: Make Fire Heart texture iconset for 1.20.1 and older
    //#if MC >= 1.20.2
    @Redirect(
            method = "drawHeart",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;getTexture(ZZZ)Lnet/minecraft/util/Identifier;"
            )
    )
    private Identifier fireHud$redirectHeartTexture(InGameHud.HeartType originalType, boolean hardcore, boolean blinking, boolean half) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.cameraEntity instanceof PlayerEntity player) {
            if (originalType == InGameHud.HeartType.NORMAL) {
                if (fireHud$shouldRenderSoulFireHeart(player) && fireHud$shouldRenderFireHeart(player)) {
                    return fireHud$getSoulFireHeartTexture(hardcore, half, blinking);
                } else if (fireHud$shouldRenderFireHeart(player)) {
                    return fireHud$getFireHeartTexture(hardcore, half, blinking);
                }
            }
        }
        return originalType.getTexture(hardcore, blinking, half);
    }

    /**
     * Determines if Fire Hearts should be rendered for the given player, based on configuration and player state.
     * This is used to decide whether to override the vanilla heart icons with Fire Heart textures.
     */
    @Unique
    private boolean fireHud$shouldRenderFireHeart(PlayerEntity player) {
        if (!FireHudConfig.renderFireHearts) return false;

        if (!FireHudConfig.FirstPersonFire.fireResistance && player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) return false;

        boolean hasFrostWalker = false;
        //#if FABRIC && MC >= 1.20.5
        for (RegistryEntry<Enchantment> enchantment : player.getEquippedStack(EquipmentSlot.FEET).getEnchantments().getEnchantments()) {
            if (ClientTags.isInWithLocalFallback(EnchantTags.FROST_WALKER, enchantment)) {
                hasFrostWalker = true;
                break;
            }
        }
        //#elseif MC < 1.20.5
        //$$ hasFrostWalker = EnchantmentHelper.hasFrostWalker(player);
        //#endif

        boolean steppingOnMagma = player.getSteppingBlockState().getBlock() == Blocks.MAGMA_BLOCK &&
                !player.bypassesSteppingEffects();
        boolean steppingOnLitCampfire = player.getSteppingBlockState().getBlock() instanceof CampfireBlock &&
                Boolean.TRUE.equals(player.getSteppingBlockState().get(Properties.LIT));

        return player.isOnFire() || (!hasFrostWalker && (steppingOnMagma || steppingOnLitCampfire));
    }

    /**
     * Determines if Soul Fire Hearts should be rendered for the given player, based on configuration and player state.
     * Used to decide whether to override the vanilla heart icons with Soul Fire Heart textures.
     */
    @Unique
    private boolean fireHud$shouldRenderSoulFireHeart(PlayerEntity player) {
        boolean isOnSoulFire = ((Accessor_SoulFireEntity) player).fireHud$isOnSoulFire();
        boolean standingOnSoulCampfire = player.getSteppingBlockState().getBlock() == Blocks.SOUL_CAMPFIRE;

        return FireHudConfig.renderSoulFire && (isOnSoulFire || standingOnSoulCampfire);
    }
    //#endif
    
    /**
     * Returns the appropriate Identifier for the Fire Heart texture based on the player's state.
     * Used by the heart texture redirect logic to select the correct Fire Heart icon.
     */
    @Unique
    public Identifier fireHud$getFireHeartTexture(boolean hardcore, boolean half, boolean blinking) {
        if (!hardcore) {
            if (half) return blinking ? FIRE_HEART_HALF_BLINKING_TEXTURE : FIRE_HEART_HALF_TEXTURE;
            return blinking ? FIRE_HEART_FULL_BLINKING_TEXTURE : FIRE_HEART_FULL_TEXTURE;
        }
        if (half) return blinking ? FIRE_HEART_HARDCORE_HALF_BLINKING_TEXTURE : FIRE_HEART_HARDCORE_HALF_TEXTURE;
        return blinking ? FIRE_HEART_HARDCORE_FULL_BLINKING_TEXTURE : FIRE_HEART_HARDCORE_FULL_TEXTURE;
    }

    /**
     * Returns the appropriate Identifier for the Soul Fire Heart texture based on the player's state.
     * Used by the heart texture redirect logic to select the correct Soul Fire Heart icon.
     */
    @Unique
    public Identifier fireHud$getSoulFireHeartTexture(boolean hardcore, boolean half, boolean blinking) {
        if (!hardcore) {
            if (half) return blinking ? SOUL_FIRE_HEART_HALF_BLINKING_TEXTURE : SOUL_FIRE_HEART_HALF_TEXTURE;
            return blinking ? SOUL_FIRE_HEART_FULL_BLINKING_TEXTURE : SOUL_FIRE_HEART_FULL_TEXTURE;
        }
        if (half) return blinking ? SOUL_FIRE_HEART_HARDCORE_HALF_BLINKING_TEXTURE : SOUL_FIRE_HEART_HARDCORE_HALF_TEXTURE;
        return blinking ? SOUL_FIRE_HEART_HARDCORE_FULL_BLINKING_TEXTURE : SOUL_FIRE_HEART_HARDCORE_FULL_TEXTURE;
    }
}
