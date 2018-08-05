package com.netease.nim.demo.main.model;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.fragment.ChatRoomListFragment;
import com.netease.nim.demo.main.fragment.MainTabFragment;
import com.netease.nim.demo.main.fragment.MyErrorsFragment;
import com.netease.nim.demo.main.fragment.MyHomeFragment;
import com.netease.nim.demo.main.fragment.SessionListFragment;
import com.netease.nim.demo.main.reminder.ReminderId;

public enum MainTab {
    HOME_ROOM(0, ReminderId.HOME, MyHomeFragment.class, R.string.my_home, R.layout.my_home_tab),
    CHAT_ROOM(1, ReminderId.INVALID, ChatRoomListFragment.class, R.string.chat_room, R.layout.chat_room_tab),
    ERROR_ADMIN(2, ReminderId.ERROR, MyErrorsFragment.class, R.string.my_error, R.layout.my_error_tab),
    RECENT_CONTACTS(3, ReminderId.SESSION, SessionListFragment.class, R.string.main_tab_session, R.layout.session_list);
//    CONTACT(4, ReminderId.CONTACT, ContactListFragment.class, R.string.main_tab_contact, R.layout.contacts_list);


    public final int tabIndex;

    public final int reminderId;

    public final Class<? extends MainTabFragment> clazz;

    public final int resId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class<? extends MainTabFragment> clazz, int resId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }

        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }
        return null;
    }
}
