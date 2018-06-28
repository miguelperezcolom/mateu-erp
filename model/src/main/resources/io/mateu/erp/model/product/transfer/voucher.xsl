BussoHola Demo.



        Buscar




        Logout


        Menú
        Filtro:


        Home
        Configuración
        AppConfig
        Xsls
        Marcas
        Delegaciones
        Channel Managers
        Proveedores
        Mercados
        Productos
        Departamentos
        Agentes contratación
        Agentes facturación
        Divisas
        Impuestos
        Usuarios
        Accesos
        Mapa geográfico
        Cláusulas
        Webs
        Grupos traducciones
        Traducciones
        Templates email
        Importación reservas
        Alarmas
        Integraciones
        Clientes
        Facturación
        Facturas proveedores
        VAT
        Contabilidad
        Cartera
        Informes
        Producto
        Reservas
        Utilidades
        Documentación
        Incidencias
        Newsletters
        Contactos
        AppConfig 1
        Xsls 1


        Grabar




        Grabar y reset




        Recargar


        Voucher cliente
        Please book
        Factura
        Factura proveedor
        Abono
        Contrato hotel
        Contrato traslado
        Carta pagos
        Dita

        <?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA5" page-height="148.5mm" page-width="210mm" margin="2mm 18mm 18mm 18mm">
                    <fo:region-body margin-top="10mm"/>
                    <!--
                    <fo:region-before display-align="after"/>
                    -->
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="dinA5">

                <!-- Header -->
                <!--
                <fo:static-content flow-name="xsl-region-before">
                </fo:static-content>
                -->

                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow"></fo:block>
                </fo:static-content>

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body" >

                    <xsl:for-each select="/basket/services/*">

                        <xsl:if test="status != 'CANCELLED' or not(/basket/services/*[status != 'CANCELLED'])">

                            <xsl:if test="position() > 1"><fo:block page-break-before="always"></fo:block></xsl:if>

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                <fo:table-column column-width="54mm"></fo:table-column>
                                <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="1px"></fo:table-column>
                                <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="1px"></fo:table-column>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell text-align="left"  font-size="8pt">
                                            <fo:block><fo:external-graphic src="{/basket/urllogo}" content-width="scale-to-fit" width="40mm"/></fo:block>
                                            <fo:block><xsl:value-of select="/basket/brand"></xsl:value-of></fo:block>
                                            <fo:block>+420 233 107 511 - kabrtova@estec.cz</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-weight="bold" padding="1mm">
                                            <xsl:choose>
                                                <xsl:when test="providerLocator != ''">
                                                    <fo:block font-size="9pt">LOCATOR: <fo:inline font-weight="normal"><xsl:value-of select="/basket/locator"></xsl:value-of></fo:inline></fo:block>
                                                    <fo:block font-size="9pt" padding-after="8pt">LOCATOR FOR PROVIDER: <fo:inline font-weight="normal"><xsl:value-of select="providerLocator"></xsl:value-of></fo:inline></fo:block>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <fo:block font-size="9pt" padding-after="8pt">LOCATOR: <fo:inline font-weight="normal"><xsl:value-of select="/basket/locator"></xsl:value-of></fo:inline></fo:block>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <fo:block font-size="9pt">AGENCY: <fo:inline font-weight="normal"><xsl:value-of select="/basket/customer"></xsl:value-of></fo:inline></fo:block>
                                            <fo:block font-size="9pt" padding-after="8pt">AGENCY REFERENCE: <fo:inline font-weight="normal"><xsl:value-of select="/basket/agencyReference"></xsl:value-of></fo:inline></fo:block>
                                            <fo:block font-size="9pt">USER: <fo:inline font-weight="normal"><xsl:value-of select="/basket/created/@user"></xsl:value-of></fo:inline></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                            <!--
                                            <fo:block font-weight="bold"><xsl:value-of select="name(.)"></xsl:value-of></fo:block>
                                            -->
                                            <xsl:choose>
                                                <xsl:when test="name(.) = 'hotelBooking'">
                                                    <fo:block font-weight="bold"><xsl:value-of select="establishment/name"/> <xsl:value-of select="establishment/category"/></fo:block>
                                                    <fo:block><xsl:value-of select="address/address/address1"/></fo:block>
                                                    <fo:block><xsl:value-of select="address/address/zip"/> - <xsl:value-of select="address/address/city"/></fo:block>
                                                    <fo:block><xsl:value-of select="address/address/country"/></fo:block>
                                                    <xsl:if test="address/address/email != ''">
                                                        <fo:block><xsl:value-of select="address/address/email"/></fo:block>
                                                    </xsl:if>
                                                    <xsl:if test="address/address/telephone != ''">
                                                        <fo:block>t.: <xsl:value-of select="address/address/telephone"/></fo:block>
                                                    </xsl:if>
                                                    <xsl:if test="address/address/fax != ''">
                                                        <fo:block>f.: <xsl:value-of select="address/address/fax"/></fo:block>
                                                    </xsl:if>
                                                </xsl:when>
                                                <xsl:when test="name(.) = 'transferBooking'">
                                                    <fo:block font-weight="bold"><xsl:choose>
                                                        <xsl:when test="lines/line/transferType = 'ARRIVAL'">ARRIVAL</xsl:when>
                                                        <xsl:otherwise>DEPARTURE</xsl:otherwise>
                                                    </xsl:choose> TRANSFER</fo:block>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <fo:block font-weight="bold"><xsl:value-of select="description"></xsl:value-of></fo:block>
                                                    <fo:block><xsl:value-of select="provider/address/address1"/></fo:block>
                                                    <fo:block><xsl:value-of select="provider/address/zip"/> - <xsl:value-of select="provider/address/city"/></fo:block>
                                                    <fo:block><xsl:value-of select="provider/address/country"/></fo:block>
                                                    <xsl:if test="provider/address/email != ''">
                                                        <fo:block><xsl:value-of select="provider/address/email"/></fo:block>
                                                    </xsl:if>
                                                    <xsl:if test="provider/address/telephone != ''">
                                                        <fo:block>t.: <xsl:value-of select="provider/address/telephone"/></fo:block>
                                                    </xsl:if>
                                                    <xsl:if test="provider/address/fax != ''">
                                                        <fo:block>f.: <xsl:value-of select="provider/address/fax"/></fo:block>
                                                    </xsl:if>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>

                            </fo:table>







                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                <fo:table-column></fo:table-column>
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell border-bottom-style="solid" border-right-width="1px" padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt"><xsl:choose>
                                                <xsl:when test="status = 'CANCELLED'">CANCELLED SERVICE</xsl:when>
                                                <xsl:otherwise>VOUCHER</xsl:otherwise>
                                            </xsl:choose></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                                <fo:table-column column-width="14mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="3mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-width="1px">
                                        <fo:table-cell padding="1mm" number-columns-spanned="2">
                                            <fo:block>date and time of booking</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" number-columns-spanned="2">
                                            <fo:block>leader</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" number-columns-spanned="2">
                                            <fo:block>passengers list</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" number-columns-spanned="3">

                                            <xsl:choose>
                                                <xsl:when test="name(.) = 'excursionBooking'">
                                                    <fo:block>activity</fo:block>
                                                </xsl:when>

                                                <xsl:otherwise>
                                                    <fo:block>remarks</fo:block>
                                                </xsl:otherwise>
                                            </xsl:choose>

                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell
                                                number-columns-spanned="2"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="1px">
                                            <fo:block><xsl:value-of select="created/@when"></xsl:value-of></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                number-columns-spanned="2"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="1px">
                                            <fo:block>Name: <xsl:value-of select="/basket/titular"></xsl:value-of></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                number-columns-spanned="2"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="1px">






                                            <xsl:choose>
                                                <xsl:when test="/basket/passengers/passenger">
                                                    <xsl:for-each select="/basket/passengers/passenger[position() &gt; 1]">

                                                        <fo:block>Name: <xsl:value-of select="name"></xsl:value-of> </fo:block>

                                                    </xsl:for-each>
                                                </xsl:when>
                                                <xsl:otherwise><fo:block>--</fo:block></xsl:otherwise>
                                            </xsl:choose>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                number-columns-spanned="3"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="1px">
                                            <xsl:choose>
                                                <xsl:when test="remarks/remark">
                                                    <xsl:for-each select="remarks/remark">
                                                        <fo:block><xsl:value-of select="."></xsl:value-of> <fo:inline font-weight="bold"><xsl:value-of select="comment1"></xsl:value-of> <xsl:value-of select="comment2"></xsl:value-of></fo:inline></fo:block>
                                                    </xsl:for-each>
                                                </xsl:when>
                                                <xsl:otherwise><fo:block></fo:block></xsl:otherwise>
                                            </xsl:choose>
                                            <fo:block><xsl:value-of select="commentForProvider"></xsl:value-of></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>

                                <xsl:choose>
                                    <xsl:when test="name(.)= 'hotelBooking'">

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="3mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-width="1px">
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>rooms</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="2">
                                                    <fo:block>occupation / room</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="2">
                                                    <fo:block>room type</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>board type</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="2">
                                                    <fo:block>dates</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>status</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>

                                        <xsl:for-each select="lines/line">

                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="numberOfRooms"></xsl:value-of></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            number-columns-spanned="2"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:choose>
                                                            <xsl:when test="occupationEntries/entry">
                                                                <xsl:for-each select="occupationEntries/entry">
                                                                    <xsl:value-of select="."/> x <xsl:value-of select="@paxes"/> paxes<br/>
                                                                </xsl:for-each>
                                                                <xsl:if test="childrenA != '0'"><xsl:for-each select="childrenA"/> children A</xsl:if>
                                                                <xsl:if test="childrenB != '0'"><xsl:for-each select="childrenB"/> children B</xsl:if>
                                                                <xsl:for-each select="children/children">
                                                                    <xsl:value-of select="."/> <xsl:choose>
                                                                    <xsl:when test="@fromPax = '1'">1st</xsl:when>
                                                                    <xsl:otherwise>2nd</xsl:otherwise>
                                                                </xsl:choose> children from <xsl:value-of select="@fromAge"/> to <xsl:value-of select="@toAge"/> years<br/>
                                                                </xsl:for-each>
                                                            </xsl:when>
                                                            <xsl:otherwise><xsl:value-of select="adults" /> adults <xsl:if test="children != '0'">
                                                                + <xsl:value-of select="children" /> children (<xsl:for-each select="childAges/age">
                                                                <xsl:if test="position() > 1">,</xsl:if>
                                                                <xsl:value-of select="." />
                                                            </xsl:for-each>)</xsl:if></xsl:otherwise>
                                                        </xsl:choose></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            number-columns-spanned="2"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:choose>
                                                            <xsl:when test="roomType/name"><xsl:value-of select="roomType/name/t[@l = 'es']"/></xsl:when>
                                                            <xsl:otherwise><xsl:value-of select="roomName"/></xsl:otherwise>
                                                        </xsl:choose></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:choose>
                                                            <xsl:when test="../../boardType/name"><xsl:value-of select="../../boardType/name/t[@l = 'es']"/></xsl:when>
                                                            <xsl:otherwise><xsl:value-of select="../../boardType"/></xsl:otherwise>
                                                        </xsl:choose></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            number-columns-spanned="2"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="from"/> - <xsl:value-of select="to"/></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="status"/></fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                                <!--
                                                <fo:table-row>
                                                    <fo:table-cell
                                                        number-columns-spanned="6"
                                                        padding="1mm">
                                                        <fo:block></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                        font-size="6pt"
                                                        number-columns-spanned="3"
                                                        padding="1mm">
                                                        <fo:block>* From 00:00 (GMT+1) of the date indicated</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                                -->
                                            </fo:table-body>

                                        </xsl:for-each>


                                    </xsl:when>
                                    <xsl:when test="name(.) = 'transferBooking'">

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="3mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-width="1px">
                                                <fo:table-cell padding="1mm" number-columns-spanned="3">
                                                    <fo:block>description</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="2">
                                                    <fo:block>flight</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>vehicle</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>transfer type</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>pax</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>status</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>

                                        <xsl:for-each select="lines/line">

                                            <xsl:variable name="hotelN"><xsl:choose>
                                                <xsl:when test="establishment/name"><xsl:value-of select="establishment/name" /></xsl:when>
                                                <xsl:otherwise><xsl:value-of select="hotelName" /></xsl:otherwise>
                                            </xsl:choose></xsl:variable>

                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell
                                                            number-columns-spanned="3"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:choose>
                                                            <xsl:when test="transferType = 'ARRIVAL'">
                                                                PICKUP AT <xsl:value-of select="airport"/> AND TRANSFER TO THE HOTEL <xsl:value-of select="$hotelN"/> (<xsl:value-of select="city" />)
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                PICKUP AT THE HOTEL <xsl:value-of select="$hotelN"/> (<xsl:value-of select="city" />) AND TRANSFER TO <xsl:value-of select="airport"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            number-columns-spanned="2"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="flightTime" /> <xsl:value-of select="flightNumber" /></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="numberOfVehicles" /> X <xsl:value-of select="transport/name/t[@l = 'es']" /></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="transport/type"/></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="numberOfPaxes - numberOfChilds"/> ADULT<xsl:if test="(numberOfPaxes - numberOfChilds) > 1">S</xsl:if>
                                                            <xsl:if test="numberOfChilds > 0">, <xsl:value-of select="numberOfChilds"/> CHILD<xsl:if test="numberOfChilds > 1">REN</xsl:if></xsl:if>
                                                            <xsl:if test="babies > 0">, <xsl:value-of select="babies"/> INFANT<xsl:if test="babies > 1">S</xsl:if></xsl:if>	</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="1px">
                                                        <fo:block><xsl:value-of select="status"/></fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                                <!--
                                                <fo:table-row>
                                                    <fo:table-cell
                                                        number-columns-spanned="6"
                                                        padding="1mm">
                                                        <fo:block></fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                        font-size="6pt"
                                                        number-columns-spanned="3"
                                                        padding="1mm">
                                                        <fo:block>* From 00:00 (GMT+1) of the date indicated</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                                -->
                                            </fo:table-body>

                                        </xsl:for-each>


                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="3mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-width="1px">
                                                <fo:table-cell padding="1mm" number-columns-spanned="9">
                                                    <fo:block><xsl:choose>
                                                        <xsl:when test="transferType = 'ARRIVAL'">arrival</xsl:when>
                                                        <xsl:when test="transferType = 'DEPARTURE'">departure</xsl:when>
                                                    </xsl:choose>instructions</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell
                                                        number-columns-spanned="9"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="1px">
                                                    <fo:block><xsl:value-of select="instructions" /></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>


                                    </xsl:when>
                                    <xsl:otherwise>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="3mm">
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-width="1px">
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>quantity</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="5">
                                                    <fo:block>description</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm" number-columns-spanned="2">
                                                    <fo:block>dates</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block>status</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="1px">
                                                    <fo:block><xsl:value-of select="paxes"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        number-columns-spanned="5"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="1px">
                                                    <fo:block><xsl:value-of select="purchaseLines/line/@description"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        number-columns-spanned="2"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="1px">
                                                    <fo:block><xsl:value-of select="from"></xsl:value-of> - <xsl:value-of select="to"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="1px">
                                                    <fo:block><xsl:value-of select="status"></xsl:value-of></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>


                                    </xsl:otherwise>
                                </xsl:choose>

                            </fo:table>






                        </xsl:if>


                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow" margin-top="1cm">Guaranteed and payable by <xsl:value-of select="payableBy"></xsl:value-of></fo:block>


                    </xsl:for-each>


                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="*"/>
</xsl:stylesheet>
