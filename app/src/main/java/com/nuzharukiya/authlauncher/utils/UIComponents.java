package com.nuzharukiya.authlauncher.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuzharukiya.authlauncher.R;

/**
 * Created by Nuzha Rukiya on 18/01/10.
 */

public class UIComponents {

    private static final int STATUS_BAR_COLOR = R.color.material_color;
    private static final int BUILD_VER = Build.VERSION.SDK_INT;

    private Context context;

    //    Toolbar
    private boolean hasToolbar = false;
    private ImageView ivStartIcon, ivEndIcon;
    private TextView tvTitle, tvEndAction;

    public UIComponents(Context context, boolean hasToolbar) {
        this.context = context;
        this.hasToolbar = hasToolbar;

        initApp();
    }

    private void initApp() {

        setStatusBarColor(context);

        if (hasToolbar) {
            initToolbar();
        }
    }

    /**
     * Makes the status bar at the top transparent
     */
    public void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = ((Activity) context).getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * Sets the status bar color
     *
     * @param context
     */
    private void setStatusBarColor(Context context) {
        if (BUILD_VER >= Build.VERSION_CODES.LOLLIPOP) {
            ((Activity) context).getWindow().setStatusBarColor(context.getResources().getColor(STATUS_BAR_COLOR));
        }
    }

//    Material Toolbar

    private void initToolbar() {

        View rootToolbarView = ((Activity) context).findViewById(R.id.toolbar);

        if (rootToolbarView != null) {
            // Make sure the parent layout includes material_toolbar.
            ivStartIcon = rootToolbarView.findViewById(R.id.ivStartIcon);
            tvTitle = rootToolbarView.findViewById(R.id.tvTitle);
            ivEndIcon = rootToolbarView.findViewById(R.id.ivEndIcon);
            tvEndAction = rootToolbarView.findViewById(R.id.tvEndAction);
        }
    }

    public void setToolbarItems(int resTitle) {
        setToolbarItems(Constants.HIDE_ICON, resTitle, Constants.HIDE_ICON);
    }

    public void setToolbarItems(int resStartIcon, int resTitle) {
        setToolbarItems(resStartIcon, resTitle, Constants.HIDE_ICON);
    }

    /**
     * Sets the start icon, end icon and the toolbar title
     * If you don't want an icon to be visible,
     * Give the res value as Constants.HIDE_ICON
     *
     * @param resStartIcon
     * @param resTitle
     * @param resEndIcon
     */
    public void setToolbarItems(int resStartIcon, int resTitle, int resEndIcon) {

//        Start Icon
        initIcon(resStartIcon, ivStartIcon, true);
//        End Icon
        initIcon(resEndIcon, ivEndIcon, false);

//        Title
        tvTitle.setText(context.getString(resTitle));
    }

    public void setToolbarItems(int resStartIcon, int resTitle, String endString) {

//        Start Icon
        initIcon(resStartIcon, ivStartIcon, true);
//        End Icon
        initText(endString, tvEndAction, false);

//        Title
        tvTitle.setText(context.getString(resTitle));
    }

    private void setOnClickIntent(ImageView imageView, final Intent intent) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("from", "notifications");
                context.startActivity(intent);
            }
        });
    }

    /**
     * Sets the icon's visibility
     * Readjusts the layout if necessary
     * Sets the res with the icon
     *
     * @param resStartIcon
     * @param ivIcon
     * @param bStartIcon
     */
    private void initIcon(int resStartIcon, ImageView ivIcon, boolean bStartIcon) {
        switch (resStartIcon) {
            case Constants.HIDE_ICON: {

                if (bStartIcon) {
                    // Center text if there is no left icon
                    centerTitle();
                }
                showIcon(ivIcon, false);
            }
            break;
            case R.drawable.ic_left: {
                goBackOnClickStartIcon();
                setIcon(ivIcon, resStartIcon);
                showIcon(ivIcon, true);
            }
            break;
            default: {
                setIcon(ivIcon, resStartIcon);
                showIcon(ivIcon, true);
            }
            break;
        }
    }

    private void initText(String text, TextView tvAction, boolean bStart) {
        switch (text) {
//            case Constants.HIDE_TEXT: {
//
//                if (bStart) {
//                    // Center text if there is no left icon
//                    centerTitle();
//                }
//                showIcon(tvAction, false);
//            }
//            break;
            default: {
                setAction(tvAction, text);
                showIcon(tvAction, true);
            }
            break;
        }
    }

    /**
     * Center the title in the toolbar
     */
    public void centerTitle() {
        ConstraintLayout.LayoutParams tvTitleParams = (ConstraintLayout.LayoutParams) tvTitle.getLayoutParams();
        tvTitleParams.horizontalBias = 0.5f;
        tvTitle.setLayoutParams(tvTitleParams);
    }

    /**
     * If the left-side icon is the 'back' icon
     * On click will result in the user navigating back
     */
    private void goBackOnClickStartIcon() {
        showIcon(ivStartIcon, true);
        ivStartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).onBackPressed();
            }
        });
    }

    /**
     * Starts an activity on click of the left-side icon
     *
     * @param nextIntent
     */
    public void onClickStartIcon(final Intent nextIntent) {
        showIcon(ivStartIcon, true);
        ivStartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(nextIntent);
                ((Activity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });
    }

    /**
     * Starts an activity on click of the right-side icon
     *
     * @param nextIntent
     */
    public void onClickEndIcon(final Intent nextIntent) {
        showIcon(ivEndIcon, true);
        ivEndIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(nextIntent);
                ((Activity) context).finish();
            }
        });
    }

    /**
     * Sets the icon with the given resource
     *
     * @param ivIcon
     * @param resIcon
     */
    private void setIcon(ImageView ivIcon, int resIcon) {
        ivIcon.setImageResource(resIcon);
        if (BUILD_VER >= Build.VERSION_CODES.LOLLIPOP) {
            ivIcon.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
        }
    }

    private void setAction(TextView tvAction, String text) {
        tvAction.setText(text);
    }

    /**
     * Hides the bottom navigation bar
     */
    public void hideNavigationBar() {
        View decorView = ((Activity) context).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * Hides the bottom navigation bar
     */
    public void hideNavigationBarImmersive() {
        View decorView = ((Activity) context).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void setTitle(String sTitle) {
        showTitle();
        tvTitle.setText(sTitle);
    }

    public void showTitle() {
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        tvTitle.setVisibility(View.INVISIBLE);
    }

    /**
     * Sets the icon's visibility
     *
     * @param view
     * @param bShowIcon
     */
    public void showIcon(View view, boolean bShowIcon) {
        view.setVisibility(bShowIcon ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Moves the layout up when the keyboard appears
     */
    public void adjustPan() {
        ((Activity) context).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Force hide keyboard
     */
    public void forceHideKeyboard() {
        if (isKeyboardVisible()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public boolean isKeyboardVisible() {
        ///This method is based on the one described at http://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device
        Rect r = new Rect();
        View contentView = ((Activity) context).findViewById(android.R.id.content);
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();

        int keypadHeight = screenHeight - r.bottom;

        return
                (keypadHeight > screenHeight * 0.15);
    }

    /**
     * Use this to prevent the keyboard from showing up on starting an activity
     */
    public void hideKeyboard() {
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
