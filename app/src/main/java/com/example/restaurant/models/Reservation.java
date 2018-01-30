package com.example.restaurant.models;

import android.widget.ListAdapter;

import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.utils.XmlUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Reservation
{

    protected String reservationID = null;
    Date reservationTime = null;

    public String getCustomersEmail() {
        return customer.getmEmail();
    }

    Customer customer = null;
    List<OrderedDish> orderedDishes;
    Integer numOfVisitors = 0;

    List<OrderedDish> previousDishes;


    public void InitPrevDishes()
    {}

    public Reservation(String id, String email)
    {
        orderedDishes = new ArrayList<OrderedDish>();
        customer = new Customer(email);
        List <Dish> dishes = ReservationDao.getInstance().getMenu().getDishes();
        for(Dish dish: dishes)
        {
            orderedDishes.add(new OrderedDish(dish));
        }
        numOfVisitors = 0;
        reservationID = id;
    }


    public static Reservation fromXML(Element element)
    {
        String resID = XmlUtils.getXMLValue("Id", element);

        String strPersons = XmlUtils.getXMLValue("Persons", element);
        String strEmail = XmlUtils.getXMLValue("EMail", element);
        Reservation reservation  = new Reservation(resID, strEmail);
        reservation.setNumOfVisitors(Integer.parseInt(strPersons));
        String strDate = XmlUtils.getXMLValue("DateTime", element);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = dateFormat.parse(strDate);
            reservation.setReservationTime(date);
        } catch (ParseException ex)
        {
            ex.printStackTrace();
            reservation.setReservationTime(new Date());
        }

        NodeList nList = element.getElementsByTagName("OrderItem");
        for (int i=0; i< nList.getLength(); i++)
        {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element2 = (Element) node;
                String dishStr = XmlUtils.getXMLValue("Id", element2);
                {
                    for(OrderedDish dish: reservation.getOrderedDishes())
                    {
                        if(dish.getDishID().equals(dishStr))
                        {
                            dish.setSelected(true);
                        }
                    }
                }
            }
        }

        return reservation;
    }

    public String getReservationID() {
        return reservationID;
    }


    public Date getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Date reservationTime) {

        this.reservationTime = reservationTime;
    }

    public Integer getNumOfVisitors() {
        return numOfVisitors;
    }

    public void setNumOfVisitors(Integer numOfVisitors) {
        this.numOfVisitors = numOfVisitors;
    }

    public void setDishesFromMenu(Menu menu)
    {
        for(Dish dish : menu.getDishes())
        {
            orderedDishes.add(new OrderedDish(dish));
        }
    }

    public List<OrderedDish> getOrderedDishes()
    {
        return orderedDishes;
    }
    private boolean isSelectedDishes() {

        boolean res = false;
        for (OrderedDish dish : orderedDishes)
        {
            if(dish.isSelected())
            {
                res = true;
                break;
            }

        }
        return res;
    }

    public boolean isReservationValid()
    {
        return (numOfVisitors > 0) && (reservationTime != null) && (customer != null) && (isSelectedDishes());
    }

    public String getBriefDescription()
    {
        String stringDate = DateFormat.getDateTimeInstance().format(reservationTime);
        String res = String.format("%s Date %s, persons %d", reservationID, stringDate, numOfVisitors);
        return res;
    }

    private String makeXMLTag(String tagName, String value)
    {
        return String.format("<%s>%s</%s>\r\n", tagName, value, tagName);
    }

//    @Override
//    public String toString()
//    {
//        StringBuilder dishes = new StringBuilder();
//        for(OrderedDish dish : orderedDishes)
//        {
//            if(dish.isSelected())
//            {
//                dishes.append(makeXMLTag("OrderItem", makeXMLTag("DishId", dish.getDishID())));
//            }
//        }
//
//        StringBuilder builder = new StringBuilder();
//        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        builder.append(makeXMLTag("DateTime", dateFormat.format(reservationTime)));
//        builder.append(makeXMLTag("EMail", customer.getmEmail()));
//        builder.append(makeXMLTag("Items", dishes.toString()));
//        builder.append(makeXMLTag("Persons", numOfVisitors.toString()));
//
//        return makeXMLTag("Order", builder.toString());
//    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<Order xmlns=\"http://api-mob.biz-apps.ru\">");
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        builder.append(makeXMLTag("DateTime", dateFormat.format(reservationTime)));
        builder.append(makeXMLTag("EMail", customer.getmEmail()));
        builder.append(makeXMLTag("Persons", numOfVisitors.toString()));
        builder.append("</Order>");

        return  builder.toString();
    }

}
