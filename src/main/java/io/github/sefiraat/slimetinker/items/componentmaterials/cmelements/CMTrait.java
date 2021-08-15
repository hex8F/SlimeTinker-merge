package io.github.sefiraat.slimetinker.items.componentmaterials.cmelements;

import io.github.mooy1.infinitylib.items.StackUtils;
import io.github.sefiraat.slimetinker.SlimeTinker;
import io.github.sefiraat.slimetinker.categories.Categories;
import io.github.sefiraat.slimetinker.items.componentmaterials.ComponentMaterial;
import io.github.sefiraat.slimetinker.items.workstations.smeltery.DummySmelteryTrait;
import io.github.sefiraat.slimetinker.utils.ThemeUtils;
import io.github.sefiraat.slimetinker.utils.enums.ThemeItemType;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
public class CMTrait {

    private CMTraits parent;
    private ComponentMaterial parentCM;

    private final String traitName;
    private final String[] lore;
    private final String addedBy;
    private final SlimefunItemStack partType;
    private SlimefunItemStack itemStack;
    private SlimefunItem item;

    public CMTrait(SlimefunItemStack partType, String addedBy, String traitName, String... lore) {
        this.traitName = traitName;
        this.addedBy = addedBy;
        this.partType = partType;
        this.lore = lore;
    }

    protected void setupTrait(CMTraits parent, ComponentMaterial parentCM) {
        this.parent = parent;
        this.parentCM = parentCM;

        List<String> newLore = new ArrayList<>(Arrays.asList(lore));
        newLore.add("");
        newLore.add(ThemeUtils.ITEM_TYPEDESC + "Added by: " + addedBy);
        this.itemStack =
                ThemeUtils.themedItemStack(
                        traitName.toUpperCase(Locale.ROOT).replace(" ","_") + "_TRAIT_" + partType.getItemId().toUpperCase(Locale.ROOT) + "_" + StackUtils.getIDorType(parentCM.getRepresentativeStack()),
                        CMTraits.getTraitTexture(addedBy),
                        ThemeItemType.PROP,
                        "Trait : " + traitName,
                        newLore
                );
        this.item = new SlimefunItem(Categories.TRAITS, itemStack, DummySmelteryTrait.TYPE, CMTraits.propRecipe(partType, parentCM.getRepresentativeStack()));
        item.register(SlimeTinker.inst());

    }

}
