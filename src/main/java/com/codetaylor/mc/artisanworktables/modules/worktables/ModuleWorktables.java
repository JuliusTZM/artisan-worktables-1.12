package com.codetaylor.mc.artisanworktables.modules.worktables;

import com.codetaylor.mc.artisanworktables.ModArtisanWorktables;
import com.codetaylor.mc.artisanworktables.api.ArtisanAPI;
import com.codetaylor.mc.artisanworktables.modules.worktables.api.ModuleWorktablesAPI_Impl;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.BlockWorkshop;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.BlockWorkstation;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.BlockWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.item.ItemWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.network.CPacketWorktableFluidUpdate;
import com.codetaylor.mc.artisanworktables.modules.worktables.network.SPacketWorktableTab;
import com.codetaylor.mc.artisanworktables.modules.worktables.network.SPacketWorktableTankDestroyFluid;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.workshop.TileEntityWorkshop;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.workshop.TileEntityWorkshopMage;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.workstation.TileEntityWorkstation;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.workstation.TileEntityWorkstationMage;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.worktable.TileEntityWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.worktable.TileEntityWorktableMage;
import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModuleWorktables
    extends ModuleBase {

  public static final String MOD_NAME = ModArtisanWorktables.NAME;
  public static final String MOD_ID = ModArtisanWorktables.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModArtisanWorktables.CREATIVE_TAB;
  public static final ModArtisanWorktables MOD_INSTANCE = ModArtisanWorktables.INSTANCE;

  public static class Lang {

    public static final String WORKTABLE_TITLE = "tile." + MOD_ID + ".worktable.%s.name";
    public static final String WORKSTATION_TITLE = "tile." + MOD_ID + ".workstation.%s.name";
    public static final String WORKSHOP_TITLE = "tile." + MOD_ID + ".workshop.%s.name";

    public static final String JEI_TOOLTIP_SHAPELESS_RECIPE = "jei." + MOD_ID + ".tooltip.shapeless.recipe";
    public static final String JEI_TOOLTIP_CHANCE = "jei." + MOD_ID + ".tooltip.chance";
    public static final String JEI_XP_COST = "jei." + MOD_ID + ".xp.cost";
    public static final String JEI_XP_REQUIRED = "jei." + MOD_ID + ".xp.required";
    public static final String JEI_LEVEL_COST = "jei." + MOD_ID + ".level.cost";
    public static final String JEI_LEVEL_REQUIRED = "jei." + MOD_ID + ".level.required";

    public static final String GUI_TOOLTIP_FLUID_DESTROY = "gui." + MOD_ID + ".tooltip.fluid.destroy";
    public static final String GUI_TOOLTIP_FLUID_EMPTY = "gui." + MOD_ID + ".tooltip.fluid.empty";
  }

  public static class Textures {

    public static final String WORKTABLE_GUI = "textures/gui/worktable_%s.png";
    public static final String WORKSTATION_GUI = "textures/gui/workstation_%s.png";
    public static final String WORKSHOP_GUI = "textures/gui/workshop_%s.png";
  }

  public static class Blocks {

    public static final BlockWorktable WORKTABLE = new BlockWorktable();
    public static final BlockWorkstation WORKSTATION = new BlockWorkstation();
    public static final BlockWorkshop WORKSHOP = new BlockWorkshop();
  }

  public static IPacketService PACKET_SERVICE;

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
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker.builder.IZenRecipeBuilder"
    );

    this.registerIntegrationPlugin(
        "crafttweaker",
        "com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker.builder.IZenRecipeBuilderCopyStrategy"
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

    try {
      Field field = ArtisanAPI.class.getDeclaredField("MODULE_WORKTABLES_INSTANCE");
      field.setAccessible(true);

      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

      field.set(null, new ModuleWorktablesAPI_Impl(
          Loader.isModLoaded("gamestages"),
          ModuleWorktablesConfig.getAPIWrapper()
      ));

    } catch (Exception e) {
      throw new RuntimeException("Unable to inject module worktables API into ArtisanAPI", e);
    }

    super.onConstructionEvent(event);
  }

  @Override
  public void onNetworkRegister(IPacketRegistry registry) {

    // Server
    registry.register(SPacketWorktableTab.class, SPacketWorktableTab.class, Side.SERVER);
    registry.register(SPacketWorktableTankDestroyFluid.class, SPacketWorktableTankDestroyFluid.class, Side.SERVER);

    // Client
    registry.register(CPacketWorktableFluidUpdate.class, CPacketWorktableFluidUpdate.class, Side.CLIENT);
  }

  @Override
  public void onRegister(Registry registry) {

    if (ModuleWorktablesConfig.ENABLE_WORKTABLES) {

      registry.registerBlock(
          Blocks.WORKTABLE,
          new ItemWorktable(Blocks.WORKTABLE),
          BlockWorktable.NAME
      );

      registry.registerTileEntities(
          TileEntityWorktable.class,
          TileEntityWorktableMage.class
      );
    }

    if (ModuleWorktablesConfig.ENABLE_WORKSTATIONS) {

      registry.registerBlock(
          Blocks.WORKSTATION,
          new ItemWorktable(Blocks.WORKSTATION),
          BlockWorkstation.NAME
      );

      registry.registerTileEntities(
          TileEntityWorkstation.class,
          TileEntityWorkstationMage.class
      );
    }

    if (ModuleWorktablesConfig.ENABLE_WORKSHOPS) {

      registry.registerBlock(
          Blocks.WORKSHOP,
          new ItemWorktable(Blocks.WORKSHOP),
          BlockWorkshop.NAME
      );

      registry.registerTileEntities(
          TileEntityWorkshop.class,
          TileEntityWorkshopMage.class
      );
    }
  }

  @Override
  public void onClientRegister(Registry registry) {

    if (ModuleWorktablesConfig.ENABLE_WORKTABLES) {
      registry.registerClientModelRegistrationStrategy(Blocks.WORKTABLE.getModelRegistrationStrategy());
    }

    if (ModuleWorktablesConfig.ENABLE_WORKSTATIONS) {
      registry.registerClientModelRegistrationStrategy(Blocks.WORKSTATION.getModelRegistrationStrategy());
    }

    if (ModuleWorktablesConfig.ENABLE_WORKSHOPS) {
      registry.registerClientModelRegistrationStrategy(Blocks.WORKSHOP.getModelRegistrationStrategy());
    }
  }

}
