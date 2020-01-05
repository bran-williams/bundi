package com.branwilliams.bundi.engine;

import com.branwilliams.bundi.engine.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Scanner;

public class CardMain {

//        System.out.println("Suites: " + Arrays.toString(CardSuit.values()));
//        System.out.println("Enter card suit: ");
//        Scanner scanner = new Scanner(System.in);
//        String readLine = scanner.next();
//        CardSuit readCardsuit = CardSuit.valueOf(readLine.toUpperCase());
//        System.out.println("Selected suite = " + readCardsuit.displayName);

    public static void main(String[] args) {
        System.out.println("************************************************************");

        // card ranks are determinable.
        assert CardRank.FIVE.hasHigherRank(CardRank.TWO);
        assert !CardRank.TWO.hasHigherRank(CardRank.FIVE);

        String filePath = "cards.json";
        String fileContents = IOUtils.readFile(filePath, "");
        System.out.println("File contents for " + filePath + ":");
        System.out.println(fileContents);
        System.out.println("");

        // Gson parser parses JSON objects.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("Parsed card:");
        Card card = gson.fromJson(fileContents, Card.class);
        System.out.println(card);

        System.out.println("************************************************************");

    }
}
