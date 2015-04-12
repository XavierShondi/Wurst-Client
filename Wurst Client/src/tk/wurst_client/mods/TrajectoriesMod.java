package tk.wurst_client.mods;

import java.awt.Color;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.darkstorm.minecraft.gui.util.RenderUtil;
import org.lwjgl.opengl.GL11;

import tk.wurst_client.events.EventManager;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;

@Info(category = Category.RENDER,
	description = "Shows projectiles' path of motion",
	name = "Trajectories")
public class TrajectoriesMod extends Mod implements RenderListener
{
	
	public double pZ = -9000.0D;
	public double pY = -9000.0D;
	public double pX = -9000.0D;
	
	@Override
	public void onEnable()
	{
		EventManager.render.addListener(this);
	}
	
	@Override
	public void onRender()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack item = player.inventory.getCurrentItem();
		if(item == null)
			return;
		float power = 0F;
		switch(Item.getIdFromItem(item.getItem()))
		{
			case 261:
				power = 3.2F;
				break;
			case 332:
			case 344:
			case 346:
			case 368:
				power = 1.6F;
				break;
			case 373:
			case 384:
				power = 0.8F;
				break;
			default:
				return;
		}
		
		float playerX = (float)player.posX;
		float playerY = (float)player.posY;
		float playerZ = (float)player.posZ;
		float playerYaw = player.rotationYaw;
		float playerPitch = player.rotationPitch;
		float var15 = playerYaw / 180.0F * 3.141593F;
		float var16 = MathHelper.sin(var15);
		float var17 = MathHelper.cos(var15);
		float var18 = playerPitch / 180.0F * 3.141593F;
		float var19 = MathHelper.cos(var18);
		float var20 = playerPitch / 180.0F * 3.141593F;
		float var21 = MathHelper.sin(var20);
		float origX = playerX - var17 * 0.16F;
		float origY = playerY - 0.1F;
		float origZ = playerZ - var16 * 0.16F;
		float nextSegX = -var16 * var19 * 0.4F;
		float nextSegY = -var21 * 0.4F;
		float nextSegZ = var17 * var19 * 0.4F;
		float var28 =
			MathHelper.sqrt_double(nextSegX * nextSegX + nextSegY * nextSegY
				+ nextSegZ * nextSegZ);
		nextSegX /= var28;
		nextSegY /= var28;
		nextSegZ /= var28;
		nextSegX *= power;
		nextSegY *= power;
		nextSegZ *= power;
		float newX = origX;
		float newY = origY;
		float newZ = origZ;
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		RenderUtil.setColor(Color.RED);
		GL11.glBegin(GL11.GL_LINES);
		for(int i = 0; i < 256; i++)
		{
			newX += nextSegX;
			newY += nextSegY;
			newZ += nextSegZ;
			Vec3 vecOrig = new Vec3(origX, origY, origZ);
			Vec3 vecNew = new Vec3(newX, newY, newZ);
			MovingObjectPosition movingObj =
				Minecraft.getMinecraft().theWorld.rayTraceBlocks(vecOrig,
					vecNew, false, true, false);
			if(movingObj != null)
			{
				origX = (float)movingObj.hitVec.xCoord;
				origY = (float)movingObj.hitVec.yCoord;
				origZ = (float)movingObj.hitVec.zCoord;
				break;
			}
			GL11.glVertex3d(origX - playerX, origY - playerY, origZ - playerZ);
			GL11.glVertex3d(newX - playerX, newY - playerY, newZ - playerZ);
			
			AxisAlignedBB bounding =
				AxisAlignedBB.fromBounds(origX - 0.125F, origY, origZ - 0.125F,
					origX + 0.125F, origY + 0.25F, origZ + 0.125F);
			float var4 = 0.0F;
			for(int var36 = 0; var36 < 5; var36++)
			{
				float var37 =
					(float)(bounding.minY + (bounding.maxY - bounding.minY)
						* var36 / 5.0D);
				float var38 =
					(float)(bounding.minY + (bounding.maxY - bounding.minY)
						* (var36 + 1) / 5.0D);
				AxisAlignedBB var39 =
					AxisAlignedBB.fromBounds(bounding.minX, var37,
						bounding.minZ, bounding.maxX, var38, bounding.maxZ);
				if(Minecraft.getMinecraft().theWorld.isAABBInMaterial(var39,
					Material.water))
					var4 += 0.2F;
			}
			float var40 = var4 * 2.0F - 1.0F;
			nextSegY += 0.04F * var40;
			float var37 = 0.92F;
			if(var4 > 0.0F)
			{
				var37 *= 0.9F;
				nextSegY *= 0.8F;
			}
			nextSegX *= var37;
			nextSegY *= var37;
			nextSegZ *= var37;
			
			origX = newX;
			origY = newY;
			origZ = newZ;
		}
		pX = origX;
		pY = origY;
		pZ = origZ;
		GL11.glVertex3d(pX - playerX - 0.1000000014901161D, pY - playerY, pZ
			- playerZ - 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX - 0.1000000014901161D, pY - playerY, pZ
			- playerZ + 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX + 0.1000000014901161D, pY - playerY, pZ
			- playerZ + 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX + 0.1000000014901161D, pY - playerY, pZ
			- playerZ - 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX + 0.1000000014901161D, pY - playerY, pZ
			- playerZ + 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX - 0.1000000014901161D, pY - playerY, pZ
			- playerZ + 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX - 0.1000000014901161D, pY - playerY, pZ
			- playerZ - 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX + 0.1000000014901161D, pY - playerY, pZ
			- playerZ - 0.1000000014901161D);
		GL11.glVertex3d(pX - playerX - 0.1000000014901161D, pY - playerY, pZ
			- playerZ - 0.1000000014901161D);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	@Override
	public void onDisable()
	{
		EventManager.render.removeListener(this);
	}
}