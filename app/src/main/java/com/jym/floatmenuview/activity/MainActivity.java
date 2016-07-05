package com.jym.floatmenuview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jym.floatmenuview.R;
import com.jym.floatmenuview.adapter.MainAdapter;
import com.jym.floatmenuview.view.ControllableSlideViewPager;
import com.jym.floatmenuview.view.FloatMenuView;

public class MainActivity extends AppCompatActivity {

    private ControllableSlideViewPager vpContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpContent = (ControllableSlideViewPager) findViewById(R.id.vpContent);

        final FloatMenuView fv_test = (FloatMenuView) findViewById(R.id.fv_test);
        fv_test.setOnControllerClickListener(new FloatMenuView.OnControllerClickListener() {
            @Override
            public void onControllerDown() {
                vpContent.setCanSlide(false); //设置ViewPager不可滑动
            }

            @Override
            public void onControllerMoveFinished() {
                vpContent.setCanSlide(true); //设置ViewPager可以滑动
            }

            @Override
            public void onControllerClick() {
                fv_test.setControlledVisibility(!fv_test.getControlledVisibility());
                if (!fv_test.getControlledVisibility()) {
                    vpContent.setCanSlide(true); //设置ViewPager可以滑动
                }
            }
        });

        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        vpContent.setAdapter(adapter);
    }
}
