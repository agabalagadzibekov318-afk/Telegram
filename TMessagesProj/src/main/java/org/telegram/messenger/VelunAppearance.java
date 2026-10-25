package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme;

/**
 * Non-privileged iOS-inspired color palette for VelunGram.
 *
 * All logic is purely local. No Premium, verification, ownership,
 * payments, Stars, gifts or Telegram service values are changed.
 */
public final class VelunAppearance {
    private static volatile boolean settingsLoaded;

    private VelunAppearance() {}

    public static boolean isEnabled() {
        if (!settingsLoaded && ApplicationLoader.applicationContext != null) {
            synchronized (VelunAppearance.class) {
                if (!settingsLoaded) {
                    VelunConfig.loadSettings();
                    settingsLoaded = true;
                }
            }
        }
        return VelunConfig.iosAppearance;
    }

    /**
     * 0 means do not override this key. Palette uses #AARRGGBB colors.
     */
    public static int colorForKey(int key) {
        boolean dark = Theme.getActiveTheme() != null && Theme.isCurrentThemeDark();
        final int canvas = dark ? 0xff000000 : 0xfff2f2f7;
        final int surface = dark ? 0xff1c1c1e : 0xffffffff;
        final int text = dark ? 0xfff5f5f7 : 0xff1c1c1e;
        final int secondary = dark ? 0xff98989e : 0xff8e8e93;
        final int blue = dark ? 0xff0a84ff : 0xff007aff;
        final int green = dark ? 0xff30d158 : 0xff34c759;
        if (key == Theme.key_windowBackgroundGray) return canvas;
        if (key == Theme.key_windowBackgroundWhite) return surface;
        if (key == Theme.key_dialogBackground) return surface;
        if (key == Theme.key_actionBarDefault) return surface;
        if (key == Theme.key_windowBackgroundWhiteBlackText) return text;
        if (key == Theme.key_dialogTextBlack) return text;
        if (key == Theme.key_chats_name) return text;
        if (key == Theme.key_actionBarDefaultTitle) return text;
        if (key == Theme.key_actionBarDefaultSubtitle) return secondary;
        if (key == Theme.key_windowBackgroundWhiteGrayText2 ||
            key == Theme.key_windowBackgroundWhiteGrayText3 ||
            key == Theme.key_windowBackgroundWhiteGrayText ||
            key == Theme.key_chats_message ||
            key == Theme.key_chats_date ||
            key == Theme.key_chats_actionMessage ||
            key == Theme.key_windowBackgroundWhiteGrayIcon) return secondary;
        if (key == Theme.key_windowBackgroundWhiteBlueText ||
            key == Theme.key_windowBackgroundWhiteBlueText2 ||
            key == Theme.key_windowBackgroundWhiteBlueButton ||
            key == Theme.key_windowBackgroundWhiteBlueIcon ||
            key == Theme.key_actionBarDefaultIcon) return blue;
        if (key == Theme.key_switchTrackChecked) return green;
        if (key == Theme.key_chat_outBubble) return dark ? 0xff204f76 : 0xffd6ecff;
        if (key == Theme.key_chat_inBubble) return surface;
        if (key == Theme.key_chat_messageTextOut ||
            key == Theme.key_chat_messageTextIn) return text;
        if (key == Theme.key_chat_outTimeText ||
            key == Theme.key_chat_inTimeText) return secondary;
        return 0;
    }
}
