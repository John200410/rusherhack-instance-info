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
		this.getLogger().info("InstanceInfo loaded!");
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("InstanceInfo unloaded!");
	}
	
}
