package io.mateu.erp.model.product.hotel;


import io.mateu.mdd.core.annotations.ColumnWidth;
import io.mateu.mdd.core.annotations.Ignored;
import io.mateu.mdd.core.annotations.ListColumn;
import io.mateu.mdd.core.data.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class StopSalesMonth {

    @ListColumn
    private int year;

    @ListColumn
    private String month;
    @Ignored
    private int monthValue;

    @ListColumn(value = "01")
    @ColumnWidth(40)
    private Data day_1;
    @ListColumn(value = "02")
    @ColumnWidth(40)
    private Data day_2;
    @ListColumn(value = "03")
    @ColumnWidth(40)
    private Data day_3;
    @ListColumn(value = "04")
    @ColumnWidth(40)
    private Data day_4;
    @ListColumn(value = "05")
    @ColumnWidth(40)
    private Data day_5;
    @ListColumn(value = "06")
    @ColumnWidth(40)
    private Data day_6;
    @ListColumn(value = "07")
    @ColumnWidth(40)
    private Data day_7;
    @ListColumn(value = "08")
    @ColumnWidth(40)
    private Data day_8;
    @ListColumn(value = "09")
    @ColumnWidth(40)
    private Data day_9;
    @ListColumn(value = "10")
    @ColumnWidth(40)
    private Data day_10;
    @ListColumn(value = "11")
    @ColumnWidth(40)
    private Data day_11;
    @ListColumn(value = "12")
    @ColumnWidth(40)
    private Data day_12;
    @ListColumn(value = "13")
    @ColumnWidth(40)
    private Data day_13;
    @ListColumn(value = "14")
    @ColumnWidth(40)
    private Data day_14;
    @ListColumn(value = "15")
    @ColumnWidth(40)
    private Data day_15;
    @ListColumn(value = "16")
    @ColumnWidth(40)
    private Data day_16;
    @ListColumn(value = "17")
    @ColumnWidth(40)
    private Data day_17;
    @ListColumn(value = "18")
    @ColumnWidth(40)
    private Data day_18;
    @ListColumn(value = "19")
    @ColumnWidth(40)
    private Data day_19;
    @ListColumn(value = "20")
    @ColumnWidth(40)
    private Data day_20;
    @ListColumn(value = "21")
    @ColumnWidth(40)
    private Data day_21;
    @ListColumn(value = "22")
    @ColumnWidth(40)
    private Data day_22;
    @ListColumn(value = "23")
    @ColumnWidth(40)
    private Data day_23;
    @ListColumn(value = "24")
    @ColumnWidth(40)
    private Data day_24;
    @ListColumn(value = "25")
    @ColumnWidth(40)
    private Data day_25;
    @ListColumn(value = "26")
    @ColumnWidth(40)
    private Data day_26;
    @ListColumn(value = "27")
    @ColumnWidth(40)
    private Data day_27;
    @ListColumn(value = "28")
    @ColumnWidth(40)
    private Data day_28;
    @ListColumn(value = "29")
    @ColumnWidth(40)
    private Data day_29;
    @ListColumn(value = "30")
    @ColumnWidth(40)
    private Data day_30;
    @ListColumn(value = "31")
    @ColumnWidth(40)
    private Data day_31;

}
