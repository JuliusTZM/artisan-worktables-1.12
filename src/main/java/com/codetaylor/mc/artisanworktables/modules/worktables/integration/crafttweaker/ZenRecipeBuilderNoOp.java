package com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;

public class ZenRecipeBuilderNoOp
    implements IZenRecipeBuilder {

  public static final ZenRecipeBuilderNoOp INSTANCE = new ZenRecipeBuilderNoOp();

  private ZenRecipeBuilderNoOp() {
    //
  }

  @Override
  public IZenRecipeBuilder setShaped(IIngredient[][] ingredients) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setShapeless(IIngredient[] ingredients) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setFluid(ILiquidStack fluidIngredient) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setMirrored() {

    return this;
  }

  @Override
  public IZenRecipeBuilder setTool(IIngredient tool, int damage) {

    return this;
  }

  @Override
  public IZenRecipeBuilder addOutput(IItemStack output, int weight) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setExtraOutputOne(IItemStack output, float chance) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setExtraOutputTwo(IItemStack output, float chance) {

    return this;
  }

  @Override
  public IZenRecipeBuilder setExtraOutputThree(IItemStack output, float chance) {

    return this;
  }

  @Override
  public IZenRecipeBuilder requireGameStages(String require, String[] stages) {

    return this;
  }

  @Override
  public IZenRecipeBuilder excludeGameStages(String[] stages) {

    return this;
  }

  @Override
  public void create() {
    //
  }
}
