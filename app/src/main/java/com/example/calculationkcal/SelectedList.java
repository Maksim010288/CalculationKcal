package com.example.calculationkcal;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class SelectedList {

    private Context context;
    private View view;
    private AutoCompleteTextView autoCompleteTextView;

    public SelectedList(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void getHuman() {
        String[] items = {"ЧОЛОВІК", "ЖІНКА"};
        getHumanList(items, "human");
    }

    public void getLoad() {
        String[] items = {"СИДЯЧИЙ СПОСІБ ЖИТТЯ", "ЛЕГКА", "ПОМІРНА", "ВИСОКА", "ДУЖЕ ВИСОКА"};
        getHumanList(items, "load");
    }

    public void getGoal() {
        String[] items = {"ВТРИМАТИ ВАГУ", "СКИНУТИ ВАГУ", "НАБРАТИ ВАГУ"};
        getHumanList(items, "goal");
    }


    private void getHumanList(String[] items, String text) {
        // 2. Створюємо адаптер (використовуємо стандартний системний макет для рядка)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.drop_down_item,
                android.R.id.text1,
                items
        );

        adapter.setDropDownViewResource(R.layout.drop_down_item);

        if (text.equals("human")) {
            autoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewHuman);
        } else if (text.equals("load")) {
            autoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewLoad);
        } else if (text.equals("goal")) {
            autoCompleteTextView = view.findViewById(R.id.autoCompleteTextViewGoal);
        }
        autoCompleteTextView.setAdapter(adapter);

        // 4. Обробляємо вибір елемента користувачем
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

            }
        });
    }
}
