package com.hayukleung.kps;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
  @BindView(R.id.kps_switch) Button mKpsSwitch;
  @BindView(R.id.kps_bar) RelativeLayout mKpsBar;
  @BindView(R.id.kps_panel) View mKpsPanel;
  @BindView(R.id.activity_chat_room) RelativeLayout mActivityChatRoom;

  private InputMethodManager mInputMethodManager;
  private int mKeyboardHeight = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat_room);
    ButterKnife.bind(this);

    mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    mActivityChatRoom.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

          @Override public void onGlobalLayout() {

            if (0 == mKeyboardHeight) {
              Log.e("kps --> ", String.format(" root view height %d px",
                  mActivityChatRoom.getRootView().getHeight()));
              Log.e("kps --> ", String.format("      view height %d px", mActivityChatRoom.getHeight()));

              Rect rect = new Rect();
              getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

              Log.e("kps --> ", String.format("status bar height %d px", rect.top));

              mKeyboardHeight = mActivityChatRoom.getRootView().getHeight()
                  - mActivityChatRoom.getHeight()
                  - rect.top;

              Log.e("kps --> ", String.format("  keyboard height %d px", mKeyboardHeight));

              ViewGroup.LayoutParams params = mKpsPanel.getLayoutParams();
              params.height = mKeyboardHeight;
              mKpsPanel.setLayoutParams(params);
            }
          }
        });
  }

  @OnClick({ R.id.kps_switch, R.id.kps_text }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.kps_switch: {
        if (View.VISIBLE == mKpsPanel.getVisibility()) {
          hideKeyboard();
          mKpsPanel.setVisibility(View.GONE);
        } else {
          hideKeyboard();
          mKpsPanel.setVisibility(View.VISIBLE);
        }
        break;
      }
      case R.id.kps_text: {
        showKeyboard();
        mKpsPanel.setVisibility(View.GONE);
        break;
      }
    }
  }

  @OnTouch({ R.id.kps_content }) public boolean onTouch(View view) {
    switch (view.getId()) {
      case R.id.kps_content: {
        hideKeyboard();
        mKpsPanel.setVisibility(View.GONE);
        return true;
      }
      default: {
        return false;
      }
    }
  }

  private void showKeyboard() {
    mInputMethodManager.showSoftInput(mKpsText, SHOW_IMPLICIT);
    setSoftInputMode(SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_RESIZE);
  }

  private void hideKeyboard() {
    mInputMethodManager.hideSoftInputFromWindow(mKpsText.getWindowToken(), HIDE_NOT_ALWAYS);
    setSoftInputMode(SOFT_INPUT_STATE_UNCHANGED | SOFT_INPUT_ADJUST_PAN);
  }

  /**
   * 动态更改软键盘模式
   *
   * Desired operating mode for any soft input area. May be any combination
   * of:
   *
   * <ul>
   * <li> One of the visibility states
   * {@link #SOFT_INPUT_STATE_UNSPECIFIED},
   * {@link #SOFT_INPUT_STATE_UNCHANGED},
   * {@link #SOFT_INPUT_STATE_HIDDEN},
   * {@link #SOFT_INPUT_STATE_ALWAYS_VISIBLE},
   * {@link #SOFT_INPUT_STATE_VISIBLE}.
   * <li> One of the adjustment options
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
