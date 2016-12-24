package de.floobyflaps.constructbuilder.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.floobyflaps.constructbuilder.ConstructBuilder;
import de.floobyflaps.constructbuilder.event.BuildRegister;
import de.floobyflaps.constructbuilder.event.TemplateRegister;
import de.floobyflaps.constructbuilder.util.Animation;
import de.floobyflaps.constructbuilder.util.TemplateCreator;
import de.floobyflaps.constructbuilder.util.TemplateCreator.Vorlage;

public class ConstructCommandExecutor implements CommandExecutor, TabCompleter{

	public static final String PERM_RANDOM = "cbuild.random";
	public static final String PERM_KONST = "cbuild.konstrukte";
	public static final String PERM_ANIM = "cbuild.animation";
	public static final String PERM_VORLAGE = "cbuild.vorlage";
	public static final String PERM_LÖSCHEN = "cbuild.loeschen";
	public static final String PERM_ADMIN = "cbuild.*";
	public static final char PARAGRAPH = '\u00a7';
	
	/*
	 * Alle Befehle:
	 *  - /CBuild löschen <VorlagenName>
	 *  - /CBuild addVorlage <Name>
	 *  - /CBuild Random <Blöcke pro Sekunde|Linien pro Sekunde>
	 *  - /CBuild Vorlage <VorlagenName> <Blöcke pro Sekunde|Linien pro Sekunde>
	 *  - /CBuild setCurrentAnimation <AnimationsArt>
	 *   
	 *  Information:
	 *    - Blöcke Pro Sekunde in einer Schrittweite von 0.2 ab 0.2
	 *    - Animationsarten sowie Vorlagennamen sieht man mit Tabulator
	 *    - 
	 */
	/**
	 * Tabulator Events
	 * 
	 */
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		ArrayList<String> h = new ArrayList<String>();
		if(cmd.getName().equalsIgnoreCase("CBuild")){
			if(s.hasPermission(PERM_ADMIN)||
					s.hasPermission(PERM_ANIM)||
					s.hasPermission(PERM_KONST)||
					s.hasPermission(PERM_LÖSCHEN)||
					s.hasPermission(PERM_RANDOM)||
					s.hasPermission(PERM_VORLAGE)){
				if(args.length == 1){
					h.add("löschen");
					h.add("Random");
					h.add("Vorlage");
					h.add("addVorlage");
					h.add("setCurrentAnimation");
				}else if(args.length == 2){
					if(args[0].equalsIgnoreCase("Vorlage") || args[0].equalsIgnoreCase("löschen")){
						//Lädt alle Vorlagen
						Iterator<String> it = TemplateCreator.getRawTemplateData().keySet().iterator();
						while(it.hasNext()){
							h.add(it.next());
						}
					}else if(args[0].equalsIgnoreCase("setCurrentAnimation")){
						for(Animation a : ConstructBuilder.getAnimationen()){
							h.add(a.getAnimationName());
						}
					}
				}
			}
		}
		return h;
	}

	/**
	 * Alle Kommandos
	 * 
	 */
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		Player p = null;
		if(s instanceof Player){
			p = (Player) s;
		}
		if(cmd.getName().equalsIgnoreCase("CBuild")){
			if (p == null) {
				s.sendMessage("Dieser Befehl ist ausschließlich fuer Spieler gedacht!");
				return true;
			}
			if (args.length == 2) {
				//CMD - LÖSCHEN
				if (args[0].equalsIgnoreCase("löschen")){
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_LÖSCHEN)) {
						try{
							Vorlage v = new TemplateCreator.Vorlage(args[1]);
							v.löschen();
							p.sendMessage(PARAGRAPH+"aVorlage gelöscht!");
						}catch(Exception e){
							p.sendMessage(PARAGRAPH+"cDie Vorlage gibt es nicht!");
						}
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_LÖSCHEN+"!");
					}
					//CMD - VORLAGE ADDEN
				} else if (args[0].equalsIgnoreCase("addVorlage")) {
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_VORLAGE)) {
						if(TemplateRegister.internPlayers.contains(p)){
							TemplateRegister.internPlayers.remove(p);
							Location Loc1 = TemplateRegister.loc1.remove(p.getName().toLowerCase());
							Location Loc2 = TemplateRegister.loc2.remove(p.getName().toLowerCase());
							try{
								new TemplateCreator(args[1], Loc1, Loc2);
							}catch(Exception e){
								p.sendMessage(PARAGRAPH+"cVorlage konnte nicht erstellt werden.");
								return true;
							}
							p.sendMessage(PARAGRAPH+"aVorlage erstellt!");
						}else{
							TemplateRegister.internPlayers.add(p);
							p.getInventory().setItemInMainHand(TemplateRegister.registerItem());
							p.sendMessage(PARAGRAPH+"aDu kannst nun ein Bereich mit dem Item makieren!\n"
									+ "Gib am Ende wieder /CBuild addVorlage <Name> ein!");
						}
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_VORLAGE+"!");
					}
					//CMD- RANDOM
				} else if (args[0].equalsIgnoreCase("Random")) {
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_RANDOM)) {
						Integer i = null;
						ArrayList<Vorlage> a = (ArrayList<Vorlage>) TemplateCreator.getTemplates();
						int i1 = (int) ((Math.random() * a.size())); 
						Vorlage v = a.get(i1);
						try{
							i = Integer.parseInt(args[1]);
							if(i < 1){
								throw new NumberFormatException();
							}
						}catch(NumberFormatException e){
							boolean b1 = true;
							try{
								if(args[1].substring(args[1].length()-1).equals("L")){
									b1 = false;
								}
								i = Integer.parseInt(args[1].substring(0, args[1].length()-1));
								if(i < 1){
									throw new NumberFormatException();
								}
								i = -i;
							}catch(Exception e1){
								p.sendMessage(PARAGRAPH+"cDie Zahl ist keine Ganzzahl oder unter 1!");
								return true;
							}
							if(b1){
								p.sendMessage(PARAGRAPH+"cDie Zahl ist keine Ganzzahl oder unter 1!");
								return true;
							}
						}
						p.sendMessage(PARAGRAPH+"aMache ein Rechtsklick auf den Boden!");
						p.getInventory().setItemInMainHand(BuildRegister.verifier());
						BuildRegister.map.put(p.getName().toLowerCase(), new Object[]{v,i});
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_RANDOM+"!");
					}
					//CMD - ANIMATION
				} else if (args[0].equalsIgnoreCase("setCurrentAnimation")) {
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_ANIM)) {
						Animation a = ConstructBuilder.getAnimationOverName(args[1]);
						if(a != null){
							ConstructBuilder.Animationstyp = a.getAnimationName();
							p.sendMessage(PARAGRAPH+"aAnimation gesetzt!");
						}else{
							p.sendMessage(PARAGRAPH+"7Die Animation existiert nicht!");
						}
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_ANIM+"!");
					}
				} else {
					//Gucken nach möglichen Kommandos
					//Löschen
					if(args[0].toLowerCase().startsWith("l")){
						if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_LÖSCHEN)) {
							p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
									+ PARAGRAPH+"7 - /CBuild Löschen <VorlagenName>");
						} else {
							p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_LÖSCHEN+"!");
						}
						//Random
					}else if(args[0].toLowerCase().startsWith("r")){
						if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_RANDOM)) {
							p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
									+ PARAGRAPH+"7 - /CBuild Random <Blöcke pro Sekunde|Linien pro Sekunde>");
						} else {
							p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_RANDOM+"!");
						}
						//Animation
					}else if(args[0].toLowerCase().startsWith("s")){
						if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_ANIM)) {
							p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
									+ PARAGRAPH+"7  - /CBuild setCurrentAnimation <AnimationsArt>");
						} else {
							p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_ANIM+"!");
						}
						//Vorlagen adden
					}else if (args[0].toLowerCase().startsWith("a")){
						if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_VORLAGE)) {
							p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
									+ PARAGRAPH+"7 - /CBuild addVorlage <Name>");
						} else {
							p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_VORLAGE+"!");
						}
					}else{
						//Oder Alle anderen möglichen Kommandos
						if (p.hasPermission(PERM_ADMIN)) {
							p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
									+ PARAGRAPH+"7 - /CBuild setCurrentAnimation <AnimationsArt> "+PARAGRAPH+"8"
											+ "oder\n"+PARAGRAPH+"7 - /CBuild Random <Blöcke pro Sekunde|Linien pro Sekunde> "+PARAGRAPH+"8oder\n"
													+ ""+PARAGRAPH+"7 - /CBuild addVorlage <Name> "+PARAGRAPH+"8oder\n"
															+ PARAGRAPH+"7 - /CBuild Löschen <VorlagenName>");
						} else {
							p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_ADMIN+"!");
						}
					}
				}
			} else if (args.length == 3) {
				//CMD - VORLAGE
				if (args[0].equalsIgnoreCase("Vorlage")) {
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_KONST)) {
						Vorlage v = null;
						Integer i = null;
						try{
							v = new Vorlage(args[1]);
						}catch(Exception e){
							p.sendMessage(PARAGRAPH+"cDie Vorlage konnte nicht geladen werden!");
							return true;
						}
						try{
							i = Integer.parseInt(args[2]);
							if(i < 1){
								throw new NumberFormatException();
							}
						}catch(NumberFormatException e){
							boolean b1 = true;
							try{
								if(args[2].substring(args[2].length()-1).equals("L")){
									b1 = false;
								}
								i = Integer.parseInt(args[2].substring(0, args[2].length()-1));
								if(i < 1){
									throw new NumberFormatException();
								}
								i = -i;
							}catch(Exception e1){
								p.sendMessage(PARAGRAPH+"cDie Zahl ist keine Ganzzahl oder unter 1!");
								return true;
							}
							if(b1){
								p.sendMessage(PARAGRAPH+"cDie Zahl ist keine Ganzzahl oder unter 1!");
								return true;
							}
						}
						p.sendMessage(PARAGRAPH+"aMache ein Rechtsklick auf den Boden!");
						p.getInventory().setItemInMainHand(BuildRegister.verifier());
						BuildRegister.map.put(p.getName().toLowerCase(), new Object[]{v,i});
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_KONST+"!");
					}
				} else {
					//Falsche Eingabe Gucken nach möglicher antwort
					if (p.hasPermission(PERM_ADMIN) || p.hasPermission(PERM_KONST)) {
						p.sendMessage(PARAGRAPH+"eMeintest du vielleicht:\n"
								+ PARAGRAPH+"7 - /CBuild Vorlage <VorlagenName> <Blöcke pro Sekunde|Linien pro Sekunde>");
					} else {
						p.sendMessage(PARAGRAPH+"cDir fehlt die Permission "+PERM_KONST+"!");
					}
				}
			}else{
				//Hilfe
				String Hilfe = PARAGRAPH+"eAlle Befehle:";
				if(p.hasPermission(PERM_ADMIN) ||
					p.hasPermission(PERM_ANIM) ||
					p.hasPermission(PERM_KONST) ||
					p.hasPermission(PERM_RANDOM)){
					if(p.hasPermission(PERM_ADMIN)){
						Hilfe += "\n"+PARAGRAPH+"7 - /CBuild addVorlage <Name>";
						Hilfe += "\n"+PARAGRAPH+"7 - /CBuild Random <Blöcke pro Sekunde>";
						Hilfe += "\n"+PARAGRAPH+"7 - /CBuild Vorlage <VorlagenName> <Blöcke pro Sekunde>";
						Hilfe += "\n"+PARAGRAPH+"7 - /CBuild setCurrentAnimation <AnimationsArt>";
						Hilfe += "\n"+PARAGRAPH+"bInfo:";
						Hilfe += "\n"+PARAGRAPH+"7Blöcke Pro Sekunde in einer Schrittweite von 1 ab 1";
						Hilfe += "\n"+PARAGRAPH+"7Animationsarten sowie Vorlagennamen sieht man mit einem Tabulator-Klick";
						Hilfe += "\n"+PARAGRAPH+"7Linien pro Sekunde ab 1; Schrittweite 1 und am Ende ein L Bsp (1L)";
					}else{
						if(p.hasPermission(PERM_VORLAGE)){
							Hilfe += "\n"+PARAGRAPH+"7 - /CBuild addVorlage <Name>";
						}
						if(p.hasPermission(PERM_RANDOM)){
							Hilfe += "\n"+PARAGRAPH+"7 - /CBuild Random <Blöcke pro Sekunde|Linien pro Sekunde>";
						}
						if(p.hasPermission(PERM_KONST)){
							Hilfe += "\n"+PARAGRAPH+"7 - /CBuild Vorlage <VorlagenName> <Blöcke pro Sekunde|Linien pro Sekunde>";
						}
						if(p.hasPermission(PERM_ANIM)){
							Hilfe += "\n"+PARAGRAPH+"7 - /CBuild setCurrentAnimation <AnimationsArt>";
						}
						Hilfe += "\n"+PARAGRAPH+"bInfo:";
						Hilfe += "\n"+PARAGRAPH+"7Blöcke Pro Sekunde in einer Schrittweite von 1 ab 1";
						Hilfe += "\n"+PARAGRAPH+"7Animationsarten sowie Vorlagennamen sieht man mit einem Tabulator-Klick";
						Hilfe += "\n"+PARAGRAPH+"7Linien pro Sekunde ab 1; Schrittweite 1 und am Ende ein L Bsp (1L)";
					}
				}else{
					//KEIN RECHT
					p.sendMessage(PARAGRAPH+"cDu besitzt keinen Zugriff auf diesen Befehl!\n"
							+ "Dir fehlt die Permission "+PERM_ADMIN+"!");
					return true;
				}
				p.sendMessage(Hilfe);
			}
			return true;
		}
		return false;
	}
}
