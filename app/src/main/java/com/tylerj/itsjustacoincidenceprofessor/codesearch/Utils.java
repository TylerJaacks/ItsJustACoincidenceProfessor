package com.tylerj.itsjustacoincidenceprofessor.codesearch;

import org.apache.commons.codec.language.bm.Lang;

/**
 * Utils for getting the languageId from an enumId.
 */
public class Utils {
    /**
     * Turns a enumId into a language id.
     * @param language Enum for the language.
     * @return the language id.
     */
    public static int languageEnumToId(Languages language) {
        switch (language) {
            case JAVA:
                return 23;
            case C:
                return 28;
            case CS:
                return 6;
            case H:
                return 15;
            case CPP:
                return 16;
            case HTML:
                return 3;
            case PHP:
                return 24;
            case M:
                return 21;
            case GROOVY:
                return 49;
            case JS:
                return 22;
            case PY:
                return 19;
            case PL:
                return 51;
            case RB:
                return 32;
            case LUA:
                return 54;
            case VB:
                return 30;
            case PS:
                return 146;
            case GO:
                return 55;
            case JSON:
                return 122;
        }

        return 0;
    }

    /**
     * Turns a enumId into a location id.
     * @param locations Enum for the location.
     * @return the location id.
     */
    public static int locationEnumToId(Locations locations) {
        switch (locations) {
            case GOOGLE_CODE:
                return 1;
            case GITHUB:
                return 2;
            case CODEPLEX:
                return 3;
            case SOURCEFORGE:
                return 4;
            case BITBUCKET:
                return 5;
        }

        return 0;
    }

    public static Languages getLocation(String lang){

        switch(lang){
            case "JAVA":
                return Languages.JAVA;
            case "C":
                return Languages.C;
            case "CS":
                return Languages.CS;
            case "H":
                return Languages.H;
            case "CPP":
                return Languages.CPP;
            case "HTML":
                return Languages.HTML;
            case "PHP":
                return Languages.PHP;
            case "M":
                return Languages.M;
            case "GROOVY":
                return Languages.GROOVY;
            case "JS":
                return Languages.JS;
            case "PY":
                return Languages.PY;
            case "PL":
                return Languages.PL;
            case "RB":
                return Languages.RB;
            case "LUA":
                return Languages.LUA;
            case "VB":
                return Languages.VB;
            case "PS":
                return Languages.PS;
            case "GO":
                return Languages.GO;
            case "JSON":
                return Languages.JSON;
            default:
                return null;

        }

    }
}
