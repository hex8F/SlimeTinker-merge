package io.github.sefiraat.slimetinker.items.workstations.smeltery;

import io.github.mooy1.infinitylib.recipes.RecipeMap;
import io.github.mooy1.infinitylib.recipes.ShapedRecipe;
import io.github.sefiraat.slimetinker.SlimeTinker;
import io.github.sefiraat.slimetinker.utils.ThemeUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class DummySmelteryAlloy {

    private DummySmelteryAlloy() {
        throw new IllegalStateException("Utility class");
    }

    public static final SlimefunItemStack STACK = new SlimefunItemStack(
            "DUMMY_TINKERS_SMELTERY_ALLOY",
            Material.CHISELED_POLISHED_BLACKSTONE,
            ThemeUtils.ITEM_MACHINE + "Tinker's Smeltery",
            "",
            ThemeUtils.PASSIVE + "This alloy is made in the Tinker's",
            ThemeUtils.PASSIVE + "Smeltery by inputting correct metal",
            ThemeUtils.PASSIVE + "types and clicking Alloy.",
            "",
            ThemeUtils.LORE_TYPE_MACHINE
    );

    private static final RecipeMap<ItemStack> RECIPES = new RecipeMap<>(ShapedRecipe::new);
    public static final RecipeType TYPE = new RecipeType(SlimeTinker.inst().getKeys().getWsDummySmelteryA(), STACK, RECIPES::put);

}
