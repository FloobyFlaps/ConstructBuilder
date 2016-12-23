package de.floobyflaps.constructbuilder.event;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TemplateRegister implements Listener {
	
	public static final char PARAGRAPH = '\u00a7';
	public static HashMap<String, Location> loc1 = new HashMap<String, Location>();
	public static HashMap<String, Location> loc2 = new HashMap<String, Location>();

	public static ArrayList<Player> internPlayers = new ArrayList<Player>();
	/**
	 * Wird beim Event zur eindeutigen
	 * Aussage benutzt
	 * @return Ein eindeutigen Itemstack
	 */
	public static ItemStack registerItem(){
		ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(PARAGRAPH+"8Der Makierer");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(PARAGRAPH+"e");
		lore.add(PARAGRAPH+"7Mit Rechtsklick und Linksklick");
		lore.add(PARAGRAPH+"7registrierst du die Eckpunkte!");
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
	/**
	 * Setzt die Punkte für ein Bereich für das
	 * erstellen von Vorlagen
	 * @param e Wird von bukkit übergeben
	 */
	@EventHandler
	public void onBreak(PlayerInteractEvent e){
		if(e.getClickedBlock() != null){
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getInventory().
					getItemInMainHand().equals(registerItem()) ) {
				Location Loc = e.getClickedBlock().getLocation();
				loc1.put(e.getPlayer().getName().toLowerCase(), Loc);
				e.getPlayer().sendMessage(PARAGRAPH+"3Loc1 auf (X:"+Loc.getBlockX()+";"
						+ "Y:"+Loc.getBlockY()+";Z:"+Loc.getBlockZ()+")");
			}else if(e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().getInventory().
					getItemInMainHand().equals(registerItem()) ){
				e.setCancelled(true);
				Location Loc = e.getClickedBlock().getLocation();
				loc2.put(e.getPlayer().getName().toLowerCase(), Loc);
				e.getPlayer().sendMessage(PARAGRAPH+"3Loc2 auf (X:"+Loc.getBlockX()+";"
						+ "Y:"+Loc.getBlockY()+";Z:"+Loc.getBlockZ()+")");
			}
		}
	}

}
