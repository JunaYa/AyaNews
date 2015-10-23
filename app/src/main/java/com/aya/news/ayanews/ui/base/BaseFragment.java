package com.aya.news.ayanews.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aya.news.ayanews.R;
import com.aya.news.ayanews.ui.view.CustomProgressDialog;

/**
 * Created by Single on 2015/10/22.
 */
public class BaseFragment extends Fragment {
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public View findViewById(int resId) {
        View v = null;
        if (rootView != null) {
            v = rootView.findViewById(resId);
        }
        return v;
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void setHeaderTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.page_title);
        titleView.setText(title);
    }

    protected void showBackBtn() {
        ImageButton back = (ImageButton) findViewById(R.id.back_btn);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void showBackBtn(int resId, View.OnClickListener onClickListener) {
        ImageButton backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setImageResource(resId);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(onClickListener);
    }

    protected void showRightBtn(int resId, View.OnClickListener onClickListener) {
        ImageButton rightBtn = (ImageButton) findViewById(R.id.btn_right);
        rightBtn.setImageResource(resId);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(onClickListener);
    }

    protected void showRightBtn(String str, View.OnClickListener onClickListener) {
        Button right = (Button) findViewById(R.id.btn_right_text);
        right.setText(str);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(onClickListener);
    }

    private void finish() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    protected void startFragment(String fragmentName, Bundle args) {
        Intent intent = new Intent(getActivity(), MyFragmentActivity.class);
        intent.putExtra("RootFragment", fragmentName);
        if (args != null) intent.putExtras(args);
        startActivity(intent);
    }

    private CustomProgressDialog mProgressDialog;

    protected CustomProgressDialog showProgressDialog() {
        return showProgressDialog(false);
    }

    protected CustomProgressDialog showProgressDialog(boolean canclable) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return mProgressDialog;
        } else {
            mProgressDialog = CustomProgressDialog.show(getActivity());
            mProgressDialog.setCancelable(canclable);
            return mProgressDialog;
        }
    }

    protected void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

}
