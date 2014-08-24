package me.libraryaddict.inventory.events;

import me.libraryaddict.inventory.NamedInventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NamedCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private NamedInventory inv;

    public NamedCloseEvent(NamedInventory inventory) {
        this.inv = inventory;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NamedInventory getInventory() {
        return inv;
    }

    public Player getPlayer() {
        return inv.getPlayer();
    }

    public String getName() {
        return getInventory().getName();
    }

}
