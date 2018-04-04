package com.college.xdick.college.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.college.xdick.college.IM_util.MsgAdapter;
import com.college.xdick.college.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class ChatActivity extends AppCompatActivity{

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;

    private List<BmobIMMessage> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        IMoperation();
        initMsgs();
        initView();



    }

    private void initMsgs() {

    }
    private void initView(){
        adapter = new MsgAdapter(ChatActivity.this, R.layout.item_msg, msgList);
        inputText = (EditText)findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgListView = (ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if(!"".equals(content)) {
                    BmobIMMessage msg = new BmobIMMessage();
                    msg.setContent(content);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                }
            }
        });
    }

    private void IMoperation(){

       /* BmobIMUserInfo info = new BmobIMUserInfo();
        BmobIMConversation conversationEntrance = BmobIM.
                getInstance().startPrivateConversation(info, null);
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);*/

    }
}

