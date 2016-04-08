package cn.hukecn.speechbrowser.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.hukecn.speechbrowser.R;
import cn.hukecn.speechbrowser.DAO.MyDataBase;
import cn.hukecn.speechbrowser.bean.MailBean;
import cn.hukecn.speechbrowser.util.ToastUtil;

public class MailManagerActivity extends Activity {

	EditText et_username = null;
	EditText et_password = null;
	Button btn_add = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_manager);
		
		et_password = (EditText) findViewById(R.id.edt_pw);
		et_username = (EditText) findViewById(R.id.edt_id);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//检查用户名密码合法性
				String uid = et_username.getText().toString();
				String pwd = et_password.getText().toString();
				
				if(uid.length() > 0 && pwd.length() > 0)
				{
					MailBean bean = new MailBean();
					bean.type = "QQ";
					bean.username = uid;
					bean.password = pwd;
					MyDataBase db = MyDataBase.getInstance();
					long state = db.insertMail(bean);
					if(state != -1)
					{
						ToastUtil.toast(bean.type + "邮箱账户配置成功");
						finish();
					}else
					{
						ToastUtil.toast(bean.type + "邮箱账户配置失败，请重新配置");
					}

				}else
					ToastUtil.toast("您的用户名或密码配置不合法");
			}
		});

		MyDataBase db = MyDataBase.getInstance();
		List<MailBean> list = db.queryMail("QQ");
		
		if(list == null)
			ToastUtil.toast("请配置您的QQ邮箱账号，以便使用邮件服务...");
		else
		{
			MailBean bean = list.get(list.size() - 1);
			et_username.setText(bean.username);
			et_password.setText(bean.password);
		}
	}
}
