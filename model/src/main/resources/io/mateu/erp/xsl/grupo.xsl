<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <xsl:variable name="moneda"><xsl:value-of select="@moneda"/></xsl:variable>
        <xsl:variable name="monedacontable"><xsl:value-of select="@monedacontable"/></xsl:variable>
        <xsl:variable name="exchangerate"><xsl:value-of select="@exchangerate"/></xsl:variable>



        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="2mm 20mm 35mm 20mm">
                    <fo:region-body margin-top="31mm"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="dinA4">

                <!-- Header -->
                <fo:static-content flow-name="xsl-region-before">
                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                        <fo:table-column column-width="54mm"></fo:table-column>
                        <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.2px"></fo:table-column>
                        <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.2px"></fo:table-column>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left"  font-size="8pt">
                                    <fo:block><fo:external-graphic src="url('{@urllogo}')" content-width="scale-to-fit" width="40mm"/></fo:block>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/issuer/@businessName"/> - NIF: <xsl:value-of select="quotationRequests/quotationRequest/issuer/@vatid"/></fo:block>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/issuer/@address"/> - <xsl:value-of select="quotationRequests/quotationRequest/issuer/@zip"/> <xsl:value-of select="quotationRequests/quotationRequest/issuer/@resort"/></fo:block>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/issuer/@email"/> - t. <xsl:value-of select="quotationRequests/quotationRequest/issuer/@telephone"/> - f. <xsl:value-of select="quotationRequests/quotationRequest/issuer/@fax"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-weight="bold" padding="1mm">
                                    <fo:block font-size="9pt" space-after="-2pt">GRUPO <xsl:value-of select="quotationRequests/quotationRequest/@title"/></fo:block>
                                    <fo:block font-size="7pt" font-style="italic" space-after="2pt">Group</fo:block>
                                    <fo:block font-size="9pt" space-after="-2pt">FECHA <xsl:value-of select="quotationRequests/quotationRequest/@date"/></fo:block>
                                    <fo:block font-size="7pt" font-style="italic" space-after="2pt">Date</fo:block>
                                    <xsl:if test="quotationRequests/quotationRequest/@expiryDate">
                                        <fo:block font-size="9pt" space-after="-2pt">VÁLIDO HASTA <xsl:value-of select="quotationRequests/quotationRequest/@expiryDate"/></fo:block>
                                        <fo:block font-size="7pt" font-style="italic" space-after="2pt">Expiry date</fo:block>
                                    </xsl:if>
                                    <fo:block></fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                    <fo:block font-weight="bold"><xsl:value-of select="quotationRequests/quotationRequest/recipient/@name"/></fo:block>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/recipient/@businessName"/></fo:block>
                                    <xsl:if test="quotationRequests/quotationRequest/recipient/@vatid">
                                        <fo:block>NIF: <xsl:value-of select="quotationRequests/quotationRequest/recipient/@vatid"/></fo:block>
                                    </xsl:if>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/recipient/@address"/></fo:block>
                                    <fo:block><xsl:value-of select="quotationRequests/quotationRequest/recipient/@zip"/> <xsl:value-of select="recipient/@resort"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>

                    </fo:table>
                </fo:static-content>

                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">Inscrita en el Reg. Mercantil de Baleares, Folio 53 del Tomo 656 del Archivo, Libro 571 Sección 3a de Sociedades, Hoja no 14.746, Incrip. 1a C.I.F. A07234636.</fo:block>
                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">En cumplimiento de la Ley de Servicios de la Sociedad de la Información y de Comercio Electrónico y de la Vigente ley Orgánica 15/1999 de Protección de Datos Española, le comunicamos que su dirección postal forma parte de nuestra base de datos con la finalidad de informar y gestionar reservas. Así mismo le notificamos la posibilidad de ejercer sus derechos de acceso, rectificación, cancelación u oposición de esos datos dirigiéndose a Viajes Es Freus, s.l., Carrer de Navarra no 7, 07800 Eivissa (Illes Balears). Esta factura va dirigida, de manera exclusiva, a su destinatario y contiene información confidencial y sujeta al secreto profesional, cuya divulgación no está permitida por la ley. En caso de haber recibido esta factura por error, le rogamos que, de forma inmediata, nos lo comunique y proceda a su eliminación. Asimismo, le comunicamos que la distribución, copia o utilización de esta factura, cualquiera que fuera su finalidad, están prohibidos por la ley.</fo:block>

                    <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Página <fo:page-number></fo:page-number> de <fo:page-number-citation-last ref-id="end"/></fo:block>

                </fo:static-content>

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body" >


                    <xsl:for-each select="//quotationRequest">

                        <!--
                        <fo:block>FACTURA</fo:block>
                        -->


                        <!-- TÍTULO -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="1px" padding="1mm">
                                       <fo:block font-weight="700" font-size="12pt">PROFORMA <fo:inline font-size="9pt" font-style="italic" font-weight="normal">PRO-FORMA</fo:inline></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>




<!-- ESTANCIAS -->

                        <xsl:if test="hotels/line">

                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="30mm"></fo:table-column>
                            <fo:table-column column-width="44mm"></fo:table-column>
                            <fo:table-column column-width="50mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>

                            <!-- CONCEPTOS -->

                            <xsl:for-each select="hotels">

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="3">
                                            <fo:block font-weight="700" font-size="9pt">HOTELES <fo:inline font-size="7pt" font-style="italic" font-weight="normal">Hotels</fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="line">

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="7">
                                            <fo:block font-weight="700" font-size="9pt"><xsl:value-of select="@hotel"/>
                                                <fo:inline space-start="5mm" font-size="7pt" font-style="italic" font-weight="normal">First service: <xsl:value-of
                                                        select="@firstService"/></fo:inline>
                                                <fo:inline font-size="7pt" font-style="italic" font-weight="normal">, last service: <xsl:value-of
                                                        select="@lastService"/></fo:inline>
                                            </fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>fechas</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>habitación</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>régimen</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>nº habs.</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>adultos/hab.</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>niños/hab.</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>total</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="lines/line">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@start"/> to <xsl:value-of select="@end"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@room"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@board"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@rooms"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@adultsPerRoom"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@childrenPerRoom"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>


                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="7">
                                            <fo:block><xsl:value-of select="@specialRequests"/></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>




                                        <xsl:if test="@adultTaxPerNight or @childTaxPerNight">
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px" number-columns-spanned="6">
                                                    <fo:block text-align="right">Adult taxes (<xsl:value-of select="@adultTaxPerNight"/> x pax x night)</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block text-align="right"><xsl:value-of select="@totalAdTax"/> €</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:if>


                                        <xsl:if test="@childTaxPerNight">
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px" number-columns-spanned="6">
                                                    <fo:block text-align="right">Child taxes (<xsl:value-of select="@childTaxPerNight"/> x pax x night)</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block text-align="right"><xsl:value-of select="@totalChTax"/> €</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:if>

                                        <fo:table-row>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px" number-columns-spanned="6">
                                                                                <fo:block text-align="right">Total hotel <xsl:value-of
                                                                                        select="@hotel"/></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                                                            </fo:table-cell>
                                                                        </fo:table-row>

                                    </xsl:for-each>
                                </fo:table-body>
                            </xsl:for-each>

                        </fo:table>

                        </xsl:if>

<!-- TRASLADOS -->

                        <xsl:if test="transfers/line">

                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="30mm"></fo:table-column>
                            <fo:table-column column-width="30mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>
                            <fo:table-column column-width="32mm"></fo:table-column>
                            <fo:table-column column-width="32mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>

                            <!-- CONCEPTOS -->

                            <xsl:for-each select="transfers">

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>


                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="3">
                                            <fo:block font-weight="700" font-size="9pt">TRASLADOS <fo:inline font-size="7pt" font-style="italic" font-weight="normal">Transfers</fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>


                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>fecha y hora vuelo</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block> nº vuelo (proc. / dest.)</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>tipo</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>origen</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>destino</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>pax</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>total</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="line">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@flightDate"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@flightNumber"/> (<xsl:value-of select="@flightOriginOrDestination"/>)</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@transferType"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@origin"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@destination"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@pax"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>


                                    <!--
                                                                        <fo:table-row>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right">Total servicio:</fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                                                            </fo:table-cell>
                                                                        </fo:table-row>
                                    -->
                                </fo:table-body>
                            </xsl:for-each>

                        </fo:table>

                        </xsl:if>

<!-- EXCURSIONES -->

                        <xsl:if test="excursions/line">

                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="20mm"></fo:table-column>
                            <fo:table-column column-width="44mm"></fo:table-column>
                            <fo:table-column column-width="40mm"></fo:table-column>
                            <fo:table-column column-width="30mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>

                            <!-- CONCEPTOS -->

                            <xsl:for-each select="excursions">

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="3">
                                            <fo:block font-weight="700" font-size="9pt">EXCURSIONES <fo:inline font-size="7pt" font-style="italic" font-weight="normal">Excursions</fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>


                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>fecha</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>excursión</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>variante</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>turno</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>adultos</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>niños</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>total</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="line">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@date"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@excursion"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@variant"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@shift"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@adults"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@children"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>


                                    <!--
                                                                        <fo:table-row>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right">Total servicio:</fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                                                            </fo:table-cell>
                                                                        </fo:table-row>
                                    -->
                                </fo:table-body>
                            </xsl:for-each>

                        </fo:table>

                        </xsl:if>

<!-- GENÉRICOS -->

                        <xsl:if test="generics/line">

                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="30mm"></fo:table-column>
                            <fo:table-column column-width="54mm"></fo:table-column>
                            <fo:table-column column-width="40mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="10mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>

                            <!-- CONCEPTOS -->

                            <xsl:for-each select="generics">

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" number-columns-spanned="3">
                                            <fo:block font-weight="700" font-size="9pt">OTROS SERVICIOS <fo:inline font-size="7pt" font-style="italic" font-weight="normal">Other services</fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>



                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>fechas</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>producto</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>variante</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>uds.</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>adultos</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>niños</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>total</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="line">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@start"/> to <xsl:value-of select="@end"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@product"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@variant"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@units"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@adults"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@children"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>


                                    <!--
                                                                        <fo:table-row>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block></fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right">Total servicio:</fo:block>
                                                                            </fo:table-cell>
                                                                            <fo:table-cell
                                                                                    text-align="right"
                                                                                    padding="1mm"
                                                                                    border-right-style="solid"
                                                                                    border-right-width="0.2px">
                                                                                <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                                                            </fo:table-cell>
                                                                        </fo:table-row>
                                    -->
                                </fo:table-body>
                            </xsl:for-each>

                        </fo:table>

                        </xsl:if>


                        <!-- TEXTO LIBRE -->
                        <xsl:if test="lines/line">

                            <!-- COLUMNAS -->
                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                                <fo:table-column column-width="30mm"></fo:table-column>
                                <fo:table-column column-width="84mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>

                                <!-- CONCEPTOS -->

                                <xsl:for-each select="lines">

                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>


                                        <fo:table-row>
                                            <fo:table-cell padding="1mm" number-columns-spanned="3">
                                                <fo:block font-weight="700" font-size="9pt">OTROS SERVICIOS <fo:inline font-size="7pt" font-style="italic" font-weight="normal">Other services</fo:inline></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                                <fo:block>fechas</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                                <fo:block>concepto</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                                <fo:block>unidades</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                                <fo:block>precio</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                                <fo:block>total</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <xsl:for-each select="line">

                                            <fo:table-row>
                                                <fo:table-cell
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block><xsl:value-of select="@start"/> to <xsl:value-of select="@end"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block><xsl:value-of select="@text"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block><xsl:value-of select="@units"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block><xsl:value-of select="@price"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.2px">
                                                    <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:for-each>


                                        <!--
                                                                            <fo:table-row>
                                                                                <fo:table-cell
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block></fo:block>
                                                                                </fo:table-cell>
                                                                                <fo:table-cell
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block></fo:block>
                                                                                </fo:table-cell>
                                                                                <fo:table-cell
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block></fo:block>
                                                                                </fo:table-cell>
                                                                                <fo:table-cell
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block></fo:block>
                                                                                </fo:table-cell>
                                                                                <fo:table-cell
                                                                                        text-align="right"
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block text-align="right">Total servicio:</fo:block>
                                                                                </fo:table-cell>
                                                                                <fo:table-cell
                                                                                        text-align="right"
                                                                                        padding="1mm"
                                                                                        border-right-style="solid"
                                                                                        border-right-width="0.2px">
                                                                                    <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                                                                </fo:table-cell>
                                                                            </fo:table-row>
                                        -->
                                    </fo:table-body>
                                </xsl:for-each>

                            </fo:table>

                        </xsl:if>


                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                        <fo:table-column column-width="174mm"></fo:table-column>


                        <!-- PRECIO FINAL -->
                        <!-- PRECIO FINAL -->
                        <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell padding="3mm">
                                <fo:block></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                        <fo:table-cell
                                padding="1mm">
                            <fo:block><xsl:value-of select="@text"/></fo:block>
                        </fo:table-cell>
                        </fo:table-row>
                        </fo:table-body>
                        </fo:table>


                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="15mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>
                            <fo:table-column column-width="25mm"></fo:table-column>
                            <fo:table-column column-width="40mm"></fo:table-column>
                            <fo:table-column column-width="54mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>


                            <!-- PRECIO FINAL -->
                            <!-- PRECIO FINAL -->
                            <fo:table-body font-weight="bold">
                                <fo:table-row>
                                    <fo:table-cell padding="3mm">
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell
                                            number-columns-spanned="4"
                                            text-align="right"
                                            padding="1mm">
                                        <fo:block text-align="right"><xsl:if test="@watermark"><fo:external-graphic src="url('{@watermark}')" content-width="scale-to-fit" width="30mm"/></xsl:if></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell
                                            number-columns-spanned="1"
                                            text-align="right"
                                            padding="1mm"
                                            border-right-style="solid"
                                            border-right-width="0.2px">

                                        <xsl:if test="@total">
                                            <fo:block space-after="3pt">TOTAL</fo:block>
                                        </xsl:if>
                                        <fo:block space-after="3pt">pagado</fo:block>
                                        <fo:block space-after="3pt">pendiente de pago</fo:block>

                                        <xsl:if test="$moneda != $monedacontable"><fo:block space-after="3pt" color="white">x</fo:block><fo:block space-after="3pt">Exchage rate: <xsl:value-of select="$exchangerate"/></fo:block></xsl:if>

                                    </fo:table-cell>
                                    <fo:table-cell
                                            text-align="right"
                                            padding="1mm"
                                            border-right-style="solid"
                                            border-right-width="0.2px">

                                        <xsl:if test="@total">
                                            <fo:block space-after="3pt"><xsl:value-of select="@total"/>&#x00A0; €</fo:block>
                                        </xsl:if>
                                        <fo:block space-after="3pt"><xsl:value-of select="@paid"/>&#x00A0; €</fo:block>
                                        <fo:block space-after="3pt"><xsl:value-of select="@pending"/>&#x00A0; €</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                            <!-- CANCELACIÓN -->


                            <fo:table-body>

                                <xsl:if test="reserva">
                                    <fo:table-row>
                                        <fo:table-cell padding="5mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell number-columns-spanned="6" padding="1mm">
                                            <fo:block>GASTOS DE CANCELACIÓN</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell number-columns-spanned="4" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>concepto</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>desde</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>valoración</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="reserva/servicio/costecancelacion">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    number-columns-spanned="4"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="../@descripcion"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@desde"/> 00:00 AM</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@coste"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                    <fo:table-row>
                                        <fo:table-cell
                                                number-columns-spanned="4"
                                                padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                font-size="6pt"
                                                padding="1mm">
                                            <fo:block>* Fechas y horas de horario local del país de destino.</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                </xsl:if>

                                <fo:table-row>
                                    <fo:table-cell padding="5mm">
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell number-columns-spanned="6" padding="1mm">
                                        <fo:block><xsl:value-of select="facturadopor/@pago"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                            </fo:table-body>


                        </fo:table>



                    </xsl:for-each>

                    <fo:block id="end"></fo:block>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>