/**
 * 
 */
package com.d3.d3xmpp.activites;

import org.jivesoftware.smackx.packet.VCard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.ImgConfig;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.CircularImage;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.util.XmppLoadThread;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
public class FriendActivity extends BaseActivity {
	@D3View(click="onClick") Button operBtn;
	@D3View TextView nameView,sexView,signView,nickNameView,phoneView,emailView;
	@D3View CircularImage headView;
	private String username;
	private User friend;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.acti_friend);
		initTitle();
		username = getIntent().getStringExtra("username");
		nameView.setText(username);
//		ImgConfig.showHeadImg(username, headView);
		
		if (username.equals(Constants.USER_NAME)) {
			operBtn.setVisibility(View.GONE);
		}
		
		if (XmppConnection.getInstance().getFriendList().contains(new Friend(username))) {
			operBtn.setText("�Ƴ�ͨѶ¼");
		}
		else {
			operBtn.setText("���ӵ�ͨѶ¼");
		}

//		Map<String, String> map = new HashMap<String, String>();
//		map.put("userNameA", Constants.USER_NAME);
//		map.put("userNameB", username);
//		new LoadThread(this,Constants.IS_FRIEND,map) {
//			
//			@Override
//			protected void refreshUI(String result) {
//				try {
//					JSONObject jsonObject = new JSONObject(result);
//					if (jsonObject.getString("state").equals("0")) {
//						operBtn.setText("�Ƴ�ͨѶ¼");
//					}
//					else if (jsonObject.getString("state").equals("1")){
//						operBtn.setText("���ӵ�ͨѶ¼");
//					}
//					
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		};
		initData();
	}
	
	public void	initData() {
		new XmppLoadThread(this) {
			
			@Override
			protected void result(Object object) {
				VCard vCard = (VCard)object;
				friend = new User(vCard);
				if (friend!=null) {
					if (friend.sex != null) {
						sexView.setText(friend.sex);
					}
					if (friend.intro != null) {
						signView.setText(friend.intro);
					}
					if (friend.nickname != null) {
						nickNameView.setText(friend.nickname);
					}
					if (friend.email != null) {
						emailView.setText(friend.email);
					}
					if (friend.mobile != null) {
						phoneView.setText(friend.mobile);
					}
					friend.showHead(headView);
				}
			}
			
			@Override
			protected Object load() {
				return XmppConnection.getInstance().getUserInfo(username);
			}
		};
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.operBtn:
			if (operBtn.getText().equals("���ӵ�ͨѶ¼")) {
				XmppConnection.getInstance().addUser(username);
				Tool.initToast(getApplicationContext(), "���ӳɹ����ȴ�ͨ����֤");
				MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
				operBtn.setText("�Ƴ�ͨѶ¼");
			}
			else if (operBtn.getText().equals("�Ƴ�ͨѶ¼")){
				XmppConnection.getInstance().removeUser(username);
				Tool.initToast(getApplicationContext(), "�Ƴ��ɹ�");
				MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
				operBtn.setText("���ӵ�ͨѶ¼");
				NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(username);
				MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
				MsgDbHelper.getInstance(getApplicationContext()).delChatMsg(username);
			}
			break;

		default:
			break;
		}
		
	}
}