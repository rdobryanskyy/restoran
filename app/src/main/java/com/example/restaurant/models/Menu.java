package com.example.restaurant.models;

import com.example.restaurant.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Menu
{

    private List<Dish> dishes;
    private String menuID;

    private XmlPullParserFactory xmlFactoryObject;
    private XmlPullParser menuParser;


    public Menu(String menuXml)
    {
        dishes = new ArrayList<Dish>();
        try
        {
            InputStream is = new ByteArrayInputStream(menuXml.getBytes(StandardCharsets.UTF_8.name()));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element=doc.getDocumentElement();
            element.normalize();
            NodeList nList = doc.getElementsByTagName("Dish");

            for (int i=0; i< nList.getLength(); i++)
            {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element2 = (Element) node;
                    Dish newDish = new Dish(XmlUtils.getXMLValue("id", element2),
                            XmlUtils.getXMLValue("name", element2),
                            XmlUtils.getXMLValue("price", element2));
                    dishes.add(newDish);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

}
