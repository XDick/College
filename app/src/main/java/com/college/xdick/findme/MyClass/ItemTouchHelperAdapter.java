package com.college.xdick.findme.MyClass;

/**
 * Created by Administrator on 2018/5/21.
 */

public interface ItemTouchHelperAdapter {

    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);
}