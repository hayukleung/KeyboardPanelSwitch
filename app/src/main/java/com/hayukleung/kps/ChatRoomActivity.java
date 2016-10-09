package com.hayukleung.kps;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class ChatRoomActivity extends AppCompatActivity {

  @BindView(R.id.kps_content) ScrollView mKpsContent;
  @BindView(R.id.kps_text) EditText mKpsText;
  @BindView(R.id.kps_switch_panel) Button mKpsSwitchPanel;
  @BindView(R.id.kps_bar) RelativeLayout mKpsBar;
  @BindView(R.id.kps_panel) View mKpsPanel;
  @BindView(R.id.activity_chat_room) RelativeLayout mActivityChatRoom;

  private InputMethodManager mInputMethodManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_room);
    ButterKnife.bind(this);

    mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    setSoftInputMode(SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_PAN);

    mKpsText.postDelayed(new Runnable() {
      @Override public void run() {
        switchToKeyboard();
      }
    }, 200);
  }

  @Override protected void onResume() {
    super.onResume();
    mActivityChatRoom.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

          private int keyboardHeight = 0;
          private boolean hasNavigationBar = true;

          @Override public void onGlobalLayout() {

            if (0 == keyboardHeight) {
              int screenHeight = mActivityChatRoom.getRootView().getHeight();
              Log.e("kps --> ", String.format("        screen height %d px", screenHeight));
              int activityHeight = mActivityChatRoom.getHeight();
              Log.e("kps --> ", String.format("      activity height %d px", activityHeight));

              Rect rect = new Rect();
              getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
              int statusBarHeight = rect.top;
              Log.e("kps --> ", String.format("    status bar height %d px", statusBarHeight));

              TypedValue tv = new TypedValue();
              getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
              int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
              Log.e("kps --> ", String.format("    action bar height %d px", actionBarHeight));

              Resources resources = getResources();
              int navigationBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height","dimen", "android"));
              Log.e("kps --> ", String.format("navigation bar height %d px", navigationBarHeight));

              keyboardHeight = screenHeight - activityHeight - statusBarHeight - actionBarHeight - navigationBarHeight;

              if (0 == keyboardHeight + navigationBarHeight) {
                // 不存在navigation bar
                hasNavigationBar = false;
              }

              keyboardHeight += hasNavigationBar ? 0 : navigationBarHeight;

              Log.e("kps --> ", String.format("      keyboard height %d px", keyboardHeight));

              ViewGroup.LayoutParams params = mKpsPanel.getLayoutParams();
              params.height = keyboardHeight;
              mKpsPanel.setLayoutParams(params);

              if (0 < keyboardHeight) {
                // 及时移除监听
                mActivityChatRoom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              }
            }
          }
        });
  }

  @OnClick({ R.id.kps_switch_panel, R.id.kps_text }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.kps_switch_panel: {
        if (View.VISIBLE == mKpsPanel.getVisibility()) {
          switchToKeyboard();
        } else {
          switchToPanel();
        }
        break;
      }
      case R.id.kps_text: {
        switchToKeyboard();
        break;
      }
    }
  }

  @OnTouch({ R.id.kps_content }) public boolean onTouch(View view) {
    switch (view.getId()) {
      case R.id.kps_content: {
        switchNone();
        return true;
      }
      default: {
        return false;
      }
    }
  }

  /**
   * 切换至键盘
   */
  private void switchToKeyboard() {
    showKeyboard();
    mKpsPanel.postDelayed(new Runnable() {
      @Override public void run() {
        mKpsPanel.setVisibility(View.GONE);
      }
    }, 20);
  }

  /**
   * 切换至面板
   */
  private void switchToPanel() {
    hideKeyboard();
    mKpsPanel.setVisibility(View.VISIBLE);
  }

  /**
   * 隐藏键盘与面板
   */
  private void switchNone() {
    hideKeyboard();
    mKpsPanel.setVisibility(View.GONE);
  }

  /**
   * 显示键盘
   */
  private void showKeyboard() {
    mInputMethodManager.showSoftInput(mKpsText, SHOW_IMPLICIT);
    setSoftInputMode(SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_RESIZE);
  }

  /**
   * 隐藏键盘
   */
  private void hideKeyboard() {
    mInputMethodManager.hideSoftInputFromWindow(mKpsText.getWindowToken(), HIDE_NOT_ALWAYS);
    setSoftInputMode(SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_PAN);
  }

  /**
   * 动态更改软键盘模式
   *
   * Desired operating mode for any soft input area. May be any combination of:
   *
   * <ul>
   * <li> One of the visibility states
   * {@link #SOFT_INPUT_STATE_UNSPECIFIED},
   * {@link #SOFT_INPUT_STATE_UNCHANGED},
   * {@link #SOFT_INPUT_STATE_HIDDEN},
   * {@link #SOFT_INPUT_STATE_VISIBLE},
   * {@link #SOFT_INPUT_STATE_ALWAYS_HIDDEN},
   * {@link #SOFT_INPUT_STATE_ALWAYS_VISIBLE}.
   * <li> One of the adjustment options
   * {@link #SOFT_INPUT_ADJUST_NOTHING},
   * {@link #SOFT_INPUT_ADJUST_UNSPECIFIED},
   * {@link #SOFT_INPUT_ADJUST_RESIZE},
   * {@link #SOFT_INPUT_ADJUST_PAN}.
   *
   * @param mode
   */
  private void setSoftInputMode(int mode) {
    getWindow().setSoftInputMode(mode);
  }
}
