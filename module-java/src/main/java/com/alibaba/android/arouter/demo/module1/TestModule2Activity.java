package com.alibaba.android.arouter.demo.module1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/module/2", group = "m2", extraPaths = {"/module/<:id>"})
public class TestModule2Activity extends AppCompatActivity {
    @Autowired
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_module2);
        ARouter.getInstance().inject(this);
        if (id != null && !id.isEmpty()) {
            Toast.makeText(this, "我是通配符跳转：" + id, Toast.LENGTH_LONG).show();
        }
    }
}
