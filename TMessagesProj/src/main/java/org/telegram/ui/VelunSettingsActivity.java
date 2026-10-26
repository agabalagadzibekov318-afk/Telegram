package org.telegram.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.VelunConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;

/**
 * Local appearance preview. This screen does not alter account/server state,
 * blockchain assets, real badges, billing, or Telegram entitlement checks.
 */
public class VelunSettingsActivity extends BaseFragment {
    private LinearLayout rows;
    private TextDetailCell preview;

    private interface ValueSaver {
        void save(String value);
    }

    @Override
    public View createView(Context context) {
        VelunConfig.loadSettings();

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle("VelunGram — Set");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        root.addView(scroll, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        rows = new LinearLayout(context);
        rows.setOrientation(LinearLayout.VERTICAL);
        rows.setPadding(0, AndroidUtilities.dp(12), 0, AndroidUtilities.dp(24));
        scroll.addView(rows, new ScrollView.LayoutParams(
            ScrollView.LayoutParams.MATCH_PARENT,
            ScrollView.LayoutParams.WRAP_CONTENT
        ));

        TextView notice = new TextView(context);
        notice.setText("VELUNGRAM • ЛОКАЛЬНОЕ ОФОРМЛЕНИЕ\n" +
            "VelunGram — неофициальный клиент Telegram. " +
            "Эти значения видны только в предпросмотре на этом устройстве. " +
            "Они не дают Premium, галочку, подарки, Stars или TON.");
        notice.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        notice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        notice.setGravity(Gravity.START);
        notice.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(12),
            AndroidUtilities.dp(20), AndroidUtilities.dp(16));
        rows.addView(notice);

        addIOSAppearanceToggle(context);
        addThemeSelection(context);
        addPerformanceToggle(context);

        addToggle(context, "Premium • визуальный предпросмотр",
            VelunConfig.isLocalPremium, true);
        addToggle(context, "Галочка • визуальный предпросмотр",
            VelunConfig.isVerifiedGalochka, false);

        addEditable(context, "Рейтинг (демо)", VelunConfig.fakeRating,
            false, value -> VelunConfig.fakeRating = value);
        addEditable(context, "NFT-юзернейм (демо)", VelunConfig.nftUsername,
            false, value -> VelunConfig.nftUsername = value);
        addEditable(context, "NFT-номер (демо)", VelunConfig.nftPhone,
            false, value -> VelunConfig.nftPhone = value);
        addEditable(context, "Telegram Stars (демо)", VelunConfig.starsBalance,
            true, value -> VelunConfig.starsBalance = value);
        addEditable(context, "TON (демо)", VelunConfig.tonBalance,
            true, value -> VelunConfig.tonBalance = value);
        addEditable(context, "GRAM (демо)", VelunConfig.gramBalance,
            true, value -> VelunConfig.gramBalance = value);

        TextView heading = new TextView(context);
        heading.setText("ПРЕДПРОСМОТР (НЕ РЕАЛЬНЫЙ БАЛАНС)");
        heading.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        heading.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(18),
            AndroidUtilities.dp(20), AndroidUtilities.dp(8));
        rows.addView(heading);

        preview = new TextDetailCell(context);
        preview.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(preview, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(75)));
        updatePreview();

        return fragmentView = root;
    }

    private void addThemeSelection(Context context) {
        TextDetailCell cell = new TextDetailCell(context);
        cell.setTextAndValue("Чёрно-фиолетовые темы", VelunConfig.themeName(), true);
        cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(cell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(68)));
        cell.setOnClickListener(v -> new AlertDialog.Builder(context)
            .setTitle("Тема VelunGram")
            .setItems(new String[]{"Обычная", "Чёрный аметист", "Неоновая слива"}, (d, which) -> {
                VelunConfig.themeMode = which;
                VelunConfig.saveSettings();
                cell.setTextAndValue("Чёрно-фиолетовые темы", VelunConfig.themeName(), true);
                android.widget.Toast.makeText(context, "Перезапустите VelunGram для применения темы.", android.widget.Toast.LENGTH_SHORT).show();
            }).show());
    }

    private void addPerformanceToggle(Context context) {
        final String title = "⚡ Турбо для слабых устройств";
        TextCheckCell cell = new TextCheckCell(context);
        cell.setTextAndCheck(title, VelunConfig.performanceMode, true);
        cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(cell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(51)));
        cell.setOnClickListener(v -> {
            VelunConfig.setPerformanceMode(!VelunConfig.performanceMode);
            cell.setTextAndCheck(title, VelunConfig.performanceMode, true);
            android.widget.Toast.makeText(context,
                "Lite Mode: тяжёлые анимации, эффекты и автопроигрывание отключены. Для максимального эффекта перезапустите приложение.",
                android.widget.Toast.LENGTH_LONG).show();
        });
    }

    private void addIOSAppearanceToggle(Context context) {
        final String title = "iOS-оформление (цвета и элементы)";
        TextCheckCell cell = new TextCheckCell(context);
        cell.setTextAndCheck(title, VelunConfig.iosAppearance, true);
        cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(cell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(51)));
        cell.setOnClickListener(v -> {
            VelunConfig.iosAppearance = !VelunConfig.iosAppearance;
            cell.setTextAndCheck(title, VelunConfig.iosAppearance, true);
            VelunConfig.saveSettings();
            updatePreview();
            android.widget.Toast.makeText(context,
                "Цвета iOS применяются к новым экранам. Перезапустите приложение для полного обновления.",
                android.widget.Toast.LENGTH_LONG).show();
        });
    }

    private void addToggle(Context context, String text, boolean state, boolean premium) {
        TextCheckCell cell = new TextCheckCell(context);
        cell.setTextAndCheck(text, state, true);
        cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(cell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(51)));
        cell.setOnClickListener(v -> {
            boolean newValue;
            if (premium) {
                VelunConfig.isLocalPremium = !VelunConfig.isLocalPremium;
                newValue = VelunConfig.isLocalPremium;
            } else {
                VelunConfig.isVerifiedGalochka = !VelunConfig.isVerifiedGalochka;
                newValue = VelunConfig.isVerifiedGalochka;
            }
            cell.setTextAndCheck(text, newValue, true);
            VelunConfig.saveSettings();
            updatePreview();
        });
    }

    private void addEditable(Context context, String title, String initial,
                             boolean numeric, ValueSaver setter) {
        TextDetailCell cell = new TextDetailCell(context);
        cell.setTextAndValue(title, emptyLabel(initial), true);
        cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        rows.addView(cell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(68)));

        cell.setOnClickListener(v -> {
            final EditText field = new EditText(context);
            field.setSingleLine(true);
            field.setSelectAllOnFocus(true);
            field.setText(currentValue(title));
            field.setSelection(field.getText().length());
            field.setPadding(AndroidUtilities.dp(18), AndroidUtilities.dp(12),
                AndroidUtilities.dp(18), AndroidUtilities.dp(12));
            if (numeric) {
                field.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL
                    | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }

            new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(field)
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String value = field.getText().toString().trim();
                    setter.save(value);
                    VelunConfig.saveSettings();
                    cell.setTextAndValue(title, emptyLabel(value), true);
                    updatePreview();
                })
                .show();
        });
    }

    private String currentValue(String title) {
        switch (title) {
            case "Рейтинг (демо)": return VelunConfig.fakeRating;
            case "NFT-юзернейм (демо)": return VelunConfig.nftUsername;
            case "NFT-номер (демо)": return VelunConfig.nftPhone;
            case "Telegram Stars (демо)": return VelunConfig.starsBalance;
            case "TON (демо)": return VelunConfig.tonBalance;
            case "GRAM (демо)": return VelunConfig.gramBalance;
            default: return "";
        }
    }

    private String emptyLabel(String value) {
        return value == null || value.isEmpty() ? "Не задано" : value;
    }

    private void updatePreview() {
        if (preview == null) {
            return;
        }
        String status = "iOS: " + (VelunConfig.iosAppearance ? "ON" : "OFF") + " • Premium: " + (VelunConfig.isLocalPremium ? "ON" : "OFF")
            + " • Галочка: " + (VelunConfig.isVerifiedGalochka ? "ON" : "OFF")
            + " • ⭐ " + VelunConfig.starsBalance
            + " • TON " + VelunConfig.tonBalance
            + " • GRAM " + VelunConfig.gramBalance;
        preview.setTextAndValue("Только визуальный режим", status, false);
    }
}
