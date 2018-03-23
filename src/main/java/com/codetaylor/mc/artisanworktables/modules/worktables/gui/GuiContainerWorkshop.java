package com.codetaylor.mc.artisanworktables.modules.worktables.gui;

import com.codetaylor.mc.artisanworktables.modules.worktables.gui.element.GuiElementFluidTankLarge;
import com.codetaylor.mc.artisanworktables.modules.worktables.gui.element.GuiElementMageEffect;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.TileEntityBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "yalter.mousetweaks.api.MouseTweaksDisableWheelTweak", modid = "mousetweaks")
public class GuiContainerWorkshop
    extends GuiContainerBase {

  public GuiContainerWorkshop(
      Container container,
      ResourceLocation backgroundTexture,
      String titleKey,
      int textShadowColor,
      TileEntityBase tileEntity,
      int width,
      int height
  ) {

    super(container, backgroundTexture, titleKey, textShadowColor, tileEntity, width, height);
  }

  @Override
  protected void addMageEffectElement(Container container) {

    this.guiContainerElementAdd(new GuiElementMageEffect(
        this,
        container,
        143,
        62
    ));
  }

  @Override
  protected void addFluidTankElement() {

    this.guiContainerElementAdd(new GuiElementFluidTankLarge(
        this,
        this.tileEntity.getTank(),
        this.tileEntity.getPos(),
        this.textShadowColor,
        8,
        17
    ));
  }
}
