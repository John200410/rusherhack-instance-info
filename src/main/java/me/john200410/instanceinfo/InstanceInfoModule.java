package me.john200410.instanceinfo;

import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.SharedConstants;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.rusherhack.client.api.events.render.EventRender2D;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.event.stage.Stage;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.rusherhack.core.setting.BooleanSetting;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author John200410
 */
public class InstanceInfoModule extends ToggleableModule {
	
	/**
	 * Settings
	 */
	private final BooleanSetting account = new BooleanSetting("Account", true);
	private final BooleanSetting server = new BooleanSetting("Server", true);
	private final BooleanSetting skinIcon = new BooleanSetting("SkinIcon", true);
	
	/**
	 * Variables
	 */
	private String cachedAccountName = "";
	private boolean isUsingCustomIcon = false;
	
	public InstanceInfoModule() {
		super("InstanceInfo", "Add additional information to the game window's title", ModuleCategory.CLIENT);
		this.registerSettings(this.account, this.server, this.skinIcon);
		
		//enabled by default
		this.setToggled(true);
	}
	
	@Subscribe(stage = Stage.ALL)
	private void onUpdate(EventRender2D event) {
		this.update();
	}
	
	private void update() {
		final Window window = mc.getWindow();
		final String accountString = mc.getUser().getName();
		
		//icon update
		if(this.skinIcon.getValue()) {
			if(!this.cachedAccountName.equals(accountString)) {
				this.cachedAccountName = accountString;
				this.updateIcon(accountString);
			}
		} else if(this.isUsingCustomIcon) {
			this.setVanillaIcon();
		}
		
		//window title
		if(!this.account.getValue() && !this.server.getValue()) {
			return;
		}
		
		final ServerData serverData = mc.getCurrentServer();
		final String serverString = mc.level == null ? "Not Connected" : serverData == null ? "Singleplayer" : serverData.ip;
		
		String title = "";
		
		if(this.account.getValue()) {
			title += accountString;
		}
		
		if(this.server.getValue()) {
			if(!title.isEmpty()) {
				title += " - ";
			}
			title += serverString;
		}
		
		window.setTitle(title);
	}
	
	@Override
	public void onDisable() {
		if(this.isUsingCustomIcon) {
			this.setVanillaIcon();
		}
	}
	
	private void updateIcon(String accountString) {
		final String url = String.format("https://mc-heads.net/avatar/%s", accountString);
		
		NativeImage nativeImage = null;
		MemoryStack memoryStack = null;
		ArrayList<ByteBuffer> bufferList = new ArrayList<>();
		try {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
												 .uri(URI.create(url))
												 .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
												 .build();
			
			final HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
			nativeImage = NativeImage.read(response.body());
			memoryStack = MemoryStack.stackPush();
			
			GLFWImage.Buffer buffer = GLFWImage.malloc(1, memoryStack);
			ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);
			bufferList.add(byteBuffer);
			byteBuffer.asIntBuffer().put(nativeImage.getPixelsRGBA());
			buffer.position(0);
			buffer.width(nativeImage.getWidth());
			buffer.height(nativeImage.getHeight());
			buffer.pixels(byteBuffer);
			GLFW.glfwSetWindowIcon(mc.getWindow().getWindow(), buffer.position(0));
			
		} catch(Exception exception) {
			exception.printStackTrace();
		} finally {
			if(nativeImage != null) {
				nativeImage.close();
			}
			bufferList.forEach(MemoryUtil::memFree);
		}
	}
	
	private void setVanillaIcon() {
		try {
			mc.getWindow().setIcon(mc.getVanillaPackResources(), SharedConstants.getCurrentVersion().isStable() ? IconSet.RELEASE : IconSet.SNAPSHOT);
		} catch(IOException ignored) {
		}
		this.isUsingCustomIcon = false;
	}
	
}
