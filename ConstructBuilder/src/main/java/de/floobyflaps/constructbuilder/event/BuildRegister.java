package de.floobyflaps.constructbuilder.event;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.floobyflaps.constructbuilder.util.Builder;
import de.floobyflaps.constructbuilder.util.TemplateCreator.Vorlage;

public class BuildRegister implements Listener {
	
	public static HashMap<String, Object[]> map = new HashMap<String, Object[]>();
	
	/**
	 * Itemstack zur eindeutigen Erkennung
	 * bei einem Event
	 * @return ein Eindeutigen Itemstack
	 */
	
	public static ItemStack verifier(){
		ItemStack item = new ItemStack(Material.BOWL,1);
		ItemMeta m = item.getItemMeta();
		m.setDisplayName('\u00a7'+"cKlick mich auf den Boden"); //§
		item.setItemMeta(m);
		return item;
	}
	
	/**
	 * Nach diesem Event wird aus einer Vorlage
	 * ein Builder für das System
	 * @param e Wird von Bukkit übergeben
	 */
	@EventHandler
	public void onint(PlayerInteractEvent e){
		if(e.getClickedBlock() != null){
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK &&
					e.getPlayer().getInventory().getItemInMainHand().equals(verifier())){
				e.getPlayer().getInventory().setItemInMainHand(null);
				Object[] o = map.get(e.getPlayer().getName().toLowerCase());
				if(o != null){
					Builder.startBuild(e.getClickedBlock().getLocation().add(0, 1, 0), new Builder((Vorlage)o[0], (Integer) o[1]));
					map.remove(e.getPlayer().getName().toLowerCase());
				}
			}
		}
	}

}
