package com.college.xdick.college.IM_util;

import cn.bmob.newim.bean.BmobIMExtraMessage;

/**添加好友请求-自定义消息类型
 * @author :smile
 * @project:AddFriendMessage
 * @date :2016-01-30-17:28
 */
//TODO 好友管理：9.5、自定义添加好友的消息类型
public class AddFriendMessage extends BmobIMExtraMessage {

    public AddFriendMessage(){}

    @Override
    public String getMsgType() {
        //自定义一个`add`的消息类型
        return "add";
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        return true;
    }



}