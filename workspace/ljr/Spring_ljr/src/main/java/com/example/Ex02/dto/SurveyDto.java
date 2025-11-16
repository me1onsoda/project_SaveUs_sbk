package com.example.Ex02.dto;

import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD

public class SurveyDto {

    @NotNull private Integer q1;
    @NotNull private Integer q2;
    @NotNull private Integer q3;
    @NotNull private Integer q4;
    @NotNull private Integer q5;
    @NotNull private Integer q6;
    @NotNull private Integer q7;

    public Integer getQ1() { return q1; }
    public void setQ1(Integer q1) { this.q1 = q1; }

    public Integer getQ2() { return q2; }
    public void setQ2(Integer q2) { this.q2 = q2; }

    public Integer getQ3() { return q3; }
    public void setQ3(Integer q3) { this.q3 = q3; }

    public Integer getQ4() { return q4; }
    public void setQ4(Integer q4) { this.q4 = q4; }

    public Integer getQ5() { return q5; }
    public void setQ5(Integer q5) { this.q5 = q5; }

    public Integer getQ6() { return q6; }
    public void setQ6(Integer q6) { this.q6 = q6; }

    public Integer getQ7() { return q7; }
    public void setQ7(Integer q7) { this.q7 = q7; }
=======
import lombok.Data;

@Data
public class SurveyDto {

    // 단백질
    @NotNull(message = "문항을 선택해주세요.")
    private Integer protein1;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer protein2;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer protein3;

    // 지방
    @NotNull(message = "문항을 선택해주세요.")
    private Integer fat1;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer fat2;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer fat3;

    // 탄수화물
    @NotNull(message = "문항을 선택해주세요.")
    private Integer carb1;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer carb2;
    @NotNull(message = "문항을 선택해주세요.")
    private Integer carb3;

    // 결과 저장용
    private String proteinType;
    private String fatType;
    private String carbType;

    public Integer getProtein1() { return protein1; }
    public void setProtein1(Integer protein1) { this.protein1 = protein1; }

    public Integer getProtein2() { return protein2; }
    public void setProtein2(Integer protein2) { this.protein2 = protein2; }

    public Integer getProtein3() { return protein3; }
    public void setProtein3(Integer protein3) { this.protein3 = protein3; }

    public Integer getFat1() { return fat1; }
    public void setFat1(Integer fat1) { this.fat1 = fat1; }

    public Integer getFat2() { return fat2; }
    public void setFat2(Integer fat2) { this.fat2 = fat2; }

    public Integer getFat3() { return fat3; }
    public void setFat3(Integer fat3) { this.fat3 = fat3; }

    public Integer getCarb1() { return carb1; }
    public void setCarb1(Integer carb1) { this.carb1 = carb1; }

    public Integer getCarb2() { return carb2; }
    public void setCarb2(Integer carb2) { this.carb2 = carb2; }

    public Integer getCarb3() { return carb3; }
    public void setCarb3(Integer carb3) { this.carb3 = carb3; }

    public String getProteinType() { return proteinType; }
    public void setProteinType(String proteinType) { this.proteinType = proteinType; }

    public String getFatType() { return fatType; }
    public void setFatType(String fatType) { this.fatType = fatType; }

    public String getCarbType() { return carbType; }
    public void setCarbType(String carbType) { this.carbType = carbType; }
>>>>>>> home2
}
