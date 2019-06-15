package com.lothrazar.arrowharvest;

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ChorusArrowMod.MODID, certificateFingerprint = "@FINGERPRINT@", updateJSON = "https://raw.githubusercontent.com/Lothrazar/ChorusArrow/master/update.json")
public class ChorusArrowMod {

  public static final String MODID = "arrowharvest";
  public static Logger logger;
  private Configuration config;
  private List<String> willDropAsBlock;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    config = new Configuration(event.getSuggestedConfigurationFile());
    config.load();
    String category = MODID;
    String[] harvestme = config.getStringList("willDropAsBlock", category, new String[] {
        "minecraft:chorus_flower",
        "minecraft:pumpkin",
        "minecraft:melon_block",
        "minecraft:cocoa",
        "minecraft:web",
    }, "Harvest these");
    willDropAsBlock = Arrays.asList(harvestme);
    config.save();
    MinecraftForge.EVENT_BUS.register(this);
  }

  public static ItemStack getMetadataDrop(IBlockState state) {
    Item item = Item.getItemFromBlock(state.getBlock());
    int meta = 0;
    if (item.getHasSubtypes()) {
      meta = state.getBlock().getMetaFromState(state);
    }
    return new ItemStack(item, 1, meta);
  }

  @SubscribeEvent
  public void onProjectileImpactEvent(ProjectileImpactEvent event) {
    if (event.getRayTraceResult() != null
        && event.getEntity() instanceof EntityArrow) {
      BlockPos pos = event.getRayTraceResult().getBlockPos();
      World world = event.getEntity().world;
      IBlockState blockState = world.getBlockState(pos);
      if (pos == null || world == null || blockState == null) {
        return;
      }
      if (UtilString.isInList(willDropAsBlock, blockState.getBlock().getRegistryName())) {
        if (world.destroyBlock(pos, false)
            && world.isRemote == false) {
          //        logger.info(block.getRegistryName() + " HARVEST");
          world.spawnEntity(new EntityItem(
              world,
              pos.getX(), pos.getY(), pos.getZ(),
              getMetadataDrop(blockState)));
        }
      }
    }
  }

  @EventHandler
  public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = "Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    if (logger == null) {
      System.out.println(msg);
    }
    else {
      logger.error(msg);
    }
  }
}
