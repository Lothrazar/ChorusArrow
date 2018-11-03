package com.lothrazar.chorusarrow;

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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ExampleMod.MODID)
public class ExampleMod {

  public static final String MODID = "chorusarrow";
  private static Logger logger;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    //        net.minecraftforge.event.entity.ProjectileImpactEvent
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onProjectileImpactEvent(ProjectileImpactEvent event) {
    if(event.getRayTraceResult()!=null && event.getEntity() instanceof EntityArrow) {
     BlockPos pos= event.getRayTraceResult().getBlockPos();
     World world=event.getEntity().world; 
     
     if(world.getBlockState(pos).getBlock() == Blocks.CHORUS_FLOWER) {
       //do it. but true isnt dropping it so
       world.destroyBlock(pos, false);
       if(world.isRemote==false)
       world.spawnEntity(new EntityItem(
           world,
           pos.getX(),pos.getY(),pos.getZ(),
           new ItemStack(Blocks.CHORUS_FLOWER)));
     }
     
    }
    
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    // some example code
    logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }
}
