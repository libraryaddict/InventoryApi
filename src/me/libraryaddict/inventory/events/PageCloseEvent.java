package me.libraryaddict.inventory.events;

import me.libraryaddict.inventory.PageInventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PageCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private PageInventory inv;

    public PageCloseEvent(PageInventory inventory) {
        this.inv = inventory;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public PageInventory getInventory() {
        return inv;
    }

    public Player getPlayer() {
        return inv.getPlayer();
    }

    public String getName() {
        return getInventory().getName();
    }

}
