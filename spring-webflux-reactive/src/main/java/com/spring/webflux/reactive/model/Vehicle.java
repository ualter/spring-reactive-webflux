package com.spring.webflux.reactive.model;

import java.io.Serializable;

public class Vehicle implements Serializable {
    
    private static final long serialVersionUID = -6415713305955411683L;
    private String  carPlateNumber;
    private Long    weight;
    private Integer speed;
    
    public Vehicle() {
    	
    }
    public Vehicle(String carPlateNumber, Long weight, Integer speed) {
        super();
        this.carPlateNumber = carPlateNumber;
        this.weight = weight;
        this.speed = speed;
    }
    public String getCarPlateNumber() {
        return carPlateNumber;
    }
    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }
    public Long getWeight() {
        return weight;
    }
    public void setWeight(Long weight) {
        this.weight = weight;
    }
    public Integer getSpeed() {
        return speed;
    }
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Vehicle [carPlateNumber=");
        builder.append(carPlateNumber);
        builder.append(", weight=");
        builder.append(weight);
        builder.append(", speed=");
        builder.append(speed);
        builder.append("]");
        return builder.toString();
    }
}
