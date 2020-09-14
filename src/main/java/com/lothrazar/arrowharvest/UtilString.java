/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.arrowharvest;

import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class UtilString {

  /**
   * One day i might make this a setting or an input arg for now i have no use to turn it off
   */
  public static final boolean matchWildcard = true;

  /**
   * If the list has "hc:*_sapling" and input is "hc:whatever_sapling" then match is true
   * 
   * @param list list of config values
   * @param toMatch the resource location to match
   * @deprecated
   * @return whether the resloc is in the list
   */
  public static boolean isInList(final List<String> list, ResourceLocation toMatch) {
    if (toMatch == null || list == null) {
      return false;
    }
    out:
    for (String strFromList : list) {
      if (strFromList == null || strFromList.isEmpty()) {
        continue out; // just ignore me
      }

      String[] blockIdArray = strFromList.split(":");
      
      // Invalid
      if (blockIdArray.length < 2) {
        ChorusArrowMod.logger.error("Invalid config value for block: " + strFromList);
        continue out;
      }
      
      // Mod ID checking
      String modIdFromList = blockIdArray[0];
      String modIdToMatch = toMatch.getResourceDomain();
      if (modIdFromList.equals(modIdToMatch) == false) {
        continue out;
      }
      
      // ID
      String blockIdFromList = blockIdArray[1]; // has the wildcard id
      String blockIdToMatch = toMatch.getResourcePath();
      
      // Wildcard check
      if (matchWildcard) {
        String blockIdListWCRegex = blockIdFromList.replace("*", ".*");
        if (Pattern.matches(blockIdListWCRegex, blockIdToMatch) == false) {
          continue out;
        }
        return true;
      } 
      
      // Face value id check
      else {
        if (blockIdFromList.equals(blockIdToMatch)) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * If the list has "hc:*_sapling:3" and input is "hc:whatever_sapling:3" then match is true
   * 
   * @param list list of config values
   * @param blockState the block state to match
   * @return whether the blockstate is in the list
   */
  public static boolean isInList(final List<String> list, IBlockState blockState) {
    ResourceLocation toMatch = blockState.getBlock().getRegistryName();
    
    if (toMatch == null || list == null) {
      return false;
    }
    
    out:
    for (String strFromList : list) {
      if (strFromList == null || strFromList.isEmpty()) {
        continue out; // just ignore me
      }

      String[] blockIdArray = strFromList.split(":");
      
      // Invalid
      if (blockIdArray.length < 2) {
        ChorusArrowMod.logger.error("Invalid config value for block: " + strFromList);
        continue out;
      }
      
      // Mod ID checking
      String modIdFromList = blockIdArray[0];
      String modIdToMatch = toMatch.getResourceDomain();
      if (modIdFromList.equals(modIdToMatch) == false) {
        continue out;
      }
      
      // ID w/o meta (wildcard meta falls under here)
      String blockIdFromList = blockIdArray[1]; // has the wildcard id
      String blockIdToMatch = toMatch.getResourcePath();
      
      // ID w/meta (meta check)
      if (blockIdArray.length == 3 && "*".equals(blockIdArray[2]) == false) {
        try {
          int blockMeta = Integer.parseInt(blockIdArray[2]);
          if (blockMeta != blockState.getBlock().getMetaFromState(blockState)) {
            continue out;
          }
        } catch (NumberFormatException e) {
          ChorusArrowMod.logger.error("Invalid meta value for block: " + strFromList);
        }
      }
      
      // Wildcard check
      if (matchWildcard) {
        String blockIdListWCRegex = blockIdFromList.replace("*", ".*");
        if (Pattern.matches(blockIdListWCRegex, blockIdToMatch)) {
          return true;
        }
      } 
      
      // Face value id check
      else {
        if (blockIdFromList.equals(blockIdToMatch)) {
          return true;
        }
      }
    }
    return false;
  }
}
