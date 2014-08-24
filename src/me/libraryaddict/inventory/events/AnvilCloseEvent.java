package me.libraryaddict.inventory.events;

import me.libraryaddict.inventory.AnvilInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AnvilCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private AnvilInventory inv;

    public AnvilCloseEvent(AnvilInventory inventory) {
        this.inv = inventory;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public AnvilInventory getInventory() {
        return inv;
    }

    public Player getPlayer() {
        return inv.getPlayer();
    }

    public String getName() {
        return getInventory().getName();
    }

}
