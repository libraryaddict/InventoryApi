package me.libraryaddict.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.libraryaddict.inventory.events.PagesTurnEvent;
import me.libraryaddict.inventory.events.PagesClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public final class PageInventory extends ClickInventory {

    protected ItemStack backAPage, forwardsAPage, exitInventory;
    protected int currentPage;
    protected boolean dynamicInventorySize = true;
    private int inventorySize = 54;
    protected boolean pageDisplayedInTitle;
    protected HashMap<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    protected String title = "Inventory";
    private String titleFormat = "%Title% - Page %Page%";

    public PageInventory(Player player) {
        this(null, player);
    }

    public PageInventory(Player player, boolean dymanicInventory) {
        this(null, player, dymanicInventory);
    }

    public PageInventory(Player player, int inventorySize) {
        this(null, player, inventorySize);
    }

    public PageInventory(String inventoryName, Player player) {
        super(inventoryName, player);
    }

    public PageInventory(String inventoryName, Player player, boolean dymanicInventory) {
        super(inventoryName, player);
        dynamicInventorySize = dymanicInventory;
    }

    public PageInventory(String inventoryName, Player player, int inventorySize) {
        super(inventoryName, player);
        this.inventorySize = Math.min(54, (int) (Math.ceil((double) inventorySize / 9)) * 9);
        this.dynamicInventorySize = false;
        pages.put(0, new ItemStack[0]);
    }

    public ItemStack getExitInventory() {
        return exitInventory;
    }

    public void setExitInventory(ItemStack item) {
        this.exitInventory = item;
    }

    /**
     * Get the itemstack which is the backpage
     */
    public ItemStack getBackPage() {
        if (backAPage == null) {
            backAPage = InventoryApi.setNameAndLore(new ItemStack(Material.SIGN), ChatColor.RED + "Back",
                    new String[] { ChatColor.BLUE + "Click this to move back" });
        }
        return backAPage;
    }

    /**
     * Get the current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Get the itemstack which is the next page
     */
    public ItemStack getForwardsPage() {
        if (forwardsAPage == null) {
            forwardsAPage = InventoryApi.setNameAndLore(new ItemStack(Material.SIGN), ChatColor.RED + "Forward",
                    new String[] { ChatColor.BLUE + "Click this to move forward" });
        }
        return forwardsAPage;
    }

    @Override
    public String getTitle() {
        return getPageTitle();
    }

    /**
     * Get the items in a page
     */
    public ItemStack[] getPage(int pageNumber) {
        if (pages.containsKey(pageNumber))
            return pages.get(pageNumber);
        return null;
    }

    /**
     * Get pages
     */
    public HashMap<Integer, ItemStack[]> getPages() {
        return pages;
    }

    protected String getPageTitle() {
        return (this.isPageDisplayedInTitle() ? titleFormat.replace("%Title%", title).replace("%Page%",
                (getCurrentPage() + 1) + "") : title);
    }

    public boolean isPageDisplayedInTitle() {
        return this.pageDisplayedInTitle;
    }

    protected void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (this.checkInMenu(event.getRawSlot())) {
            if (item != null) {
                int newPage = 0;
                if (item.equals(getBackPage())) {
                    newPage = -1;
                } else if (item.equals(getForwardsPage())) {
                    newPage = 1;
                }
                if (newPage != 0) {
                    PagesTurnEvent newEvent = new PagesTurnEvent(this, event.getSlot(), event, getCurrentPage() + newPage);
                    Bukkit.getPluginManager().callEvent(newEvent);
                    if (!newEvent.isCancelled()) {
                        setPage(getCurrentPage() + newPage);
                    }
                    event.setCancelled(true);
                    return;
                }
            }
            int slot = event.getSlot();
            if (isPlayerInventory()) {
                slot -= 9;
                if (slot < 0) {
                    slot += 36;
                }
            }
            PagesClickEvent itemClickEvent = new PagesClickEvent(this, slot, event);
            if (!isModifiable()) {
                itemClickEvent.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(itemClickEvent);
            if (itemClickEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } else if (!this.isModifiable() && event.isShiftClick() && item != null && item.getType() != Material.AIR) {
            for (int slot = 0; slot < currentInventory.getSize(); slot++) {
                ItemStack invItem = currentInventory.getItem(slot);
                if (invItem == null || invItem.getType() == Material.AIR
                        || (invItem.isSimilar(item) && item.getAmount() < item.getMaxStackSize())) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    /**
     * Opens the inventory for use
     */
    public void openInventory() {
        if (isInventoryInUse())
            return;
        saveContents();
        ItemStack[] pageItems = getItemsForPage();
        if (currentInventory == null) {
            if (isPlayerInventory()) {
                currentInventory = getPlayer().getInventory();
            } else {
                currentInventory = Bukkit.createInventory(null, pageItems.length, getPageTitle());
            }
        }
        setItems(pageItems);
        openInv();
    }

    private ItemStack[] getItemsForPage() {
        ItemStack[] pageItems = pages.get(Math.max(getCurrentPage(), 0));
        int pageSize = pageItems.length;
        if (pages.size() > 1 || this.getExitInventory() != null) {
            pageSize += 9;
        }
        if (!this.dynamicInventorySize || isPlayerInventory()) {
            pageSize = isPlayerInventory() ? 36 : inventorySize;
        } else {
            pageSize = (int) (((pageSize + 8) / 9) * 9);
        }
        pageItems = Arrays.copyOf(pageItems, pageSize);
        if (getCurrentPage() > 0 || getExitInventory() != null) {
            pageItems[pageItems.length - 9] = getCurrentPage() == 0 ? getExitInventory() : getBackPage();
        }
        if (pages.size() - 1 > getCurrentPage()) {
            pageItems[pageItems.length - 1] = getForwardsPage();
        }
        return pageItems;
    }

    /**
     * Sets the itemstack which is the back page
     */
    public void setBackPage(ItemStack newBack) {
        backAPage = newBack;
    }

    /**
     * Sets the itemstack which is the forwards page
     */
    public void setForwardsPage(ItemStack newForwards) {
        forwardsAPage = newForwards;
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i < pages.size(); i++) {
            ItemStack[] itemArray = pages.get(i);
            for (int a = 0; a < itemArray.length - (pages.size() > 1 ? 9 : 0); a++) {
                items.add(itemArray[a]);
            }
        }
        return items;
    }

    /**
     * Moves the inventory to a page
     */
    public void setPage(int newPage) {
        if (pages.containsKey(newPage)) {
            currentPage = newPage;
            if (isInventoryInUse()) {
                ItemStack[] pageItems = getItemsForPage();
                if (!isPlayerInventory()
                        && (pageItems.length != currentInventory.getSize() || !currentInventory.getTitle().equalsIgnoreCase(
                                getPageTitle()))) {
                    currentInventory = Bukkit.createInventory(null, pageItems.length, getPageTitle());
                    currentInventory.setContents(pageItems);
                    openInv();
                } else {
                    setItems(pageItems);
                }
            }
        }
    }

    public void setPageDisplayedInTitle(boolean displayPage) {
        if (this.isPageDisplayedInTitle() != displayPage) {
            this.pageDisplayedInTitle = displayPage;
            if (isInventoryInUse()) {
                setPage(getCurrentPage());
            }
        }
    }

    /**
     * @Title = %Title%
     * @Page = %Page%
     */
    public void setPageDisplayTitleFormat(String titleFormat) {
        this.titleFormat = titleFormat;
        if (isInventoryInUse()) {
            setPage(getCurrentPage());
        }
    }

    /**
     * Auto fills out the pages with these items
     */
    public void setPages(ArrayList<ItemStack> allItems) {
        setPages(allItems.toArray(new ItemStack[allItems.size()]));
    }

    /**
     * Auto fills out the pages with these items
     */
    public void setPages(ItemStack... allItems) {
        pages.clear();
        int invPage = 0;
        boolean usePages = getExitInventory() != null || allItems.length > inventorySize;
        ItemStack[] items = null;
        int currentSlot = 0;
        int baseSize = isPlayerInventory() ? 36 : inventorySize;
        for (int currentItem = 0; currentItem < allItems.length; currentItem++) {
            if (items == null) {
                int newSize = allItems.length - currentItem;
                if (usePages && newSize + 9 > baseSize) {
                    newSize = baseSize - 9;
                } else if (newSize > baseSize) {
                    newSize = baseSize;
                }
                items = new ItemStack[newSize];
            }
            ItemStack item = allItems[currentItem];
            items[currentSlot++] = item;
            if (currentSlot == items.length) {
                pages.put(invPage, items);
                invPage++;
                currentSlot = 0;
                items = null;
            }
        }
        if (pages.keySet().size() < getCurrentPage())
            currentPage = pages.keySet().size() - 1;
        if (allItems.length == 0) {
            int size = isPlayerInventory() ? 36 : inventorySize;
            if (!isPlayerInventory() && dynamicInventorySize) {
                size = 9;
            }
            items = InventoryApi.generateEmptyPage(size);
            if (getExitInventory() != null) {
                items[0] = getExitInventory();
            }
            pages.put(0, items);
        }
        setPage(getCurrentPage());
    }

    public PageInventory setPlayerInventory() {
        super.setPlayerInventory();
        return this;
    }

    /**
     * Sets the title of the next page opened
     */
    public void setTitle(String newTitle) {
        if (!getTitle().equals(newTitle)) {
            title = newTitle;
            if (isInventoryInUse()) {
                setPage(getCurrentPage());
            }
        }
    }

}
