package com.college.xdick.findme.MyClass;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.college.xdick.findme.adapter.ActivityAdapter;
import com.college.xdick.findme.ui.Activity.MainDynamicsActivity;

import static com.college.xdick.findme.adapter.CommentAdapter.NO_MORE;
import static com.college.xdick.findme.adapter.DynamicsCommentAdapter.LOADING_MORE;
import static com.college.xdick.findme.ui.Activity.MainDynamicsActivity.ADD;

/**
 * Created by Administrator on 2018/6/7.
 */

public class CommentScrollView extends ScrollView {



        private OnScrollBottomListener _listener;
        private int _calCount;

        public interface OnScrollBottomListener {
            void srollToBottom();
        }

        public void registerOnScrollViewScrollToBottom(OnScrollBottomListener l) {
            _listener = l;
        }

        public void unRegisterOnScrollViewScrollToBottom() {
            _listener = null;
        }

        public CommentScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            View view = this.getChildAt(0);
            if (this.getHeight() + this.getScrollY() == view.getHeight()) {
                _calCount++;
                if (_calCount == 1) {
                    if (_listener != null) {
                        _listener.srollToBottom();
                    }
                }
            } else {
                _calCount = 0;
            }
        }

}