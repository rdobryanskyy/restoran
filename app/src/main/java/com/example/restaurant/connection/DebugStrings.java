package com.example.restaurant.connection;

/**
 * Created by alexeynikitenko on 1/29/18.
 */

public class DebugStrings
{
    static String debugMenu = "<?xml version=\"1.0\"?>\n" +
            "<Menu>\n" +
            "   <Dish>\n" +
            "      <id>1</id>\n" +
            "      <name>Name 2</name>\n" +
            "      <price>50$</price>\n" +
            "   </Dish>\n" +
            "\t\n" +
            "   <Dish>\n" +
            "      <id>2</id>\n" +
            "      <name>Name 2</name>\n" +
            "      <price>60$</price>\n" +
            "   </Dish>\n" +
            "\t\n" +
            "   <Dish>\n" +
            "      <id>3</id>\n" +
            "      <name>Name 3</name>\n" +
            "      <price>70$</price>\n" +
            "   </Dish>\n" +
            "\t\n" +
            "</Menu>";

    static String debugTimes = "<?xml version=\"1.0\"?>\n" +
            "<Times>\n" +
            "<Time>\n" +
            "      <value>8</value>\n" +
            "</Time>\n" +
            "<Time>\n" +
            "      <value>12</value>\n" +
            "</Time>\n" +
            "<Time>\n" +
            "      <value>22</value>\n" +
            "</Time>\n" +
            "<Time>\n" +
            "      <value>18</value>\n" +
            "</Time>\n" +
            "</Times>";

    static String debugReservations = "<?xml version=\"1.0\"?>\n" +
            "<Reservations>\n" +
            "   <Reservation>\n" +
            "      <id>RESEVATION_1</id>\n" +
            "      <email>a@a.com</email>\n" +
            "      <date>01.30.2018</date>\n" +
            "      <persons>5</persons>\n" +
            "        <Dish>\n" +
            "           <id>3</id>\n" +
            "        </Dish>\n" +
            "        <Dish>\n" +
            "           <id>1</id>\n" +
            "        </Dish>\n" +
            "   </Reservation>\n" +
            "\t\n" +
            "</Reservations>";

}
