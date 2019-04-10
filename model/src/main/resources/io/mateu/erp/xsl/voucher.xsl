<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">

        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="1cm 2cm 2cm 1cm">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="dinA4">

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body" >


                    <xsl:for-each select="//service">

                        <fo:block page-break-inside="avoid" font-size="8pt">
                            <xsl:if test="position() > 1"><xsl:attribute name="page-break-before">always</xsl:attribute></xsl:if>

                            <fo:block margin-bottom="5mm"><fo:external-graphic src="url('{@urllogo}')" content-width="scale-to-fit" width="40mm"/></fo:block>


                            <fo:block text-align="center" font-size="12pt"><fo:inline font-weight="bold">Bono</fo:inline> / <fo:inline font-style="italic">Voucher</fo:inline> / <xsl:choose>
                                <xsl:when test="@type = 'hotel'"><fo:inline font-weight="bold">Alojamiento</fo:inline> / <fo:inline font-style="italic">Accommodation</fo:inline></xsl:when>
                                <xsl:otherwise><fo:inline font-weight="bold">Servicio</fo:inline> / <fo:inline font-style="italic">Service</fo:inline></xsl:otherwise>
                            </xsl:choose></fo:block>

                            <fo:block text-align="center" font-size="8pt"><fo:inline font-weight="bold">Reserva Confirmada y Garantizada - Bono - <xsl:value-of
                                    select="@serviceType"/></fo:inline> / <fo:inline font-style="italic">Booking confirmed and guaranteed - Voucher - <xsl:value-of
                                    select="@serviceType"/></fo:inline></fo:block>

                            <fo:table margin-top="5mm" margin-bottom="5mm">
                                <fo:table-column column-width="33%"/>
                                <fo:table-column column-width="67%"/>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell background-color="#000" color="#fff" padding="3mm">
                                            <fo:block text-align="center"><fo:inline font-weight="bold">Localizador</fo:inline> / <fo:inline font-style="italic">Reference number</fo:inline>:</fo:block>
                                            <fo:block text-align="center" font-weight="bold" font-size="16pt" margin-top="2mm" margin-bottom="2mm"><xsl:value-of
                                                    select="@id"/></fo:block>
                                            <fo:block text-align="center">Válido para el <xsl:choose>
                                                <xsl:when test="@type = 'hotel'">hotel</xsl:when>
                                                <xsl:otherwise>proveedor</xsl:otherwise>
                                            </xsl:choose></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell background-color="#ffeb8e" padding="3mm">
                                            <fo:block font-weight="bold" font-size="14pt" margin-bottom="2mm"><xsl:value-of select="@header"></xsl:value-of></fo:block>
                                            <fo:block text-align-last="justify"><fo:inline><fo:inline font-weight="bold">Nombre de pasajero</fo:inline> / <fo:inline font-style="italic">Passenger name</fo:inline> :</fo:inline><fo:leader/><fo:inline><xsl:value-of
                                                    select="@leadname"/></fo:inline></fo:block>
                                            <fo:block text-align-last="justify"><fo:inline><fo:inline font-weight="bold">Fecha confirmación reserva</fo:inline> / <fo:inline font-style="italic">Booking date</fo:inline> :</fo:inline><fo:leader/><fo:inline><xsl:value-of select="@formalization"></xsl:value-of></fo:inline></fo:block>
                                            <fo:block text-align-last="justify"><fo:inline><fo:inline font-weight="bold">Expediente TO</fo:inline> / <fo:inline font-style="italic">File TO</fo:inline> :</fo:inline><fo:leader/><fo:inline><xsl:value-of select="@fileto"></xsl:value-of></fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>

                            </fo:table>

                            <!--
                            <fo:block>@type = <xsl:value-of select="@type"></xsl:value-of></fo:block>
                            -->


                            <fo:table margin-top="5mm" margin-bottom="5mm" width="100%" border-top-style="solid" border-top-width="0.5pt" border-bottom-style="solid" border-bottom-width="0.5pt">

                                <xsl:choose>
                                    <xsl:when test="@type = 'hotel'">

                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="4cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="3cm"/>
                                        <fo:table-column/>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <!--
                                                <fo:table-cell padding="2mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                                -->
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Unidades</fo:block>
                                                    <fo:block font-style="italic">Units</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Tipo habitación</fo:block>
                                                    <fo:block font-style="italic">Room type</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Desde</fo:block>
                                                    <fo:block font-style="italic">From</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Hasta</fo:block>
                                                    <fo:block font-style="italic">To</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Noches</fo:block>
                                                    <fo:block font-style="italic">Nights</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Adultos - Niños</fo:block>
                                                    <fo:block font-style="italic">Adults - Children</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Régimen</fo:block>
                                                    <fo:block font-style="italic">Board</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>

                                            <xsl:for-each select="lines/line">
                                                <fo:table-row border-top-style="solid" border-top-width="0.1pt">
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@units"></xsl:value-of> x</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@room"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@start"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@end"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@nights"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@adultsperroom"></xsl:value-of> - <xsl:value-of select="@childrenperroom"></xsl:value-of><xsl:if test="@ages"> (<xsl:value-of select="@ages"></xsl:value-of>)</xsl:if></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@board"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </xsl:for-each>

                                        </fo:table-body>

                                    </xsl:when>
                                    <xsl:when test="@type = 'transfer'">

                                        <fo:table-column column-width="1cm"/>
                                        <fo:table-column column-width="4cm"/>
                                        <fo:table-column column-width="4cm"/>
                                        <fo:table-column column-width="2.5cm"/>
                                        <fo:table-column column-width="2.5cm"/>
                                        <fo:table-column column-width="2.5cm"/>
                                        <fo:table-column/>

                                        <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Pax</fo:block>
                                                <fo:block font-style="italic">Pax</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Recogida</fo:block>
                                                <fo:block font-style="italic">Pickup</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Destino</fo:block>
                                                <fo:block font-style="italic">Dropoff</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Fecha vuelo</fo:block>
                                                <fo:block font-style="italic">Flight date</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Hora vuelo</fo:block>
                                                <fo:block font-style="italic">Flight time</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">Nº vuelo</fo:block>
                                                <fo:block font-style="italic">Flight Nr</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="2mm">
                                                <fo:block font-weight="bold">A/De</fo:block>
                                                <fo:block font-style="italic">To/From</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                            <fo:table-row border-top-style="solid" border-top-width="0.1pt">
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@pax"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@pickup"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@dropoff"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@flightDate"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@flightTime"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@flightNumber"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@flightOriginOrDestination"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>


                                        </fo:table-body>

                                    </xsl:when>

                                    <xsl:when test="@type = 'freetext'">

                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column/>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Desde</fo:block>
                                                    <fo:block font-style="italic">From</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Hasta</fo:block>
                                                    <fo:block font-style="italic">To</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Descripción servicio</fo:block>
                                                    <fo:block font-style="italic">Service description</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>

                                            <fo:table-row border-top-style="solid" border-top-width="0.1pt">
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@start"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@finish"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@description"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>


                                        </fo:table-body>

                                    </xsl:when>

                                    <xsl:when test="@type = 'generic'">

                                        <fo:table-column column-width="6cm"/>
                                        <fo:table-column column-width="6cm"/>
                                        <fo:table-column/>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Producto</fo:block>
                                                    <fo:block font-style="italic">Product</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Variante</fo:block>
                                                    <fo:block font-style="italic">Variant</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Unidades</fo:block>
                                                    <fo:block font-style="italic">Units</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>

                                            <fo:table-row border-top-style="solid" border-top-width="0.1pt">
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@product"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@variant"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block><xsl:value-of select="@units"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>


                                        </fo:table-body>

                                    </xsl:when>

                                    <xsl:otherwise>

                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="4cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="3cm"/>
                                        <fo:table-column/>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <!--
                                                <fo:table-cell padding="2mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                                -->
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Unidades</fo:block>
                                                    <fo:block font-style="italic">Units</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Tipo habitación</fo:block>
                                                    <fo:block font-style="italic">Room type</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Desde</fo:block>
                                                    <fo:block font-style="italic">From</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Hasta</fo:block>
                                                    <fo:block font-style="italic">To</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Noches</fo:block>
                                                    <fo:block font-style="italic">Nights</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Adultos - Niños</fo:block>
                                                    <fo:block font-style="italic">Adults - Children</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-weight="bold">Régimen</fo:block>
                                                    <fo:block font-style="italic">Board</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>

                                            <xsl:for-each select="lines/line">
                                                <fo:table-row border-top-style="solid" border-top-width="0.1pt">
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@units"></xsl:value-of> x</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@room"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@start"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@end"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@nights"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@adultsperroom"></xsl:value-of> - <xsl:value-of select="@childrenperroom"></xsl:value-of><xsl:if test="@ages"> (<xsl:value-of select="@ages"></xsl:value-of>)</xsl:if></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell padding="2mm">
                                                        <fo:block><xsl:value-of select="@board"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </xsl:for-each>

                                        </fo:table-body>

                                    </xsl:otherwise>
                                </xsl:choose>



                            </fo:table>


                            <fo:block font-size="12pt"><fo:inline font-weight="bold">Observaciones</fo:inline> / <fo:inline font-style="italic">Remarks</fo:inline></fo:block>

                            <fo:block><xsl:value-of select="@remarks"></xsl:value-of></fo:block>

                            <fo:block width="100%" border-top-width="0.5pt" border-top-style="solid" margin-top="5mm" margin-bottom="2mm"></fo:block>


                            <xsl:for-each select="supplier">

                                <fo:block width="100%" border-width="0.1pt" border-style="solid" padding="2mm" margin="0mm">

                                    <fo:block font-weight="bold" font-size="14pt" margin-bottom="2mm"><xsl:value-of select="@name"></xsl:value-of></fo:block>

                                    <fo:block font-weight="bold"><xsl:value-of select="@address"></xsl:value-of></fo:block>

                                    <fo:block margin-top="3mm"><fo:inline font-weight="bold">Teléfono</fo:inline> / <fo:inline font-style="italic">Telephone</fo:inline>: <fo:inline padding-start="2mm"><xsl:value-of select="@telephone"></xsl:value-of></fo:inline>
                                        <fo:inline padding-start="2cm" font-weight="bold">Email</fo:inline> / <fo:inline font-style="italic">Email</fo:inline>: <fo:inline padding-start="2mm"><xsl:value-of select="@email"></xsl:value-of></fo:inline></fo:block>

                                    <fo:block margin-top="3mm"><fo:inline font-weight="bold">Loc. Proveedor</fo:inline> / <fo:inline font-style="italic">Ref. Supplier</fo:inline>: <fo:inline padding-start="2mm"><xsl:value-of select="@hisreference"></xsl:value-of></fo:inline></fo:block>

                                    <fo:block><fo:inline font-weight="bold">GPS</fo:inline>: <fo:inline padding-start="2mm"><xsl:value-of select="@gps"></xsl:value-of></fo:inline></fo:block>

                                </fo:block>

                            </xsl:for-each>

                            <fo:block margin-top="5mm" text-align="center"><fo:inline font-weight="bold">Reservado y pagadero por</fo:inline> / <fo:inline font-style="italic">Booked and payable by</fo:inline> <fo:inline padding-start="2mm"><xsl:value-of
                                    select="@payableBy"></xsl:value-of></fo:inline></fo:block>


                            <fo:block margin-bottom="5mm" text-align="center"><fo:external-graphic src="url('{@qrfile}')" content-width="scale-to-fit" width="80mm"/></fo:block>

                        </fo:block>


                    </xsl:for-each>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>