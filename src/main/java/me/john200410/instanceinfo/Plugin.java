package me.john200410.instanceinfo;

import org.rusherhack.client.api.RusherHackAPI;

/**
 * RusherHack plugin that adds additional information to the game window's title
 *
 * @author John200410
 */
public class Plugin extends org.rusherhack.client.api.plugin.Plugin {
	
	@Override
	public void onLoad() {
		RusherHackAPI.getModuleManager().registerFeature(new InstanceInfoModule());
		this.getLogger().info(this.getName() + " loaded!");
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info(this.getName() + " unloaded!");
	}
	
	@Override
	public String getName() {
		return "InstanceInfo";
	}
	
	@Override
	public String getVersion() {
		return "v1.0";
	}
	
	@Override
	public String getDescription() {
		return "Adds additional information to the game window's title";
	}
	
	@Override
	public String[] getAuthors() {
		return new String[]{"John200410"};
	}
}
