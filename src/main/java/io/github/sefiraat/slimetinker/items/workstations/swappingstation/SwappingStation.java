package io.github.sefiraat.slimetinker.items.workstations.swappingstation;

import io.github.mooy1.infinitylib.slimefun.AbstractContainer;
import io.github.sefiraat.slimetinker.SlimeTinker;
import io.github.sefiraat.slimetinker.utils.GUIItems;
import io.github.sefiraat.slimetinker.utils.IDStrings;
import io.github.sefiraat.slimetinker.utils.ItemUtils;
import io.github.sefiraat.slimetinker.utils.ThemeUtils;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import me.mrCookieSlime.Slimefun.cscorelib2.data.PersistentDataAPI;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SwappingStation extends AbstractContainer {

    private static final int[] BACKGROUND_SLOTS = {0,1,2,3,4,5,6,7,8,9,11,13,15,17,18,19,20,21,22,23,24,25,26};
    private static final int INPUT_ITEM = 10;
    private static final int INPUT_PART = 12;
    protected static final int CRAFT_BUTTON = 14;
    protected static final int OUTPUT_SLOT = 16;

    public SwappingStation(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean craft(BlockMenu blockMenu, Player player) {

        ItemStack item = blockMenu.getItemInSlot(INPUT_ITEM);
        ItemStack part = blockMenu.getItemInSlot(INPUT_PART);

        // No tool dummy!
        if (item == null) {
            player.sendMessage(ThemeUtils.WARNING + "Input a tool into the first slot.");
            return false;
        }

        if (part == null) {
            player.sendMessage(ThemeUtils.WARNING + "Input a replacement part into the second slot.");
            return false;
        }

        String partClass = ItemUtils.getPartClass(part);
        String partType = ItemUtils.getPartType(part);
        String partMaterial = ItemUtils.getPartMaterial(part);

        if (ItemUtils.isTool(item)) {
            if (partClass != null && ItemUtils.partIsTool(partClass)) {
                return swapTool(blockMenu, player, item, partClass, partType, partMaterial);
            } else {
                player.sendMessage(ThemeUtils.WARNING + "This part cannot be swapped onto this tool.");
            }
        } else if (ItemUtils.isArmour(item)) {
            if (partClass != null && ItemUtils.partIsArmour(partClass)) {
                return swapArmour(blockMenu, player, item, partClass, partType, partMaterial);
            } else {
                player.sendMessage(ThemeUtils.WARNING + "This part cannot be swapped onto this tool.");
            }
        } else {
            player.sendMessage(ThemeUtils.WARNING + "The item in the first slot isn't a Tinker's item.");
        }

        return false;

    }

    private boolean swapTool(BlockMenu blockMenu, Player player, ItemStack item, String partClass, String partType, String partMaterial) {

        // The part is a head part but the type is either null or not matching the tool (Axe head part for shovel etc.)
        if (partClass.equals(IDStrings.HEAD) && (partType != null && !partType.equals(ItemUtils.getToolTypeName(item)))) {
            player.sendMessage(ThemeUtils.WARNING + "This head type cannot be swapped onto this tool.");
            return false;
        }

        ItemStack newTool = item.clone();
        ItemMeta newToolMeta = newTool.getItemMeta();

        String swappedMaterial;

        switch (partClass) {
            case IDStrings.HEAD:
                swappedMaterial = ItemUtils.getToolHeadMaterial(newTool);
                break;
            case IDStrings.BINDING:
                swappedMaterial = ItemUtils.getToolBindingMaterial(newTool);
                break;
            case IDStrings.ROD:
                swappedMaterial = ItemUtils.getToolRodMaterial(newTool);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + partClass);
        }

        switch (partClass) {
            case IDStrings.HEAD:
                PersistentDataAPI.setString(newToolMeta, SlimeTinker.inst().getKeys().getToolInfoHeadMaterial(), partMaterial);
                break;
            case IDStrings.BINDING:
                PersistentDataAPI.setString(newToolMeta, SlimeTinker.inst().getKeys().getToolInfoBinderMaterial(), partMaterial);
                break;
            case IDStrings.ROD:
                PersistentDataAPI.setString(newToolMeta, SlimeTinker.inst().getKeys().getToolInfoRodMaterial(), partMaterial);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + partClass);
        }

        newTool.setItemMeta(newToolMeta);

        ItemUtils.rebuildTinkerLore(newTool);
        ItemUtils.rebuildTinkerName(newTool);

        blockMenu.pushItem(newTool, OUTPUT_SLOT);
        blockMenu.getItemInSlot(INPUT_ITEM).setAmount(blockMenu.getItemInSlot(INPUT_ITEM).getAmount() - 1);
        blockMenu.getItemInSlot(INPUT_PART).setAmount(blockMenu.getItemInSlot(INPUT_PART).getAmount() - 1);

        return false;

    }

    private boolean swapArmour(BlockMenu blockMenu, Player player, ItemStack item, String partClass, String partType, String partMaterial) {

        // The part is a plate part but the type is either null or not matching the armour (Helm plates for boots etc..)
        if (partClass.equals(IDStrings.PLATE) && (partType != null && !partType.equals(ItemUtils.getArmourTypeName(item)))) {
            player.sendMessage(ThemeUtils.WARNING + "This plate type cannot be swapped onto this armour.");
            return false;
        }

        ItemStack newArmour = item.clone();
        ItemMeta newArmourMeta = newArmour.getItemMeta();

        switch (partClass) {
            case IDStrings.PLATE:
                PersistentDataAPI.setString(newArmourMeta, SlimeTinker.inst().getKeys().getArmourInfoPlateMaterial(), partMaterial);
                break;
            case IDStrings.GAMBESON:
                PersistentDataAPI.setString(newArmourMeta, SlimeTinker.inst().getKeys().getArmourInfoGambesonMaterial(), partMaterial);
                break;
            case IDStrings.LINKS:
                PersistentDataAPI.setString(newArmourMeta, SlimeTinker.inst().getKeys().getArmourInfoLinksMaterial(), partMaterial);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + partClass);
        }

        newArmour.setItemMeta(newArmourMeta);

        ItemUtils.rebuildTinkerLore(newArmour);
        ItemUtils.rebuildTinkerName(newArmour);

        blockMenu.pushItem(newArmour, OUTPUT_SLOT);
        blockMenu.getItemInSlot(INPUT_ITEM).setAmount(blockMenu.getItemInSlot(INPUT_ITEM).getAmount() - 1);
        blockMenu.getItemInSlot(INPUT_PART).setAmount(blockMenu.getItemInSlot(INPUT_PART).getAmount() - 1);

        return false;

    }

    @Override
    protected void setupMenu(BlockMenuPreset blockMenuPreset) {

        blockMenuPreset.drawBackground(ChestMenuUtils.getBackground(), BACKGROUND_SLOTS);
        blockMenuPreset.addItem(CRAFT_BUTTON, GUIItems.menuCraftSwap());
        blockMenuPreset.addMenuClickHandler(CRAFT_BUTTON, (player, i, itemStack, clickAction) -> false);

    }

    @Override
    protected int @NotNull [] getTransportSlots(@NotNull DirtyChestMenu dirtyChestMenu, @NotNull ItemTransportFlow itemTransportFlow, ItemStack itemStack) {
        return new int[0];
    }

    @Override
    protected void onBreak(@Nonnull BlockBreakEvent event, @Nonnull BlockMenu blockMenu, @Nonnull Location location) {
        super.onBreak(event, blockMenu, location);
        blockMenu.dropItems(location, INPUT_ITEM);
        blockMenu.dropItems(location, INPUT_PART);
        blockMenu.dropItems(location, OUTPUT_SLOT);
    }

    @Override
    protected void onNewInstance(@Nonnull BlockMenu blockMenu, @Nonnull Block b) {
        super.onNewInstance(blockMenu, b);
        blockMenu.addMenuClickHandler(CRAFT_BUTTON, (player, i, itemStack, clickAction) -> craft(blockMenu, player));
    }

}
