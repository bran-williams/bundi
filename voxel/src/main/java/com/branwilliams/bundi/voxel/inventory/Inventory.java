package com.branwilliams.bundi.voxel.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public static final int DEFAULT_MAX_INVENTORY_ITEMS = 64;

    private List<Item> items;

    private int heldItem;

    private int maxItems;

    public Inventory() {
        this(DEFAULT_MAX_INVENTORY_ITEMS);
    }

    public Inventory(int maxItems) {
        this.maxItems = maxItems;
        this.items = new ArrayList<>();
    }

    public void clearItems() {
        items.clear();
        heldItem = 0;
    }

    public void addItems(Inventory inventory) {
        for (Item item : inventory.getItems()) {
            this.addItem(item);
        }
    }

    public boolean addItem(Item item) {
        if (isFull())
            return false;
        return this.items.add(item);
    }

    public Item removeItem(int index) {
        return index < 0 || index >= this.items.size() ? null : this.items.remove(index);
    }

    public boolean removeItem(Item item) {
        return this.items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasHeldItem() {
        return  getHeldItem() != null;
    }

    public Item getHeldItem() {
        if (items.isEmpty())
            return null;

        return items.get(heldItem);
    }

    public Item nextItem() {
        heldItem++;
        if (heldItem >= items.size()) {
            heldItem = items.size() - 1;
        }
        return getHeldItem();
    }

    public Item prevItem() {
        heldItem--;
        if (heldItem < 0) {
            heldItem = 0;
        }
        return getHeldItem();
    }

    public boolean isFull() {
        return this.getNumItems() >= this.getMaxItems();
    }

    public int getMaxItems() {
        return maxItems;
    }

    public int getNumItems() {
        return items.size();
    }
}
