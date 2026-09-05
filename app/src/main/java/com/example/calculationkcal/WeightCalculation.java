package com.example.calculationkcal;

import com.example.calculationkcal.model.ActivityModel;
import com.example.calculationkcal.model.GoalModel;
import com.example.calculationkcal.model.SexHumanModel;

public class WeightCalculation {

    private String weight;
    private String height;
    private String age;
    private final int KCAL = 400;
    private Double result, activityCoefficientResult, goal;
    private SexHumanModel sexHuman;

    public WeightCalculation(String weight, String height, String age, SexHumanModel sexHuman) {
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.sexHuman = sexHuman;
    }


    public Double startWeightCalculation(ActivityModel activityModel,
                                         GoalModel goalModel) {
        int weight = Integer.parseInt(this.weight) * 10;
        double height = Float.parseFloat(this.height) * 6.25;
        int age = Integer.parseInt(this.age) * 5;
        if (sexHuman.equals(SexHumanModel.MAN)) {
            result = weight + height - age + 5;
        }else if (sexHuman.equals(SexHumanModel.WOMAN)){
            result = weight + height - age - 161;
        }

        double activity = getActivityCoefficient(result, activityModel);
        return getGoal(activity, goalModel);
    }

    private Double getActivityCoefficient(Double result, ActivityModel activityModel) {

        if (activityModel.equals(ActivityModel.SEDENTARY)) {
            activityCoefficientResult = result * 1.2;
        } else if (activityModel.equals(ActivityModel.LIGHT_ACTIVITY)) {
            activityCoefficientResult = result * 1.375;
        } else if (activityModel.equals(ActivityModel.MODERATE_ACTIVITY)) {
            activityCoefficientResult = result * 1.55;
        } else if (activityModel.equals(ActivityModel.HIGH_ACTIVITY)) {
            activityCoefficientResult = result * 1.725;
        } else if (activityModel.equals(ActivityModel.VERY_HIGH_ACTIVITY)) {
            activityCoefficientResult = result * 1.9;
        }
        return activityCoefficientResult;
    }


    private Double getGoal(Double activityCoefficientResultResult, GoalModel goalModel) {
        if (goalModel.equals(GoalModel.TO_GAIN)) {
            goal = activityCoefficientResultResult + KCAL;
        } else if (goalModel.equals(GoalModel.TO_LOSE)) {
            goal = activityCoefficientResultResult - KCAL;
        } else if (goalModel.equals(GoalModel.SUPPORT)) {
            goal = activityCoefficientResultResult;
        }
        return goal;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public SexHumanModel getSexHuman() {
        return sexHuman;
    }

    public void setSexHuman(SexHumanModel sexHuman) {
        this.sexHuman = sexHuman;
    }

    public Double getResult() {
        return result;
    }

}
