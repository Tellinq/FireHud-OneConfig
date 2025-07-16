package dev.tellinq.firehud.client.config;

import dev.tellinq.firehud.FireHudConstants;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.*;
import org.polyfrost.polyui.color.ColorUtils;
import org.polyfrost.polyui.color.PolyColor;

/**
 * The main Config entrypoint that extends the Config type and initializes your config options.
 * See <a href="https://docsv1.polyfrost.org/configuration/available-options">this link</a> for more config Options
 */
public class FireHudConfig extends Config {

    @Accordion(
            title = "First Person Fire"
    )
    public static class FirstPersonFire {
        @Include
        public static boolean enabled = true;

        @Checkbox(title = "Even with Fire Resistance applied?")
        public static boolean fireResistance = true;

        @Checkbox(title = "Even when in lava?")
        public static boolean whenInLava = true;

        @Slider(
                title = "Opacity",
                min = 0f, max = 100f,
                step = 1
        )
        public static float opacity = 90f;

        @Slider(
                title = "Height",
                min = 0f, max = 100f,
                step = 1
        )
        public static float height = 70f;

        @Switch(title = "Side Fire", description = "Renders the vanilla fire, just off to the sides instead of in the middle")
        public static boolean sideFire = false;
    }

    @Accordion(
            title = "Third Person Fire"
    )
    public static class ThirdPersonFire {
        @Include
        public static boolean enabled = true;

        @Checkbox(title = "Even with Fire Resistance applied?")
        public static boolean fireResistance = true;

        @Checkbox(title = "Even when in lava?")
        public static boolean whenInLava = true;
    }

    @Accordion(
            title = "Fire Volume/Pitch"
    )
    public static class FireVolumePitch {
        @Slider(
                title = "Fire volume",
                min = 0f, max = 100f,
                step = 1
        )
        public static float fireVolume = 100f;

        @Switch(
                title = "Randomize fire volume",
                description = "The fire block's volume has random additions added each time it plays. This toggles whether or not that randomness is applied."
        )
        public static boolean randomizeFireVolume = true;

        @Slider(
                title = "Fire Pitch",
                min = 0f, max = 100f,
                step = 1
        )
        public static float firePitch = 30f;

        @Switch(
                title = "Randomize fire pitch",
                description = "The fire block's pitch has random additions added each time it plays. This toggles whether or not that randomness is applied."
        )
        public static boolean randomizeFirePitch = true;
    }


    // TODO: Get vignette working on 1.19.4 and older
    //#if MC >= 1.20
    @Accordion(
            title = "Fire Vignette"
    )
    public static class FireVignette {

        @RadioButton(
                title = "Render Fire Vignette",
                options = {"Off",  "Full", "Upper", "Lower"}
        )
        public static int renderFireVignette = 0;

        @Slider(
                title = "Opacity",
                min = 0f, max = 100f,
                step = 1
        )
        public static float opacity = 90f;

        @Slider(
                title = "Scale",
                min = 0, max = 4,
                step = 1
        )
        public static int scale = 0;
    }
    //#endif

    //#if MC >= 1.20.2
    @Switch(
            title = "Render Fire Hearts",
            description = "Makes the player's hearts have a special texture when on fire, similar to poison or wither effects",
            subcategory = "Fire Hearts"
    )
    public static boolean renderFireHearts = false;
    //#endif

    @Accordion(
            title = "Fire Screen Tint"
    )
    public static class FireScreenTint {
        @Include
        public static boolean enabled = false;

        @Color(title = "Tint Start Color")
        public static PolyColor tintStartColor = PolyColor.TRANSPARENT;

        @Color(title = "Tint End Color")
        public static PolyColor tintEndColor = ColorUtils.rgba(255, 0, 0, 0.4f);

        @Color(title = "Soul Tint Start Color")
        public static PolyColor soulTintStartColor = PolyColor.TRANSPARENT;

        @Color(title = "Soul Tint End Color")
        public static PolyColor soulTintEndColor = ColorUtils.rgba(0, 0, 255, 0.4f);
    }

    @Switch(
            title = "Render Soul Fire",
            description = "Change the fire color to match soul fire when standing on soul fire",
            subcategory = "Soul Fire")
    public static boolean renderSoulFire = true;

    @Accordion(
            title = "Lava Fog",
            description = "Lava Fog"
    )
    public static class Lava {
        @RadioButton(
                title = "Render Lava Fog",
                description = "Toggles rendering the fog effect when in lava, making it easier to see",
                options = {"Vanilla",  "Light Fog", "No Fog"}
        )
        public static int renderLavaFog = 0;

        @Slider(
                title = "Light Fog Distance",
                description = "The view distance when in lava with Render Lava Fog set to Light Fog",
                min = 0f, max = 100f,
                step = 1
        )
        public static float distance = 50f;
    }

    public static final FireHudConfig INSTANCE = new FireHudConfig(); // The instance of the Config.

    public FireHudConfig() {
        super(FireHudConstants.ID + ".json", "assets/firehud/icon.png", FireHudConstants.NAME, Category.QOL); // TODO: Change your category here.
    }

}

