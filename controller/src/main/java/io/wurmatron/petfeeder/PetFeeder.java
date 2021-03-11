package io.wurmatron.petfeeder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;

public class PetFeeder {

    // Global Instances
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // Program Vars
    public static Javalin server;

    public static void main(String[] args) {
    }
}
