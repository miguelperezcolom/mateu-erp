package io.mateu.erp.model.booking;

import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.pontus.vizcomponent.model.Subgraph;
import com.vaadin.ui.HorizontalLayout;
import io.mateu.erp.model.booking.generic.GenericService;
import io.mateu.erp.model.booking.hotel.HotelService;
import io.mateu.erp.model.booking.parts.FreeTextBooking;
import io.mateu.erp.model.booking.parts.GenericBooking;
import io.mateu.erp.model.booking.parts.HotelBooking;
import io.mateu.erp.model.booking.parts.TransferBooking;
import io.mateu.erp.model.booking.transfer.TransferDirection;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.erp.model.workflow.TaskResult;
import io.mateu.erp.model.workflow.TaskStatus;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.reflection.ReflectionHelper;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class BookingMap extends HorizontalLayout {

    private Object current;
    private Object parent;
    private Map<Subgraph.Node, Object> reverseMap = new HashMap<>();
    private Map<Integer, Integer> widthPerLevel = new HashMap<>();

    public int getMaxWidth() {
        int max = 1;
        for (int k : widthPerLevel.values()) if (k > max) max = k;
        return max;
    }

    public BookingMap(QuotationRequest group) {
        current = group;
        build();
    }

    public BookingMap(File file) {
        current = file;
        build();
    }

    public BookingMap(Booking booking) {
        current = booking;
        build();
    }

    public BookingMap(Service service) {
        current = service;
        build();
    }

    public BookingMap(PurchaseOrder po) {
        current = po;
        build();
    }

    public BookingMap(AbstractTask task) {
        current = task;
        build();
    }

    public BookingMap(Invoice invoice) {
        current = invoice;
        build();
    }


    private void build() {

        locateParent();


        final VizComponent component = new VizComponent();


        Graph graph = new Graph("G", Graph.DIGRAPH);

        buildGraph(graph, null, parent, 0);

        component.setSizeFull();
        component.setWidth("" + (getMaxWidth() * 150) + "px");
        component.setHeight("" + (widthPerLevel.size() * 150) + "px");
        component.drawGraph(graph);

        component.addClickListener((VizComponent.NodeClickListener) e -> {

            Subgraph.Node n = e.getNode();

            System.out.println("clicked on " + n.getId());

            Object p = reverseMap.get(n);

            if (p != null) {
                MDD.edit(p);
            } else {
                System.out.println("no reverse for " + n.getId());
            }


        });

        addComponent(component);


    }

    private void buildGraph(Graph graph, Graph.Node parentNode, Object object, int level) {

        if (object != null) {

            widthPerLevel.put(level, widthPerLevel.getOrDefault(level, 0) + 1);

            level++;

            Graph.Node node = new Graph.Node(object.getClass().getName() + " " + ReflectionHelper.getId(object));
            node.setParam("label", getTitle(object));
            //node.setParam("tooltip", "AAAAAAA\nBBB\nCCC"); //getToolTip(object));
            graph.addNode(node);

            reverseMap.put(node, object);

            if (object.equals(current)) node.setParam("fontsize", "20");

            //node.setParam("style", "bold");

            if (parentNode != null) {
                graph.addEdge(parentNode, node);
            }

            if (object instanceof QuotationRequest) {
                if (((QuotationRequest) object).isActive()) node.setParam("color", "green");
                else node.setParam("color", "red");

                if (((QuotationRequest) object).getFile() != null) {
                    buildGraph(graph, node, ((QuotationRequest) object).getFile(), level);
                }
            } else if (object instanceof File) {
                if (((File) object).isActive()) node.setParam("color", "green");
                else node.setParam("color", "red");

                for (Booking booking : ((File) object).getBookings()) {
                    buildGraph(graph, node, booking, level);
                }
            } else if (object instanceof Booking) {
                if (((Booking) object).isActive()) node.setParam("color", "green");
                else node.setParam("color", "red");

                for (Service service : ((Booking) object).getServices()) {
                    buildGraph(graph, node, service, level);
                }
            } else if (object instanceof Service) {
                if (((Service) object).isActive()) node.setParam("color", "green");
                else node.setParam("color", "red");

                if (ProcessingStatus.CONFIRMED.equals(((Service) object).getProcessingStatus())) node.setParam("fillcolor", "\"#7BBD7B\"");
                else if (ProcessingStatus.REJECTED.equals(((Service) object).getProcessingStatus())) node.setParam("fillcolor", "\"#9A3838\"");
                else if (ProcessingStatus.SENT.equals(((Service) object).getProcessingStatus())) node.setParam("fillcolor", "\"#F0BC3C\"");
                else node.setParam("fillcolor", "\"#6A79CC\"");
                node.setParam("style", "filled");
                for (PurchaseOrder purchaseOrder : ((Service) object).getPurchaseOrders()) {
                    buildGraph(graph, node, purchaseOrder, level);
                }
            } else if (object instanceof PurchaseOrder) {
                if (((PurchaseOrder) object).isActive()) node.setParam("color", "\"#7BBD7B\"");
                else node.setParam("color", "\"#9A3838\"");

                if (PurchaseOrderStatus.PENDING.equals(((PurchaseOrder) object).getStatus())) node.setParam("fillcolor", "\"#F0BC3C\"");
                else if (PurchaseOrderStatus.CONFIRMED.equals(((PurchaseOrder) object).getStatus())) node.setParam("fillcolor", "\"#7BBD7B\"");
                else if (PurchaseOrderStatus.REJECTED.equals(((PurchaseOrder) object).getStatus())) node.setParam("fillcolor", "\"#9A3838\"");
                node.setParam("style", "filled");
                for (SendPurchaseOrdersTask task : ((PurchaseOrder) object).getSendingTasks()) {
                    buildGraph(graph, node, task, level);
                }
            } else if (object instanceof AbstractTask) {
                if (TaskStatus.PENDING.equals(((AbstractTask) object).getStatus())) node.setParam("fillcolor", "\"#6A79CC\"");
                if (TaskStatus.RUNNING.equals(((AbstractTask) object).getStatus())) node.setParam("fillcolor", "\"#F0BC3C\"");
                else if (TaskResult.OK.equals(((AbstractTask) object).getResult())) node.setParam("fillcolor", "\"#7BBD7B\"");
                else if (TaskResult.ERROR.equals(((AbstractTask) object).getResult())) node.setParam("fillcolor", "\"#9A3838\"");
                node.setParam("style", "filled");
            }

        }

    }

    private String getToolTip(Object p) {
        return "Mi tooltip " + getTitle(p);
    }

    private String getTitle(Object p) {
        String s = p.toString();
        if (p instanceof QuotationRequest) {
            s = "Group " + ((QuotationRequest) p).getId();
        } else if (p instanceof File) {
            s = "File " + ((File) p).getId();
        } else if (p instanceof Booking) {
            if (p instanceof HotelBooking) {
                s = ((HotelBooking) p).getHotel().getName();
            } else if (p instanceof TransferBooking) {
                s = ((TransferBooking) p).getTransferType().name();
            } else if (p instanceof GenericBooking) {
                s = ((GenericBooking) p).getProduct().getName();
            } else if (p instanceof FreeTextBooking) {
                s = ((FreeTextBooking) p).getServiceDescription();
                if (s != null && s.length() > 10) s = s.substring(0, 10) + "...";
            } else {
                s = p.getClass().getSimpleName() + " " + ((Booking) p).getId();
            }
        } else if (p instanceof Service) {
            if (p instanceof TransferService) {
                if (TransferDirection.INBOUND.equals(((TransferService) p).getDirection())) {
                    s = "ARR";
                } else if (TransferDirection.OUTBOUND.equals(((TransferService) p).getDirection())) {
                    s = "DEP";
                } else {
                    s = "P2P";
                }
                s += " " + ((Service)p).getStart().format(DateTimeFormatter.ofPattern("MM-dd"));
            } else {
                s = ((Service)p).getStart().format(DateTimeFormatter.ofPattern("MM-dd")) + " to " + ((Service)p).getStart().format(DateTimeFormatter.ofPattern("MM-dd"));
            }
        } else if (p instanceof PurchaseOrder) {
            s = ((PurchaseOrder) p).getProvider().getName();
        } else if (p instanceof AbstractTask) {
            s = ((AbstractTask) p).getAudit().getCreated().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }
        return "\"" + (s != null?s.replaceAll("\"", "\\\""):"") + "\"";
    }

    private void locateParent() {
        Object p = current;
        while (p != null) {
            parent = p;
            if (p instanceof QuotationRequest) {
                p = null;
            } else if (p instanceof File) {
                p = ((File) p).getQuotationRequest();
            } else if (p instanceof Booking) {
                p = ((Booking) p).getFile();
            } else if (p instanceof Service) {
                p = ((Service) p).getBooking();
            } else if (p instanceof PurchaseOrder) {
                p = ((PurchaseOrder) p).getServices().get(0);
            } else if (p instanceof SendPurchaseOrdersTask) {
                p = ((SendPurchaseOrdersTask) p).getPurchaseOrders().get(0);
            } else {
                p = null;
            }
        }

    }

}
