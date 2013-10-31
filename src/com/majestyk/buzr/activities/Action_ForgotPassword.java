package com.majestyk.buzr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.majestyk.buzr.R;
import com.majestyk.buzr.apis.API_ForgotPassword;
import com.majestyk.buzr.apis.OnTaskCompleteListener;

public class Action_ForgotPassword extends Activity implements OnClickListener {

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_forgot_password);

		findViewById(R.id.submit).setOnClickListener(this);

	}

	@Override
	public final void onClick(View view) {
		switch (view.getId()) {
		case R.id.submit:
			changePassword();
			break;
		}
	}

	private final void changePassword() {
		boolean cancel = false;
		View focusView = null;

		EditText mEmailView = (EditText) findViewById(R.id.email);
		EditText mUsernameView = (EditText) findViewById(R.id.username);

		String mEmail = mEmailView.getText().toString();
		String mUsername = mUsernameView.getText().toString();

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}		

		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}		

		if (cancel) {
			focusView.requestFocus();
		} else {
			new API_ForgotPassword(Action_ForgotPassword.this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success)
						finish();
				}
			}).execute("user/forgot", mEmail, mUsername);
		}
	}	
}
