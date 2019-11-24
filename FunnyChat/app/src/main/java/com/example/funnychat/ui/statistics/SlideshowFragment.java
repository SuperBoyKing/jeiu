package com.example.funnychat.ui.statistics;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.funnychat.R;
import com.example.funnychat.background.GetterChat;
import com.example.funnychat.chat.ClientActivity;
import com.example.funnychat.foreground.MainActivity;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    PieChart pieChart;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        /*FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });*/
        pieChart = root.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        try {
            GetterChat getterChat = new GetterChat();
            String data = getterChat.execute("send").get();
            ArrayList<JsonObject> obj_list = new ArrayList<JsonObject>();
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = (JsonArray)parser.parse(data);
            JsonObject obj;
            int sum = 0;
            for (int i=0; i<jsonArray.size(); ++i) {
                obj = (JsonObject) jsonArray.get(i);
                obj_list.add(obj);
                int count = obj_list.get(i).get("count").getAsInt();
                sum += count;
            }

            for (int i=0; i<obj_list.size(); i++) {
                int count = obj_list.get(i).get("count").getAsInt();
                String chat = obj_list.get(i).get("message").getAsString();
                yValues.add(new PieEntry((count*100)/sum, chat));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"noun");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        return root;
    }
}