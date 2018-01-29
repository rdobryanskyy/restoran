package com.example.restaurant.models;

import android.widget.ListAdapter;

import com.example.restaurant.database.ReservationDao;
import com.example.restaurant.utils.XmlUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Reservation
{

    protected String reservationID = null;
    Date reservationTime = null;
    Customer customer = null;
    List<OrderedDish> orderedDishes;
    Integer numOfVisitors = 0;


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
        String resID = XmlUtils.getXMLValue("id", element);
        String strDate = XmlUtils.getXMLValue("date", element);
        String strPersons = XmlUtils.getXMLValue("persons", element);
        String strEmail = XmlUtils.getXMLValue("email", element);
        Reservation reservation  = new Reservation(resID, strEmail);
        reservation.setNumOfVisitors(Integer.parseInt(strPersons));
        Date date = new Date();
        reservation.setReservationTime(date);

        NodeList nList = element.getElementsByTagName("dish");
        for (int i=0; i< nList.getLength(); i++)
        {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element2 = (Element) node;
                String dishStr = XmlUtils.getXMLValue("id", element2);
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

    @Override
    public String toString()
    {
        String info = String.format("<id>%s</id><email>%s</email><date>%s</date><persons>%s</persons>",
                reservationID,
                customer.getmEmail(),
                DateFormat.getDateTimeInstance().format(reservationTime),
                numOfVisitors.toString());

        StringBuilder dishes = new StringBuilder();
        for(OrderedDish dish : orderedDishes)
        {
            if(dish.isSelected())
            {
                String strDish = String.format("<Dish><id>%s</is></Dish>", dish.getDishID());
                dishes.append(strDish);
            }
        }
        String xmlDishes = dishes.toString();
        info += xmlDishes;
        String res = String.format("<Reservations>%s</Reservations>", info);

        return res;
    }

}
