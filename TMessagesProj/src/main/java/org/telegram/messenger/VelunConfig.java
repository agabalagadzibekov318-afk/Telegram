package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Device-only visual demo settings for VelunGram.
 * These values never grant Telegram Premium, authentication, ownership,
 * verification, Stars, crypto, or transaction capabilities.
 */
public final class VelunConfig {
    private static final String PREFERENCES_NAME = "velungram_visual_settings";

    public static boolean isLocalPremium = false;
    public static boolean isVerifiedGalochka = false;
    public static boolean iosAppearance = false;
    public static String fakeRating = "0";
    public static String nftUsername = "";
    public static String nftPhone = "";
    public static String starsBalance = "0";
    public static String tonBalance = "0";
    public static String gramBalance = "0";

    private VelunConfig() {
    }

    private static SharedPreferences preferences() {
        Context context = ApplicationLoader.applicationContext;
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void loadSettings() {
        SharedPreferences preferences = preferences();
        if (preferences == null) {
            return;
        }

        iosAppearance = preferences.getBoolean("ios_appearance", false);
        isLocalPremium = preferences.getBoolean("premium_preview", false);
        isVerifiedGalochka = preferences.getBoolean("verification_preview", false);
        fakeRating = preferences.getString("rating_preview", "0");
        nftUsername = preferences.getString("username_preview", "");
        nftPhone = preferences.getString("phone_preview", "");
        starsBalance = preferences.getString("stars_preview", "0");
        tonBalance = preferences.getString("ton_preview", "0");
        gramBalance = preferences.getString("gram_preview", "0");
    }

    public static synchronized void saveSettings() {
        SharedPreferences preferences = preferences();
        if (preferences == null) {
            return;
        }

        preferences.edit()
            .putBoolean("ios_appearance", iosAppearance)
            .putBoolean("premium_preview", isLocalPremium)
            .putBoolean("verification_preview", isVerifiedGalochka)
            .putString("rating_preview", fakeRating)
            .putString("username_preview", nftUsername)
            .putString("phone_preview", nftPhone)
            .putString("stars_preview", starsBalance)
            .putString("ton_preview", tonBalance)
            .putString("gram_preview", gramBalance)
            .apply();
    }
}
