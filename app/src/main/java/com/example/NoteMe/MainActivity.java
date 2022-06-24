package com.example.NoteMe;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        service = new Intent(this, MyService.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Animation avt = AnimationUtils.loadAnimation(this,R.anim.splash_anim_img);
        Animation avttext2 = AnimationUtils.loadAnimation(this,R.anim.splast_text2);
        ImageView avatarimg = this.findViewById(R.id.avatar);
        TextView texttesst2 = findViewById(R.id.text2);
        Animation avttext = AnimationUtils.loadAnimation(MainActivity.this,R.anim.splast_text);
        TextView texttesst = findViewById(R.id.text);
        Animation avttext3 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.splast_text);
        TextView texttesst3 = findViewById(R.id.text3);


        avatarimg.setAnimation(avt);
        avt.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                texttesst.setAnimation(avttext);
                texttesst2.setAnimation(avttext2);
                texttesst3.setAnimation(avttext3);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        avttext2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                texttesst.setVisibility(View.VISIBLE);
                texttesst2.setVisibility(View.VISIBLE);
                texttesst3.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, LottieActivyti.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }
    @Override
    protected void onStart() {
        MainActivity.this.startService(service);
        super.onStart();
    }


}