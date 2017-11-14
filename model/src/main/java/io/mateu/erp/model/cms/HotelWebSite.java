package io.mateu.erp.model.cms;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import io.mateu.erp.model.authentication.AuthToken;
import io.mateu.erp.model.financials.Actor;
import io.mateu.erp.model.product.hotel.Hotel;
import io.mateu.erp.model.product.hotel.Room;
import io.mateu.erp.model.util.Helper;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.mdd.server.annotations.Owned;
import io.mateu.ui.mdd.server.annotations.Tab;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class HotelWebSite extends Website {

    @Tab("Hotel")
    @ManyToOne
    private AuthToken authToken;

    @Tab("Home")
    @OneToOne
    @Owned
    private HotelHomePage home;

    @Tab("Offers")
    @OneToOne
    @Owned
    private HotelOffersPage offers;

    @Tab("Rooms")
    @OneToOne
    @Owned
    private AbstractPage rooms;

    @Tab("Services")
    @OneToOne
    @Owned
    private HotelServicesPage services;

    @Tab("Fotos")
    @OneToOne
    @Owned
    private AbstractPage fotos;

    @Tab("Map")
    @OneToOne
    @Owned
    private AbstractPage map;

    @Tab("Contact")
    @OneToOne
    @Owned
    private HotelContactPage contact;


    @Tab("Booking")
    @OneToOne
    @Owned
    private HotelBookingPage booking;

    @Override
    public Data toData() {
        return super.toData();
    }

    @Override
    public void createContentFiles(EntityManager em, File contentDir, File dataDir) throws Throwable {

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "_index.md");

            if (!f.exists()) Files.write(getHome().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "ofertas.md");

            if (!f.exists()) Files.write(getOffers().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "habitaciones.md");

            if (!f.exists()) Files.write(getRooms().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "servicios.md");

            if (!f.exists()) Files.write(getServices().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "contacto.md");

            if (!f.exists()) Files.write(getContact().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "reserva.md");

            if (!f.exists()) Files.write(getBooking().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "mapa.md");

            if (!f.exists()) Files.write(getMap().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "fotos.md");

            if (!f.exists()) Files.write(getFotos().toMd().toString(), f, Charsets.UTF_8);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "config.md");

            if (!f.exists()) Files.write(("+++\n" +
                    "title = \"Config\"\n" +
                    "layout = \"config\"\n" +
                    "+++").toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(dataDir.getAbsolutePath() + java.io.File.separator + "todo.json");

            Map<String, Object> m = new HashMap<>();

            {
                List mensajes = new ArrayList();
                for (Card c : getHome().getMessages()) {
                    mensajes.add(Helper.hashmap("titulo", c.getHeader(), "texto", c.getText()));
                }
                m.put("home", Helper.hashmap("mensajeprincipal", getHome().getMainMessage(), "mensajesecundario", getHome().getSubMessage(), "mensajes", mensajes));
            }


            {
                List mensajes = new ArrayList();
                for (Card c : getOffers().getOffers()) {
                    mensajes.add(Helper.hashmap("titulo", c.getHeader(), "texto", c.getText(), "imagen", "/images/habitacion1.jpg"));
                }
                m.put("ofertas", mensajes);
            }

            if (getAuthToken() != null && getAuthToken().getHotel() != null) {
                List mensajes = new ArrayList();
                for (Room c : getAuthToken().getHotel().getRooms()) {
                    mensajes.add(Helper.hashmap("titulo", c.getType().getName().getEs(), "texto", c.getDescription().getEs(), "imagen", "/images/habitacion1.jpg"));
                }
                m.put("habitaciones", mensajes);
            }

            {
                List mensajes = new ArrayList();
                mensajes.add(Helper.hashmap("titulo", "Dirección", "texto", getContact().getAddress()));
                mensajes.add(Helper.hashmap("titulo", "Teléfono", "texto", getContact().getTelephone()));
                mensajes.add(Helper.hashmap("titulo", "Email", "texto", getContact().getEmail()));

                m.put("contacto", mensajes);
            }


            if (!f.exists()) Files.write(Helper.toJson(m), f, Charsets.UTF_8);
        }

    }
}
