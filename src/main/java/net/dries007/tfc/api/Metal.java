/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.NonNullFunction;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.TFCItemGroup;

public class Metal
{
    private final Tier tier;
    //todo private final Fluid fluid;

    private final ResourceLocation id;

    public Metal(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        this.tier = Tier.valueOf(JSONUtils.getInt(json, "tier"));
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Tier getTier()
    {
        return tier;
    }

    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("metal." + id.getNamespace() + "." + id.getPath());
    }

    /**
     * Default metals that are used for block registration calls.
     * Not extensible.
     *
     * @see Metal instead and register via json
     */
    public enum Default
    {
        BISMUTH(true, false, false, false),
        BISMUTH_BRONZE(true, true, true, true),
        BLACK_BRONZE(true, true, true, true),
        BRONZE(true, true, true, true),
        BRASS(true, false, false, false),
        COPPER(true, true, true, true),
        GOLD(true, false, false, false),
        NICKEL(true, false, false, false),
        ROSE_GOLD(true, false, false, false),
        SILVER(true, false, false, false),
        TIN(true, false, false, false),
        ZINC(true, false, false, false),
        STERLING_SILVER(true, false, false, false),
        WROUGHT_IRON(true, true, true, true),
        CAST_IRON(false, false, false, false),
        PIG_IRON(false, false, false, false),
        STEEL(true, true, true, true),
        BLACK_STEEL(true, true, true, true),
        BLUE_STEEL(true, true, true, true),
        RED_STEEL(true, true, true, true),
        WEAK_STEEL(false, false, false, false),
        WEAK_BLUE_STEEL(false, false, false, false),
        WEAK_RED_STEEL(false, false, false, false),
        HIGH_CARBON_STEEL(false, false, false, false),
        HIGH_CARBON_BLACK_STEEL(false, false, false, false),
        HIGH_CARBON_BLUE_STEEL(false, false, false, false),
        HIGH_CARBON_RED_STEEL(false, false, false, false),
        UNKNOWN(false, false, false, false);

        private final boolean parts, tools, armor, utility;

        Default(boolean parts, boolean tools, boolean armor, boolean utility)
        {
            this.parts = parts;
            this.tools = tools;
            this.armor = armor;
            this.utility = utility;
        }

        public boolean hasParts()
        {
            return parts;
        }

        public boolean hasArmor()
        {
            return armor;
        }

        public boolean hasTools()
        {
            return tools;
        }

        public boolean hasUtilities()
        {
            return utility;
        }
    }

    /**
     * Metals / Anvils:
     * T0 - Rock - Work None, Weld T1
     * T1 - Copper - Work T1, Weld T2
     * T2 - Bronze / Bismuth Bronze / Black Bronze - Work T2, Weld T3
     * T3 - Wrought Iron - Work T3, Weld T4
     * T4 - Steel - Work T4, Weld T5
     * T5 - Black Steel - Work T5, Weld T6
     * T6 - Red Steel / Blue Steel - Work T6, Weld T6
     *
     * Devices:
     * T0 - Rock Anvil
     * T1 - Pit Kiln / Fire pit
     * T2 - Forge
     * T3 - Bloomery
     * T4 - Blast Furnace / Crucible
     */
    public enum Tier
    {
        TIER_0, TIER_I, TIER_II, TIER_III, TIER_IV, TIER_V, TIER_VI;

        private static final Tier[] VALUES = values();

        @Nonnull
        public static Tier valueOf(int tier)
        {
            return tier < 0 || tier > VALUES.length ? TIER_I : VALUES[tier];
        }

        @Nonnull
        public Tier next()
        {
            return this == TIER_VI ? TIER_VI : VALUES[this.ordinal() + 1];
        }

        @Nonnull
        public Tier previous()
        {
            return this == TIER_0 ? TIER_0 : VALUES[this.ordinal() - 1];
        }

        public boolean isAtLeast(@Nonnull Tier requiredInclusive)
        {
            return this.ordinal() >= requiredInclusive.ordinal();
        }

        public boolean isAtMost(@Nonnull Tier requiredInclusive)
        {
            return this.ordinal() <= requiredInclusive.ordinal();
        }

        public ITextComponent getDisplayName()
        {
            return new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".enum.tier." + this.name().toLowerCase());
        }
    }

    public enum BlockType
    {
        ANVIL(Type.UTILITY, metal -> new Block(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE))),
        LAMP(Type.UTILITY, metal -> new Block(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(4, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)));

        public static final Metal.BlockType[] VALUES = values();

        public static Metal.BlockType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : ANVIL;
        }

        private final NonNullFunction<Metal.Default, Block> blockFactory;
        private final Type type;

        BlockType(Type type, NonNullFunction<Metal.Default, Block> blockFactory)
        {
            this.type = type;
            this.blockFactory = blockFactory;
        }

        public Block create(Metal.Default metal)
        {
            return blockFactory.apply(metal);
        }

        public boolean hasType(Default metal)
        {
            return type.hasType(metal);
        }
    }

    public enum ItemType
    {
        INGOT(Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        NUGGET(Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        DUST(Type.DEFAULT, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        SCRAP(Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        DOUBLE_INGOT(Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHEET(Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        DOUBLE_SHEET(Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        ROD(Type.PART, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        TUYERE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        PICK(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        PICK_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        PROPICK(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        PROPICK_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        AXE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        AXE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHOVEL(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHOVEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HOE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HOE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        CHISEL(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        CHISEL_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HAMMER(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HAMMER_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SAW(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SAW_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        JAVELIN(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        JAVELIN_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SWORD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SWORD_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        MACE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        MACE_HEAD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        KNIFE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        KNIFE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SCYTHE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SCYTHE_BLADE(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        SHEARS(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        UNFINISHED_HELMET(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        HELMET(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_CHESTPLATE(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        CHESTPLATE(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_GREAVES(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        GREAVES(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        UNFINISHED_BOOTS(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),
        BOOTS(Type.ARMOR, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL))),

        SHIELD(Type.TOOL, metal -> new Item(new Item.Properties().group(TFCItemGroup.METAL)));


        public static final Metal.ItemType[] VALUES = values();

        public static Metal.ItemType valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : INGOT;
        }

        private final NonNullFunction<Metal.Default, Item> itemFactory;
        private final Type type;

        ItemType(Type type, NonNullFunction<Metal.Default, Item> itemFactory)
        {
            this.type = type;
            this.itemFactory = itemFactory;
        }

        public Item create(Metal.Default metal)
        {
            return itemFactory.apply(metal);
        }

        public boolean hasType(Default metal)
        {
            return type.hasType(metal);
        }

        /*
        public boolean hasType(Metal metal)
        {
            if (!metal.usable)
            {
                return this == ItemType.INGOT;
            }
            return !this.isToolItem() || metal.getToolMetal() != null;
        }
        /**
         * Used to find out if the type has a mold
         *
         * @param metal Null, if checking across all types. If present, checks if the metal is compatible with the mold type
         * @return if the type + metal combo have a valid mold
         */
        /*
        public boolean hasMold(@Nullable Metal metal)
        {
            if (metal == null)
            {
                // Query for should the mold exist during registration
                return hasMold;
            }
            if (this == ItemType.INGOT)
            {
                // All ingots are able to be cast in molds
                return true;
            }
            if (hasMold)
            {
                // All tool metals can be used in tool molds with tier at most II
                return metal.isToolMetal() && metal.getTier().isAtMost(Tier.TIER_II);
            }
            return false;
        }
        /**
         * Does this item type require a tool metal to be made
         *
         * @return true if this must be made from a tool item type
         */
        /*
        public boolean isToolItem()
        {
            return toolItem;
        }
        public int getArmorSlot()
        {
            return armorSlot;
        }
        public boolean isArmor() { return armorSlot != -1; }
        /**
         * What armor slot this ItemArmor should use? If this is not armor, return the MainHand slot
         *
         * @return which slot this item should be equipped.
         */
        /*
        public EntityEquipmentSlot getEquipmentSlot()
        {
            switch (armorSlot)
            {
                case 0:
                    return EntityEquipmentSlot.HEAD;
                case 1:
                    return EntityEquipmentSlot.CHEST;
                case 2:
                    return EntityEquipmentSlot.LEGS;
                case 3:
                    return EntityEquipmentSlot.FEET;
                default:
                    return EntityEquipmentSlot.MAINHAND;
            }
        }
        public int getSmeltAmount()
        {
            return smeltAmount;
        }
        public String[] getPattern()
        {
            return pattern;
        }
        */
    }

    private enum Type
    {
        DEFAULT(metal -> true),
        PART(Default::hasParts),
        TOOL(Default::hasTools),
        ARMOR(Default::hasArmor),
        UTILITY(Default::hasUtilities);

        private final Predicate<Metal.Default> predicate;

        Type(Predicate<Metal.Default> predicate)
        {
            this.predicate = predicate;
        }

        boolean hasType(Metal.Default metal)
        {
            return predicate.test(metal);
        }
    }
}
