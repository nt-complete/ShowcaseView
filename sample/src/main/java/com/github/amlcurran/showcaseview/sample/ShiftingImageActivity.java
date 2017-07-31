package com.github.amlcurran.showcaseview.sample;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.espian.showcaseview.sample.R;
import com.github.amlcurran.showcaseview.OnShowcaseEventListenerAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class ShiftingImageActivity extends Activity {

    Button shiftButtonButton;
    Button shiftImageButton;

    private int buttonPlacement = 0;
    private int imageGravity = 0;
    private RelativeLayout.LayoutParams buttonLPs;

    private ShowcaseView shiftingImageSV;
    private ShowcaseView shiftingButtonSV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shifting_image);

        shiftButtonButton = (Button) findViewById(R.id.shifting_button_button);
        shiftImageButton = (Button) findViewById(R.id.shifting_image_button);

        buttonLPs = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        buttonLPs.setMargins(margin, margin, margin, margin);

        createButtonShowcase();
        createImageShowcase();

        setOnClickListeners();
    }

    private void createImageShowcase() {
        shiftingImageSV = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(shiftImageButton))
                .setContentText(R.string.shifting_image_body)
                .setContentTitle(R.string.shifting_image_title)
                .setContentImage(R.drawable.evolution)
                .buildNoShow();

        shiftingImageSV.setOnShowcaseEventListener(new OnShowcaseEventListenerAdapter() {
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {
                super.onShowcaseViewShow(showcaseView);
                shiftImageButton.setText(R.string.shift);
            }

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                super.onShowcaseViewHide(showcaseView);
                shiftImageButton.setText(R.string.shifting_image);
            }
        });
    }

    private void createButtonShowcase() {
        shiftingButtonSV = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(shiftButtonButton))
                .setContentText(R.string.shifting_button_body)
                .setContentTitle(R.string.shifting_button_title)
                .setContentImage(R.drawable.kitten)
                .buildNoShow();

        shiftingButtonSV.setButtonPosition(buttonLPs);

        shiftingButtonSV.setOnShowcaseEventListener(new OnShowcaseEventListenerAdapter() {
            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {
                super.onShowcaseViewShow(showcaseView);
                shiftButtonButton.setText(R.string.shift);
            }

            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                super.onShowcaseViewHide(showcaseView);
                shiftButtonButton.setText(R.string.shifting_button);
            }
        });

    }


    private void setOnClickListeners() {

        shiftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shiftingImageSV.isShowing()) {
                    imageGravity = ++imageGravity % 7;
                    switch (imageGravity) {
                        case 0:
                            Toast.makeText(ShiftingImageActivity.this, "Center vertical", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.CENTER_VERTICAL);
                            break;
                        case 1:
                            Toast.makeText(ShiftingImageActivity.this, "Center horizontal", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.CENTER_HORIZONTAL);
                            break;
                        case 2:
                            Toast.makeText(ShiftingImageActivity.this, "Center", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.CENTER);
                            break;
                        case 3:
                            Toast.makeText(ShiftingImageActivity.this, "Top", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.TOP);
                            break;
                        case 4:
                            Toast.makeText(ShiftingImageActivity.this, "Bottom", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.BOTTOM);
                            break;
                        case 5:
                            Toast.makeText(ShiftingImageActivity.this, "Left", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.LEFT);
                            break;
                        case 6:
                            Toast.makeText(ShiftingImageActivity.this, "Right", Toast.LENGTH_SHORT).show();
                            shiftingImageSV.setImageGravity(ShowcaseView.RIGHT);
                            break;

                    }

                } else {
                    shiftingImageSV.show();
                }
            }
        });

        shiftButtonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shiftingButtonSV.isShowing()) {
                    buttonPlacement = ++buttonPlacement % 6;
                    switch (buttonPlacement) {
                        case 0:
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            break;
                        case 1:
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                            buttonLPs.addRule(RelativeLayout.CENTER_VERTICAL);
                            break;
                        case 2:
                            buttonLPs.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                            break;
                        case 3:
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            break;
                        case 4:
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                            buttonLPs.addRule(RelativeLayout.CENTER_VERTICAL);
                            break;
                        case 5:
                            buttonLPs.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                            buttonLPs.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            break;
                    }

                    shiftingButtonSV.setButtonPosition(buttonLPs);
                } else {

                    shiftingButtonSV.show();
                }
            }
        });
    }
}
