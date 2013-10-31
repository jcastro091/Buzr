package com.majestyk.buzr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.majestyk.buzr.R;
import com.majestyk.buzr.apis.API_ChangePassword;
import com.majestyk.buzr.apis.OnTaskCompleteListener;

public class Action_ChangePassword extends Activity implements OnClickListener {

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_change_password);

		findViewById(R.id.change_password).setOnClickListener(this);

	}
	
	@Override
	public final void onClick(View view) {
		switch (view.getId()) {
		case R.id.change_password:
			changePassword();
			break;
		}
	}

	private final void changePassword() {
		boolean cancel = false;
		View focusView = null;

		EditText mPasswordView = (EditText) findViewById(R.id.password);
		EditText mConfirmPView = (EditText) findViewById(R.id.confirm_password);

		String mPassword = mPasswordView.getText().toString();
		String mConfirmP = mConfirmPView.getText().toString();

		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}		

		if (TextUtils.isEmpty(mConfirmP)) {
			mConfirmPView.setError(getString(R.string.error_field_required));
			focusView = mConfirmPView;
			cancel = true;
		}		

		if (!TextUtils.equals(mPassword.toString(), mConfirmP.toString())) {
			mConfirmPView.setError(getString(R.string.error_password_match));
			focusView = mConfirmPView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			new API_ChangePassword(Action_ChangePassword.this, new OnTaskCompleteListener() {
				public void onComplete(boolean success) {
					if(success)
						finish();
				}
			}).execute("user/changePassword", mPassword);
		}
	}	
}
