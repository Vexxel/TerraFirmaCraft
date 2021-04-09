package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.GrillTileEntity;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GrillScreen extends TileEntityScreen<GrillTileEntity, GrillContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit_grill.png");

    public GrillScreen(GrillContainer container, PlayerInventory playerInventory, ITextComponent name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * ((FirepitTileEntity) tile).getSyncableData().get(FirepitTileEntity.DATA_SLOT_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temp > 0)
            blit(matrixStack, leftPos + 30, topPos + 84 - Math.min(51, temp), 176, 0, 15, 5);
    }
}