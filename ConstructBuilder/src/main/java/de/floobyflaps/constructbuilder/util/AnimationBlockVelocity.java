package de.floobyflaps.constructbuilder.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import de.floobyflaps.constructbuilder.ConstructBuilder;

public class AnimationBlockVelocity implements Animation {

	@SuppressWarnings("deprecation")
	public void animateBlockSet(Location Loc, Object[] Data) {
		Location Spawn = Loc.clone().add(0.5,0, 0.5);
		MaterialData md = new MaterialData(Material.valueOf(Data[1].toString()));
		md.setData((Byte) Data[0]);
		FallingBlock fb = Spawn.getWorld().spawnFallingBlock(Spawn, md.getItemType(), md.getData());
		fb.setDropItem(false);
		fb.setGlowing(true);
		fb.setVelocity(new Vector(0,0.5,0));
		onTick(fb, Loc);
	}
	/**
	 * Tickt den Block, lässt ihn kurz vor den Aufprall löschen,
	 * verhindert fehlplatzierung
	 * @param e Fallender Block
	 * @param Loc ZielPosition des Fallenden Blocks
	 */
	public void onTick(final FallingBlock e,final Location Loc){
		Bukkit.getScheduler().runTaskLater(ConstructBuilder.getInstance(), new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				e.remove();
				Loc.getBlock().setTypeIdAndData(e.getBlockId(), e.getBlockData(), true);
				Loc.getWorld().playSound(Loc, Sound.ENTITY_ENDERDRAGON_SHOOT, 0.01f, 0.5f);
				Loc.getWorld().spawnParticle(Particle.NOTE,e.getLocation().add(0.5, 1, 0),40);
				Loc.getWorld().spawnParticle(Particle.FLAME,e.getLocation().add(0, 1, 0.5),4);
				Loc.getWorld().spawnParticle(Particle.NOTE,e.getLocation().add(0.5, 1, 0.5),4);
				Loc.getWorld().spawnParticle(Particle.SNOWBALL,e.getLocation().add(0, 1, 0),40);
				Loc.getWorld().spawnParticle(Particle.NOTE,e.getLocation().add(-0.5, 1, 0),4);
				Loc.getWorld().spawnParticle(Particle.FLAME,e.getLocation().add(0, 1, -0.5),4);
				Loc.getWorld().spawnParticle(Particle.NOTE,e.getLocation().add(-0.5, 1, -0.5),40);
			}
		}, 20);
	}
	
	public String getAnimationName() {
		return "BlockVelocity";
	}

	public void animateEnd(final Location Loc, Builder build,double radius) {
		final double radius1 = radius+1;
		Bukkit.getScheduler().runTaskLater(ConstructBuilder.getInstance(),new Runnable() {
			public void run() {
				for(int i = 0; i <= 360; i+=5){
					Location Loc1 = Kreis(i, radius1, Loc);
					Loc1.getWorld().spawnParticle(Particle.END_ROD,Loc1,4);
				}
				Loc.add(0,0.5,0);
				for(int i = 0; i <= 360; i+=5){
					Location Loc1 = Kreis(i, radius1, Loc);
					Loc1.getWorld().spawnParticle(Particle.END_ROD,Loc1,4);
				}
				Loc.add(0,0.5,0);
				for(int i = 0; i <= 360; i+=5){
					Location Loc1 = Kreis(i, radius1, Loc);
					Loc1.getWorld().spawnParticle(Particle.END_ROD,Loc1,4);
				}
			}
		}, 20);
	}
	
	/**
	 * Lässt ein Kreis-Punkt ausrechnen
	 * @param Grad Grad-punkt des Kreises
	 * @param radius Radius des kreises
	 * @param Loc Mittelpunkt des Kreises
	 * @return Kreis - Punkt
	 */
	
	public static Location Kreis(double Grad,double radius,Location Loc){
		double x = Math.sin((2*Math.PI/360)*Grad)*radius;
		double z = Math.cos((2*Math.PI/360)*Grad)*radius;
		return Loc.clone().add(x, 0, z);
	}

}
