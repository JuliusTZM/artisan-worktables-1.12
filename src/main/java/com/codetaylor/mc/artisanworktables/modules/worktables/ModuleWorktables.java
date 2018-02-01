package com.codetaylor.mc.artisanworktables.modules.worktables;

import com.codetaylor.mc.artisanworktables.ModArtisanWorktables;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.BlockWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.item.ItemWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.network.SPacketWorktableTab;
import com.codetaylor.mc.artisanworktables.modules.worktables.network.SPacketWorktableTankDestroyFluid;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.*;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModuleWorktables
    extends ModuleBase {

  public static final String MOD_NAME = ModArtisanWorktables.NAME;
  public static final String MOD_ID = ModArtisanWorktables.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModArtisanWorktables.CREATIVE_TAB;
  public static final ModArtisanWorktables MOD_INSTANCE = ModArtisanWorktables.INSTANCE;

  public static class Lang {

    public static final String WORKTABLE_TITLE = "tile." + MOD_ID + ".worktable.%s.name";
    public static final String JEI_TOOLTIP_SHAPELESS_RECIPE = "jei." + MOD_ID + ".tooltip.shapeless.recipe";
    public static final String JEI_TOOLTIP_CHANCE = "jei." + MOD_ID + ".tooltip.chance";
    public static final String GUI_TOOLTIP_FLUID_DESTROY = "gui." + MOD_ID + ".tooltip.fluid.destroy";
    public static final String GUI_TOOLTIP_FLUID_EMPTY = "gui." + MOD_ID + ".tooltip.fluid.empty";
  }

  public static class Textures {

    public static final String WORKTABLE_GUI = "textures/gui/worktable_%s.png";
  }

  public static class Blocks {

    public static final BlockWorktable WORKTABLE = new BlockWorktable();
  }

  public static IPacketService PACKET_SERVICE;
  public static boolean MOD_LOADED_GAMESTAGES = false;

  public ModuleWorktables() {

    super(0, MOD_ID);

    this.setRegistry(new Registry(MOD_ID, CREATIVE_TAB));
    this.enableAutoRegistry();

    PACKET_SERVICE = this.enableNetwork();

    this.registerIntegrationPlugin(
        "crafttweaker",
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker.ZenWorktable"
    );

    this.registerIntegrationPlugin(
        "crafttweaker",
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker.IZenRecipeBuilder"
    );

    this.registerIntegrationPlugin(
        "jei",
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.jei.PluginJEI"
    );

    this.registerIntegrationPlugin(
        "gamestages",
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.gamestages.PluginGameStages"
    );
  }

  @Override
  public void onConstructionEvent(FMLConstructionEvent event) {

    super.onConstructionEvent(event);

    MOD_LOADED_GAMESTAGES = Loader.isModLoaded("gamestages");
  }

  @Override
  public void onNetworkRegister(IPacketRegistry registry) {

    registry.register(SPacketWorktableTab.class, SPacketWorktableTab.class, Side.SERVER);
    registry.register(SPacketWorktableTankDestroyFluid.class, SPacketWorktableTankDestroyFluid.class, Side.SERVER);
  }

  @Override
  public void onRegister(Registry registry) {

    registry.registerBlock(
        Blocks.WORKTABLE,
        new ItemWorktable(Blocks.WORKTABLE),
        BlockWorktable.NAME
    );
    registry.registerTileEntities(
        TileEntityWorktableBasic.class,
        TileEntityWorktableBlacksmith.class,
        TileEntityWorktableCarpenter.class,
        TileEntityWorktableChemist.class,
        TileEntityWorktableEngineer.class,
        TileEntityWorktableJeweler.class,
        TileEntityWorktableMage.class,
        TileEntityWorktableMason.class,
        TileEntityWorktableScribe.class,
        TileEntityWorktableTailor.class
    );
  }

  @Override
  public void onClientRegister(Registry registry) {

    registry.registerItemModelStrategy(Blocks.WORKTABLE.getModelRegistrationStrategy());
  }
}
