package de.floobyflaps.constructbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.floobyflaps.constructbuilder.command.ConstructCommandExecutor;
import de.floobyflaps.constructbuilder.event.BuildRegister;
import de.floobyflaps.constructbuilder.event.TemplateRegister;
import de.floobyflaps.constructbuilder.util.Animation;
import de.floobyflaps.constructbuilder.util.AnimationBlockFall;
import de.floobyflaps.constructbuilder.util.AnimationBlockVelocity;
import de.floobyflaps.constructbuilder.util.Builder;

public class ConstructBuilder extends JavaPlugin {
  
  @Override
  public void onEnable() {
      instance = this;
      ConstructCommandExecutor cce = new ConstructCommandExecutor();
      getCommand("CBuild").setExecutor(cce);
      getCommand("CBuild").setTabCompleter(cce);
      Bukkit.getPluginManager().registerEvents(new TemplateRegister(), this);
      Bukkit.getPluginManager().registerEvents(new BuildRegister(), this);
      LoadFile();
      AddAnimation(new AnimationBlockFall());
      AddAnimation(new AnimationBlockVelocity());
      if(Animationstyp == null){
    	  Animationstyp = getAnimationen().get(0).getAnimationName();
      }
      Builder.OnEnable();
  }
  
  @Override
	public void onDisable() {
		WriteFile();
	}
  
  /**
   * Für das Lesen von der letzten Animation
   * 
   */
  private static void LoadFile(){
	  File f = new File(getInstance().getDataFolder(),"Animation.txt");
	  if(!f.exists()){
		  try {
			f.createNewFile();
		} catch (IOException e) {}
	  }
	  try {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
		Object[] o = br.lines().toArray();
		if(o.length > 0){
			Animationstyp = o[0].toString();
		}
		br.close();
	} catch (Exception e) {}
  }
  
  /**
   * Für das Speichern von der momentanen Animation
   * 
   */
  private static void WriteFile(){
	  File f = new File(getInstance().getDataFolder(),"Animation.txt");
	  if(!f.exists()){
		  try {
			f.createNewFile();
		} catch (IOException e) {}
	  }
	  try {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		bw.write(Animationstyp);
		bw.close();
	} catch (Exception e) {}
  }
  
  public static String Animationstyp;
  
  public static void AddAnimation(Animation a){
	  Animationen.add(a);
  }
  /**
   * Sucht nach einer registrierten Animation
   * @param s AnimationsName
   * @return Animation
   */
  public static Animation getAnimationOverName(String s){
	  for(Animation a : getAnimationen()){
		  if(a.getAnimationName().equals(s)){
			  return a;
		  }
	  }
	  return null;
  }
  
  public static ArrayList<Animation> getAnimationen() {
	return Animationen;
  }
  
  private static ArrayList<Animation> Animationen = new ArrayList<Animation>();
  
  private static ConstructBuilder instance;
  
  public static ConstructBuilder getInstance() {
      return instance;
  }
  

}
