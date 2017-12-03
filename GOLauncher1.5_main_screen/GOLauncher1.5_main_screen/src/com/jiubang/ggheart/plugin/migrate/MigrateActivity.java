package com.jiubang.ggheart.plugin.migrate;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RelativeLayout;


/**
 * 
 * <br>类描述:
 * <br>功能详细描述:
 * 
 * @author  ruxueqin
 * @date  [2013-5-23]
 */
public class MigrateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		RelativeLayout view = new RelativeLayout(this);
		setContentView(view);
		
		String uri = getIntent().getStringExtra("uri");
		boolean clearflag = getIntent().getBooleanExtra("clearflag", false);
		MigrateIntoDesk migtate = new MigrateIntoDesk(this, Uri.parse(uri), clearflag, view);
		migtate.startMigrate();
	}
}
