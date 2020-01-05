package com.branwilliams.bundi.voxel.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<Item> items;

    private int heldItem;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void clearItems() {
        items.clear();
        heldItem = 0;
    }

    public void addItems(Inventory inventory) {
        for (Item item : inventory.getItems()) {
            this.items.add(item);
        }
    }

    public boolean addItem(Item item) {
        return this.items.add(item);
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
}
