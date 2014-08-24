package me.libraryaddict.inventory.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.libraryaddict.inventory.AnvilInventory;

public class AnvilTypeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled;
    private AnvilInventory inv;
    private String lettering;

    public AnvilTypeEvent(AnvilInventory inventory, String lettering) {
        this.inv = inventory;
        this.lettering = lettering;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public AnvilInventory getInventory() {
        return inv;
    }

    public String getString() {
        return lettering;
    }

    public String getName() {
        return getInventory().getName();
    }

    public void setString(String newString) {
        this.lettering = newString;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Player getPlayer() {
        return inv.getPlayer();
    }
}
