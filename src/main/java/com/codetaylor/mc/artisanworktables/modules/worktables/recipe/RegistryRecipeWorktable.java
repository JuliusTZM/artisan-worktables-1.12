package com.codetaylor.mc.artisanworktables.modules.worktables.recipe;

import com.codetaylor.mc.artisanworktables.modules.worktables.ModuleWorktables;
import com.codetaylor.mc.artisanworktables.modules.worktables.reference.EnumTier;
import com.codetaylor.mc.artisanworktables.modules.worktables.tile.spi.CraftingMatrixStackHandler;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RegistryRecipeWorktable {

  private List<IRecipeWorktable> recipeList;

  public RegistryRecipeWorktable() {

    this.recipeList = new ArrayList<>();
  }

  public List<IRecipeWorktable> getRecipeList(List<IRecipeWorktable> result) {

    result.addAll(this.recipeList);
    return result;
  }

  public List<IRecipeWorktable> getRecipeListByTier(List<IRecipeWorktable> result, EnumTier tier) {

    for (IRecipeWorktable recipe : this.recipeList) {

      if (recipe.matchTier(tier)) {
        result.add(recipe);
      }
    }

    return result;
  }

  public IRecipeWorktable addRecipe(IRecipeWorktable recipe) {

    this.recipeList.add(recipe);
    return recipe;
  }

  @Nullable
  public IRecipeWorktable findRecipe(
      EntityPlayer player,
      ItemStack[] tools,
      CraftingMatrixStackHandler craftingMatrix,
      @Nullable FluidStack fluidStack,
      ISecondaryIngredientMatcher secondaryIngredientMatcher
  ) {

    Collection<String> unlockedStages;

    if (ModuleWorktables.MOD_LOADED_GAMESTAGES
        && player != null) {
      unlockedStages = this.getUnlockedStages(player);

    } else {
      unlockedStages = Collections.emptySet();
    }

    for (IRecipeWorktable recipe : this.recipeList) {

      if (recipe.matches(unlockedStages, tools, craftingMatrix, fluidStack, secondaryIngredientMatcher)) {
        return recipe;
      }
    }

    return null;
  }

  public boolean containsRecipeWithToolInSlot(ItemStack tool, int toolIndex) {

    for (IRecipeWorktable recipe : this.recipeList) {

      if (recipe.isValidTool(tool, toolIndex)) {
        return true;
      }
    }

    return false;
  }

  public boolean containsRecipeWithToolInAnySlot(ItemStack tool) {

    for (IRecipeWorktable recipe : this.recipeList) {
      int toolCount = recipe.getToolCount();

      for (int i = 0; i < toolCount; i++) {

        if (recipe.isValidTool(tool, i)) {
          return true;
        }
      }
    }

    return false;
  }

  private Collection<String> getUnlockedStages(EntityPlayer player) {

    return PlayerDataHandler.getStageData(player).getUnlockedStages();
  }
}
