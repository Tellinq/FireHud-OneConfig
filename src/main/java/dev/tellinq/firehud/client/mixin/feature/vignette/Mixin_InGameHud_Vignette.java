package dev.tellinq.firehud.client.mixin.feature.vignette;

//#if MC <= 1.21.1
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
//#if FABRIC && MC >= 1.20.5
import dev.tellinq.firehud.client.accessor.Accessor_SoulFireEntity;
//#endif
import dev.tellinq.firehud.client.FireHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
//#if MC >= 1.21
import net.minecraft.client.render.RenderTickCounter;
//#endif
import net.minecraft.client.util.Window;
//#if MC <= 1.20.4
//$$ import net.minecraft.enchantment.EnchantmentHelper;
//#endif
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import dev.tellinq.firehud.client.config.FireHudConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for {@link InGameHud} to add custom rendering for Fire Hearts and Soul Fire Hearts,
 * as well as fire vignette overlays in the HUD. This mixin handles:
 * <ul>
 *   <li>Redirecting heart textures to custom Fire and Soul Fire heart textures based on player state.</li>
 *   <li>Determining when to render Fire Hearts or Soul Fire Hearts depending on player status effects,
 *       enchantments, and block interactions.</li>
 *   <li>Rendering a fire vignette overlay in first-person view when the player is on fire, with
 *       configurable opacity, scale, and corner selection.</li>
 *   <li>Supporting multiple Minecraft and Fabric API versions via preprocessor directives.</li>
 * </ul>
 *
 * <p>
 * Key features include:
 * <ul>
 *   <li>Custom heart textures for normal, hardcore, half, and blinking states for both Fire and Soul Fire hearts.</li>
 *   <li>Integration with enchantment tags and status effects to determine rendering conditions.</li>
 *   <li>Configurable vignette rendering based on user settings and GUI scale.</li>
 *   <li>Compatibility with multiple Minecraft versions using conditional compilation.</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> Some features are marked as TODO for older Minecraft versions.
 * </p>
 */
@Mixin(InGameHud.class)
public abstract class Mixin_InGameHud_Vignette {
    @Unique private static final Identifier FIRE_VIGNETTE = FireHud.getFireHudResource("textures/fire/fire_vignette.png");
    @Unique private static final Identifier SOUL_FIRE_VIGNETTE = FireHud.getFireHudResource("textures/fire/soul_fire_vignette.png");

    /**
     * Helper method to determine if the HUD vignette should be scaled to a specific value, based on configuration
     * and the current GUI scale. Used to adjust the size of the fire vignette overlay.
     */
    // TODO: Get vignette working on 1.19.4 and older
    //#if MC >= 1.20
    @Unique
    private boolean fireHud$scaleHelper(int scale) {
        int hudScale = FireHudConfig.FireVignette.scale;
        int guiScale = MinecraftClient.getInstance().options.getGuiScale().getValue();
        return hudScale == scale || hudScale == 0 && guiScale == scale;
    }

    /**
     * Injects custom rendering logic into the in-game HUD to display a fire or soul fire vignette overlay
     * when the player is on fire or affected by soul fire. The vignette's appearance is controlled by configuration
     * and adapts to the player's state and GUI scale.
     */
    @Inject(
            //#if MC >= 1.20.5
            method = "renderMiscOverlays",
            //#else
            //$$ method = "render",
            //#endif
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I"))
    private void fireHud$render(
                        //#if MC <= 1.19.4
                        //$$ MatrixStack matrices,
                        //#else
                        DrawContext context,
                        //#endif
                        //#if MC >= 1.21
                        RenderTickCounter tickCounter,
                        //#else
                        //$$ float tickDelta,
                        //#endif
                        CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        Identifier texture = player != null && ((Accessor_SoulFireEntity) player).fireHud$isOnSoulFire() ? SOUL_FIRE_VIGNETTE : FIRE_VIGNETTE;
        //#if MC <= 1.19.4
        Window window = client.getWindow();
        //$$ int width = window.getScaledWidth();
        //$$ int height = window.getScaledHeight();
        //#else
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        //#endif
        int var1 = fireHud$scaleHelper(4) ? 1 : fireHud$scaleHelper(3) ? 2 : fireHud$scaleHelper(2) ? 3 : fireHud$scaleHelper(1) ? 4 : 1;
        int var2 = fireHud$scaleHelper(4) ? 2 : fireHud$scaleHelper(3) ? 4 : fireHud$scaleHelper(2) ? 6 : fireHud$scaleHelper(1) ? 8 : 2;
        int var3 = fireHud$scaleHelper(4) ? 1 : fireHud$scaleHelper(3) ? 3 : fireHud$scaleHelper(2) ? 5 : fireHud$scaleHelper(1) ? 7 : 3;
        
        if (player != null) {
            if (!(!FireHudConfig.FirstPersonFire.whenInLava && player.isInLava())) {
                if (!(!FireHudConfig.FirstPersonFire.fireResistance && player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))) {
                    if (player.isOnFire() && client.options.getPerspective().isFirstPerson()) {
                        if (FireHudConfig.FireVignette.renderFireVignette == 1) {
                            fireHud$renderTopLeftCorner(texture, context, width, height, var1, var2);
                            fireHud$renderTopRightCorner(texture, context, width, height, var1, var2, var3);
                            fireHud$renderBottomLeftCorner(texture, context, width, height, var1, var2, var3);
                            fireHud$renderBottomRightCorner(texture, context, width, height, var1, var2, var3);
                        }
                        if (FireHudConfig.FireVignette.renderFireVignette == 2) {
                            fireHud$renderTopLeftCorner(texture, context, width, height, var1, var2);
                            fireHud$renderTopRightCorner(texture, context, width, height, var1, var2, var3);
                        }
                        if (FireHudConfig.FireVignette.renderFireVignette == 3) {
                            fireHud$renderBottomLeftCorner(texture, context, width, height, var1, var2, var3);
                            fireHud$renderBottomRightCorner(texture, context, width, height, var1, var2, var3);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Renders the top-left corner of the fire vignette overlay on the HUD.
     * Used internally by the vignette rendering logic to compose the full overlay.
     */
    @Unique
    private void fireHud$renderTopLeftCorner(Identifier texture, DrawContext context, int width, int height, int var1, int var2) {
        fireHud$renderOverlay(context, texture, FireHudConfig.FireVignette.opacity / 100f, 0, 0, 0, 0, width / var2, height / var2, width / var1, height / var1);
    }

    /**
     * Renders the top-right corner of the fire vignette overlay on the HUD.
     * Used internally by the vignette rendering logic to compose the full overlay.
     */
    @Unique
    private void fireHud$renderTopRightCorner(Identifier texture, DrawContext context, int width, int height, int var1, int var2, int var3) {
        fireHud$renderOverlay(context, texture, FireHudConfig.FireVignette.opacity / 100f, (width / var2) * var3, 0, width / var2, 0, width, height / var2, width / var1, height / var1);
    }

    /**
     * Renders the bottom-left corner of the fire vignette overlay on the HUD.
     * Used internally by the vignette rendering logic to compose the full overlay.
     */
    @Unique
    private void fireHud$renderBottomLeftCorner(Identifier texture, DrawContext context, int width, int height, int var1, int var2, int var3) {
        fireHud$renderOverlay(context, texture, FireHudConfig.FireVignette.opacity / 100f, 0, (height / var2) * var3, 0, height / var2, width / var2, height, width / var1, height / var1);
    }

    /**
     * Renders the bottom-right corner of the fire vignette overlay on the HUD.
     * Used internally by the vignette rendering logic to compose the full overlay.
     */
    @Unique
    private void fireHud$renderBottomRightCorner(Identifier texture, DrawContext context, int width, int height, int var1, int var2, int var3) {
        fireHud$renderOverlay(context, texture, FireHudConfig.FireVignette.opacity / 100f, (width / var2) * var3, (height / var2) * var3, width / var2, height / var2, width, height, width / var1, height / var1);
    }
    
    /**
     * Core helper for rendering a section of the vignette overlay with the specified parameters.
     * Handles drawing the vignette texture with the correct opacity, scaling, and position.
     * Used by the vignette corner rendering methods.
     */
    @Unique
    private void fireHud$renderOverlay(DrawContext context, Identifier texture, float opacity, int xPos, int yPos, int uStart, int vStart, int uEnd, int vEnd, int textureWidth, int textureHeight) {
        //#if MC <= 1.21.1
        //$$  RenderSystem.disableDepthTest();
        //$$  RenderSystem.depthMask(false);
        //$$  context.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
        //$$  context.drawTexture(texture, xPos, yPos, -90, uStart, vStart, uEnd, vEnd, textureWidth, textureHeight);
        //$$  RenderSystem.depthMask(true);
        //$$  RenderSystem.enableDepthTest();
        //$$  context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //#elseif MC >= 1.21.2
        int i = ColorHelper.getWhite(opacity);
        context.drawTexture(RenderLayer::getGuiTextured, texture, xPos, yPos, uStart, vStart, uEnd, vEnd, textureWidth, textureHeight, i);
        //#endif
    }
    //#endif
}
