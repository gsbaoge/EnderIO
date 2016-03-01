package crazypants.enderio.machine.vat;

import java.util.Random;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRenderMapper;
import crazypants.enderio.machine.soul.SoulBinderRenderMapper;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.IRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVat extends AbstractMachineBlock<TileVat> {

  public static BlockVat create() {
    PacketHandler.INSTANCE.registerMessage(PacketTanks.class,PacketTanks.class,PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketVatProgress.class, PacketVatProgress.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketDumpTank.class,PacketDumpTank.class,PacketHandler.nextID(), Side.SERVER);
    BlockVat res = new BlockVat();
    res.init();
    return res;
  }

  @SideOnly(Side.CLIENT)
  private static MachineRenderMapper RENDER_MAPPER;
  
  public BlockVat() {
    super(ModObject.blockVat, TileVat.class);
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVat) {
      return new ContainerVat(player.inventory, (TileVat) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileVat) {
      return new GuiVat(player.inventory, (TileVat) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_STILL;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
    // Spit some "steam" out the spout
    if (isActive(world, pos)) {
      TileVat te = (TileVat) world.getTileEntity(pos);
      float pX = pos.getX() + 0.5f;
      float pY = pos.getY() + 0.7f;
      float pZ = pos.getZ() + 0.5f;

      EnumFacing dir = te.getFacingDir();
      pX += 0.6f * dir.getFrontOffsetX();
      pZ += 0.6f * dir.getFrontOffsetZ();

      double velX = ((rand.nextDouble() * 0.075) + 0.025) * dir.getFrontOffsetX();
      double velZ = ((rand.nextDouble() * 0.075) + 0.025) * dir.getFrontOffsetZ();

      int num = rand.nextInt(4) + 2;
      for (int k = 0; k < num; k++) {
        EffectRenderer er = Minecraft.getMinecraft().effectRenderer;
        EntityFX fx = er.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), pX, pY, pZ, 1, 1, 1, 0);        
        fx.setRBGColorF(1 - (rand.nextFloat() * 0.2f), 1 - (rand.nextFloat() * 0.1f), 1 - (rand.nextFloat() * 0.2f));
        fx.setVelocity(velX, -0.06, velZ);
      }
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (RENDER_MAPPER == null) {
      RENDER_MAPPER = new SoulBinderRenderMapper();
    }
    return RENDER_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper(ItemStack stack) {
    if (RENDER_MAPPER == null) {
      RENDER_MAPPER = new SoulBinderRenderMapper();
    }
    return RENDER_MAPPER;
  }
}
