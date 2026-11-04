/*
 * Alkogram Privacy & Ghost Mode settings
 */

package com.exteragram.messenger.preferences;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exteragram.messenger.ExteraConfig;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;

public class AlkogramPrivacyPreferencesActivity extends BasePreferencesActivity {

    private int ghostHeaderRow;
    private int hideReadRow;
    private int hideReadDetailRow;
    private int hideTypingRow;
    private int hideTypingDetailRow;

    private int bypassHeaderRow;
    private int allowScreenshotsRow;
    private int allowScreenshotsDetailRow;
    private int bypassNoForwardsRow;
    private int bypassNoForwardsDetailRow;
    private int saveSelfDestructingRow;
    private int saveSelfDestructingDetailRow;

    private int antiDeleteHeaderRow;
    private int antiDeleteRow;
    private int antiDeleteDetailRow;

    private int selfBotHeaderRow;
    private int selfBotRow;
    private int selfBotDetailRow;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        ghostHeaderRow = newRow();
        hideReadRow = newRow();
        hideReadDetailRow = newRow();
        hideTypingRow = newRow();
        hideTypingDetailRow = newRow();

        bypassHeaderRow = newRow();
        allowScreenshotsRow = newRow();
        allowScreenshotsDetailRow = newRow();
        bypassNoForwardsRow = newRow();
        bypassNoForwardsDetailRow = newRow();
        saveSelfDestructingRow = newRow();
        saveSelfDestructingDetailRow = newRow();

        antiDeleteHeaderRow = newRow();
        antiDeleteRow = newRow();
        antiDeleteDetailRow = newRow();

        selfBotHeaderRow = newRow();
        selfBotRow = newRow();
        selfBotDetailRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == hideReadRow) {
            ExteraConfig.editor.putBoolean("alko_hideRead", ExteraConfig.alkoHideRead ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoHideRead);
        } else if (position == hideTypingRow) {
            ExteraConfig.editor.putBoolean("alko_hideTyping", ExteraConfig.alkoHideTyping ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoHideTyping);
        } else if (position == allowScreenshotsRow) {
            ExteraConfig.editor.putBoolean("alko_allowScreenshots", ExteraConfig.alkoAllowScreenshots ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoAllowScreenshots);
        } else if (position == bypassNoForwardsRow) {
            ExteraConfig.editor.putBoolean("alko_bypassNoForwards", ExteraConfig.alkoBypassNoForwards ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoBypassNoForwards);
        } else if (position == saveSelfDestructingRow) {
            ExteraConfig.editor.putBoolean("alko_saveSelfDestructing", ExteraConfig.alkoSaveSelfDestructing ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoSaveSelfDestructing);
        } else if (position == antiDeleteRow) {
            ExteraConfig.editor.putBoolean("alko_antiDelete", ExteraConfig.alkoAntiDelete ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoAntiDelete);
        } else if (position == selfBotRow) {
            ExteraConfig.editor.putBoolean("alko_selfBotEnabled", ExteraConfig.alkoSelfBotEnabled ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoSelfBotEnabled);
        }
    }

    @Override
    protected String getTitle() {
        return "Alkogram Settings";
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 3: // HeaderCell
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ghostHeaderRow) {
                        headerCell.setText(LocaleController.getString("GhostMode", R.string.GhostMode));
                    } else if (position == bypassHeaderRow) {
                        headerCell.setText(LocaleController.getString("BypassRestrictions", R.string.BypassRestrictions));
                    } else if (position == antiDeleteHeaderRow) {
                        headerCell.setText(LocaleController.getString("AntiDeleteMessages", R.string.AntiDeleteMessages));
                    } else if (position == selfBotHeaderRow) {
                        headerCell.setText(LocaleController.getString("AlkogramSelfBotHeader", R.string.AlkogramSelfBotHeader));
                    }
                    break;
                case 5: // TextCheckCell
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == hideReadRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("HideReadStatus", R.string.HideReadStatus),
                                ExteraConfig.alkoHideRead,
                                false
                        );
                    } else if (position == hideTypingRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("HideTypingStatus", R.string.HideTypingStatus),
                                ExteraConfig.alkoHideTyping,
                                false
                        );
                    } else if (position == allowScreenshotsRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("AllowScreenshots", R.string.AllowScreenshots),
                                ExteraConfig.alkoAllowScreenshots,
                                false
                        );
                    } else if (position == bypassNoForwardsRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("BypassNoForwards", R.string.BypassNoForwards),
                                ExteraConfig.alkoBypassNoForwards,
                                false
                        );
                    } else if (position == saveSelfDestructingRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("SaveSelfDestructing", R.string.SaveSelfDestructing),
                                ExteraConfig.alkoSaveSelfDestructing,
                                false
                        );
                    } else if (position == antiDeleteRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("AntiDeleteMessages", R.string.AntiDeleteMessages),
                                ExteraConfig.alkoAntiDelete,
                                false
                        );
                    } else if (position == selfBotRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("AlkogramSelfBotTitle", R.string.AlkogramSelfBotTitle),
                                ExteraConfig.alkoSelfBotEnabled,
                                false
                        );
                    }
                    break;
                case 8: // TextInfoPrivacyCell
                    TextInfoPrivacyCell infoCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == hideReadDetailRow) {
                        infoCell.setText(LocaleController.getString("HideReadStatusDetail", R.string.HideReadStatusDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == hideTypingDetailRow) {
                        infoCell.setText(LocaleController.getString("HideTypingStatusDetail", R.string.HideTypingStatusDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == allowScreenshotsDetailRow) {
                        infoCell.setText(LocaleController.getString("AllowScreenshotsDetail", R.string.AllowScreenshotsDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == bypassNoForwardsDetailRow) {
                        infoCell.setText(LocaleController.getString("BypassNoForwardsDetail", R.string.BypassNoForwardsDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == saveSelfDestructingDetailRow) {
                        infoCell.setText(LocaleController.getString("SaveSelfDestructingDetail", R.string.SaveSelfDestructingDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == antiDeleteDetailRow) {
                        infoCell.setText(LocaleController.getString("AntiDeleteMessagesDetail", R.string.AntiDeleteMessagesDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == selfBotDetailRow) {
                        infoCell.setText(LocaleController.getString("AlkogramSelfBotDetail", R.string.AlkogramSelfBotDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
            }
        }
    }
}
