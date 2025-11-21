package com.platzi.play.persistence.mapper;

import org.mapstruct.Named;

public class StateMapper {
    @Named("stateToBoolean")
    public static Boolean stateToBoolean(String estado) {
        if (estado == null) return null;

        return switch (estado.toUpperCase()) {
            case "D" -> true;
            case "N" -> false;
            default -> null;
        };
    }

    @Named("booleanToState")
    public static String booleanToState(Boolean state) {
        return state == null ? "N" : (state ? "D" : "N");
    }


}


