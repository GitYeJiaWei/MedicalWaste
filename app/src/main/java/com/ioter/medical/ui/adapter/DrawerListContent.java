package com.ioter.medical.ui.adapter;




import com.ioter.medical.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold the data for Navigation Drawer Items
 */
public class DrawerListContent {
    //An array of sample (Settings) items.
    public static List<DrawerItem> ITEMS = new ArrayList<>();

    //A map of sample (Settings) items, by ID.
    public static Map<String, DrawerItem> ITEM_MAP = new HashMap<>();

    static {
        addItem(new DrawerItem("1", "操作人员", R.mipmap.size));
        addItem(new DrawerItem("2", "VIP", R.mipmap.size));
        addItem(new DrawerItem("3", "退出登陆", R.mipmap.size));
        }

    /**
     * Method to add a new item
     *
     * @param item - Item to be added
     */
    private static void addItem(DrawerItem item) {

        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A Drawer item represents an entry in the navigation drawer.
     */
    public static class DrawerItem {
        public String id;
        public String content;
        public int icon;

        public DrawerItem(String id, String content, int icon_id) {
            this.id = id;
            this.content = content;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
