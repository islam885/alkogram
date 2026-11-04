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

    private int antiDeleteHeaderRow;
    private int antiDeleteRow;
    private int antiDeleteDetailRow;

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        ghostHeaderRow = newRow();
        hideReadRow = newRow();
        hideReadDetailRow = newRow();
        hideTypingRow = newRow();
        hideTypingDetailRow = newRow();

        antiDeleteHeaderRow = newRow();
        antiDeleteRow = newRow();
        antiDeleteDetailRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == hideReadRow) {
            ExteraConfig.editor.putBoolean("alko_hideRead", ExteraConfig.alkoHideRead ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoHideRead);
        } else if (position == hideTypingRow) {
            ExteraConfig.editor.putBoolean("alko_hideTyping", ExteraConfig.alkoHideTyping ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoHideTyping);
        } else if (position == antiDeleteRow) {
            ExteraConfig.editor.putBoolean("alko_antiDelete", ExteraConfig.alkoAntiDelete ^= true).apply();
            ((TextCheckCell) view).setChecked(ExteraConfig.alkoAntiDelete);
        }
    }

    @Override
    protected String getTitle() {
        return "Alkogram Ghost & Privacy";
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
                    } else if (position == antiDeleteHeaderRow) {
                        headerCell.setText(LocaleController.getString("AntiDeleteMessages", R.string.AntiDeleteMessages));
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
                    } else if (position == antiDeleteRow) {
                        checkCell.setTextAndCheck(
                                LocaleController.getString("AntiDeleteMessages", R.string.AntiDeleteMessages),
                                ExteraConfig.alkoAntiDelete,
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
                    } else if (position == antiDeleteDetailRow) {
                        infoCell.setText(LocaleController.getString("AntiDeleteMessagesDetail", R.string.AntiDeleteMessagesDetail));
                        infoCell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
            }
        }
    }
}

