package com.example.calculationkcal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.calculationkcal.model.MarkerModel;

public class SeekBarLogic {
    private Context context;
    private TextView tvWeightValue, tvHeightValue, tvAgeValue;
    private SeekBar seekBarWeight, seekBarHeight, seekBarAge;
    // Задаємо мінімальну вагу для шкали
    private final int MIN_WEIGHT = 30;
    private final int MIN_HEIGHT = 30;
    private final int MIN_AGE = 10;

    public SeekBarLogic(Context context, TextView tvWeightValue, SeekBar seekBarWeight) {
        this.context = context;
        this.tvWeightValue = tvWeightValue;
        this.seekBarWeight = seekBarWeight;
    }

    @SuppressLint("SetTextI18n")
    public void startLogic(View view, MarkerModel markerModel) {

        tvWeightValue = view.findViewById(R.id.weightSeekBarText);
        tvHeightValue = view.findViewById(R.id.heightSeekBarText);
        tvAgeValue = view.findViewById(R.id.ageSeekBarText);

        seekBarWeight = view.findViewById(R.id.seekBarWeight);
        seekBarHeight = view.findViewById(R.id.seekBarHeight);
        seekBarAge = view.findViewById(R.id.seekBarAge);


        int initialWeight = MIN_WEIGHT + seekBarWeight.getProgress();
        int initialHeight = MIN_HEIGHT + seekBarHeight.getProgress();
        int initialAge = MIN_AGE + seekBarAge.getProgress();

        if (markerModel.equals(MarkerModel.WEIGHT)) {
            tvWeightValue.setText("Вага: " + initialWeight + " кг");
        } else if (markerModel.equals(MarkerModel.HEIGHT)) {
            tvHeightValue.setText("Зріст: " + initialHeight + " см");
        } else if (markerModel.equals(MarkerModel.AGE)) {
            tvAgeValue.setText("Вік " + initialAge);
        }

        // Слухач зміни значень на шкалі
        seekBarWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Обчислюємо реальну вагу: мінімум + поточний крок
                int currentWeight = MIN_WEIGHT + progress;
                tvWeightValue.setText("Вага: " + currentWeight + " кг");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Викликається в момент дотику до повзунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Викликається, коли користувач відпускає повзунок
            }
        });

        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Обчислюємо реальну вагу: мінімум + поточний крок
                int currentWeight = MIN_HEIGHT + progress;
                tvHeightValue.setText("Зріст: " + currentWeight + " см");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Викликається в момент дотику до повзунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Викликається, коли користувач відпускає повзунок
            }
        });

        seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Обчислюємо реальну вагу: мінімум + поточний крок
                int currentAge = MIN_AGE + progress;
                tvAgeValue.setText("Вік: " + currentAge + " р.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Викликається в момент дотику до повзунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Викликається, коли користувач відпускає повзунок
            }
        });
    }
}
