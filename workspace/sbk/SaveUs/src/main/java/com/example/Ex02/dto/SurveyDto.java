package com.example.Ex02.dto;

import jakarta.validation.constraints.NotNull;

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
}
