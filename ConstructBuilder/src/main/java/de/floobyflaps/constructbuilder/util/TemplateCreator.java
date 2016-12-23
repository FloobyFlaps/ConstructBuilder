package de.floobyflaps.constructbuilder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import de.floobyflaps.constructbuilder.ConstructBuilder;

public class TemplateCreator {
	
	/**
	 * 
	 * @param Name Name des Templates
	 * @param Loc1 Erste Koordinate des ausgew‰hlten Bereichs
	 * @param Loc2 Zweite Koordinate des ausgew‰hlten Bereichs
	 * @throws IllegalAccessException Bei falschen Welten oder
	 * 	vergebenen Namen
	 */
	
	@SuppressWarnings("deprecation")
	public TemplateCreator(String Name,Location Loc1,Location Loc2) 
			throws IllegalAccessException {
		if(!Loc1.getWorld().getName().equals(Loc2.getWorld().getName())){
			throw new IllegalArgumentException("Loc1 und Loc2 m¸ssen"
					+ "die gleichen Welten aufweisen!");
		}
		Map<String, Object> map = GetFileContent();
		if(!map.containsKey(Name)){
			HashMap<String, Object> TemplateDaten = new HashMap<String, Object>();
			//Herausfinden der kleinsten Koordinate/Ordinate
			double kleinX;
			double groﬂX;
			kleinX = (Loc1.getX() > Loc2.getX() ? Loc2.getX() : Loc1.getX());
			groﬂX = (Loc1.getX() > Loc2.getX() ? Loc1.getX() : Loc2.getX());
			double kleinY;
			double groﬂY;
			kleinY = (Loc1.getY() > Loc2.getY() ? Loc2.getY() : Loc1.getY());
			groﬂY = (Loc1.getY() > Loc2.getY() ? Loc1.getY() : Loc2.getY());
			double kleinZ;
			double groﬂZ;
			kleinZ = (Loc1.getZ() > Loc2.getZ() ? Loc2.getZ() : Loc1.getZ());
			groﬂZ = (Loc1.getZ() > Loc2.getZ() ? Loc1.getZ() : Loc2.getZ());
			//Erstellen der Maxime-Locations
			Location klein = new Location(Loc1.getWorld(), kleinX, kleinY, kleinZ);
			Location groﬂ = new Location(Loc1.getWorld(), groﬂX, groﬂY, groﬂZ);
			//Jetzt auslesen der BlockDaten
			/*
			 * Konstrukt soll sich zuerst in x und z
			 * und dann nach y (oben) aufbauen.
			 * 
			 */
			//KontrollVariablen
			int y1 = 0;
			int x1 = 0;
			int z1 = 0;
			Object [] [] [] Data = new Object
					[((groﬂ.getBlockY()-klein.getBlockY())+1)]
					[((groﬂ.getBlockX()-klein.getBlockX())+1)]
					[((groﬂ.getBlockZ()-klein.getBlockZ())+1)];
			for(int y = klein.getBlockY(); y <= groﬂ.getBlockY(); y++){
				x1 = 0;
				for(int x = klein.getBlockX(); x <= groﬂ.getBlockX(); x++){
					z1 = 0;
					for(int z = klein.getBlockZ(); z <= groﬂ.getBlockZ(); z++){
						Block b = klein.clone().add(x1, y1, z1).getBlock();
						if(b.getType() == Material.AIR) {
							//Unnˆtige Luft Ignorieren
						}else{
							Data [y1] [x1] [z1] = new Object[]{b.getData(),b.getType().toString()};
						}
						z1 += 1;
					}
					x1 += 1;
				}
				y1 += 1;
			}
			//Speichern der Daten
			TemplateDaten.put("3D_Array", Data);
			TemplateDaten.put("Klein", klein.serialize());
			TemplateDaten.put("Groﬂ", groﬂ.serialize());
			map.put(Name, TemplateDaten);
			WriteFile(map);
		}else{
			throw new IllegalArgumentException("Name ist schon vergeben!");
		}
	}
	/**
	 * 
	 * @return Alle Vorlagen in Form von der Klasse Vorlage
	 */
	public static List<Vorlage> getTemplates(){
		Map<String, Object> map = GetFileContent();
		Iterator<String> it = map.keySet().iterator();
		List<Vorlage> alleVorlagen = new ArrayList<TemplateCreator.Vorlage>();
		while(it.hasNext()){
			String name = it.next();
			alleVorlagen.add(new Vorlage(map.get(name), name));
		}
		return alleVorlagen;
	}
	
	/**
	 * 
	 * 	@return Alle Vorlagen in Form eigener Daten
	 */
	
	public static Map<String, Object> getRawTemplateData(){
		return GetFileContent();
	}
	
	/**
	 * Liest die Vorlagen Datenbank aus
	 * @return Alle gespeicherten Vorlagen
	 */
	private static Map<String, Object> GetFileContent(){
		File f = new File(ConstructBuilder.getInstance().getDataFolder(),"/Vorlagen.db"); 
		//Erstellt eine Datenbank im relativen Ordner des Plugin namen
		if(f.exists()){
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) ois.readObject();
				ois.close();
				return map;
			} catch (FileNotFoundException e) {
				System.err.println("ConstructBuilder >> Vorlagen Datenbank fehlt!");
			} catch (IOException e) {
				System.err.println("ConstructBuilder >> Fehler bei Laden der Vorlagen Datenbank!");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("ConstructBuilder >> Vorlagen Datenbank[Klasse nicht gefunden]!");
			}
		}
		/* Falls die Datei noch nicht vorhanden ist
		 * oder ein Fehler kommt, wird
		 * eine Leere Map zur¸ck gegeben
		 * 
		 */
		return new HashMap<String, Object>();
	}
	
	/**
	 * Schreibt die Vorlagen in die Vorlagen Datenbank
	 * 
	 * @param SerializedData Alle Vorlagen
	 */
	private static void WriteFile(Map<String, Object> SerializedData) {
		File f = new File(ConstructBuilder.getInstance().getDataFolder(),"/Vorlagen.db"); 
		//Erstellt eine Datenbank im relativen Ordner des Plugin namen
		if(!f.exists()) {
			try {
				f.getParentFile().mkdirs();
				f.getParentFile().mkdir();
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("ConstructBuilder >> Vorlagen Datenbank konnte nicht erstellt werden!");
				e.printStackTrace();
			}
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(SerializedData);
			oos.close();
		} catch (FileNotFoundException e) {
			System.err.println("ConstructBuilder >> Vorlagen Datenbank fehlt!");
		} catch (IOException e) {
			System.err.println("ConstructBuilder >> Fehler bei Laden der Vorlagen Datenbank!");
			e.printStackTrace();
		}
	}
	
	public static class Vorlage{
		/**
		 * Versucht eine Vorlage zu laden
		 * @param Name Ist der Name der Vorlage
		 */
		public Vorlage(String Name){
			this(GetFileContent().get(Name),Name);
		}
		@SuppressWarnings("unchecked")
		private Vorlage(Object Map,String Name) {
			if(Map == null || Name == null){
				throw new IllegalArgumentException("Template ist nicht gespeichert!");
			}else{
				this.Name = Name;
				Map<String, Object> map = (java.util.Map<String, Object>) Map;
				locKlein = Location.deserialize((java.util.Map<String, Object>) map.get("Klein"));
				locGroﬂ = Location.deserialize((java.util.Map<String, Object>) map.get("Groﬂ"));
				Data = (Object[][][]) map.get("3D_Array");
			}
		}
		private String Name;
		private Location locKlein;
		private Location locGroﬂ;
		private Object[][][] Data;
		public Object[][][] getData() {
			return Data;
		}
		/**
		 * 
		 * @return Die groesste Ecke des Bereiches
		 */
		public Location getLocGroﬂ() {
			return locGroﬂ;
		}
		/**
		 * 
		 * @return Die kleinste Ecke des Bereiches
		 */
		public Location getLocKlein() {
			return locKlein;
		}
		/**
		 * 
		 * @return Den Namen der Vorlage
		 */
		public String getName() {
			return Name;
		}
		/**
		 * Lˆscht die Vorlage in der Datenbank
		 */
		public void lˆschen(){
			Map<String, Object> map = GetFileContent();
			if(map.containsKey(Name)){
				map.remove(Name);
				WriteFile(map);
			}
		}
	}
}
