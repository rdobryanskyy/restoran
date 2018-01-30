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

    static String debugTimes = "<ArrayOfstring xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "<string>08:00</string>\n" +
            "<string>08:30</string>\n" +
            "<string>09:00</string>\n" +
            "<string>09:30</string>\n" +
            "<string>10:00</string>\n" +
            "<string>10:30</string>\n" +
            "<string>11:00</string>\n" +
            "<string>11:30</string>\n" +
            "<string>12:00</string>\n" +
            "<string>12:30</string>\n" +
            "<string>13:00</string>\n" +
            "<string>13:30</string>\n" +
            "<string>14:00</string>\n" +
            "<string>14:30</string>\n" +
            "<string>15:00</string>\n" +
            "<string>15:30</string>\n" +
            "<string>16:00</string>\n" +
            "<string>16:30</string>\n" +
            "<string>17:00</string>\n" +
            "<string>17:30</string>\n" +
            "<string>18:00</string>\n" +
            "<string>18:30</string>\n" +
            "<string>19:00</string>\n" +
            "<string>19:30</string>\n" +
            "<string>20:00</string>\n" +
            "<string>20:30</string>\n" +
            "<string>21:00</string>\n" +
            "<string>21:30</string>\n" +
            "<string>22:00</string>\n" +
            "<string>22:30</string>\n" +
            "</ArrayOfstring>";

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
