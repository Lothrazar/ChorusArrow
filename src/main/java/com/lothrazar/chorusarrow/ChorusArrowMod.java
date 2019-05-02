package com.lothrazar.chorusarrow;

import org.apache.logging.log4j.Logger;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ChorusArrowMod.MODID, certificateFingerprint = "@FINGERPRINT@")
public class ChorusArrowMod {

  public static final String MODID = "chorusarrow";
  private static Logger logger;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onProjectileImpactEvent(ProjectileImpactEvent event) {
    if (event.getRayTraceResult() != null && event.getEntity() instanceof EntityArrow) {
      BlockPos pos = event.getRayTraceResult().getBlockPos();
      World world = event.getEntity().world;
      if (world.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER) {
        //do it. but true isnt dropping it so
        world.destroyBlock(pos, false);
        if (world.isRemote == false)
          world.spawnEntity(new EntityItem(
              world,
              pos.getX(), pos.getY(), pos.getZ(),
              new ItemStack(Blocks.CHORUS_FLOWER)));
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
