package dev.tellinq.firehud.client;

//#if MC >= 1.20.5
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
//#endif

public interface EnchantTags {
    //#if MC >= 1.20.5
    TagKey<Enchantment> FROST_WALKER = EnchantTags.of("prevents_fire_hearts");
            
    private static TagKey<Enchantment> of(String id) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, FireHud.getIdentifierOf(FireHud.MOD_ID, id));
    }
    //#endif
}
