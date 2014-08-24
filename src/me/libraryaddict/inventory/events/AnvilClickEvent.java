package me.libraryaddict.inventory.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.inventory.AnvilInventory;
import me.libraryaddict.inventory.AnvilInventory.AnvilSlot;

public class AnvilClickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled;
    private AnvilInventory inv;
    private AnvilSlot slot;
    private InventoryClickEvent event;

    public AnvilClickEvent(AnvilInventory inventory, AnvilSlot slot, InventoryClickEvent invEvent) {
        this.inv = inventory;
        this.event = invEvent;
        this.slot = slot;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public AnvilInventory getInventory() {
        return inv;
    }

    public ItemStack getItemStack() {
        return inv.getItem(slot);
    }

    public InventoryClickEvent getEvent() {
        return event;
    }

    public String getName() {
        return getInventory().getName();
    }
    
    public String getItemName() {
        return getInventory().getItemName();
    }

    public AnvilSlot getSlot() {
        return slot;
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
