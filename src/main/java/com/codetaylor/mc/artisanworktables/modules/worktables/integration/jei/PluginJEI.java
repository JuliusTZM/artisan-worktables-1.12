package com.codetaylor.mc.artisanworktables.modules.worktables.integration.jei;

import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktables;
import com.codetaylor.mc.artisanworktables.modules.worktables.api.WorktableAPI;
import com.codetaylor.mc.artisanworktables.modules.worktables.block.BlockWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.IRecipeWorktable;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RecipeWorktableShaped;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RecipeWorktableShapeless;
import com.codetaylor.mc.artisanworktables.modules.worktables.recipe.RegistryRecipeWorktable;
import mezz.jei.api.*;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PluginJEI
    implements IModPlugin {

  public static IRecipeRegistry RECIPE_REGISTRY;

  private IJeiHelpers jeiHelpers;

  @Override
  public void register(IModRegistry registry) {

    this.jeiHelpers = registry.getJeiHelpers();

    // Workbench
    for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
      registry.addRecipeCategories(this.createWorkbenchCategory(type));
    }

    for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
      registry.addRecipeCatalyst(
          new ItemStack(ModuleWorktables.Blocks.WORKTABLE, 1, type.getMeta()),
          PluginJEI.createUID(type)
      );
    }

    for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
      registry.handleRecipes(RecipeWorktableShaped.class, JEIRecipeWrapperWorktable::new, PluginJEI.createUID(type));
      registry.handleRecipes(RecipeWorktableShapeless.class, JEIRecipeWrapperWorktable::new, PluginJEI.createUID(type));
    }

    // TODO: recipe click area

    // Transfer handlers
    IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
    for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
      transferRegistry.addRecipeTransferHandler(new JEIRecipeTransferInfoWorktable(type, PluginJEI.createUID(type)));
    }

    for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
      List<IRecipeWorktable> recipeList = new ArrayList<>();
      RegistryRecipeWorktable recipeRegistry = WorktableAPI.RECIPE_REGISTRY_MAP.get(type.getName());
      recipeRegistry.getRecipeList(recipeList);
      registry.addRecipes(recipeList, PluginJEI.createUID(type));
    }
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    // Expose the recipe registry for use in the game stages event handler.
    RECIPE_REGISTRY = jeiRuntime.getRecipeRegistry();

    // If gamestages is loaded, hide all of the staged worktable recipes from JEI.
    if (ModuleWorktables.MOD_LOADED_GAMESTAGES) {

      for (BlockWorktable.EnumType type : BlockWorktable.EnumType.values()) {
        RegistryRecipeWorktable registry = WorktableAPI.RECIPE_REGISTRY_MAP.get(type.getName());

        if (registry != null) {
          List<IRecipeWorktable> recipeList = registry.getRecipeList(new ArrayList<>());

          for (IRecipeWorktable recipe : recipeList) {

            if (recipe.getGameStageName() != null) {
              IRecipeWrapper recipeWrapper = RECIPE_REGISTRY.getRecipeWrapper(recipe, PluginJEI.createUID(type));

              if (recipeWrapper != null) {
                RECIPE_REGISTRY.hideRecipe(recipeWrapper);
              }
            }
          }
        }
      }
    }
  }

  private JEICategoryWorktable createWorkbenchCategory(BlockWorktable.EnumType type) {

    return new JEICategoryWorktable(
        PluginJEI.createUID(type),
        this.createTitleTranslateKey(type),
        this.createBackground(type)
    );
  }

  private IDrawable createBackground(BlockWorktable.EnumType type) {

    IGuiHelper guiHelper = this.jeiHelpers.getGuiHelper();
    ResourceLocation resourceLocation = new ResourceLocation(
        ModuleWorktables.MOD_ID,
        String.format(ModuleWorktables.Textures.WORKTABLE_GUI, type.getName())
    );
    return guiHelper.createDrawable(resourceLocation, 0 + 3, 0 + 3, 176 - 6, 80);
  }

  private String createTitleTranslateKey(BlockWorktable.EnumType type) {

    return String.format(ModuleWorktables.Lang.WORKTABLE_TITLE, ModuleWorktables.MOD_ID, type.getName());
  }

  public static String createUID(BlockWorktable.EnumType type) {

    return ModuleWorktables.MOD_ID + "_" + type.getName();
  }
}
