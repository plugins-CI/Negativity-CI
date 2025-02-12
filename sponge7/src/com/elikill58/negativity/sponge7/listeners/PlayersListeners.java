package com.elikill58.negativity.sponge7.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerChangeWorldEvent;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerTeleportEvent;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge7.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge7.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge7.impl.location.SpongeWorld;

public class PlayersListeners {
	
	@Listener
	public void onPreLogin(ClientConnectionEvent.Auth e) {
		LoginEvent event = new LoginEvent(e.getProfile().getUniqueId(), e.getProfile().getName().orElse(null),
				e.isCancelled() ? Result.KICK_BANNED : Result.ALLOWED, e.getConnection().getAddress().getAddress(), e.getMessage().toPlain());
		EventManager.callEvent(event);
		e.setMessage(Text.of(event.getKickMessage()));
		e.setCancelled(!event.getLoginResult().equals(Result.ALLOWED));
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join e, @First Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpongePlayer(p));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, e.getMessage().toPlain());
		EventManager.callEvent(event);
		e.setMessage(Text.of(event.getJoinMessage()));
	}

	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e, @First Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpongePlayer(p));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, e.getMessage().toPlain());
		EventManager.callEvent(event);
		e.setMessage(Text.of(event.getQuitMessage()));
		NegativityPlayer.removeFromCache(p.getUniqueId());
	}

	@Listener
	public void onTeleport(MoveEntityEvent.Teleport e, @First Player p) {
		if(!e.getFromTransform().getExtent().equals(e.getToTransform().getExtent())) {
			EventManager.callEvent(new PlayerChangeWorldEvent(SpongeEntityManager.getPlayer(p), World.getWorld(e.getToTransform().getExtent().getName(), (a) -> new SpongeWorld(e.getToTransform().getExtent()))));
		}
		
		EventManager.callEvent(new PlayerTeleportEvent(SpongeEntityManager.getPlayer(p), SpongeLocation.toCommon(e.getFromTransform().getLocation()),
				SpongeLocation.toCommon(e.getToTransform().getLocation())));
	}
	
	@Listener
	public void onDeath(DestructEntityEvent.Death e, @First Player p){
		EventManager.callEvent(new PlayerDeathEvent(SpongeEntityManager.getPlayer(p)));
	}

	@Listener
	public void onInteract(InteractEvent e, @First Player p) {
		PlayerInteractEvent event = new PlayerInteractEvent(SpongeEntityManager.getPlayer(p), Action.LEFT_CLICK_AIR);
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}

	@Listener
	public void onItemConsume(UseItemStackEvent.Finish e, @First Player p) {
		PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(SpongeEntityManager.getPlayer(p), new SpongeItemStack(e.getItemStackInUse().createStack()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
}
