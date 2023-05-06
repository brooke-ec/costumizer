package net.nimajnebec.costumizer.protocol;

import io.netty.channel.ChannelPipeline;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.NoSuchElementException;

public class InterceptorInjector implements Listener {

    public static final String HANDLER_NAME = "costumizer_packet_interceptor";

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.injectInterceptor(player);
    }

    public void removeInterceptor(Player player) throws NoSuchElementException {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        handle.connection.connection.channel.pipeline().remove(HANDLER_NAME);
    }

    public void injectInterceptor(Player player) {
        PacketInterceptor handler = new PacketInterceptor(player);
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        ChannelPipeline pipeline = handle.connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", HANDLER_NAME, handler);
    }
}
