package io.mateu.erp.model.booking;

import com.vaadin.pontus.vizcomponent.VizComponent;
import com.vaadin.pontus.vizcomponent.model.Graph;
import com.vaadin.pontus.vizcomponent.model.Subgraph;
import com.vaadin.ui.HorizontalLayout;
import io.mateu.erp.model.invoicing.Invoice;
import io.mateu.erp.model.workflow.AbstractTask;
import io.mateu.erp.model.workflow.SendPurchaseOrdersTask;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.vaadinport.vaadin.MDDUI;

import java.util.HashMap;
import java.util.Map;

public class BookingMap extends HorizontalLayout {

    private Object current;
    private Object parent;
    private Map<Subgraph.Node, Object> reverseMap = new HashMap<>();


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

        buildGraph(graph, null, parent);

        component.setSizeFull();
        //component.setWidth("400px");
        //component.setHeight("300px");
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

    private void buildGraph(Graph graph, Graph.Node parentNode, Object object) {

        if (object != null) {

            Graph.Node node = new Graph.Node(getTitle(object));
            //node.setParam("tooltip", getToolTip(object));
            graph.addNode(node);

            reverseMap.put(node, object);

            if (object.equals(current)) node.setParam("color", "red");

            if (parentNode != null) {
                graph.addEdge(parentNode, node);
            }

            if (object instanceof QuotationRequest) {
                if (((QuotationRequest) object).getFile() != null) {
                    buildGraph(graph, node, ((QuotationRequest) object).getFile());
                }
            } else if (object instanceof File) {
                for (Booking booking : ((File) object).getBookings()) {
                    buildGraph(graph, node, booking);
                }
            } else if (object instanceof Booking) {
                for (Service service : ((Booking) object).getServices()) {
                    buildGraph(graph, node, service);
                }
            } else if (object instanceof Service) {
                for (PurchaseOrder purchaseOrder : ((Service) object).getPurchaseOrders()) {
                    buildGraph(graph, node, purchaseOrder);
                }
            } else if (object instanceof PurchaseOrder) {
                for (SendPurchaseOrdersTask task : ((PurchaseOrder) object).getSendingTasks()) {
                    buildGraph(graph, node, task);
                }
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
            s = p.getClass().getSimpleName() + " " + ((Booking) p).getId();
        } else if (p instanceof Service) {
            s = ((Service) p).getServiceType().name() + " " + ((Service) p).getId();
        } else if (p instanceof PurchaseOrder) {
            s = "Purchase Order " + ((PurchaseOrder) p).getId();
        } else if (p instanceof AbstractTask) {
            s = "Task " + ((AbstractTask) p).getId();
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
            } else {
                p = null;
            }
        }

    }

}
