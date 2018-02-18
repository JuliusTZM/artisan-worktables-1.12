package com.codetaylor.mc.artisanworktables.modules.tools.material;

import com.codetaylor.mc.athenaeum.parser.recipe.item.MalformedRecipeItemException;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CustomMaterialListConverter {

  private CustomMaterialConverter customMaterialConverter;

  public CustomMaterialListConverter(CustomMaterialConverter customMaterialConverter) {

    this.customMaterialConverter = customMaterialConverter;
  }

  public List<CustomMaterial> convert(DataCustomMaterialList data, Logger logger) {

    List<CustomMaterial> result = new ArrayList<>();
    List<DataCustomMaterial> list = data.getList();

    for (DataCustomMaterial dataCustomMaterial : list) {

      try {
        result.add(this.customMaterialConverter.convert(dataCustomMaterial));

      } catch (MalformedRecipeItemException e) {
        logger.error("", e);
      }
    }

    return result;
  }
}
