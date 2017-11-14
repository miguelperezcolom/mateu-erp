package io.mateu.erp.model.cms;

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

            if (!f.exists()) Files.write(getHome().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "ofertas.md");

            if (!f.exists()) Files.write(getOffers().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "habitaciones.md");

            if (!f.exists()) Files.write(getRooms().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "servicios.md");

            if (!f.exists()) Files.write(getServices().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "contacto.md");

            if (!f.exists()) Files.write(getContact().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "reserva.md");

            if (!f.exists()) Files.write(getBooking().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "mapa.md");

            if (!f.exists()) Files.write(getMap().toMd().toString().getBytes(), f);
        }

        {
            java.io.File f = new java.io.File(contentDir.getAbsolutePath() + java.io.File.separator + "fotos.md");

            if (!f.exists()) Files.write(getFotos().toMd().toString().getBytes(), f);
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

            /*
            {
  "home" : {
    "mensajes" : [
      {
        "titulo" : "Mejor precio garantizado"
        , "texto" : "No encontrará un precio mejor."
      }
      , {
            "titulo" : "Ventajas reserva online"
            , "texto" : "Late checkout, cocktail de bienvenida, las mejores habitaciones disponibles y otras ventajas si reserva en nuestra web."
          }
    ]
  }
  , "ofertas" : [
    {
      "titulo" : "Venta anticipada"
      , "texto" : "15% de descuento si hace su reserva antes del 15 de Abril."
      , "imagen" : "/images/fondohotel1_1920.jpg"
    }
    , {
      "titulo" : "7 x 6"
      , "texto" : "Una noche gratis al reservar una suite para estancias en el mes de Mayo."
      , "imagen" : "/images/fondohotel2_1920.jpg"
    }
    , {
      "titulo" : "Primer niño gratis"
      , "texto" : "Para todas las familias que reserven una habitación con vista mar."
      , "imagen" : "/images/fondohotel3_1920.jpg"
    }
  ], "habitaciones" : [
    {
      "titulo" : "Habitación doble"
      , "texto" : "Habitación con 2 camas individuales."
      , "imagen" : "/images/habitacion1.jpg"
    }
    , {
      "titulo" : "Habitación individual"
      , "texto" : "Para aquellos que viajan solos."
      , "imagen" : "/images/habitacion4.jpg"
    }
    , {
      "titulo" : "Suite"
      , "texto" : "Lujosa habitación con todas las comodidades."
      , "imagen" : "/images/habitacion3.jpg"
    }
  ]
  , "servicios" : [
      {
        "titulo" : "Recepción 24 horas"
        , "texto" : "Habitación con 2 camas individuales."
        , "icono" : "clock-o"
      }
      , {
        "titulo" : "Teléfono"
        , "texto" : "Para aquellos que viajan solos."
        , "icono" : "phone"
      }
      , {
        "titulo" : "Ducha"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "shower"
      }
      , {
        "titulo" : "Solarium"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "sun-o"
      }
      , {
        "titulo" : "Taxi"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "taxi"
      }
      , {
        "titulo" : "Accesibilidad"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "wheelchair"
      }
      , {
        "titulo" : "Ciclismo"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "bicycle"
      }
      , {
        "titulo" : "Aceptamos VISA"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "cc-visa"
      }
      , {
        "titulo" : "WIFI"
        , "texto" : "Lujosa habitación con todas las comodidades."
        , "icono" : "wifi"
      }
    ]
    , "contacto" : [
      {
        "titulo" : "Dirección"
        , "texto" : "Gremi fusters, 11"
      }
      , {
        "titulo" : "CP"
        , "texto" : "07009"
      }
      , {
        "titulo" : "Población"
        , "texto" : "Palma de Mallorca"
      }
      , {
        "titulo" : "Tel."
        , "texto" : "971 12 34 56"
      }
      , {
        "titulo" : "Email"
        , "texto" : "demo@quoon.net"
      }
    ]
}

             */

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
                    mensajes.add(Helper.hashmap("titulo", c.getHeader(), "texto", c.getText()));
                }
                m.put("ofertas", mensajes);
            }

            if (getAuthToken() != null && getAuthToken().getHotel() != null) {
                List mensajes = new ArrayList();
                for (Room c : getAuthToken().getHotel().getRooms()) {
                    mensajes.add(Helper.hashmap("titulo", c.getType().getName().getEs(), "texto", c.getDescription().getEs()));
                }
                m.put("habitaciones", mensajes);
            }

            if (!f.exists()) Files.write(Helper.toJson(m).getBytes(), f);
        }

    }
}
