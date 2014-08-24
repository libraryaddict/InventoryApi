package me.libraryaddict.inventory.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.libraryaddict.inventory.NamedInventory;
import me.libraryaddict.inventory.Page;

public class NamedPageClickEvent extends ItemClickEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private NamedInventory inv;
    private Page page;

    public NamedPageClickEvent(NamedInventory inventory, Page page, int slot, InventoryClickEvent invEvent) {
        super(slot, invEvent);
        this.inv = inventory;
        this.page = page;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NamedInventory getInventory() {
        return inv;
    }

    public ItemStack getItemStack() {
        if (slot >= 0)
            return inv.getItem(slot);
        return null;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public Player getPlayer() {
        return inv.getPlayer();
    }

}
