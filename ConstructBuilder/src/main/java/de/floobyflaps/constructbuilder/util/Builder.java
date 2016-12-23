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
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import de.floobyflaps.constructbuilder.ConstructBuilder;
import de.floobyflaps.constructbuilder.util.TemplateCreator.Vorlage;

public class Builder {
	
	/**
	 * 
	 * @param Loc Die Position an der Das Geb‰ude steht
	 * @param b Der Builder f¸r das Konstrukt
	 */
	
	public static void startBuild(Location Loc,Builder b){
		Location diff = b.getDifference();
		for(int x = 0; x <= diff.getBlockX(); x++){
			for(int y = 0; y <= diff.getBlockY(); y++){
				for(int z = 0; z <= diff.getBlockZ(); z++){
					Loc.clone().add(x, y, z).getBlock().setType(Material.AIR);
				}
			}
		}
		b.serialize(Loc);
		onTick(0, 0, 0, b,Loc);
	}
	
	/**
	 * L‰dt nicht beendete Konstruktionen neu
	 * 
	 */
	
	public static void OnEnable(){
		Bukkit.getScheduler().runTaskLater(ConstructBuilder.getInstance(), new Runnable() {
			public void run() {
				ArrayList<Builder> b = getAllAuftraege();
				for(Builder b1 : b){
					forceBuild(b1);
				}
			}
		}, 34);
	}
	
	/**
	 * 
	 * Baut ein nicht beendetes Konstrukt auf
	 * @param b Der Builder des Konstrukts
	 */
	private static void forceBuild(Builder b){
		Location diff = b.getDifference();
		for(int x = 0; x <= diff.getBlockX(); x++){
			for(int y = 0; y <= diff.getBlockY(); y++){
				for(int z = 0; z <= diff.getBlockZ(); z++){
					b.start.clone().add(x, y, z).getBlock().setType(Material.AIR);
				}
			}
		}
		onTick(0, 0, 0, b,b.start);
	}
	
	/**
	 * Die Methode baut das Konstrukt nach und nach auf
	 * @param x Konstrukt an der Stelle x
	 * @param y Konstrukt an der Stelle y
	 * @param z Konstrukt an der Stelle z
	 * @param b der Builder des Konstrukts
	 * @param Loc Stelle an dem das Konstrukt steht
	 */
	
	private static void onTick(int x,int y,int z,Builder b,Location Loc){
		boolean b1 = false;
		Animation a = ConstructBuilder.getAnimationOverName(ConstructBuilder.Animationstyp);
		if(b.getBlocksPerSecond() < 0){
			int i = -b.getBlocksPerSecond();
			Location dif = b.getDifference();
			for(int i1 = 0; i1 < i; i1++){
				boolean isEmpty = true;
				Object[] line = b.getData()[y][x];
				for(int z1 = 0; z1 < line.length; z1++){
					if(line[z1] != null){
						isEmpty = false;
					}
				}
				if(isEmpty){
					i1 -= 1;
				}else{
					for(int z1 = 0; z1 < line.length; z1++){
						if(line[z1] != null){
							a.animateBlockSet(Loc.clone().add(x, y, z1), (Object[])b.Data[y][x][z1]);
						}
					}
				}
				if(x == (dif.getBlockX())){
					x = 0;
					if(y == (dif.getBlockY())){
						b1 = true;
						b.Remove();
						break;
					}else{
						y+=1;
					}
				}else{
					x+=1;
				}
			}
		}else{
			int i = b.getBlocksPerSecond();
			Location dif = b.getDifference();
			for(int i1 = 0; i1 < i; i1++){
				if(b.Data[y][x][z] == null){
					i1 -= 1;
				}else{
					a.animateBlockSet(Loc.clone().add(x, y, z), (Object[])b.Data[y][x][z]);
				}
				if(z == (dif.getBlockZ())){
					z = 0;
					if(x == (dif.getBlockX())){
						x = 0;
						if(y == (dif.getBlockY())){
							b1 = true;
							b.Remove();
							break;
						}else{
							y+=1;
						}
					}else{
						x+=1;
					}
				}else{
					z+=1;
				}
			}
		}
		if(!b1){
			final int x1 = x;
			final int y1 = y;
			final int z1 = z;
			final Builder b2 = b;
			final Location Loc1 = Loc;
			Bukkit.getScheduler().runTaskLater(ConstructBuilder.getInstance(), new Runnable() {
				public void run() {
					onTick(x1, y1, z1, b2, Loc1);
				}
			}, 20);
		}else{
			final Builder b2 = b;
			final Animation a3 = a;
			final Location Loc1 = Loc;
			Bukkit.getScheduler().runTaskLater(ConstructBuilder.getInstance(), new Runnable() {
				public void run() {
					b2.Remove();
					Location middle = b2.getDifference();
					double x1 = ((double)middle.getBlockX()+1)/2;
					double z1 = ((double)middle.getBlockZ()+1)/2;
					double groesse = (x1 > z1 ? x1 : z1); 
					a3.animateEnd(Loc1.clone().add(x1,
							((double)middle.getBlockY()+1)/2,
							z1)
							,b2,groesse);
				}
			}, 20);
		}
	}
	
	public Builder(Vorlage v,int blocksPerSecond) {
		this(v.getLocKlein(),v.getLocGroﬂ(),v.getData(),blocksPerSecond);
	}
	/**
	 * @param klein Der kleinste Punkt in einem Bereich
	 * @param groﬂ Der grˆﬂte Punkt in einem Bereich
	 * @param Data Die Daten von den Blˆcken zwischen und auf
	 * den Punkten
	 */
	private int number;
	private Location klein;
	private Location groﬂ;
	private int blocksPerSecond;
	private Object[][][] Data;
	private Location start;
	public Builder(Location klein,Location groﬂ,Object [] [] [] Data,int blocksPerSecond){
		this.klein = klein;
		this.groﬂ = groﬂ;
		this.Data = Data;
		this.blocksPerSecond = blocksPerSecond;
	}
	
	private Builder(Location klein,Location groﬂ,Object[][][] Data,int number,int blocksPerSecond,Location start){
		this.klein = klein;
		this.groﬂ = groﬂ;
		this.Data = Data;
		this.number = number;
		this.blocksPerSecond = blocksPerSecond;
		this.start = start;
	}
	public int getNumber() {
		return number;
	}
	public Location getKlein() {
		return klein;
	}
	/**
	 * 
	 * 
	 * @return Die Differenzen zwischen jeweiliger Ordinate
	 */
	public Location getDifference(){ 
		return new Location(klein.getWorld(),
				groﬂ.getBlockX()-klein.getBlockX(),
				groﬂ.getBlockY()-klein.getBlockY(),
				groﬂ.getBlockZ()-klein.getBlockZ());
	}
	public int getBlocksPerSecond() {
		return blocksPerSecond;
	}
	public Location getGroﬂ() {
		return groﬂ;
	}
	public Object[][][] getData() {
		return Data;
	}
	/**
	 * 
	 * Speichert den Auftrag
	 * @param Loc Punkt an dem der Builder aufbaut
	 */
	private void serialize(Location Loc){
		Map<String, Object> map = ReadFile();
		number = map.keySet().size();
		Object[] o = new Object[]{klein.serialize(),groﬂ.serialize(),Data,number,blocksPerSecond,Loc.serialize()};
		map.put(""+map.keySet().size(), o);
		WriteFile(map);
	}
	/**
	 * Entfernt den Auftrag
	 * 
	 */
	public void Remove(){
		Map<String, Object> map = ReadFile();
		map.remove(""+number);
		for(int i = number+1; i < map.keySet().size(); i++){
			Object[] o = (Object[]) map.remove(""+i);
			o[3] = (((Integer)o[3])-1);
			map.put(""+(i-1), o);
		}
		WriteFile(map);
	}
	
	/**
	 * 
	 * 
	 * @return Alle Momenatenen Auftr‰ge
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Builder> getAllAuftraege(){
		ArrayList<Builder> b = new ArrayList<Builder>();
		Map<String, Object> map = ReadFile();
		for(String s : map.keySet()){
			Object[] o = (Object[]) map.get(s);
			b.add(new Builder(Location.deserialize((Map<String, Object>) o[0]),
					Location.deserialize((Map<String, Object>) o[1]),
					(Object[][][]) o[2],
					(Integer)o[3],
					(Integer)o[4],
					Location.deserialize((Map<String, Object>) o[5])));
		}
		return b;
	}
	
	/**
	 * Managen vom Lesen der Auftr‰ge
	 * @return Alle Auftr‰ge
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> ReadFile(){
		File f = new File(ConstructBuilder.getInstance().getDataFolder(),"Auftraege.db");
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			Object o = ois.readObject();
			ois.close();
			return (Map<String, Object>) o;
		}catch(Exception e){
			return new HashMap<String, Object>();
		}
	}
	
	/**
	 * 
	 * Managen vom Schreiben der Auftr‰ge
	 * @param map Auftr‰ge
	 */
	private static void WriteFile(Map<String, Object> map){
		File f = new File(ConstructBuilder.getInstance().getDataFolder(),"Auftraege.db");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(map);
			oos.close();
		} catch (FileNotFoundException e) {
			System.err.println("ConstructBuilder >> Auftrag Datenbank fehlt!");
		} catch (IOException e) {
			System.err.println("ConstructBuilder >> Fehler bei Laden der Auftrag Datenbank!");
			e.printStackTrace();
		}
	}

}
