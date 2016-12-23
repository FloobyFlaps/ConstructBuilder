package de.floobyflaps.constructbuilder.util;

import org.bukkit.Location;

public interface Animation{

	/**
	 * 
	 * @return Den Namen einer registierten Animation
	 */
	
	public String getAnimationName();
	/**
	 * Animierung des zusetzenden Blocks
	 * @param Data Die Werte für den Block
	 * @param Loc Die Position wo der Block hin soll
	 * 
	 */
	public void animateBlockSet(Location Loc,Object[] Data);
	/**
	 * Animierung nach Abschluss des Konstrukts
	 * @param build Der Builder der Animation
	 * @param Loc Die Mitte des Bereiches
	 * 
	 */
	public void animateEnd(Location Loc,Builder build,double radius);
	
}
