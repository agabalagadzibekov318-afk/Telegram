package org.telegram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.VelunConfig;

/**
 * Clearly labelled, on-device preview of VelunGram visuals. Never impersonates
 * a Telegram server entitlement, alters another user's profile, or sends a gift.
 */
public final class VelunHudView {
    private static final int WHITE = 0xfffaf6ff;
    private static final int MUTED = 0xffc8b7df;

    private VelunHudView() {}

    private static int dp(float value) {
        return AndroidUtilities.dp(value);
    }

    private static GradientDrawable gradient(int first, int second, float radius) {
        GradientDrawable result = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{first, second});
        result.setCornerRadius(dp(radius));
        result.setStroke(dp(1), 0x557a57a3);
        return result;
    }

    private static TextView label(Context context, String value, int size, int color, boolean bold) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) {
            view.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        }
        return view;
    }

    /** First item in Settings; deliberately two ordinary setting rows tall. */
    public static View createSettingsTile(Context context, Runnable openSettings) {
        FrameLayout outer = new FrameLayout(context);
        outer.setPadding(dp(14), dp(3), dp(14), dp(5));
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(13), dp(16), dp(12));
        card.setBackground(gradient(0xff26143e, 0xff57317c, 22));
        outer.addView(card, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout top = new LinearLayout(context);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        ImageView icon = new ImageView(context);
        icon.setImageResource(R.drawable.velungram_launcher);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        top.addView(icon, new LinearLayout.LayoutParams(dp(42), dp(42)));
        LinearLayout titleGroup = new LinearLayout(context);
        titleGroup.setOrientation(LinearLayout.VERTICAL);
        titleGroup.setPadding(dp(12), 0, 0, 0);
        titleGroup.addView(label(context, "VelunGram — Set", 19, WHITE, true));
        titleGroup.addView(label(context, "HUD · темы · эффекты · турбо", 12, MUTED, false));
        top.addView(titleGroup, new LinearLayout.LayoutParams(0, -2, 1f));
        top.addView(label(context, "›", 30, WHITE, false));
        card.addView(top, new LinearLayout.LayoutParams(-1, dp(53)));

        View divider = new View(context);
        divider.setBackgroundColor(0x337d60a7);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(-1, dp(1));
        dividerParams.topMargin = dp(4);
        dividerParams.bottomMargin = dp(8);
        card.addView(divider, dividerParams);

        String badges = (VelunConfig.isLocalPremium ? "✦ Premium DEMO  " : "✦ iOS STYLE  ")
                + (VelunConfig.isVerifiedGalochka ? "✓ DEMO  " : "")
                + (VelunConfig.performanceMode ? "⚡ TURBO ON" : "⚡ TURBO OFF");
        card.addView(label(context, badges, 12, 0xfff0dfff, true));
        outer.setOnClickListener(v -> {
            if (!VelunConfig.performanceMode && VelunConfig.demoAnimations) {
                outer.animate().scaleX(.98f).scaleY(.98f).setDuration(85).withEndAction(() -> {
                    outer.animate().scaleX(1f).scaleY(1f).setDuration(180).start();
                    openSettings.run();
                }).start();
            } else {
                openSettings.run();
            }
        });
        outer.setContentDescription("VelunGram — Set. Открыть визуальные настройки и HUD.");
        return outer;
    }

    /** Always labels synthetic badges, gifts and balances as a local demonstration. */
    public static View createPreview(Context context) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(17), dp(15), dp(17), dp(16));
        card.setBackground(gradient(0xff1c102d, 0xff4c276b, 20));

        LinearLayout top = new LinearLayout(context);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        ImageView avatar = new ImageView(context);
        avatar.setImageResource(R.drawable.velungram_launcher);
        top.addView(avatar, new LinearLayout.LayoutParams(dp(53), dp(53)));

        LinearLayout text = new LinearLayout(context);
        text.setOrientation(LinearLayout.VERTICAL);
        text.setPadding(dp(12), 0, 0, 0);
        text.addView(label(context, "VelunGram HUD  •  DEMO", 16, WHITE, true));
        text.addView(label(context, "Локальный предпросмотр профиля", 12, MUTED, false));
        top.addView(text, new LinearLayout.LayoutParams(0, -2, 1));
        card.addView(top);

        String badges = (VelunConfig.isLocalPremium ? "✦ Premium (демо)  " : "")
                + (VelunConfig.isVerifiedGalochka ? "✓ Галочка (демо)  " : "");
        if (badges.isEmpty()) badges = "Нажми переключатели ниже, чтобы увидеть эффекты";
        TextView badge = label(context, badges, 13, 0xffe5c8ff, true);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(-1, -2);
        badgeParams.topMargin = dp(13);
        card.addView(badge, badgeParams);

        String details = "🎁 " + giftName() + "   ⭐ " + VelunConfig.starsBalance
                + "   TON " + VelunConfig.tonBalance + "   GRAM " + VelunConfig.gramBalance
                + "\nNFT: " + value(VelunConfig.nftUsername) + "  ·  Номер: " + value(VelunConfig.nftPhone)
                + "\nРейтинг: " + value(VelunConfig.fakeRating);
        TextView values = label(context, details, 13, WHITE, false);
        values.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams valuesParams = new LinearLayout.LayoutParams(-1, -2);
        valuesParams.topMargin = dp(10);
        card.addView(values, valuesParams);

        TextView message = label(context, "Пример чата: «Привет! 💜 Так выглядит VelunGram»", 13, WHITE, false);
        message.setPadding(dp(12), dp(10), dp(12), dp(10));
        message.setBackground(gradient(0xff6c438d, 0xff7952ac, 14));
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(-1, -2);
        bubbleParams.topMargin = dp(13);
        card.addView(message, bubbleParams);

        TextView footer = label(context, "Только на этом устройстве • ничего не отправлено в Telegram", 11, MUTED, false);
        LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(-1, -2);
        footerParams.topMargin = dp(12);
        card.addView(footer, footerParams);
        return card;
    }

    public static void animate(View view) {
        if (view == null || VelunConfig.performanceMode || !VelunConfig.demoAnimations) return;
        view.setAlpha(.35f);
        view.setTranslationY(dp(10));
        view.animate().alpha(1f).translationY(0).setDuration(360)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    public static void showPreviewDialog(Context context) {
        FrameLayout wrapper = new FrameLayout(context);
        wrapper.setPadding(dp(12), dp(8), dp(12), dp(12));
        View preview = createPreview(context);
        wrapper.addView(preview, new FrameLayout.LayoutParams(-1, -2));
        animate(preview);
        new AlertDialog.Builder(context)
                .setTitle("VelunGram HUD")
                .setView(wrapper)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void showGiftDialog(Context context, Runnable onChanged) {
        new AlertDialog.Builder(context)
                .setTitle("Подарок в HUD (только демо)")
                .setSingleChoiceItems(new String[]{"💎 Аметист", "🌹 Роза", "🎁 Сюрприз", "⭐ Звезда", "🦋 Бабочка"},
                        Math.max(0, Math.min(4, VelunConfig.demoGift)), (dialog, selected) -> {
                            VelunConfig.demoGift = selected;
                            VelunConfig.saveSettings();
                            dialog.dismiss();
                            if (onChanged != null) onChanged.run();
                            Toast.makeText(context, "Демо-подарок изменён. Реальный подарок не отправлен.", Toast.LENGTH_SHORT).show();
                        })
                .setNegativeButton("Отмена", null)
                .show();
    }

    public static String giftName() {
        switch (VelunConfig.demoGift) {
            case 1: return "Роза";
            case 2: return "Сюрприз";
            case 3: return "Звезда";
            case 4: return "Бабочка";
            default: return "Аметист";
        }
    }

    private static String value(String v) {
        return v == null || v.trim().isEmpty() ? "—" : v;
    }
}
