package com.self.rxdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mochen.rxbus.RxBus;
import com.mochen.rxbus.annotation.Subscribe;
import com.mochen.rxbus.annotation.Tag;

import java.util.ArrayList;

public class Test1Activity extends AppCompatActivity implements View.OnClickListener {

    TextView btn1, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btn_click1);
        btn2 = findViewById(R.id.btn_click2);
        btn3 = findViewById(R.id.btn_click3);
        btn4 = findViewById(R.id.btn_click4);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);


        RxBus.get().register(this);

    }

    @Subscribe(tags = {@Tag(value = "test")})
    public void test1(Test1 test) {
        Toast.makeText(this, test.toString(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(tags = {@Tag(value = "test")})
    public void test1(Test3 test) {
        Toast.makeText(this, test.getText(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe(tags = {@Tag(value = "null_test")})
    public void test2() {
        Toast.makeText(this, "这是第一个activity，postTag", Toast.LENGTH_SHORT).show();
    }

    @Subscribe()
    public void test3() {
        Toast.makeText(this, "这是第一个activity，post", Toast.LENGTH_SHORT).show();
    }

    @Subscribe()
    public void test4(ArrayList datas) {
        Toast.makeText(this, "这是第一个activity，post-obj；" + datas.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_click1:
                startActivity(new Intent(this, Test2Activity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
