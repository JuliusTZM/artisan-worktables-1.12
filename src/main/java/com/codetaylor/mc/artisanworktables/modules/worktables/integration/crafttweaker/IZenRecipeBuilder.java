package com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.artisanworktables.IRecipeBuilder")
public interface IZenRecipeBuilder {

  @ZenMethod
  IZenRecipeBuilder setShaped(IIngredient[][] ingredients);

  @ZenMethod
  IZenRecipeBuilder setShapeless(IIngredient[] ingredients);

  /*
  @ZenMethod
  IZenRecipeBuilder copyRecipe(ICraftingRecipe recipe);
  */

  @ZenMethod
  IZenRecipeBuilder copyRecipeByName(String recipeName);

  @ZenMethod
  IZenRecipeBuilder copyRecipeInputByName(String recipeName);

  /**
   * Copies recipe ingredients and outputs for all the recipe outputs provided.
   * <p>
   * This method is mutually exclusive with the following methods and can't be called
   * on the same builder: {@link IZenRecipeBuilder#copyRecipeByName(String)},
   * {@link IZenRecipeBuilder#copyRecipeInputByName(String)},
   * {@link IZenRecipeBuilder#copyRecipeOutputByName(String)}.
   *
   * @param recipeOutput  outputs of the recipes to copy
   * @param excludeOutput true to not copy recipe outputs, defaults to false
   * @return builder
   */
  @ZenMethod
  IZenRecipeBuilder copyRecipes(IIngredient[] recipeOutput, @Optional boolean excludeOutput);

  @ZenMethod
  IZenRecipeBuilder setFluid(ILiquidStack fluidIngredient);

  @ZenMethod
  IZenRecipeBuilder setSecondaryIngredients(IIngredient[] secondaryIngredients);

  @ZenMethod
  IZenRecipeBuilder setMirrored();

  @Deprecated
  @ZenMethod
  IZenRecipeBuilder setTool(IIngredient tool, int damage);

  @ZenMethod
  IZenRecipeBuilder addTool(IIngredient tool, int damage);

  @ZenMethod
  IZenRecipeBuilder addOutput(IItemStack output, @Optional int weight);

  @ZenMethod
  IZenRecipeBuilder copyRecipeOutputByName(String recipeName);

  @ZenMethod
  IZenRecipeBuilder setExtraOutputOne(IItemStack output, float chance);

  @ZenMethod
  IZenRecipeBuilder setExtraOutputTwo(IItemStack output, float chance);

  @ZenMethod
  IZenRecipeBuilder setExtraOutputThree(IItemStack output, float chance);

  @ZenMethod
  IZenRecipeBuilder requireGameStages(String require, String[] stages);

  @ZenMethod
  IZenRecipeBuilder excludeGameStages(String[] stages);

  @ZenMethod
  IZenRecipeBuilder setMinimumTier(int minimumTier);

  @ZenMethod
  IZenRecipeBuilder setExperienceRequired(int experienceRequired);

  @ZenMethod
  IZenRecipeBuilder setLevelRequired(int levelRequired);

  @ZenMethod
  IZenRecipeBuilder setConsumeExperience(boolean consumeExperience);

  @ZenMethod
  IZenRecipeBuilder create();

}
