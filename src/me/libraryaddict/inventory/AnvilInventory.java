package me.libraryaddict.inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import me.libraryaddict.inventory.events.AnvilClickEvent;
import me.libraryaddict.inventory.events.AnvilTypeEvent;
import net.minecraft.server.v1_7_R4.ContainerAnvil;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.util.com.google.common.base.Objects;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

public class AnvilInventory extends ClickInventory {
    private class AnvilContainer extends ContainerAnvil {
        private String n;

        public AnvilContainer(EntityHuman entity) {
            super(entity.inventory, entity.world, 0, 0, 0, entity);
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }

        @Override
        public void a(String origString) {
            AnvilTypeEvent event = new AnvilTypeEvent(AnvilInventory.this, origString);
            Bukkit.getPluginManager().callEvent(event);
            String newString = event.getString();
            if (!event.isCancelled()) {
                this.n = newString;
                itemName = origString;
                setItemName(newString);
                if (getSlot(2).hasItem()) {
                    net.minecraft.server.v1_7_R4.ItemStack itemstack = getSlot(2).getItem();

                    if (StringUtils.isBlank(newString))
                        itemstack.t();
                    else {
                        itemstack.c(this.n);
                    }
                }
            }

            e();
        }

    }

    public enum AnvilSlot {
        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

        private int slot;

        private AnvilSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return null;
        }
    }

    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
    private String itemName;
    private HashMap<Object, Object> data = new HashMap<Object, Object>();

    public void setItemName(String newString) {
        if (!Objects.equal(newString, itemName)) {
            ItemBuilder builder = new ItemBuilder(Material.PAPER);
            ItemStack item = currentInventory.getItem(AnvilSlot.OUTPUT.getSlot());
            if (item != null && item.getType() != Material.AIR) {
                builder = new ItemBuilder(item);
            }
            builder.setRawTitle(newString);
            this.itemName = newString;
            setSlot(AnvilSlot.INPUT_LEFT, builder.build());
        }
    }

    public Object getData(Object obj) {
        if (data.containsKey(obj)) {
            return data.get(obj);
        }
        return null;
    }

    public AnvilInventory setData(Object obj, Object value) {
        data.put(obj, value);
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public AnvilInventory(String inventoryName, Player player) {
        super(inventoryName, player);
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
        if (currentInventory != null) {
            currentInventory.setItem(slot.getSlot(), item);
        }
        if (item != null && item.hasItemMeta()) {
            this.itemName = item.getItemMeta().getDisplayName();
            if (itemName == null) {
                itemName = "";
            }
        }
    }

    public void openInventory() {
        if (isInventoryInUse()) {
            return;
        }
        EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();

        AnvilContainer container = new AnvilContainer(p);
        openInv();
        int c = p.nextContainerCounter();

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
        StructureModifier<Object> mods = packet.getModifier();
        mods.write(0, c);
        mods.write(1, 8);
        mods.write(2, "Repairing");
        mods.write(3, 9);
        mods.write(4, true);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Set their active container to the container
        p.activeContainer = container;

        // Set their active container window id to that counter stuff
        p.activeContainer.windowId = c;

        // Add the slot listener
        p.activeContainer.addSlotListener(p); // Set the items to the items from the inventory given
        currentInventory = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) {
            currentInventory.setItem(slot.getSlot(), items.get(slot));
        }
    }

    public void closeInventory(boolean forceClose) {
        currentInventory.clear();
        super.closeInventory(forceClose);
    }
    public void closeInventory() {
        currentInventory.clear();
        super.closeInventory();
    }

    public ItemStack getItem(AnvilSlot slot) {
        ItemStack item = currentInventory == null ? items.get(slot) : currentInventory.getItem(slot.getSlot());
        ItemBuilder builder = new ItemBuilder(item);
        builder.setRawTitle(getItemName());
        return builder.build();
    }

    @Override
    protected void onInventoryClick(InventoryClickEvent event) {
        if (event.getRawSlot() < 3) {
            AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilInventory.this, AnvilSlot.bySlot(event.getRawSlot()), event);
            if (!this.isModifiable()) {
                clickEvent.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(clickEvent);
            if (clickEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (event.isShiftClick()) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void setTitle(String newTitle) {
    }

}
